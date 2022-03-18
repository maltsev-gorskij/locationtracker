package ru.lyrian.location_tracker.di.modules.authentication

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.lyrian.location_tracker.model.authentication.FirebaseAuthProvider
import ru.lyrian.location_tracker.model.authentication.IAuthProvider

/**
 * Dagger Module
 * Provides abstract authentication provider repository
 */

@Module
interface IAuthProviderModule {
    companion object {
        @Provides
        fun provideFirebaseAuth() = Firebase.auth
    }

    @Binds
    fun bindIAuthProvider(firebaseAuthProvider: FirebaseAuthProvider): IAuthProvider
}