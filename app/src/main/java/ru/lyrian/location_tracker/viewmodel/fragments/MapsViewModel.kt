package ru.lyrian.location_tracker.viewmodel.fragments

import androidx.lifecycle.MutableLiveData
import ru.lyrian.location_tracker.viewmodel.base.BaseViewModel
import ru.lyrian.location_tracker.viewmodel.livedata.OneTimeValue
import javax.inject.Inject

class MapsViewModel @Inject constructor() : BaseViewModel() {
    val signOutNavigationLD = MutableLiveData<OneTimeValue<Boolean>>()

    fun navigateToLoginScreen() {
        this.signOutNavigationLD.value = OneTimeValue(true)
    }
}