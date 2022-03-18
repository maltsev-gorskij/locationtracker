package ru.lyrian.location_tracker.viewmodel.fragments

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.pojo.SignInResult
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedIn
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedOut
import ru.lyrian.location_tracker.viewmodel.base.BaseAuthViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * ViewModel for SignInFragment.
 */

class SignInViewModel @Inject constructor(private val iAuthProvider: IAuthProvider) :
    BaseAuthViewModel() {
    val signedInStatusLD = MutableLiveData<OneTimeValue<Boolean>>()
    val signInSuccessfulLD = MutableLiveData<OneTimeValue<Boolean>>()
    val signInFailedDialogLD = MutableLiveData<OneTimeValue<Boolean>>()

    fun checkSignedInStatus() {
        this.iAuthProvider
            .getSignedInUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it: SignInResult ->
                if (it is SignedIn) this.signedInStatusLD.value = OneTimeValue(true)
            }
            .addTo(this.compositeDisposable)
    }

    fun signIn(email: String, password: String) {
        val emailIsCorrect = checkEmailIsCorrect(email)
        val passwordNotEmpty = passwordIsCorrect(password)
        this.emailIsCorrectLD.value = emailIsCorrect
        this.passwordIsCorrectLD.value = passwordNotEmpty

        if (emailIsCorrect && passwordNotEmpty) {
            this.iAuthProvider
                .signIn(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: SignInResult ->
                    when (it) {
                        is SignedIn -> this.signInSuccessfulLD.value = OneTimeValue(true)
                        is SignedOut -> this.signInFailedDialogLD.value = OneTimeValue(true)
                    }
                }
                .addTo(this.compositeDisposable)
        }
    }
}