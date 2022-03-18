package ru.lyrian.location_tracker.model.local_database

import io.reactivex.Completable
import io.reactivex.Single
import ru.lyrian.location_tracker.model.local_database.entities.LocationEntity
import ru.lyrian.location_tracker.model.local_database.entities.UserEntity
import ru.lyrian.location_tracker.model.local_database.entities.UserWithLocations

interface ILocalDataSource {
    fun saveUserLocation(userEntity: UserEntity, locationEntity: LocationEntity): Completable

    fun fetchUserWithLocationsByEmail(email: String): Single<UserWithLocations>

    fun deleteAllUsers(): Completable

    fun deleteAllLocations(): Completable
}