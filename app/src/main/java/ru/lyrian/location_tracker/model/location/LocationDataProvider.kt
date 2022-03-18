package ru.lyrian.location_tracker.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import javax.inject.Inject

class LocationDataProvider @Inject constructor(private val context: Context) : ILocationDataProvider {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(this.context)
    private val locationEntrySubject: PublishSubject<LocationEntry> = PublishSubject.create()

    override fun provideLocationUpdates(): Observable<LocationEntry> {
        subscribeOnLocationUpdates()

        return this.locationEntrySubject
    }

    private fun subscribeOnLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
        locationRequest.smallestDisplacement = 10.0F

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val currentLocation = LocationEntry(location.latitude, location.longitude)
                    locationEntrySubject.onNext(currentLocation)
                }
            }
        }

        val permissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (permissionGranted) this.fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}