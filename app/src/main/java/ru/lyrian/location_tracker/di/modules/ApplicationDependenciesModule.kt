package ru.lyrian.location_tracker.di.modules

import dagger.Module
import ru.lyrian.location_tracker.di.modules.authentication.IAuthProviderModule
import ru.lyrian.location_tracker.di.modules.cloud_database.ICloudDataSourceModule
import ru.lyrian.location_tracker.di.modules.local_database.ILocalDataSourceModule
import ru.lyrian.location_tracker.di.modules.location.ILocationDataProviderModule
import ru.lyrian.location_tracker.di.modules.location.IPermissionsProviderModule
import ru.lyrian.location_tracker.di.modules.maps.YandexMapKitModule

@Module(
    includes = [
        ICloudDataSourceModule::class,
        ILocalDataSourceModule::class,
        IAuthProviderModule::class,
        YandexMapKitModule::class,
        ILocationDataProviderModule::class,
        IPermissionsProviderModule::class
    ]
)
interface ApplicationDependenciesModule