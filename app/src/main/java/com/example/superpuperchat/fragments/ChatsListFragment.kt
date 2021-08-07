package com.example.superpuperchat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.activities.ChatActivity
import com.example.superpuperchat.activities.ChatsListActivityInterface
import com.example.superpuperchat.adapters.ChatMessage
import com.example.superpuperchat.adapters.ChatsListAdapter
import com.example.superpuperchat.data_classes.User
import com.example.superpuperchat.data_classes.UserMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ChatsListFragment: Fragment(), ChatsListActivityInterface {

    private val usersDatabase: DatabaseReference = Firebase.database.reference

    private var searchIcon: ImageView? = null
    private var chatText: TextView? = null
    private var searchField: EditText? = null
    private var searchContainer: RelativeLayout? = null
    private var cancelIcon: ImageView? = null
    private var chatsRecyclerView: RecyclerView? = null
    private var loader: ProgressBar? = null

    private var adapter = ChatsListAdapter(this)

    override var localContext: Context?
        get() {
            return context
        }
        set(value) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats_list, container, false)

        setupViews(view)

        CoroutineScope(Dispatchers.IO).launch {
            setupListenerData()
        }

        chatsRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatsRecyclerView?.adapter = adapter

        return view
    }

    override fun onStop() {
        super.onStop()
        chatsRecyclerView?.visibility = View.VISIBLE
        loader?.visibility = View.GONE
    }

    private fun setupViews(view: View) {
        searchIcon = view.findViewById(R.id.search_icon)
        chatText = view.findViewById(R.id.chats_text)
        searchField = view.findViewById(R.id.search_field)
        searchContainer = view.findViewById(R.id.search_container)
        cancelIcon = view.findViewById(R.id.cancel_icon)
        chatsRecyclerView = view.findViewById(R.id.chats_list)
        loader = view.findViewById(R.id.loader)

        searchIcon?.setOnClickListener {
            openSearchMode()
        }

        cancelIcon?.setOnClickListener {
            closeSearchMode()
        }
    }

    private fun openSearchMode() {
        searchIcon?.visibility = View.GONE
        chatText?.visibility = View.GONE
        searchContainer?.visibility = View.VISIBLE
    }

    private fun closeSearchMode() {
        searchIcon?.visibility = View.VISIBLE
        chatText?.visibility = View.VISIBLE
        searchContainer?.visibility = View.GONE
    }

    private fun setupListenerData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    updateData(snapshot)
                }
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

        CoroutineScope(Dispatchers.Main).launch {
            adapter.setData(ArrayList(users))
        }
    }

    override fun routeToUser(user: User) {
        chatsRecyclerView?.visibility = View.GONE
        loader?.visibility = View.VISIBLE

        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("userId", user.id)
        startActivity(intent)
    }
}