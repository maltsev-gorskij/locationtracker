package ru.lyrian.location_tracker.utility.permissions

sealed class PermissionResult(val isGranted: Boolean) {
    class FinePermissionResult(result: Boolean) : PermissionResult(result)
    class BackgroundPermissionResult(result: Boolean) : PermissionResult(result)
    class GpsAvailabilityResult(result: Boolean) : PermissionResult(result)
}
