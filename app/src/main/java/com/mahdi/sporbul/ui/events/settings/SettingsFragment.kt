package com.mahdi.sporbul.ui.events.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mahdi.sporbul.App
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.databinding.FragmentSettingsBinding
import com.mahdi.sporbul.models.User
import com.mahdi.sporbul.ui.account.AccountActivity


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = AppSharedPrefs.getUser()!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            logoutBtn.setOnClickListener {
                AppSharedPrefs.deleteCurrentUser()
                val intent = Intent(requireContext(), AccountActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            nameItem.setDescription(user.name)
            emailItem.setDescription(user.email)
            addressItem.setDescription(user.address)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}