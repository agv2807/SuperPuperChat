package com.example.superpuperchat.tab_bar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.superpuperchat.R
import com.example.superpuperchat.sign.SignActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TabBarFragment : Fragment() {

    private val signActivity: SignActivity
        get() = activity as SignActivity

    private var bottomNavMenu: BottomNavigationView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tab_bar, container, false)

        setupViews(view)

        return view
    }

    private fun setupViews(view: View) {
        bottomNavMenu = view.findViewById(R.id.bottom_navigation)

        bottomNavMenu?.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.ic_chats -> signActivity.routeToChats()
                R.id.ic_profile -> signActivity.routeToProfile()
            }
            true
        }
    }
}