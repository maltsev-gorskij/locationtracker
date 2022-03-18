package ru.lyrian.location_tracker.model.pojo

data class UserLocations(val user: User, val locationAtTimestamp: Map<String, LocationEntry>)
