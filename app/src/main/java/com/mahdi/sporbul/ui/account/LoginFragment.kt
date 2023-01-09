package com.mahdi.sporbul.ui.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.databinding.FragmentLoginBinding
import com.mahdi.sporbul.models.UserWithPassword


class LoginFragment : Fragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signUpBtn.setOnClickListener {
                (activity as? AccountActivity)?.navigateToSignUpFragment()
            }
            loginBtn.setOnClickListener {
                validateAndLogin()
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
                is AccountViewModel.UiEvent.UserIsValid -> {
                    AppSharedPrefs.storeUser(event.user)
                    (activity as? AccountActivity)?.navigateToMain(event.user)
                }
                is AccountViewModel.UiEvent.UserIsInvalid -> {
                    requireContext().showToast("Invalid Email or Password!")
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

    private fun validateAndLogin() {
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
            requireContext().showToast("Please fill all fields")
            return
        }

        binding.apply {
            if (!isEmailValid(emailEditText.text.toString())) {
                emailInputLayout.error = "Invalid Email!"
                foundErrors = true
            } else {
                emailInputLayout.error = null
            }
        }

        if (foundErrors) {
            requireContext().showToast("Please enter a valid Email")
            return
        }

        binding.apply {
            login(emailEditText.text.toString(), passwordEditText.text.toString())
        }

    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}