package com.example.superpuperchat.user_space_screen.chat_list_screen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.user_space_screen.activities.TabBarActivity
import com.example.superpuperchat.adapters.ChatsListAdapter
import com.example.superpuperchat.models.ChatMessage
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.example.superpuperchat.user_space_screen.chat_list_screen.interfaces.ChatsListActivityInterface
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.EnumSet.of
import java.util.List.of

class ChatsListFragment: Fragment(), ChatsListActivityInterface {

    private var searchIcon: ImageView? = null
    private var chatText: TextView? = null
    private var searchField: EditText? = null
    private var searchContainer: RelativeLayout? = null
    private var cancelIcon: ImageView? = null
    private var chatsRecyclerView: RecyclerView? = null
    private var loader: ProgressBar? = null

    private var adapter = ChatsListAdapter(this)

    private val chatListViewModel = ChatsListViewModel()

    override var localContext: Context?
        get() {
            return context
        }
        set(value) {}

    private val tabBarActivity: TabBarActivity
        get() = activity as TabBarActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats_list, container, false)

        setupViews(view)

        chatListViewModel.chatsList.observe(requireActivity()) {
            adapter.setData(ArrayList(it))
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

    override fun routeToUser(user: User) {
        chatsRecyclerView?.visibility = View.GONE
        loader?.visibility = View.VISIBLE

        tabBarActivity.routeToUserChat(user.id)
    }
}