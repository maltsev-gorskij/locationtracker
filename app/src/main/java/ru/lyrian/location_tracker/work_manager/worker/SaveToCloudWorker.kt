package ru.lyrian.location_tracker.work_manager.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.cloud_database.ICloudDataSource
import ru.lyrian.location_tracker.model.local_database.ILocalDataSource
import ru.lyrian.location_tracker.model.local_database.entities.LocationEntity
import ru.lyrian.location_tracker.model.local_database.entities.UserWithLocations
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.model.pojo.SignInResult
import ru.lyrian.location_tracker.model.pojo.User
import ru.lyrian.location_tracker.model.pojo.UserLocations

class SaveToCloudWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val iAuthProvider: IAuthProvider,
    private val iLocalDataSource: ILocalDataSource,
    private val iCloudDataSource: ICloudDataSource
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return this.iAuthProvider
            .getSignedInUser()
            .filter { it is SignInResult.SignedIn }
            .cast(SignInResult.SignedIn::class.java)
            .flatMapSingle { iLocalDataSource.fetchUserWithLocationsByEmail(it.email!!) }
            .filter { it.locations.isNotEmpty() }
            .map { userWithLocations: UserWithLocations -> assembleDataForCloudDb(userWithLocations) }
            .flatMapCompletable { this.iCloudDataSource.save(it) }
            .andThen(this.iLocalDataSource.deleteAllUsers())
            .andThen(this.iLocalDataSource.deleteAllLocations())
            .toSingleDefault(Result.success())
            .onErrorReturnItem(Result.retry())
    }

    private fun assembleDataForCloudDb(userWithLocations: UserWithLocations): UserLocations {
        val locationsToSave: MutableMap<String, LocationEntry> = mutableMapOf()

        userWithLocations.locations.forEach { locationEntity: LocationEntity ->
            locationsToSave += locationEntity.timestamp.toString() to
                    LocationEntry(locationEntity.latitude, locationEntity.longitude)
        }

        val user = User(
            userWithLocations.user.email,
            locationsToSave.maxOf { it.key }.toLong()
        )

        return UserLocations(user, locationsToSave)
    }
}