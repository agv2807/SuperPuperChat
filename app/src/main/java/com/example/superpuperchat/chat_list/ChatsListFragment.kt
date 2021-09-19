package com.example.superpuperchat.chat_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.chat.ChatAssembly
import com.example.superpuperchat.models.User

interface ChatsListActivityInterface {
    var localContext: Context?
    fun routeToUser(user: User)
}

class ChatsListFragment : Fragment(), ChatsListActivityInterface {

    private var searchIcon: ImageView? = null
    private var chatText: TextView? = null
    private var searchField: EditText? = null
    private var searchContainer: RelativeLayout? = null
    private var cancelIcon: ImageView? = null
    private var chatsRecyclerView: RecyclerView? = null
    private var loader: ProgressBar? = null

    private var adapter = ChatsListAdapter(this)

    private val dataModel = ChatsListViewModel()

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

        dataModel.items.observe(requireActivity(), {
            adapter.setData(it)
        })

        chatsRecyclerView?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatsRecyclerView?.adapter = adapter

        return view
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
        parentFragmentManager.beginTransaction().apply {
            add(R.id.tap_container, ChatAssembly().chatActivity(user.id))
            commit()
        }
    }
}