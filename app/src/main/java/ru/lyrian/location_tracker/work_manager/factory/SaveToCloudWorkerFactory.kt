package ru.lyrian.location_tracker.work_manager.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.cloud_database.ICloudDataSource
import ru.lyrian.location_tracker.model.local_database.ILocalDataSource
import ru.lyrian.location_tracker.work_manager.worker.SaveToCloudWorker
import javax.inject.Inject

class SaveToCloudWorkerFactory @Inject constructor(
    private val iAuthProvider: IAuthProvider,
    private val iLocalDataSource: ILocalDataSource,
    private val iCloudDataSource: ICloudDataSource
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SaveToCloudWorker::class.java.name -> SaveToCloudWorker(
                appContext,
                workerParameters,
                this.iAuthProvider,
                this.iLocalDataSource,
                this.iCloudDataSource
            )
            else -> null
        }
    }
}