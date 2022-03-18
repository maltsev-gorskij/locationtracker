package ru.lyrian.location_tracker.model.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import ru.lyrian.location_tracker.model.pojo.SignInResult
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedIn
import ru.lyrian.location_tracker.model.pojo.SignInResult.SignedOut
import ru.lyrian.location_tracker.model.pojo.SignUpResult
import ru.lyrian.location_tracker.model.pojo.SignUpResult.SignUpFailed
import ru.lyrian.location_tracker.model.pojo.SignUpResult.SignUpSuccessful
import javax.inject.Inject

/**
 * Firebase Authentication mechanism. Provides SignIn and SignOut features.
 */

class FirebaseAuthProvider @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    IAuthProvider {
    override fun getSignedInUser(): Single<SignInResult> {
        return Single
            .create(SingleOnSubscribe<SignInResult> {
                val email = this.firebaseAuth.currentUser?.email
                val signInResult: SignInResult = when (this.firebaseAuth.currentUser) {
                    null -> SignedOut
                    else -> SignedIn(email)
                }

                if (!it.isDisposed) it.onSuccess(signInResult)
            })
            .subscribeOn(Schedulers.io())
    }

    override fun signIn(username: String, password: String): Single<SignInResult> {
        return Single
            .create { emitter: SingleEmitter<SignInResult> ->
                this.firebaseAuth
                    .signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { firebaseTask: Task<AuthResult> ->
                        if (!emitter.isDisposed) {
                            when (firebaseTask.isSuccessful) {
                                true -> emitter.onSuccess(SignedIn(username))
                                false -> emitter.onSuccess(SignedOut)
                            }
                        }
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun signUp(username: String, password: String): Single<SignUpResult> {
        return Single
            .create { emitter: SingleEmitter<SignUpResult> ->
                this.firebaseAuth
                    .createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener { firebaseTask: Task<AuthResult> ->
                        val firebaseTaskError = firebaseTask.exception?.message.toString()
                        if (!emitter.isDisposed)
                            when (firebaseTask.isSuccessful) {
                                true -> emitter.onSuccess(SignUpSuccessful)
                                false -> emitter.onSuccess(SignUpFailed(firebaseTaskError))
                            }
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun signOut(): Completable {
        return Completable
            .fromAction { this.firebaseAuth.signOut() }
            .subscribeOn(Schedulers.io())
    }
}