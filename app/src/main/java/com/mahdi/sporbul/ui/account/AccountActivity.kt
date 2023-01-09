package com.mahdi.sporbul.ui.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdi.sporbul.databinding.ActivityAccountBinding
import com.mahdi.sporbul.databinding.ActivityMainBinding
import com.mahdi.sporbul.models.User
import com.mahdi.sporbul.ui.events.MainActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var loginFragment: LoginFragment

    private val viewModel: AccountViewModel by viewModels()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.setDatabase(db)

        loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, loginFragment, LoginFragment.TAG)
            .commit()
    }

    fun navigateToSignUpFragment() {
        supportFragmentManager.beginTransaction()
            .add(binding.container.id, SignUpFragment(), SignUpFragment.TAG)
            .addToBackStack(SignUpFragment.TAG)
            .commit()
    }

    fun popBackStack() {
        supportFragmentManager.popBackStackImmediate()
    }

    fun navigateToMain(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }
}