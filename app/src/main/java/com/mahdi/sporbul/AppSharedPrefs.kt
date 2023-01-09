package com.mahdi.sporbul

import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import com.google.gson.Gson
import com.mahdi.sporbul.models.User


object AppSharedPrefs {

    private const val USER_KEY = "user_key"

    private val prefs: SharedPreferences get() =
        App.applicationContext().getSharedPreferences("prefs", MODE_PRIVATE)

    fun storeUser(user: User) {
        val userSerialized = Gson().toJson(user)
        prefs.edit().apply {
            putString(USER_KEY, userSerialized)
        }.apply()
    }

    fun deleteCurrentUser() {
        prefs.edit().apply {
            remove(USER_KEY)
        }.apply()
    }

    fun getUser(): User? {
        val serializedUser = prefs.getString(USER_KEY, null) ?: return null
        return Gson().fromJson(serializedUser, User::class.java)
    }

}