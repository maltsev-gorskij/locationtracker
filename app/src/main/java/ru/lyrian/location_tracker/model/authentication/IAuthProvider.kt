package ru.lyrian.location_tracker.model.authentication

import io.reactivex.Completable
import io.reactivex.Single
import ru.lyrian.location_tracker.model.pojo.SignInResult
import ru.lyrian.location_tracker.model.pojo.SignUpResult

/**
 * Abstraction layer between auth fragments view models and repository providing authentication mechanism
 */

interface IAuthProvider {
    fun signIn(username: String, password: String): Single<SignInResult>

    fun getSignedInUser(): Single<SignInResult>

    fun signUp(username: String, password: String): Single<SignUpResult>

    fun signOut(): Completable
}