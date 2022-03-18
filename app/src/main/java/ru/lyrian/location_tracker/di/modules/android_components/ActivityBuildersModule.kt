package ru.lyrian.location_tracker.di.modules.android_components

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lyrian.location_tracker.view.activity.MainActivity

/**
 * Dagger Module
 * Creates subcomponent for providing dependencies to MainActivity without necessity of calling inject() method
 */

@Module
interface ActivityBuildersModule {
    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity
}