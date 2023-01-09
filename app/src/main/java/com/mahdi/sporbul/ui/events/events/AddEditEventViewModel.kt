package com.mahdi.sporbul.ui.events.events

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mahdi.sporbul.models.Event

class AddEditEventViewModel : ViewModel() {

    private var database: FirebaseFirestore? = null

    fun setDatabase(db: FirebaseFirestore) {
        this.database = db
    }

    fun addEvent(event: Event) {
        database?.collection("Events")?.add(event)
    }

    fun updateEvent(id: String, event: Event) {
        database?.collection("Events")?.document(id)?.set(event)
    }


}