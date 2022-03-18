package ru.lyrian.location_tracker.di.modules.local_database

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.lyrian.location_tracker.di.annotations.scope.IAppComponentScope
import ru.lyrian.location_tracker.model.local_database.ILocalDataSource
import ru.lyrian.location_tracker.model.local_database.LocalDataSource
import ru.lyrian.location_tracker.model.local_database.LocalDatabase
import ru.lyrian.location_tracker.model.local_database.entities.LocationEntityDao
import ru.lyrian.location_tracker.model.local_database.entities.UserEntityDao
import ru.lyrian.location_tracker.model.local_database.entities.UserWithLocationsDao

@Module
interface ILocalDataSourceModule {
    companion object {
        private const val localDatabaseName = "LocalDatabase"

        @IAppComponentScope
        @Provides
        fun provideLocalDatabase(context: Context): LocalDatabase = Room
            .databaseBuilder(
                context,
                LocalDatabase::class.java,
                localDatabaseName
            )
            .fallbackToDestructiveMigration()
            .build()

        @IAppComponentScope
        @Provides
        fun provideUserEntityDao(localDatabase: LocalDatabase): UserEntityDao = localDatabase.provideUserEntityDao()

        @IAppComponentScope
        @Provides
        fun provideLocationEntityDao(localDatabase: LocalDatabase): LocationEntityDao =
            localDatabase.provideLocationEntityDao()

        @IAppComponentScope
        @Provides
        fun provideUserWithLocationsDao(localDatabase: LocalDatabase): UserWithLocationsDao =
            localDatabase.provideUserWithLocationsDao()
    }

    @Binds
    fun bindLocalDataSource(localDatabaseRepository: LocalDataSource): ILocalDataSource
}