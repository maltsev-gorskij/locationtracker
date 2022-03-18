package ru.lyrian.location_tracker.di.modules.android_components

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.lyrian.location_tracker.di.annotations.maps.ViewModelsMapKey
import ru.lyrian.location_tracker.di.annotations.qualifiers.ViewModelsMap
import ru.lyrian.location_tracker.viewmodel.activity.MainActivityViewModel
import ru.lyrian.location_tracker.viewmodel.fragments.*

/**
 * Dagger Module
 * Generate ViewModels Providers, ViewModelsFactory and declare ViewModels multibinding
 * in ViewModelsFactory
 */

@Module
interface ViewModelsModule {
    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(SignInViewModel::class)]
    fun bindSignInViewModel(signInViewModel: SignInViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(SignUpViewModel::class)]
    fun bindSignUpViewModel(signUpViewModel: SignUpViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(MapsViewModel::class)]
    fun bindMapsViewModel(mapsViewModel: MapsViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(LocationViewModel::class)]
    fun bindLocationViewModel(locationViewModel: LocationViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(RouteViewModel::class)]
    fun binsRouteViewModel(routeViewModel: RouteViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelsMap ViewModelsMapKey(MainActivityViewModel::class)]
    fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel
}