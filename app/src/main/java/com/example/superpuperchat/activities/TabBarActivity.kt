package com.example.superpuperchat.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.superpuperchat.R
import com.example.superpuperchat.data_classes.User
import com.example.superpuperchat.fragments.ChatsListFragment
import com.example.superpuperchat.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

interface ChatsListActivityInterface {
    var localContext: Context?
    fun routeToUser(user: User)
}

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

    private fun routeToChats() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tap_container, chatsFragment)
            commit()
        }
    }
}