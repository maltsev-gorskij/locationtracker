package ru.lyrian.location_tracker.di.modules.cloud_database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.lyrian.location_tracker.model.cloud_database.CloudDataSource
import ru.lyrian.location_tracker.model.cloud_database.ICloudDataSource
import javax.inject.Named

@Module
interface ICloudDataSourceModule {
    companion object {
        @Provides
        fun provideFirebaseRealtimeDatabase(@Named("firebaseRealtimeDatabaseUrl") firebaseRealtimeDatabaseUrl: String) =
            Firebase.database(firebaseRealtimeDatabaseUrl)
    }

    @Binds
    fun bindICloudDataSource(cloudDatabaseRepository: CloudDataSource): ICloudDataSource
}