package ru.lyrian.location_tracker.model.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.lyrian.location_tracker.model.local_database.entities.*

@Database(entities = [UserEntity::class, LocationEntity::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun provideUserEntityDao(): UserEntityDao

    abstract fun provideLocationEntityDao(): LocationEntityDao

    abstract fun provideUserWithLocationsDao(): UserWithLocationsDao
}