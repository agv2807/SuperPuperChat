package com.example.superpuperchat.user_space_screen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.superpuperchat.R
import com.example.superpuperchat.user_space_screen.chat_list_screen.ChatsListFragment
import com.example.superpuperchat.user_space_screen.chat_screen.ChatFragment
import com.example.superpuperchat.user_space_screen.profile_screen.ProfileFragment
import com.example.superpuperchat.user_space_screen.search_friends_screen.SearchFriendsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TabBarActivity: AppCompatActivity() {

    private val chatsFragment = ChatsListFragment()
    private val profileFragment = ProfileFragment()
    private val searchFriendsFragment = SearchFriendsFragment()

    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_bar)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.tap_container, chatsFragment)
            commit()
        }
        setupViews()
    }

    private fun setupViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation?.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.ic_chats -> routeToChats()
                R.id.ic_profile -> routeToProfile()
                R.id.ic_friends -> routeToFriends()
            }
            true
        }
    }

    private fun routeToProfile() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, profileFragment)
            commit()
        }
    }

    fun routeToChats() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, chatsFragment)
            commit()
        }
        bottomNavigation?.visibility = View.VISIBLE
    }

    fun routeToFriends() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, searchFriendsFragment)
            commit()
        }
    }

    fun routeToUserChat(id: String) {
        val chatFragment = ChatFragment(id)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, chatFragment)
            addToBackStack(null)
            commit()
        }
        bottomNavigation?.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bottomNavigation?.visibility = View.VISIBLE
    }
}