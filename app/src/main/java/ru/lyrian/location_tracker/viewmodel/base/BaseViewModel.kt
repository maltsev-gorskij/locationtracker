package ru.lyrian.location_tracker.viewmodel.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Base ViewModel for handling CompositeDisposable of all ViewModels
 */

abstract class BaseViewModel : ViewModel() {
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        this.compositeDisposable.clear()
    }
}