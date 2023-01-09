package com.mahdi.sporbul.ui.events.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mahdi.sporbul.App
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.models.Event
import com.mahdi.sporbul.models.EventDocument

class EventsFragmentViewModel : ViewModel() {

    private var database: FirebaseFirestore? = null

    private val _events = MutableLiveData<List<EventDocument>>()
    val events: LiveData<List<EventDocument>> get() = _events

    fun setDatabase(db: FirebaseFirestore) {
        this.database = db
    }

    fun fetchEvents() {
        val user = AppSharedPrefs.getUser()
        database?.let { db ->
            db.collection("Events")
                .whereEqualTo("authorEmail", user!!.email)
                .addSnapshotListener { snapshot, e ->
                    val events = snapshot?.documents?.mapNotNull {
                        EventDocument(
                            id = it.id,
                            authorEmail = it.getString("authorEmail") ?: return@mapNotNull null,
                            name = it.getString("name") ?: return@mapNotNull null,
                            typeIdx = it.getLong("typeIdx")?.toInt() ?: return@mapNotNull null,
                            ageIdx = it.getLong("ageIdx")?.toInt() ?: return@mapNotNull null,
                            cityIdx = it.getLong("cityIdx")?.toInt() ?: return@mapNotNull null,
                            time = it.getLong("time") ?: return@mapNotNull null,
                            address = it.getString("address") ?: return@mapNotNull null,
                            notes = it.getString("notes") ?: return@mapNotNull null,
                            contactNumber = it.getString("contactNumber") ?: "",
                        )
                    }
                    if (events != null && events.isNotEmpty()){
                        _events.postValue(events!!)
                    }
                }
        }
    }

}