package com.mahdi.sporbul.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.ui.account.AccountActivity
import com.mahdi.sporbul.ui.events.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val user = AppSharedPrefs.getUser()
        val intent = if (user == null) {
            Intent(this, AccountActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}