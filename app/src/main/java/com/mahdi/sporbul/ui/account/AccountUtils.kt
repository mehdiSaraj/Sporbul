package com.mahdi.sporbul.ui.account

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun isPhoneNumberValid(text: String): Boolean {
    return PhoneNumberUtils.isGlobalPhoneNumber(text)
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
