package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable

@Dao
interface LocationEntityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(locationEntity: LocationEntity): Completable

    @Query("DELETE FROM ${LocationEntity.TABLE_NAME}")
    fun deleteAll(): Completable
}