package ru.lyrian.location_tracker.view.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerAppCompatActivity
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory

abstract class BaseActivity<T : ViewBinding> : DaggerAppCompatActivity() {
    protected lateinit var binding: T
    abstract val viewModelsFactory: ViewModelsFactory

    inline fun <reified T : ViewModel> createActivityViewModel() =
        ViewModelLazy(T::class, { this.viewModelStore }, { this.viewModelsFactory })
}