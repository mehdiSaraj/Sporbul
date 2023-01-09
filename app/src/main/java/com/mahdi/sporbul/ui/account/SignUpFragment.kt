package com.mahdi.sporbul.ui.account

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.databinding.FragmentSignUpBinding
import com.mahdi.sporbul.models.UserWithPassword


class SignUpFragment : Fragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            goToLoginBtn.setOnClickListener {
                (activity as? AccountActivity)?.popBackStack()
            }
            signUpBtn.setOnClickListener {
                validateAndSignUp()
            }
        }
        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                is AccountViewModel.UiEvent.Error -> {
                    requireContext().showToast(event.message)
                }
                AccountViewModel.UiEvent.Loading -> {
                    showLoading()
                }
                AccountViewModel.UiEvent.UserAlreadyExists -> {
                    requireContext().showToast("This email already exists!")
                }
                is AccountViewModel.UiEvent.UserCreated -> {
                    AppSharedPrefs.storeUser(event.user)
                    (activity as? AccountActivity)?.navigateToMain(event.user)
                }
                else -> {}
            }
            if (event !is AccountViewModel.UiEvent.Loading) {
                stopLoading()
            }
        }
    }

    private fun stopLoading() {
        binding.logo.visibility = View.VISIBLE
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun showLoading() {
        binding.logo.visibility = View.GONE
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun validateAndSignUp() {
        var foundErrors = false
        binding.root.children.iterator().forEach { view ->
            if (view is TextInputLayout) {
                if (view.editText?.text?.isBlank() == true) {
                    foundErrors = true
                    view.editText?.error = "This field is required!"
                } else {
                    view.editText?.error = null
                }
            }
        }

        if (foundErrors) {
            requireContext().showToast("Please enter valid data")
            return
        }

        binding.apply {
            if (!isEmailValid(emailEditText.text.toString())) {
                emailInputLayout.error = "Invalid Email!"
                foundErrors = true
            } else {
                emailInputLayout.error = null
            }
            if (!isPhoneNumberValid(phoneEditText.text.toString())) {
                phoneInputLayout.error = "Invalid Number!"
                foundErrors = true
            } else {
                phoneInputLayout.error = null
            }
        }

        if (foundErrors) {
            requireContext().showToast("Please enter valid data")
            return
        }

        binding.apply {
            signUp(
                UserWithPassword(
                    name = nameEditText.text.toString(),
                    email = emailEditText.text.toString(),
                    password = viewModel.hashPassword(passwordEditText.text.toString()),
                    phone = phoneEditText.text.toString(),
                    address = addressEditText.text.toString()
                )
            )
        }

    }

    private fun signUp(userWithPassword: UserWithPassword) {
        viewModel.signUp(userWithPassword)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SignUpFragment"
    }
}