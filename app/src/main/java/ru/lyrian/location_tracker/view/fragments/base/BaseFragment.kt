package ru.lyrian.location_tracker.view.fragments.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory

/**
 * Base Fragment with ViewBinding
 */

abstract class BaseFragment<T : ViewBinding> : DaggerFragment() {
    protected var binding: T? = null
    abstract val viewModelsFactory: ViewModelsFactory

    override fun onDestroyView() {
        super.onDestroyView()

        this.binding = null
    }

    inline fun <reified T : ViewModel> createFragmentViewModel() =
        ViewModelLazy(T::class, { this.viewModelStore }, { this.viewModelsFactory })

    inline fun <reified T : ViewModel> createSharedViewModel() =
        ViewModelLazy(T::class, { requireActivity().viewModelStore }, { this.viewModelsFactory })
}