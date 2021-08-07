package com.example.superpuperchat.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.adapters.ChatAdapter
import com.example.superpuperchat.data_classes.User
import com.example.superpuperchat.data_classes.UserMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private val messagesDatabase: DatabaseReference = Firebase.database.reference

    private var chatRv: RecyclerView? = null
    private var messageField: EditText? = null
    private var sendIcon: ImageView? = null
    private var userPhoto: CircleImageView? = null
    private var userName: TextView? = null
    private var backIc: ImageView? = null

    private val adapter = ChatAdapter()

    private var friendId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupViews()
        setupListenerData()

        friendId = intent.getStringExtra("userId").toString()

        chatRv?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRv?.adapter = adapter
    }

    private fun setupViews() {
        chatRv = findViewById(R.id.chat)
        messageField = findViewById(R.id.message_field)
        sendIcon = findViewById(R.id.send_icon)
        userPhoto = findViewById(R.id.chat_profile_photo)
        userName = findViewById(R.id.chat_username)
        backIc = findViewById(R.id.back_ic)

        sendIcon?.setOnClickListener {
            if (messageField?.text.toString().isEmpty()) {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                pushEndTime()
            }
        }

        backIc?.setOnClickListener {
            finish()
        }
    }

    private var endTime = ""
    private fun pushEndTime() {
        messagesDatabase.child("user/endTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            read()
        }
    }

    private fun read() {
        messagesDatabase.child("user/endTime").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    endTime = dataSnapshot.value.toString()
                    sendMessage(messageField?.text.toString())
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
            .child(friendId).child(endTime).setValue(message)
        messagesDatabase.child("users").child(friendId).child("chats")
            .child(user.uid).child(endTime).setValue(message)
        messageField?.setText("")
    }

    private fun setupFriendInfo(name: String, uri: String?) {
        userName?.text = name
        Picasso.with(this)
            .load(uri)
            .placeholder(R.drawable.man)
            .into(userPhoto)
    }

    private fun setupListenerData() {
        val user = Firebase.auth.currentUser!!
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersSnapshot = snapshot.child("users")

                val friendUser = usersSnapshot.children.mapNotNull {
                    it.getValue<User>()
                }.firstOrNull {
                    it.id == friendId
                }

                setupFriendInfo(friendUser!!.name, friendUser.uri)

                val messages = usersSnapshot.child(user.uid).child("chats")
                    .child(friendId).children
                    .mapNotNull {
                        it.getValue<UserMessage>()
                    }

                messages.forEach {
                    it.user.name = friendUser.name
                }

                messages.forEach {
                    it.isMy = it.user.id == Firebase.auth.currentUser?.uid
                }
                adapter.setData(ArrayList(messages))
                val indexToScroll = messages.size - 1
                if (indexToScroll > 0) {
                    chatRv?.smoothScrollToPosition(indexToScroll)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }

        }
        messagesDatabase.addValueEventListener(postListener)
    }

}