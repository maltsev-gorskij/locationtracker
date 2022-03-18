package ru.lyrian.location_tracker.model.location

import io.reactivex.Observable
import ru.lyrian.location_tracker.model.pojo.LocationEntry

interface ILocationDataProvider {
    fun provideLocationUpdates(): Observable<LocationEntry>
}