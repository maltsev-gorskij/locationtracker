package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithLocations(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val locations: List<LocationEntity>
)
