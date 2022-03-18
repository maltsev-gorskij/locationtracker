package ru.lyrian.location_tracker.utility.permissions

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable

interface IPermissionsProvider {
    interface IFragmentInteractor {
        fun registerActivityResultLaunchers(
            activityResultRegistry: ActivityResultRegistry,
            lifecycleOwner: LifecycleOwner
        )
    }

    interface IViewModelInteractor {
        fun subscribeOnPermissionResults(): Observable<PermissionResult>

        fun checkFineLocationPermission()

        fun checkBackgroundLocationPermission()

        fun checkGpsAvailability()

        fun requestFineLocationPermission()

        fun requestBackgroundLocationPermission()

        fun requestEnablingGps()
    }
}