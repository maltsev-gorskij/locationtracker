package ru.lyrian.location_tracker.viewmodel.fragments

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.location.ILocationDataProvider
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.services.LocationService
import ru.lyrian.location_tracker.utility.permissions.IPermissionsProvider
import ru.lyrian.location_tracker.utility.permissions.PermissionResult.*
import ru.lyrian.location_tracker.viewmodel.base.BaseMapViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * ViewModel for MapFragment.
 */

@SuppressLint("StaticFieldLeak")
class LocationViewModel @Inject constructor(
    override val iAuthProvider: IAuthProvider,
    private val iLocationDataProvider: ILocationDataProvider,
    private val iPermissionsProvider: IPermissionsProvider.IViewModelInteractor,
    override val context: Context
) : BaseMapViewModel() {
    public override val signedInStatusLD = MutableLiveData<String>()
    val fineLocationPermissionsLD = MutableLiveData<OneTimeValue<Boolean>>()
    val backgroundLocationPermissionLD = MutableLiveData<OneTimeValue<Boolean>>()
    val backgroundLocationPermissionNecessityLD = MutableLiveData<OneTimeValue<Boolean>>()
    val backgroundLocationPermissionRationaleLD = MutableLiveData<OneTimeValue<Boolean>>()
    val gpsAvailabilityLD = MutableLiveData<OneTimeValue<Boolean>>()
    val currentLocationLD = MutableLiveData<LocationEntry>()

    fun subscribeOnPermissionResults() {
        this.iPermissionsProvider.subscribeOnPermissionResults()
            .subscribeBy(
                onNext = {
                    when (it) {
                        is FinePermissionResult -> fineLocationPermissionsLD.value = OneTimeValue(it.isGranted)
                        is BackgroundPermissionResult -> backgroundLocationPermissionLD.value =
                            OneTimeValue(it.isGranted)
                        is GpsAvailabilityResult -> gpsAvailabilityLD.value = OneTimeValue(it.isGranted)
                    }
                }
            )
            .addTo(this.compositeDisposable)
    }

    fun checkFineLocationPermission() = this.iPermissionsProvider.checkFineLocationPermission()

    fun checkBackgroundLocationPermission() = this.iPermissionsProvider.checkBackgroundLocationPermission()

    fun checkGpsAvailability() = this.iPermissionsProvider.checkGpsAvailability()

    fun requestFineLocationPermission() = this.iPermissionsProvider.requestFineLocationPermission()

    fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            this.iPermissionsProvider.requestBackgroundLocationPermission()
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            this.backgroundLocationPermissionRationaleLD.value = OneTimeValue(true)
        }
    }

    fun requestEnablingGps() = this.iPermissionsProvider.requestEnablingGps()

    fun defineBackgroundPermissionNecessity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            this.backgroundLocationPermissionNecessityLD.value = OneTimeValue(false)
        } else {
            this.backgroundLocationPermissionNecessityLD.value = OneTimeValue(true)
        }
    }

    fun startLocationUpdates() {
        if (!serviceIsRunning(LocationService::class.java)) startService()

        this.iLocationDataProvider
            .provideLocationUpdates()
            .subscribe { currentLocation ->
                currentLocationLD.value = currentLocation
            }
            .addTo(this.compositeDisposable)
    }

    @Suppress("DEPRECATION")
    private fun <T : Service> serviceIsRunning(serviceClass: Class<T>) =
        (this.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }

    private fun startService() {
        val serviceStarterIntent = Intent(this.context, LocationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.context.startForegroundService(serviceStarterIntent)
        } else {
            this.context.startService(serviceStarterIntent)
        }
    }
}