package com.example.superpuperchat.user_space_screen.chat_screen

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

class ChatViewModel(friendId: String = ""): ViewModel() {

    private val _friendId = friendId

    private val messagesDatabase: DatabaseReference = Firebase.database.reference

    val messagesData: MutableLiveData<MutableList<UserMessage>> by lazy {
        MutableLiveData<MutableList<UserMessage>>()
    }

    val friendInfo: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    init {
        setupListenerData()
    }

    private var endTime = ""
    fun pushEndTime(text: String) {
        messagesDatabase.child("user/endTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            read(text)
        }
    }

    private fun read(text: String) {
        messagesDatabase.child("user/endTime").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    endTime = dataSnapshot.value.toString()
                    sendMessage(text)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("SignedActivity.TAG", "LoginData:onCancelled", databaseError.toException())

                }
            }
        )
    }

    private fun sendMessage(text: String) {
        val user = Firebase.auth.currentUser!!
        val messageUser = User(user.uid, user.displayName.toString(), user.photoUrl.toString())
        val message = UserMessage(text = text, user = messageUser, time = endTime)

        messagesDatabase.child("users").child(user.uid).child("chats")
            .child(_friendId).child(endTime).setValue(message)
        messagesDatabase.child("users").child(_friendId).child("chats")
            .child(user.uid).child(endTime).setValue(message)
    }

    private fun setupFriendInfo(user: User) {
        friendInfo.value = user
    }

    private fun setupListenerData() {
        val user = Firebase.auth.currentUser!!
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersSnapshot = snapshot.child("users")

                val friendUser = usersSnapshot.children.mapNotNull {
                    it.getValue<User>()
                }.firstOrNull {
                    it.id == _friendId
                }

                setupFriendInfo(User("", friendUser!!.name, friendUser.uri))

                val messages = usersSnapshot.child(user.uid).child("chats")
                    .child(_friendId).children
                    .mapNotNull {
                        it.getValue<UserMessage>()
                    }

                messages.forEach {
                    it.user.name = friendUser.name
                }

                messages.forEach {
                    it.isMy = it.user.id == Firebase.auth.currentUser?.uid
                }
                messagesData.value = ArrayList(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }
        messagesDatabase.addValueEventListener(postListener)
    }

}