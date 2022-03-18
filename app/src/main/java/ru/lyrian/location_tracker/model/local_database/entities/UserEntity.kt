package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UserEntity.TABLE_NAME)
data class UserEntity(
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "last_timestamp") val lastTimestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    companion object {
        const val TABLE_NAME = "users"
    }
}
