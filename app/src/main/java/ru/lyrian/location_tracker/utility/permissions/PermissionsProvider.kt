package ru.lyrian.location_tracker.utility.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.lyrian.location_tracker.di.annotations.scope.IAppComponentScope
import ru.lyrian.location_tracker.utility.permissions.PermissionResult.*
import javax.inject.Inject

@IAppComponentScope
class PermissionsProvider @Inject constructor(private val context: Context) :
    IPermissionsProvider.IFragmentInteractor,
    IPermissionsProvider.IViewModelInteractor {
    private val permissionRegistryKey = "LocationRegistryKey"
    private val gpsRegistryKey = "GPSRegistryKey"
    private val permissionsResults: PublishSubject<PermissionResult> = PublishSubject.create()
    private val permissionsContract = ActivityResultContracts.RequestMultiplePermissions()
    private val intentSenderContract = ActivityResultContracts.StartIntentSenderForResult()
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var gpsAvailabilityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val activityResultCallback = { it: MutableMap<String, Boolean> ->
        it.entries.forEach { mapEntry: MutableMap.MutableEntry<String, Boolean> ->
            when {
                mapEntry.key == "android.permission.ACCESS_FINE_LOCATION" ->
                    this.permissionsResults.onNext(FinePermissionResult(mapEntry.value))
                mapEntry.key == "android.permission.ACCESS_BACKGROUND_LOCATION" && mapEntry.value ->
                    this.permissionsResults.onNext(BackgroundPermissionResult(mapEntry.value))
            }
        }
    }

    override fun registerActivityResultLaunchers(
        activityResultRegistry: ActivityResultRegistry,
        lifecycleOwner: LifecycleOwner
    ) {
        this.lifecycleOwner = lifecycleOwner
        this.permissionResultLauncher = activityResultRegistry.register(
            this.permissionRegistryKey,
            lifecycleOwner,
            this.permissionsContract,
            this.activityResultCallback
        )

        this.gpsAvailabilityResultLauncher = activityResultRegistry.register(
            this.gpsRegistryKey,
            lifecycleOwner,
            this.intentSenderContract
        ) {}
    }

    override fun subscribeOnPermissionResults(): Observable<PermissionResult> =
        this.permissionsResults

    override fun checkFineLocationPermission() = this.permissionsResults
        .onNext(
            FinePermissionResult(
                ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            )
        )

    override fun checkBackgroundLocationPermission() = this.permissionsResults
        .onNext(
            BackgroundPermissionResult(
                ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            )
        )

    override fun checkGpsAvailability() = this.permissionsResults
        .onNext(
            GpsAvailabilityResult(
                (this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
            )
        )

    override fun requestFineLocationPermission() {
        if (isResultLaunchersRegistered())
            this.permissionResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    override fun requestBackgroundLocationPermission() {
        if (isResultLaunchersRegistered())
            this.permissionResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
    }

    override fun requestEnablingGps() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val settingRequest = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(this.context)
        val task = settingsClient.checkLocationSettings(settingRequest)

        task.addOnFailureListener {
            val intentSender = (it as ResolvableApiException).resolution.intentSender
            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()

            this.gpsAvailabilityResultLauncher.launch(intentSenderRequest)
        }
    }

    private fun isResultLaunchersRegistered(): Boolean =
        when (this.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            true -> true
            false -> false
        }
}