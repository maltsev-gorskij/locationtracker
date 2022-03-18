package ru.lyrian.location_tracker.di.modules.location

import dagger.Binds
import dagger.Module
import ru.lyrian.location_tracker.model.location.ILocationDataProvider
import ru.lyrian.location_tracker.model.location.LocationDataProvider

@Module
interface ILocationDataProviderModule {
    @Binds
    fun bindILocationDataProvider(locationRepository: LocationDataProvider): ILocationDataProvider
}