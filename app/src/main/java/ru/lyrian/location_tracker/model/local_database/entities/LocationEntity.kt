package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = LocationEntity.TABLE_NAME)
data class LocationEntity(
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "user_id")
    var userId: Int = 0

    companion object {
        const val TABLE_NAME = "locations"
    }
}
