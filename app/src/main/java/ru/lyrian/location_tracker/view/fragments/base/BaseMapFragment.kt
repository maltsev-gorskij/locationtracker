package ru.lyrian.location_tracker.view.fragments.base

import androidx.activity.addCallback
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.lyrian.location_tracker.R

/**
 * Base class for Maps Fragments
 */

abstract class BaseMapFragment<T : ViewBinding> : BaseFragment<T>() {
    protected abstract val mapView: MapView?
    protected abstract val fabZoomIn: ExtendedFloatingActionButton?
    protected abstract val fabZoomOut: ExtendedFloatingActionButton?
    protected abstract val fabSignOut: ExtendedFloatingActionButton?
    protected abstract val mapKit: MapKit
    private val defaultAzimuth = 0.0F
    private val defaultTilt = 0.0F
    private val defaultZoom = 15.0F
    private val zoomingStep = 1
    private val zoomAnimation = Animation(Animation.Type.SMOOTH, 0.1F)
    private val cameraPositioningAnimation = Animation(Animation.Type.SMOOTH, 1F)


    override fun onStart() {
        super.onStart()

        setZoomOnClickListeners()
        setSignOutOnClickListener()
    }

    protected fun startMapKit() {
        this.mapView?.onStart()
        this.mapKit.onStart()
    }

    protected fun stopMapKit() {
        this.mapView?.onStop()
        this.mapKit.onStop()
    }

    private fun setZoomOnClickListeners() {
        this.fabZoomIn?.setOnClickListener {
            this.mapView?.map?.apply {
                val target = cameraPosition.target
                val zoomedIn = cameraPosition.zoom.plus(zoomingStep)
                val cameraPosition = CameraPosition(target, zoomedIn, defaultAzimuth, defaultTilt)
                move(cameraPosition, zoomAnimation, null)
            }
        }

        this.fabZoomOut?.setOnClickListener {
            this.mapView?.map?.apply {
                val target = cameraPosition.target
                val zoomedOut = cameraPosition.zoom.minus(zoomingStep)
                val cameraPosition = CameraPosition(target, zoomedOut, defaultAzimuth, defaultTilt)
                move(cameraPosition, zoomAnimation, null)
            }
        }
    }

    private fun setSignOutOnClickListener() = this.fabSignOut?.setOnClickListener { signOut() }

    protected fun registerOnBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.back_button_alert_title))
                .setPositiveButton(getString(R.string.fab_back)) { dialog, _ ->
                    dialog.dismiss()
                    signOut()
                }
                .setNegativeButton(getString(R.string.negative_button)) { dialog, _ -> dialog?.dismiss() }
                .setMessage(getString(R.string.back_button_alert_message))
                .show()
        }
    }

    protected fun moveToLocation(point: Point) = this.mapView?.map?.apply {
        val cameraPosition = CameraPosition(point, defaultZoom, defaultAzimuth, defaultTilt)
        this.move(cameraPosition, cameraPositioningAnimation, null)
    }

    protected abstract fun signOut()
}