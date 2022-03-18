package ru.lyrian.location_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.lyrian.location_tracker.di.annotations.qualifiers.ViewModelsMap
import javax.inject.Inject
import javax.inject.Provider

class ViewModelsFactory @Inject constructor(
    @ViewModelsMap private val viewModelsMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        viewModelsMap.getValue(modelClass as Class<ViewModel>).get() as T
}
