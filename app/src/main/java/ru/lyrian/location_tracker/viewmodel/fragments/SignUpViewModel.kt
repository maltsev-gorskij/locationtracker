package ru.lyrian.location_tracker.viewmodel.fragments

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.lyrian.location_tracker.model.authentication.IAuthProvider
import ru.lyrian.location_tracker.model.pojo.SignUpResult
import ru.lyrian.location_tracker.model.pojo.SignUpResult.SignUpFailed
import ru.lyrian.location_tracker.model.pojo.SignUpResult.SignUpSuccessful
import ru.lyrian.location_tracker.viewmodel.base.BaseAuthViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

/**
 * ViewModel for SignUpFragment.
 */

class SignUpViewModel @Inject constructor(private val iAuthProvider: IAuthProvider) :
    BaseAuthViewModel() {
    val signUpSuccessfulLD = MutableLiveData<OneTimeValue<Boolean>>()
    val signUpFailedDialogLD = MutableLiveData<OneTimeValue<String?>>()

    fun signUp(email: String, password: String) {
        val emailIsCorrect = checkEmailIsCorrect(email)
        val passwordNotEmpty = passwordIsCorrect(password)
        this.emailIsCorrectLD.value = emailIsCorrect
        this.passwordIsCorrectLD.value = passwordNotEmpty

        if (emailIsCorrect && passwordNotEmpty) {
            this.iAuthProvider
                .signUp(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: SignUpResult ->
                    when (it) {
                        is SignUpSuccessful -> this.signUpSuccessfulLD.value = OneTimeValue(true)
                        is SignUpFailed -> this.signUpFailedDialogLD.value = OneTimeValue(it.errorMessage)
                    }
                }
                .addTo(this.compositeDisposable)
        }
    }
}