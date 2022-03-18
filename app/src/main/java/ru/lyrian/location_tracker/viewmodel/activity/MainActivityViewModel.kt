package ru.lyrian.location_tracker.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedIn
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedOut
import ru.lyrian.location_tracker.viewmodel.base.BaseViewModel
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(val iAuthProvider: IAuthProvider) : BaseViewModel() {
    var viewModelReady = false
    val signedInStatusLD = MutableLiveData<Boolean>()

    fun checkSignedInStatus() {
        this.iAuthProvider
            .getSignedInUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when (it) {
                        is SignedIn -> this.signedInStatusLD.value = true
                        is SignedOut -> this.signedInStatusLD.value = false
                    }

                    this.viewModelReady = true
                }
            )
            .addTo(this.compositeDisposable)
    }
}