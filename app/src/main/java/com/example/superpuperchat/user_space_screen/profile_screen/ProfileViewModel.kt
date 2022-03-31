package com.example.superpuperchat.user_space_screen.profile_screen

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val user = auth.currentUser
    private val usersDatabase: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference

    val profileInfo: MutableLiveData<FirebaseUser?> by lazy {
        MutableLiveData<FirebaseUser?>()
    }

    val dataToast: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    init {
        profileInfo.value = user
    }

    fun updateUserName(userName: String? =  null) {
        usersDatabase.child("users").child(user!!.uid).child("name").setValue(userName)
        val nameUpdate = userProfileChangeRequest {
            displayName = userName ?: displayName
        }
        user.updateProfile(nameUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataToast.value = "Имя изменено"
                } else {
                    dataToast.value = task.exception.toString()
                }
            }
    }

    private fun updatePhoto(image: Uri?) {
        usersDatabase.child("users").child(user!!.uid).child("uri").setValue(image.toString())
        val profileUpdates = userProfileChangeRequest {
            photoUri = image ?: photoUri
        }
        auth.currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataToast.value = "Фото обновлено"
                }
            }
    }

    fun uploadFile(imageUri: Uri) {
        val ref = storageRef.child("images/${generateId()}")
        val uploadTask = ref.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePhoto(task.result)
            }
        }
    }

    private fun generateId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

}