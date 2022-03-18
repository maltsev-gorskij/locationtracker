package ru.lyrian.location_tracker.di.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import ru.lyrian.location_tracker.di.annotations.scope.IAppComponentScope
import ru.lyrian.location_tracker.di.application.App
import ru.lyrian.location_tracker.di.modules.AndroidComponentsModule
import ru.lyrian.location_tracker.di.modules.ApplicationDependenciesModule
import javax.inject.Named

/**
 * Interface, which combines all object providers for building Dagger dependency graph
 */

@IAppComponentScope
@Component(
    modules = [
        AndroidComponentsModule::class,
        ApplicationDependenciesModule::class
    ]
)
interface IAppComponent : AndroidInjector<App> {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @[BindsInstance Named("firebaseRealtimeDatabaseUrl")] firebaseRealtimeDatabaseUrl: String
        ): IAppComponent
    }
}