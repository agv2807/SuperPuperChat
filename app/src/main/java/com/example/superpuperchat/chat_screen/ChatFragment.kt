package com.example.superpuperchat.chat_screen

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.activities.TabBarActivity
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment(friendId: String = "") : Fragment() {

    private val messagesDatabase: DatabaseReference = Firebase.database.reference

    private var chatRv: RecyclerView? = null
    private var messageField: EditText? = null
    private var sendIcon: ImageView? = null
    private var userPhoto: CircleImageView? = null
    private var userName: TextView? = null
    private var backIc: ImageView? = null

    private val adapter = ChatAdapter()

    private val _friendId = friendId

    private val tabBarActivity: TabBarActivity
        get() = activity as TabBarActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        setupViews(view)
        setupListenerData()

        chatRv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatRv?.adapter = adapter

        return view
    }

    private fun setupViews(view: View) {
        chatRv = view.findViewById(R.id.chat)
        messageField = view.findViewById(R.id.message_field)
        sendIcon = view.findViewById(R.id.send_icon)
        userPhoto = view.findViewById(R.id.chat_profile_photo)
        userName = view.findViewById(R.id.chat_username)
        backIc = view.findViewById(R.id.back_ic)

        sendIcon?.setOnClickListener {
            if (messageField?.text.toString().isEmpty()) {
                Toast.makeText(context, "Введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                pushEndTime()
            }
        }

        backIc?.setOnClickListener {
            tabBarActivity.routeToChats()
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
            .child(_friendId).child(endTime).setValue(message)
        messagesDatabase.child("users").child(_friendId).child("chats")
            .child(user.uid).child(endTime).setValue(message)
        messageField?.setText("")
    }

    private fun setupFriendInfo(name: String, uri: String?) {
        userName?.text = name
        Picasso.with(context)
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
                    it.id == _friendId
                }

                setupFriendInfo(friendUser!!.name, friendUser.uri)

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
                adapter.setData(ArrayList(messages))
                val indexToScroll = messages.size - 1
                if (indexToScroll > 0) {
                    chatRv?.smoothScrollToPosition(indexToScroll)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }

        }
        messagesDatabase.addValueEventListener(postListener)
    }

}