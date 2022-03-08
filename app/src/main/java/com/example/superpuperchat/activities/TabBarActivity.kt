package com.example.superpuperchat.activities

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
import com.example.superpuperchat.chat_list_screen.ChatsListFragment
import com.example.superpuperchat.chat_screen.ChatFragment
import com.example.superpuperchat.profile_screen.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TabBarActivity: AppCompatActivity() {

    private val chatsFragment = ChatsListFragment()
    private val profileFragment = ProfileFragment()

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

    fun routeToUserChat(id: String) {
        val chatFragment = ChatFragment(id)

        bottomNavigation?.visibility = View.GONE
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, chatFragment)
            commit()
        }
    }
}