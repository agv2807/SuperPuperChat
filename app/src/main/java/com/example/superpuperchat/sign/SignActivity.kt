package com.example.superpuperchat.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.superpuperchat.R
import com.example.superpuperchat.chat_list.ChatsListFragment
import com.example.superpuperchat.profile.ProfileFragment
import com.example.superpuperchat.sign.sign_in.SignInFragment
import com.example.superpuperchat.sign.sign_up.SignUpFragment
import com.example.superpuperchat.tab_bar.TabBarFragment

class SignActivity : AppCompatActivity() {

    private val signUpFragment = SignUpFragment()
    private val signInFragment = SignInFragment()
    private val tabBarFragment = TabBarFragment()
    private val chatsListFragment = ChatsListFragment()
    private val chatsFragment = ChatsListFragment()
    private val profileFragment = ProfileFragment()

    private var bottomNavigation: FragmentContainerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_nav_fragment)

        addSupportFragmentManager(R.id.container, signInFragment)
    }

    private fun addSupportFragmentManager(container: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            add(container, fragment)
            commit()
        }
    }

    private fun replaceSupportFragmentManager(container: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(container, fragment)
            commit()
        }
    }

    fun routeChat() {
        addSupportFragmentManager(R.id.tab_bar_cont, tabBarFragment)
        replaceSupportFragmentManager(R.id.container, chatsListFragment)
    }

    fun routeToSignUp() {
        addSupportFragmentManager(R.id.container, signUpFragment)
    }

    fun routeToSignIn() {
        addSupportFragmentManager(R.id.container, signInFragment)
    }

    fun routeToProfile() {
        replaceSupportFragmentManager(R.id.container, profileFragment)
    }

    fun routeToChats() {
        replaceSupportFragmentManager(R.id.container, chatsFragment)
    }

    fun quitFromAcc() {
        replaceSupportFragmentManager(R.id.container, signInFragment)
        supportFragmentManager.beginTransaction().apply {
            remove(tabBarFragment)
            commit()
        }
    }
}