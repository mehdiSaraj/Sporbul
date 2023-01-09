package com.mahdi.sporbul.ui.events


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mahdi.sporbul.AppSharedPrefs
import com.mahdi.sporbul.models.EventsFilter
import com.mahdi.sporbul.models.User


class MainActivityViewModel : ViewModel() {

    private var database: FirebaseFirestore? = null

    private val _userData = MutableLiveData<User>(AppSharedPrefs.getUser())
    val userData: LiveData<User> get() = _userData

    private val _filter = MutableLiveData<EventsFilter?>(null)
    val filter: LiveData<EventsFilter?> get() = _filter

    fun setFilter(filter: EventsFilter?) {
        _filter.postValue(filter)
    }

    fun setDatabase(db: FirebaseFirestore) {
        this.database = db
    }

    fun fetchUserData() {
        val user = AppSharedPrefs.getUser()
        database?.let { db ->
            val usersRef = db.collection("Users")
            val query = usersRef.whereEqualTo("email", user!!.email)
            query.addSnapshotListener { snapshot, e ->
                val document = snapshot?.documents?.get(0)
                document?.let {
                    val updatedUser = User(
                        name = document.getString("name").toString(),
                        email = document.getString("email").toString(),
                        phone = document.getString("phone").toString(),
                        address = document.getString("address").toString(),
                        isAdmin = document.getBoolean("admin") ?: false,
                    )
                    _userData.postValue(updatedUser)
                    AppSharedPrefs.storeUser(updatedUser)
                }
            }
        }
    }


}