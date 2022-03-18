package ru.lyrian.location_tracker.model.cloud_database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.model.pojo.User
import ru.lyrian.location_tracker.model.pojo.UserLocations
import java.util.*
import javax.inject.Inject

class CloudDataSource @Inject constructor(private val realtimeDatabase: FirebaseDatabase) :
    ICloudDataSource {
    private val emptyUserKey = "Empty"
    private val usersDbChild = "users"
    private val locationsDbChild = "locations"
    private val emailDbChild = "email"
    private val latitudeMapKey = "latitude"
    private val longitudeMapKey = "longitude"
    private val usersDbReference = this.realtimeDatabase.getReference(this.usersDbChild)
    private val locationsDbReference = this.realtimeDatabase.getReference(this.locationsDbChild)

    override fun save(userLocations: UserLocations): Completable {
        return forceDbConnection()
            .subscribeOn(Schedulers.single())
            .andThen(getUserKeyByEmail(userLocations.user.email!!))
            .flatMap { createOrUpdateUser(it, userLocations.user) }
            .flatMapCompletable { saveLocations(it, userLocations.locationAtTimestamp) }
    }

    override fun getLocationsByDayRange(email: String, timestampRange: Pair<Long, Long>) = getUserKeyByEmail(email)
        .subscribeOn(Schedulers.io())
        .flatMapMaybe { getLocationsIfUserExisted(it, timestampRange) }

    override fun listenUserUpdates(email: String): Observable<Long> {
        return getUserKeyByEmail(email)
            .subscribeOn(Schedulers.io())
            .flatMapObservable { userId: String ->
                Observable.create { observableEmitter: ObservableEmitter<Long> ->
                    val userListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue<User>()
                            if (!observableEmitter.isDisposed) observableEmitter.onNext(user?.timestamp!!)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            if (!observableEmitter.isDisposed) observableEmitter.onError(error.toException())
                        }
                    }

                    this.usersDbReference
                        .child(userId)
                        .addValueEventListener(userListener)
                    observableEmitter.setCancellable {
                        this.usersDbReference
                            .child(userId)
                            .removeEventListener(userListener)
                    }
                }
            }
    }

    private fun forceDbConnection(): Completable = Completable.fromAction {
        this.realtimeDatabase.goOnline()
    }

    private fun getUserKeyByEmail(email: String) = Single.create { singleEmitter: SingleEmitter<String> ->
        this.usersDbReference
            .orderByChild(this.emailDbChild)
            .equalTo(email)
            .get()
            .addOnSuccessListener { dataSnapshot: DataSnapshot ->
                when (val userDataSnapshot = dataSnapshot.children.firstOrNull()) {
                    null -> if (!singleEmitter.isDisposed) singleEmitter.onSuccess(this.emptyUserKey)
                    else -> userDataSnapshot
                        .key
                        ?.let { userKey: String ->
                            if (!singleEmitter.isDisposed) singleEmitter.onSuccess(userKey)
                        }
                }
            }
            .addOnFailureListener { exception: Exception ->
                if (!singleEmitter.isDisposed) singleEmitter.onError(exception)
            }
    }

    private fun createOrUpdateUser(userKey: String, user: User) = when (userKey) {
        this.emptyUserKey -> createUser(user)
        else -> getUserFromDb(userKey)
            .flatMap { updateUserIfNecessary(userKey, it, user) }
    }

    private fun createUser(user: User) = Single.create { singleEmitter: SingleEmitter<String> ->
        this.usersDbReference
            .push()
            .key
            ?.let { userKey: String ->
                this.usersDbReference.child(userKey).setValue(user)
                    .addOnSuccessListener {
                        if (!singleEmitter.isDisposed) singleEmitter.onSuccess(userKey)
                    }
                    .addOnFailureListener { exception: Exception ->
                        if (!singleEmitter.isDisposed) singleEmitter.onError(exception)
                    }
            }
    }

    private fun getUserFromDb(userKey: String) = Single.create { singleEmitter: SingleEmitter<User> ->
        this.usersDbReference
            .child(userKey)
            .get()
            .addOnSuccessListener { dataSnapshot: DataSnapshot ->
                val userInDb = dataSnapshot.getValue(User::class.java)
                if (!singleEmitter.isDisposed) singleEmitter.onSuccess(userInDb!!)
            }
            .addOnFailureListener { exception: Exception ->
                if (!singleEmitter.isDisposed) singleEmitter.onError(exception)
            }
    }

    private fun updateUserIfNecessary(userKey: String, userInDb: User, newUser: User) =
        Single.create { singleEmitter: SingleEmitter<String> ->
            if (userInDb.timestamp!! < newUser.timestamp!!) {
                this.usersDbReference
                    .child(userKey)
                    .setValue(newUser)
                    .addOnSuccessListener {
                        if (!singleEmitter.isDisposed) singleEmitter.onSuccess(userKey)
                    }
                    .addOnFailureListener { exception: Exception ->
                        if (!singleEmitter.isDisposed) singleEmitter.onError(exception)
                    }
            }
        }

    private fun saveLocations(userKey: String, locationAtTimestamp: Map<String, LocationEntry>) =
        Completable.create { completableEmitter: CompletableEmitter ->
            this.locationsDbReference
                .child(userKey)
                .updateChildren(locationAtTimestamp)
                .addOnSuccessListener {
                    if (!completableEmitter.isDisposed) completableEmitter.onComplete()
                }
                .addOnFailureListener { exception: Exception ->
                    if (!completableEmitter.isDisposed) completableEmitter.onError(exception)
                }
        }


    private fun getLocationsIfUserExisted(userKey: String, timestampRange: Pair<Long, Long>) =
        when (userKey) {
            this.emptyUserKey -> Maybe.empty()
            else -> getLocations(userKey, timestampRange)
        }

    @Suppress("UNCHECKED_CAST")
    private fun getLocations(userKey: String, timestampRange: Pair<Long, Long>) =
        Maybe.create { maybeEmitter: MaybeEmitter<Map<String, LocationEntry>> ->
            val locationsMap = TreeMap<String, LocationEntry>()
            this.locationsDbReference
                .child(userKey)
                .orderByKey()
                .startAt(timestampRange.first.toString())
                .endAt(timestampRange.second.toString())
                .get()
                .addOnSuccessListener { dataSnapshot: DataSnapshot ->
                    when (dataSnapshot.children.count()) {
                        0 -> if (!maybeEmitter.isDisposed) maybeEmitter.onComplete()
                        else -> {
                            dataSnapshot.children.forEach {
                                val location = it.value as HashMap<String, Double>
                                locationsMap[it.key!!] =
                                    LocationEntry(location[this.latitudeMapKey], location[this.longitudeMapKey])
                            }

                            if (!maybeEmitter.isDisposed) maybeEmitter.onSuccess(locationsMap)
                        }
                    }
                }
        }
}
