package ru.lyrian.location_tracker.viewmodel.base

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.pojo.SignInResult
import ru.lyrian.location_tracker.services.LocationService

/**
 * Base ViewModel class for ViewModels of fragments with Yandex MapKit
 */

abstract class BaseMapViewModel : BaseViewModel() {
    protected abstract val iAuthProvider: IAuthProvider
    protected abstract val signedInStatusLD: MutableLiveData<String>
    protected abstract val context: Context

    fun checkSignedInStatus() {
        this.iAuthProvider
            .getSignedInUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it: SignInResult ->
                if (it is SignInResult.SignedIn) this.signedInStatusLD.value =
                    "${this.context.getString(R.string.signed_in)} \n${it.email}"
            }
            .addTo(this.compositeDisposable)
    }

    fun signOut() {
        this.iAuthProvider
            .signOut()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(this.compositeDisposable)

        stopService()
    }

    private fun stopService() {
        val serviceStopperIntent = Intent(this.context, LocationService::class.java)
        this.context.stopService(serviceStopperIntent)
    }
}