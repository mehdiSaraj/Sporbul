package com.mahdi.sporbul.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mahdi.sporbul.models.User
import com.mahdi.sporbul.models.UserWithPassword
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

class AccountViewModel : ViewModel() {

    private val _events = MutableLiveData<UiEvent>()
    val events: LiveData<UiEvent> get() = _events

    private var database: FirebaseFirestore? = null

    fun setDatabase(db: FirebaseFirestore) {
        this.database = db
    }

    fun login(email: String, pass: String) {
        database?.let { db ->
            val usersRef = db.collection("Users")
            val query = usersRef.whereEqualTo("email", email)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result.documents[0]
                    if (hashPassword(pass) == document.get("password").toString()) {
                        val user = User(
                            name = document.getString("name").toString(),
                            email = document.getString("email").toString(),
                            phone = document.getString("phone").toString(),
                            address = document.getString("address").toString(),
                            isAdmin = document.getBoolean("admin") ?: false,
                        )
                        _events.postValue(UiEvent.UserIsValid(user))
                    } else {
                        _events.postValue(UiEvent.UserIsInvalid)
                    }
                }
            }
        }
    }

    fun signUp(userWithPassword: UserWithPassword) {
        _events.postValue(UiEvent.Loading)
        database?.let { db ->
            val usersRef = db.collection("Users")
            val query = usersRef.whereEqualTo("email", userWithPassword.email)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.documents.size == 0) {
                        usersRef.add(userWithPassword).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = User(
                                    name = userWithPassword.name,
                                    email = userWithPassword.email,
                                    phone = userWithPassword.phone,
                                    address = userWithPassword.address,
                                    isAdmin = userWithPassword.isAdmin
                                )
                                _events.postValue(UiEvent.UserCreated(user))
                            } else {
                                _events.postValue(UiEvent.Error("Couldn't add user to database!"))
                            }
                        }
                    } else {
                        _events.postValue(UiEvent.UserAlreadyExists)
                    }
                }
            }
        }
    }

    fun hashPassword(passwordToHash: String): String {
        return try {
            val md = MessageDigest.getInstance("sha-256")
            md.update(passwordToHash.toByteArray())
            val bytes = md.digest()
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(((bytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            passwordToHash
        }
    }

    sealed class UiEvent {
        object Loading : UiEvent()
        class Error(val message: String) : UiEvent()
        object UserAlreadyExists : UiEvent()
        class UserIsValid(val user: User) : UiEvent()
        class UserCreated(val user: User) : UiEvent()
        object UserIsInvalid : UiEvent()
    }
}