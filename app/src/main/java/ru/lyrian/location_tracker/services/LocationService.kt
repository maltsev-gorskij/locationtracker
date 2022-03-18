package ru.lyrian.location_tracker.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import dagger.android.DaggerService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.cloud_database.ICloudDataSource
import ru.lyrian.location_tracker.model.local_database.ILocalDataSource
import ru.lyrian.location_tracker.model.local_database.entities.LocationEntity
import ru.lyrian.location_tracker.model.local_database.entities.UserEntity
import ru.lyrian.location_tracker.model.location.ILocationDataProvider
import ru.lyrian.location_tracker.model.pojo.LocationEntry
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedIn
import ru.lyrian.location_tracker.model.pojo.User
import ru.lyrian.location_tracker.model.pojo.UserLocations
import ru.lyrian.location_tracker.utility.Utility
import ru.lyrian.location_tracker.utility.network.NetworkState
import ru.lyrian.location_tracker.work_manager.worker.SaveToCloudWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationService : DaggerService() {
    private val notificationId = 1
    private val notificationChannelId = "LS"
    private val notificationChannelName = "Tracking current location"
    private val notificationDescription = "Application tracks location in background"
    private val workRepeatInterval = 15L
    private val workTag = "WorkManagerTag"
    private val uniqueWorkTag = "UniqueWorkTag"

    @Inject
    lateinit var iLocationDataProvider: ILocationDataProvider

    @Inject
    lateinit var iCloudDataSource: ICloudDataSource

    @Inject
    lateinit var iAuthProvider: IAuthProvider

    @Inject
    lateinit var networkState: NetworkState

    @Inject
    lateinit var iLocalDataSource: ILocalDataSource

    private val compositeDisposable = CompositeDisposable()

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        startForegroundService()
        scheduleSaveToCloudWork()
    }

    @SuppressLint("CheckResult")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        this.compositeDisposable.clear()
    }

    private fun startForegroundService() = startForeground(this.notificationId, createNotification())

    private fun createNotification(): Notification = NotificationCompat
        .Builder(this, createChannelId())
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_my_location)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setCategory(Notification.CATEGORY_SERVICE)
        .setContentText(this.notificationDescription)
        .build()

    private fun createChannelId() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    this.notificationChannelId,
                    this.notificationChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

        this.notificationChannelId
    } else {
        ""
    }

    private fun scheduleSaveToCloudWork() {
        val workConstraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<SaveToCloudWorker>(this.workRepeatInterval, TimeUnit.MINUTES)
            .setConstraints(workConstraints)
            .addTag(this.workTag)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(this.uniqueWorkTag, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    private fun startLocationUpdates() {
        this.iLocationDataProvider
            .provideLocationUpdates()
            .subscribe { locationEntry: LocationEntry ->
                saveLocation(locationEntry)
            }
            .addTo(this.compositeDisposable)
    }

    private fun saveLocation(locationEntry: LocationEntry) {
        this.iAuthProvider
            .getSignedInUser()
            .filter { it is SignedIn }
            .cast(SignedIn::class.java)
            .flatMapCompletable { selectDatabase(it, locationEntry) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { throwable ->
                Log.w(
                    Utility.LOG_TAG,
                    getString(R.string.ls_database_exception),
                    throwable
                )
            })
            .addTo(this.compositeDisposable)
    }

    private fun selectDatabase(signedIn: SignedIn, locationEntry: LocationEntry): Completable {
        val email = signedIn.email
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        return when (this.networkState.checkNetworkState()) {
            true -> saveToCloudDb(email, timestamp, locationEntry)
            false -> saveToLocalDb(email, timestamp, locationEntry)
        }
    }

    private fun saveToCloudDb(email: String?, timestamp: Long, locationEntry: LocationEntry): Completable {
        val userLocations = UserLocations(
            User(email, timestamp),
            mapOf(timestamp.toString() to locationEntry)
        )

        return this.iCloudDataSource.save(userLocations)
    }

    private fun saveToLocalDb(email: String?, timestamp: Long, locationEntry: LocationEntry): Completable {
        val (latitude: Double?, longitude: Double?) = locationEntry
        val userEntity = UserEntity(email!!, timestamp)
        val locationEntity = LocationEntity(timestamp, latitude!!, longitude!!)

        return this.iLocalDataSource.saveUserLocation(userEntity, locationEntity)
    }
}