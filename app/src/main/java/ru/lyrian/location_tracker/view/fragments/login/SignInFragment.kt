package ru.lyrian.location_tracker.view.fragments.login

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.FragmentSignInBinding
import ru.lyrian.location_tracker.view.fragments.base.BaseFragment
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.fragments.SignInViewModel
import javax.inject.Inject

/**
 * Fragment for logging in currently not signed in users.
 */

class SignInFragment : BaseFragment<FragmentSignInBinding>() {
    private val signInViewModel: SignInViewModel by createFragmentViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentSignInBinding.inflate(inflater, container, false)

        return (this.binding as FragmentSignInBinding).root
    }

    override fun onStart() {
        super.onStart()

        this.binding?.tePassword?.transformationMethod = PasswordTransformationMethod()

        this.signInViewModel.emailIsCorrectLD.observe(viewLifecycleOwner) {
            this.binding?.tilUserName?.error = if (it) null else getString(R.string.til_error_must_be_email)
        }

        this.signInViewModel.passwordIsCorrectLD.observe(viewLifecycleOwner) {
            this.binding?.tilPassword?.error =
                if (it) null else getString(R.string.til_error_password_is_empty)
        }

        this.signInViewModel.signInSuccessfulLD.observe(viewLifecycleOwner) {
            it.getValueIfNotRequested()?.let {
                navigateToMap()
            }
        }

        this.signInViewModel.signInFailedDialogLD.observe(viewLifecycleOwner) {
            it.getValueIfNotRequested()?.let {
                showErrorAlert()
            }
        }

        this.signInViewModel.signedInStatusLD.observe(viewLifecycleOwner) {
            it.getValueIfNotRequested()?.let {
                navigateToMap()
            }
        }

        this.binding?.btSignIn?.setOnClickListener {
            val email = this.binding?.teUserName?.text.toString()
            val password = this.binding?.tePassword?.text.toString()

            this.signInViewModel.signIn(email, password)
        }

        this.binding?.tvSignUp?.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        this.signInViewModel.checkSignedInStatus()
    }

    private fun navigateToMap() {
        val action = SignInFragmentDirections.actionSignInFragmentToMapsFragment()
        findNavController().navigate(action)
    }


    private fun showErrorAlert() = MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.madb_error_cannot_login))
        .setNegativeButton(getString(R.string.bt_ok)) { dialog, _ -> dialog?.dismiss() }
        .setMessage(getString(R.string.madb_provide_credentials))
        .show()
}
