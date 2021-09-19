package com.example.superpuperchat.chat

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatViewModel(private val friendId: String): ViewModel() {

    var indexToScroll = 0

    val items: MutableLiveData<ArrayList<UserMessage>> by lazy {
        MutableLiveData<ArrayList<UserMessage>>()
    }

    val friendUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    private val messagesDatabase: DatabaseReference = Firebase.database.reference

    init {
        setupListenerData()
    }

    fun sendMessage(text: String) {
        messagesDatabase.child("user/endTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            messagesDatabase.child("user/endTime").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        sendMessage(text, dataSnapshot.value.toString())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("SignedActivity.TAG", "LoginData:onCancelled", databaseError.toException())
                    }
                }
            )
        }
    }

    private fun sendMessage(text: String, endTime: String) {
        val user = Firebase.auth.currentUser!!
        val messageUser = User(user.uid, user.displayName.toString(), user.photoUrl.toString())
        val message = UserMessage(text = text, user = messageUser, time = endTime)

        messagesDatabase.child("users").child(user.uid).child("chats")
            .child(friendId).child(endTime).setValue(message)
        messagesDatabase.child("users").child(friendId).child("chats")
            .child(user.uid).child(endTime).setValue(message)
    }

    private fun setupListenerData() {
        val user = Firebase.auth.currentUser!!
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersSnapshot = snapshot.child("users")

                val friendUserNetwork = usersSnapshot.children.mapNotNull {
                    it.getValue<User>()
                }.firstOrNull {
                    it.id == friendId
                }

                friendUser.value = friendUserNetwork

                val messages = usersSnapshot.child(user.uid).child("chats")
                    .child(friendId).children
                    .mapNotNull {
                        it.getValue<UserMessage>()
                    }

                messages.forEach {
                    it.user.name = friendUserNetwork!!.name
                }

                messages.forEach {
                    it.isMy = it.user.id == Firebase.auth.currentUser?.uid
                }

                items.value = ArrayList(messages)
                indexToScroll = messages.size - 1
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }

        }
        messagesDatabase.addValueEventListener(postListener)
    }
}