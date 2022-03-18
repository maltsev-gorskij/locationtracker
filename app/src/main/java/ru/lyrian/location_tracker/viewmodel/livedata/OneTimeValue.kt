package ru.lyrian.location_tracker.viewmodel.livedata

/**
 * Container class for one-time values.
 *
 * Used when livedata value is necessary to be proceeded on UI only once since last change.
 */

class OneTimeValue<T>(private val value: T) {
    private var valueAlreadyRequested = false

    fun getValueIfNotRequested(): T? =
        when (valueAlreadyRequested) {
            true -> null
            false -> {
                valueAlreadyRequested = true
                value
            }
        }
}
