package ru.lyrian.location_tracker.di.modules.android_components

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lyrian.location_tracker.view.fragments.login.SignInFragment
import ru.lyrian.location_tracker.view.fragments.login.SignUpFragment
import ru.lyrian.location_tracker.view.fragments.maps.MapsFragment
import ru.lyrian.location_tracker.view.fragments.maps.nested.LocationFragment
import ru.lyrian.location_tracker.view.fragments.maps.nested.RouteFragment

/**
 * Dagger Module
 * Creates subcomponents for providing dependencies to Fragment classes without necessity of calling inject() method
 */

@Module
interface FragmentBuildersModule {
    @ContributesAndroidInjector
    fun contributeSignInFragment(): SignInFragment

    @ContributesAndroidInjector
    fun contributeSignUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    fun contributeMapsFragment(): MapsFragment

    @ContributesAndroidInjector
    fun contributeLocationFragment(): LocationFragment

    @ContributesAndroidInjector
    fun contributeRouteFragment(): RouteFragment
}