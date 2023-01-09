package com.mahdi.sporbul.models

data class EventDocument(
    val id: String,
    val authorEmail: String,
    val name: String,
    val typeIdx: Int,
    val time: Long,
    val ageIdx: Int,
    val cityIdx: Int,
    val contactNumber: String,
    val address: String,
    val notes: String
): java.io.Serializable

data class Event(
    val authorEmail: String,
    val name: String,
    val typeIdx: Int,
    val time: Long,
    val ageIdx: Int,
    val cityIdx: Int,
    val address: String,
    val contactNumber: String,
    val notes: String
): java.io.Serializable
