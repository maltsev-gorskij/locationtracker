package ru.lyrian.location_tracker.model.local_database

import androidx.room.EmptyResultSetException
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.lyrian.location_tracker.model.local_database.entities.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val userDao: UserEntityDao,
    private val locationDao: LocationEntityDao,
    private val userWithLocationsDao: UserWithLocationsDao
) : ILocalDataSource {
    override fun saveUserLocation(userEntity: UserEntity, locationEntity: LocationEntity): Completable {
        return this.userDao
            .findByEmail(userEntity.email)
            .subscribeOn(Schedulers.io())
            .onErrorReturn { throwable: Throwable ->
                when (throwable) {
                    is EmptyResultSetException -> userEntity
                    else -> throw throwable
                }
            }
            .flatMap { addOrUpdateUser(it).andThen(this.userDao.findByEmail(userEntity.email)) }
            .map { it.id }
            .flatMapCompletable { addUserLocation(it, locationEntity) }
    }

    override fun fetchUserWithLocationsByEmail(email: String): Single<UserWithLocations> {
        val emptyUserWithLocations = UserWithLocations(
            UserEntity(email, 0),
            listOf()
        )

        return this.userWithLocationsDao
            .getByEmail(email)
            .subscribeOn(Schedulers.io())
            .switchIfEmpty(Single.just(emptyUserWithLocations))
    }

    override fun deleteAllUsers(): Completable = this.userDao.deleteAll().subscribeOn(Schedulers.io())

    override fun deleteAllLocations(): Completable = this.locationDao.deleteAll().subscribeOn(Schedulers.io())

    private fun addOrUpdateUser(userEntity: UserEntity) = when (userEntity.id) {
        0 -> this.userDao.add(userEntity)
        else -> this.userDao.update(userEntity)
    }

    private fun addUserLocation(dbUserId: Int, location: LocationEntity): Completable {
        location.userId = dbUserId
        return this.locationDao.add(location)
    }
}