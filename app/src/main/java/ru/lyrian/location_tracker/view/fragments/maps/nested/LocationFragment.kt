package ru.lyrian.location_tracker.view.fragments.maps.nested

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.FragmentLocationBinding
import ru.lyrian.location_tracker.utility.permissions.IPermissionsProvider
import ru.lyrian.location_tracker.view.fragments.base.BaseMapFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.fragments.LocationViewModel
import ru.lyrian.location_tracker.viewmodel.fragments.MapsViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * Fragment for triggering rights requests and showing current user location
 */

class LocationFragment : BaseMapFragment<FragmentLocationBinding>() {
    private var finePermissionAlreadyRequested = false
    private var backgroundPermissionAlreadyRequested = false
    private var userLocationLayer: UserLocationLayer? = null
    private val locationViewModel: LocationViewModel by createFragmentViewModel()
    private val mapsViewModel: MapsViewModel by createSharedViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

    @Inject
    lateinit var iPermissionsProvider: IPermissionsProvider.IFragmentInteractor

    @Inject
    override lateinit var mapKit: MapKit

    override val mapView: MapView?
        get() = this.binding?.mvYandex
    override val fabZoomIn: ExtendedFloatingActionButton?
        get() = this.binding?.fabZoomIn
    override val fabZoomOut: ExtendedFloatingActionButton?
        get() = this.binding?.fabZoomOut
    override val fabSignOut: ExtendedFloatingActionButton?
        get() = this.binding?.fabSignOut

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentLocationBinding.inflate(inflater, container, false)
        registerOnBackPressedDispatcher()
        this.iPermissionsProvider.registerActivityResultLaunchers(
            requireActivity().activityResultRegistry,
            viewLifecycleOwner
        )

        return (this.binding as FragmentLocationBinding).root
    }

    override fun onStart() {
        super.onStart()

        this.binding?.tvSignedInUser?.bringToFront()
        subscribeOnLiveData()
        setOnClickListeners()
        this.locationViewModel.checkSignedInStatus()
        this.locationViewModel.subscribeOnPermissionResults()
        this.locationViewModel.checkFineLocationPermission()
    }

    override fun onStop() {
        super.onStop()

        stopMapKit()
    }

    private fun createUserLocationLayer() {
        val mapWindow: MapWindow = this.binding?.mvYandex?.mapWindow!!

        if (this.userLocationLayer == null) {
            this.userLocationLayer = this.mapKit.createUserLocationLayer(mapWindow)
            this.userLocationLayer?.isVisible = true
        }
    }

    private fun startLocationUpdates() = this.locationViewModel.startLocationUpdates()

    private fun subscribeOnLiveData() {
        this.locationViewModel.signedInStatusLD.observe(viewLifecycleOwner) {
            this.binding?.tvSignedInUser?.text = it
        }

        this.locationViewModel.fineLocationPermissionsLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                when (it) {
                    true -> {
                        startMapKit()
                        createUserLocationLayer()
                        startLocationUpdates()
                        this.locationViewModel.defineBackgroundPermissionNecessity()
                    }
                    false -> checkFineLocationPermissionRationaleNecessity()
                }
            }
        }

        this.locationViewModel.backgroundLocationPermissionNecessityLD.observe(
            viewLifecycleOwner
        ) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                when (it) {
                    true -> this.locationViewModel.checkBackgroundLocationPermission()
                    false -> this.locationViewModel.checkGpsAvailability()
                }
            }
        }

        this.locationViewModel.backgroundLocationPermissionRationaleLD.observe(
            viewLifecycleOwner
        ) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                if (it) this.showBackgroundPermissionRationale()
            }
        }

        this.locationViewModel.backgroundLocationPermissionLD.observe(
            viewLifecycleOwner
        ) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                when (it) {
                    true -> this.locationViewModel.checkGpsAvailability()
                    false -> checkBackgroundLocationPermissionRationaleNecessity()
                }
            }
        }

        this.locationViewModel.gpsAvailabilityLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<Boolean> ->
            oneTimeValue.getValueIfNotRequested()?.let {
                if (!it) this.locationViewModel.requestEnablingGps()
            }
        }

        this.locationViewModel.currentLocationLD.observe(viewLifecycleOwner) {
            moveToLocation(Point(it.latitude!!, it.longitude!!))
        }
    }

    private fun setOnClickListeners() {
        this.binding?.fabCurrentLocation?.setOnClickListener {
            this.userLocationLayer?.cameraPosition()?.target?.let { moveToLocation(it) }
        }
    }

    private fun checkFineLocationPermissionRationaleNecessity() {
        when (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) &&
                this.finePermissionAlreadyRequested) {
            true -> showFineLocationPermissionRationale()
            false -> this.locationViewModel.requestFineLocationPermission()
        }

        this.finePermissionAlreadyRequested = true
    }

    private fun checkBackgroundLocationPermissionRationaleNecessity() {
        when (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                this.backgroundPermissionAlreadyRequested) {
            true -> showBackgroundPermissionRationale()
            false -> {
                this.locationViewModel.requestBackgroundLocationPermission()
            }
        }

        this.backgroundPermissionAlreadyRequested = true
    }

    private fun showFineLocationPermissionRationale() = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.permission_rationale_title))
        .setPositiveButton(getString(R.string.positive_button)) { dialog, _ ->
            dialog.dismiss()
            openAppInfoActivity()
        }
        .setNegativeButton(getString(R.string.negative_button)) { dialog, _ -> dialog?.dismiss() }
        .setMessage(getString(R.string.fine_location_rationale_message))
        .show()

    private fun showBackgroundPermissionRationale() = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.permission_rationale_title))
        .setPositiveButton(getString(R.string.positive_button)) { dialog, _ ->
            dialog.dismiss()
            openAppInfoActivity()
        }
        .setNegativeButton(getString(R.string.negative_button)) { dialog, _ -> dialog?.dismiss() }
        .setMessage(getString(R.string.background_location_rationale_message))
        .show()

    private fun openAppInfoActivity() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", requireContext().packageName, null)
        })
    }

    override fun signOut() {
        this.locationViewModel.signOut()
        this.mapsViewModel.navigateToLoginScreen()
    }
}