package com.example.superpuperchat.sign_screen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.superpuperchat.R
import com.example.superpuperchat.sign_screen.SignInFragment
import com.example.superpuperchat.sign_screen.SignUpFragment
import com.example.superpuperchat.user_space_screen.activities.TabBarActivity

class MainActivity : AppCompatActivity() {

    private val signUpFragment = SignUpFragment()
    private val signInFragment = SignInFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.container, signInFragment)
            commit()
        }
    }

    fun routeChat() {
        val intent = Intent(this, TabBarActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun routeToSignUp() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, signUpFragment)
            commit()
        }
    }

    fun routeToSignIn() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, signInFragment)
            commit()
        }
    }
}