package com.example.superpuperchat.user_space_screen.chat_list_screen

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

class ChatsListViewModel : ViewModel() {

    private val usersDatabase: DatabaseReference = Firebase.database.reference

    val chatsList: MutableLiveData<MutableList<ChatMessage>> by lazy {
        MutableLiveData<MutableList<ChatMessage>>()
    }

    init {
        setupListenerData()
    }

    private fun setupListenerData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateData(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        usersDatabase.addValueEventListener(postListener)
    }

    private fun updateData(snapshot: DataSnapshot) {
        val users = snapshot.child("users").children
            .mapNotNull {
                it.getValue<User>()
            }.filter {
                it.id != Firebase.auth.currentUser?.uid
            }.map {
                ChatMessage(it)
            }
        users.map { item ->
            val lastMessage = snapshot.child("users")
                .child(Firebase.auth.currentUser?.uid!!)
                .child("chats")
                .child(item.user.id)
                .children
                .mapNotNull {
                    it.getValue<UserMessage>()
                }
                .lastOrNull()
            item.message = lastMessage
        }
        val mutableUsers = users.toMutableList()
        users.forEach {
            if (it.message == null || it.message?.text == "")
                mutableUsers.remove(it)
        }
        mutableUsers.sortByDescending {
            it.message?.time
        }
        chatsList.value = ArrayList(mutableUsers)
        //adapter.setData(ArrayList(mutableUsers))
    }

}