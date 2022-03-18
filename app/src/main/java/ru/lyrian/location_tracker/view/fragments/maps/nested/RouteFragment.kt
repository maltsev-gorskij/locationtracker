package ru.lyrian.location_tracker.view.fragments.maps.nested

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.FragmentRouteBinding
import ru.lyrian.location_tracker.view.fragments.base.BaseMapFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.fragments.MapsViewModel
import ru.lyrian.location_tracker.viewmodel.fragments.RouteViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * Fragment for drawing location history by selected date as route on map
 */

class RouteFragment : BaseMapFragment<FragmentRouteBinding>() {
    private val routeViewModel: RouteViewModel by createFragmentViewModel()
    private val mapsViewModel: MapsViewModel by createSharedViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

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

    private lateinit var rootView: ConstraintLayout
    private lateinit var mapObjects: MapObjectCollection

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentRouteBinding.inflate(inflater, container, false)
        registerOnBackPressedDispatcher()
        this.rootView = (this.binding as FragmentRouteBinding).root

        return this.rootView
    }

    override fun onStart() {
        super.onStart()
        startMapKit()
        prepareMapObjectsCollection()
        this.binding?.tvSignedInUser?.bringToFront()
        subscribeOnLiveData()
        setOnClickListeners()
        this.routeViewModel.checkSignedInStatus()
    }

    override fun onStop() {
        super.onStop()

        stopMapKit()
        this.routeViewModel.disposeUserTimestampListener()
    }

    private fun prepareMapObjectsCollection() =
        this.binding?.mvYandex?.map?.mapObjects?.addCollection()?.let { this.mapObjects = it }

    private fun subscribeOnLiveData() {
        this.routeViewModel.signedInStatusLD.observe(viewLifecycleOwner) { this.binding?.tvSignedInUser?.text = it }

        this.routeViewModel.tvSelectedDateLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<String> ->
            oneTimeValue.getValueIfNotRequested()?.let { this.binding?.tvSelectedDate?.text = it }
        }

        this.routeViewModel.snackBarLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<String> ->
            oneTimeValue.getValueIfNotRequested()?.let { displayInfoSnackBar(it) }
        }

        this.routeViewModel.drawLocationsRouteLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<List<Point>> ->
            oneTimeValue.getValueIfNotRequested()?.let { drawRoute(it) }
        }

        this.routeViewModel.moveToLocationLD.observe(viewLifecycleOwner) { oneTimeValue: OneTimeValue<Point> ->
            oneTimeValue.getValueIfNotRequested()?.let { moveToLocation(it) }
        }
    }

    private fun setOnClickListeners() {
        this.binding?.fabDatePicker?.setOnClickListener { showDatePicker() }
        this.binding?.fabRouteStart?.setOnClickListener { this.routeViewModel.moveCameraToRouteStart() }
    }

    private fun showDatePicker() {
        val materialDatePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText(getString(R.string.mdp_select_date))
            .build()

        materialDatePicker.show(requireActivity().supportFragmentManager, materialDatePicker.toString())
        materialDatePicker.addOnPositiveButtonClickListener { this.routeViewModel.getLocationsByEpochMillis(it) }
    }

    private fun drawRoute(locationRoute: List<Point>) {
        this.mapObjects.clear()
        val polyline: PolylineMapObject = this.mapObjects.addPolyline(Polyline(locationRoute))
        polyline.strokeColor = Color.BLUE
        polyline.strokeWidth = 3F
        drawStartLocationView(locationRoute[0])
    }

    private fun drawStartLocationView(startPoint: Point) {
        val textViewStart = TextView(context).apply {
            this.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.setTextColor(Color.BLACK)
            this.textSize = 10.0F
            this.text = getString(R.string.tv_start_point)
            this.background = ContextCompat.getDrawable(context, R.drawable.round_corners)
            this.setPadding(10)
        }

        this.mapObjects.addPlacemark(startPoint, ViewProvider(textViewStart))
    }

    private fun displayInfoSnackBar(message: String) {
        Snackbar
            .make(this.rootView, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(this.binding?.fabRouteStart)
            .show()
    }

    override fun signOut() {
        this.routeViewModel.signOut()
        this.mapsViewModel.navigateToLoginScreen()
    }
}