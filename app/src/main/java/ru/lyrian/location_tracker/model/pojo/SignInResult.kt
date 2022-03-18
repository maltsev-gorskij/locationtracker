package ru.lyrian.location_tracker.model.pojo

/**
 * Holder class to pass current user sign in info from firebase model class to ViewModel.
 * Contains of sealed class inheritors. Each one have nullable email property.
 *   - SignedIn: User is signed in
 *   - SignedOut: User is signed out or sign in procedure failed
 */

sealed class SignInResult {
    class SignedIn(val email: String?) : SignInResult()
    object SignedOut : SignInResult()
}