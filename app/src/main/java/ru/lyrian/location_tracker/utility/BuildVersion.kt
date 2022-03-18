package ru.lyrian.location_tracker.utility

import android.os.Build
import javax.inject.Inject

open class BuildVersion @Inject constructor() {
    fun isBelowAndroid10(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
}