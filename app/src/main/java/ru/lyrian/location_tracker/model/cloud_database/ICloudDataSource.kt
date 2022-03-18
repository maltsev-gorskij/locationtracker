package ru.lyrian.location_tracker.model.cloud_database

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.model.pojo.UserLocations

interface ICloudDataSource {
    fun save(userLocations: UserLocations): Completable

    fun getLocationsByDayRange(email: String, timestampRange: Pair<Long, Long>): Maybe<Map<String, LocationEntry>>

    fun listenUserUpdates(email: String): Observable<Long>
}