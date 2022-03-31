package com.example.superpuperchat.user_space_screen.search_friends_screen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
import com.example.superpuperchat.user_space_screen.activities.TabBarActivity
import com.example.superpuperchat.user_space_screen.chat_list_screen.interfaces.ChatsListActivityInterface

class SearchFriendsFragment() : Fragment(), ChatsListActivityInterface {

    private var searchFieldEditText: EditText? = null
    private var friendsListRv: RecyclerView? = null

    private val adapter = SearchFriendsAdapter(this)

    private val searchFriendsViewModel = SearchFriendsViewModel()

    override var localContext: Context?
        get() {
            return context
        }
        set(value) {}

    private val tabBarActivity: TabBarActivity
        get() = activity as TabBarActivity

    override fun routeToUser(user: User) {
        tabBarActivity.routeToUserChat(user.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_friends, container, false)

        setupViews(view)

        searchFriendsViewModel.friendsList.observe(requireActivity()) {
            adapter.setData(ArrayList(it))
        }

        friendsListRv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        friendsListRv?.adapter = adapter

        return view
    }

    private fun setupViews(view: View) {
        searchFieldEditText = view.findViewById(R.id.search_field)
        friendsListRv = view.findViewById(R.id.friends_list_rv)

        searchFieldEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchFriendsViewModel.rebootList(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}