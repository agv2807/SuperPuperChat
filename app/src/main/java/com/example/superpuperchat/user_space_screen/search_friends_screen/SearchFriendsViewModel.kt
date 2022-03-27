package com.example.superpuperchat.user_space_screen.search_friends_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superpuperchat.models.ChatMessage
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class SearchFriendsViewModel : ViewModel() {

    private val usersDatabase: DatabaseReference = Firebase.database.reference

    val friendsList: MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }

    init {
        setupListenerData("")
    }

    private fun setupListenerData(pat: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateData(snapshot, pat)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        usersDatabase.addValueEventListener(postListener)
    }

    private fun updateData(snapshot: DataSnapshot, pat: String) {
        val users = snapshot.child("users").children
            .mapNotNull {
                it.getValue<User>()
            }.filter {
                it.id != Firebase.auth.currentUser?.uid
            }
        val mutableUsers = users.toMutableList()
        if (pat.isEmpty()) {
            friendsList.value = ArrayList(mutableUsers)
        } else {
            users.forEach {
                if (!it.name.startsWith(pat)) {
                    mutableUsers.remove(it)
                }
            }
            friendsList.value = ArrayList(mutableUsers)
        }
    }

    fun rebootList(name: String) {
        setupListenerData(name)
    }

}