package ru.lyrian.location_tracker.viewmodel.base

import android.util.Patterns
import androidx.lifecycle.MutableLiveData

/**
 * Base ViewModel for authentication ViewModels.
 */

abstract class BaseAuthViewModel : BaseViewModel() {
    val emailIsCorrectLD = MutableLiveData<Boolean>()
    val passwordIsCorrectLD = MutableLiveData<Boolean>()

    protected fun checkEmailIsCorrect(email: String) =
        (email.isNotEmpty()) && (Patterns.EMAIL_ADDRESS.matcher(email).matches())

    protected fun passwordIsCorrect(password: String) = password.isNotEmpty()
}