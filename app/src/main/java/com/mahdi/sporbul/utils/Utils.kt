package com.mahdi.sporbul.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateTimeString(): String {
    val formatter = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.ENGLISH)
    return formatter.format(Date(this))
}


fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return formatter.format(Date(this))
}

fun Long.toTimeString(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return formatter.format(Date(this))
}