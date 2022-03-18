package ru.lyrian.location_tracker.di.modules.location

import dagger.Binds
import dagger.Module
import ru.lyrian.location_tracker.utility.permissions.IPermissionsProvider
import ru.lyrian.location_tracker.utility.permissions.PermissionsProvider

@Module
interface IPermissionsProviderModule {
    @Binds
    fun bindIFragmentInteractor(permissionsProvider: PermissionsProvider):
            IPermissionsProvider.IFragmentInteractor

    @Binds
    fun bindIViewModelInteractor(permissionsProvider: PermissionsProvider):
            IPermissionsProvider.IViewModelInteractor
}