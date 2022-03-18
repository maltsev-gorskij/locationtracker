package ru.lyrian.location_tracker.model.pojo

/**
 * Holder class to pass current user sign up info and possible error message from firebase model class to ViewModel.
 * Contains of sealed class inheritors. Each one have nullable errorMessage property.
 *   - SignUpSuccessful: User is successfully signed up
 *   - SignUpFailed: User is not signed up because of any failure
 */

sealed class SignUpResult {
    object SignUpSuccessful : SignUpResult()
    class SignUpFailed(val errorMessage: String?) : SignUpResult()
}