package ru.lyrian.location_tracker.di.modules.android_components

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lyrian.location_tracker.services.LocationService

@Module
interface ServiceBuildersModule {
    @ContributesAndroidInjector
    fun contributeLocationService(): LocationService
}