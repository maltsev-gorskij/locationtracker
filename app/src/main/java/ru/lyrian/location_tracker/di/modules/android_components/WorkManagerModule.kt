package ru.lyrian.location_tracker.di.modules.android_components

import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.lyrian.location_tracker.di.annotations.qualifiers.WorkersSet
import ru.lyrian.location_tracker.work_manager.factory.SaveToCloudWorkerFactory

@Module
interface WorkManagerModule {
    @Binds
    @[IntoSet WorkersSet]
    fun bindSaveToCloudWorkerFactory(saveToCloudWorkerFactory: SaveToCloudWorkerFactory):
            WorkerFactory
}