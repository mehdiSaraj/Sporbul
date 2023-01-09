package com.mahdi.sporbul.models

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val isAdmin: Boolean
): java.io.Serializable

data class UserWithPassword(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String,
    val isAdmin: Boolean = false
)
