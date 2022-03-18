package ru.lyrian.location_tracker.di.modules.maps

import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides

@Module
class YandexMapKitModule {
    @Provides
    fun provideMapKit(): MapKit = MapKitFactory.getInstance()
}