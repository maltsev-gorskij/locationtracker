package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
interface UserWithLocationsDao {
    @Transaction
    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE email LIKE :email")
    fun getByEmail(email: String): Maybe<UserWithLocations>
}