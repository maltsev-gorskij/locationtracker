package ru.lyrian.location_tracker.view.fragments.login

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.FragmentSignUpBinding
import ru.lyrian.location_tracker.view.fragments.base.BaseFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.fragments.SignUpViewModel
import javax.inject.Inject

/**
 * Fragment for signingUp new users
 */

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    private val signUpViewModel: SignUpViewModel by createFragmentViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return (this.binding as FragmentSignUpBinding).root
    }

    override fun onStart() {
        super.onStart()

        this.binding?.tePassword?.transformationMethod = PasswordTransformationMethod()

        this.signUpViewModel.emailIsCorrectLD.observe(viewLifecycleOwner) {
            this.binding?.tilUserName?.error = if (it) null else getString(R.string.til_error_must_be_email)
        }

        this.signUpViewModel.passwordIsCorrectLD.observe(viewLifecycleOwner) {
            this.binding?.tilPassword?.error =
                if (it) null else getString(R.string.til_error_password_is_empty)
        }

        this.signUpViewModel.signUpSuccessfulLD.observe(viewLifecycleOwner) {
            it.getValueIfNotRequested()?.let {
                val action = SignUpFragmentDirections.actionSignUpFragmentToMapsFragment()
                findNavController().navigate(action)
            }
        }

        this.signUpViewModel.signUpFailedDialogLD.observe(viewLifecycleOwner) {
            it.getValueIfNotRequested()?.let { firebaseError: String ->
                showErrorAlert(firebaseError)
            }
        }

        this.binding?.btSignUp?.setOnClickListener {
            val email = this.binding?.teUserName?.text.toString()
            val password = this.binding?.tePassword?.text.toString()

            this.signUpViewModel.signUp(email, password)
        }

        this.binding?.ivBackButton?.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    private fun showErrorAlert(message: String) = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.madb_error_signup_failed))
        .setNegativeButton(getString(R.string.bt_ok)) { dialog, _ -> dialog?.dismiss() }
        .setMessage(message)
        .show()
}