package ru.lyrian.location_tracker.work_manager.factory

import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkerFactory
import ru.lyrian.location_tracker.di.annotations.qualifiers.WorkersSet
import ru.lyrian.location_tracker.di.annotations.scope.IAppComponentScope
import javax.inject.Inject

@IAppComponentScope
class WorkersFactory @Inject constructor(@WorkersSet private val workerFactories: Set<@JvmSuppressWildcards WorkerFactory>) :
    DelegatingWorkerFactory() {
    init {
        this.workerFactories.forEach { addFactory(it) }
    }
}