package ru.lyrian.location_tracker.di.modules

import dagger.Module
import dagger.android.AndroidInjectionModule
import ru.lyrian.location_tracker.di.modules.android_components.*

@Module(
    includes = [
        AndroidInjectionModule::class,
        ActivityBuildersModule::class,
        FragmentBuildersModule::class,
        ServiceBuildersModule::class,
        ViewModelsModule::class,
        WorkManagerModule::class
    ]
)
interface AndroidComponentsModule