package ru.lyrian.location_tracker.di.application

import androidx.work.Configuration
import com.yandex.mapkit.MapKitFactory
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.lyrian.location_tracker.BuildConfig
import ru.lyrian.location_tracker.di.component.DaggerIAppComponent
import ru.lyrian.location_tracker.work_manager.factory.WorkersFactory
import javax.inject.Inject

/**
 * Substitution of system Application class for retaining Dagger component until app destruction
 */

class App : DaggerApplication(), Configuration.Provider {
    @Inject
    lateinit var workersFactory: WorkersFactory

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.yandexMapApiKey)
        MapKitFactory.initialize(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerIAppComponent
        .factory()
        .create(this, BuildConfig.firebaseRealtimeDatabaseUrl)

    override fun getWorkManagerConfiguration(): Configuration = Configuration
        .Builder()
        .setWorkerFactory(this.workersFactory)
        .build()
}