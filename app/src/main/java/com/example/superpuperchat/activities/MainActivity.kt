package com.example.superpuperchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.superpuperchat.R
import com.example.superpuperchat.fragments.SignUpFragment
import com.example.superpuperchat.fragments.SignInFragment

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