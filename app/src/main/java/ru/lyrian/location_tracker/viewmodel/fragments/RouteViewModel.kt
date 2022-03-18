package ru.lyrian.location_tracker.viewmodel.fragments

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.yandex.mapkit.geometry.Point
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.cloud_database.ICloudDataSource
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedIn
import ru.lyrian.location_tracker.utility.date.DateConverter
import ru.lyrian.location_tracker.utility.network.NetworkState
import ru.lyrian.location_tracker.viewmodel.base.BaseMapViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * ViewModel for RouteFragment.
 */

@SuppressLint("StaticFieldLeak")
class RouteViewModel @Inject constructor(
    override val iAuthProvider: IAuthProvider,
    private val iCloudDataSource: ICloudDataSource,
    private val networkState: NetworkState,
    private val dateConverter: DateConverter,
    override val context: Context
) : BaseMapViewModel() {
    public override val signedInStatusLD = MutableLiveData<String>()
    val tvSelectedDateLD = MutableLiveData<OneTimeValue<String>>()
    val drawLocationsRouteLD = MutableLiveData<OneTimeValue<List<Point>>>()
    val snackBarLD = MutableLiveData<OneTimeValue<String>>()
    val moveToLocationLD = MutableLiveData<OneTimeValue<Point>>()
    private var routeStart: Point? = null
    private val timestampListenerDisposable = CompositeDisposable()
    private var initialLocationRequest = true
    private var firstTimestampUpdate = true

    fun getLocationsByEpochMillis(epochMillis: Long) {
        this.initialLocationRequest = true
        getLocationsByDayRangeSeconds(this.dateConverter.epochMillisToDayRangeSeconds(epochMillis))

        if (this.dateConverter.epochMillisIsToday(epochMillis)) {
            listenUserTimestampUpdates()
        } else {
            disposeUserTimestampListener()
        }
    }

    fun moveCameraToRouteStart() {
        this.routeStart?.let { this.moveToLocationLD.value = OneTimeValue(it) }
    }

    private fun getLocationsByDayRangeSeconds(timeRange: Pair<Long, Long>) {
        getSignedInUserEmail()
            .flatMapMaybe { iCloudDataSource.getLocationsByDayRange(it, timeRange) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    displaySelectedDate(timeRange.first)
                    val routePoints = createRoutePoints(it)
                    this.routeStart = routePoints[0]
                    drawRoute(routePoints)
                },
                onComplete = {
                    this.routeStart = null
                    val snackBarText = "${this.context.getString(R.string.snackbar_no_locations)} " +
                            this.dateConverter.epochSecondsToFormattedString(timeRange.first)
                    this.snackBarLD.value = OneTimeValue(snackBarText)
                },
                onError = {
                    this.routeStart = null

                    when (this.networkState.checkNetworkState()) {
                        true -> this.snackBarLD.value =
                            OneTimeValue(this.context.getString(R.string.snackbar_cloud_database_error))
                        false -> this.snackBarLD.value =
                            OneTimeValue(this.context.getString(R.string.snackbar_no_internet))
                    }
                }
            )
            .addTo(this.compositeDisposable)
    }

    private fun getSignedInUserEmail(): Single<String> {
        return this.iAuthProvider
            .getSignedInUser()
            .cast(SignedIn::class.java)
            .map { signedIn: SignedIn -> signedIn.email }
    }

    private fun displaySelectedDate(timestamp: Long) {
        val dateAsString = this.dateConverter.epochSecondsToFormattedString(timestamp)
        this.tvSelectedDateLD.value = OneTimeValue(dateAsString)
    }

    private fun createRoutePoints(locations: Map<String, LocationEntry>): MutableList<Point> {
        val routePoints = mutableListOf<Point>()
        locations.forEach { routePoints.add(Point(it.value.latitude!!, it.value.longitude!!)) }

        return routePoints
    }

    private fun drawRoute(routePoints: MutableList<Point>?) {
        if (initialLocationRequest) {
            this.snackBarLD.value =
                OneTimeValue("${this.context.getString(R.string.snackbar_found_locations)} ${routePoints?.size}")
            moveCameraToRouteStart()
            this.initialLocationRequest = false
        }

        this.drawLocationsRouteLD.value = OneTimeValue(routePoints as List<Point>)
    }

    private fun listenUserTimestampUpdates() {
        getSignedInUserEmail()
            .flatMapObservable { this.iCloudDataSource.listenUserUpdates(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val dayRangeSeconds = this.dateConverter.epochSecondsToDayRangeSeconds(it)
                    getLocationsByDayRangeSeconds(dayRangeSeconds)

                    if (this.firstTimestampUpdate) {
                        this.firstTimestampUpdate = false
                    } else {
                        this.snackBarLD.value =
                            OneTimeValue(this.context.getString(R.string.snackbar_route_updated))
                    }
                }
            )
            .addTo(this.timestampListenerDisposable)
    }

    fun disposeUserTimestampListener() {
        this.timestampListenerDisposable.clear()
    }
}