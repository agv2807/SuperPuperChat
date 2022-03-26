package com.example.superpuperchat.user_space_screen.profile_screen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.superpuperchat.sign_screen.activities.MainActivity
import com.example.superpuperchat.user_space_screen.activities.TabBarActivity

class MyDialogFragment: DialogFragment() {

    private val tabBarActivity: TabBarActivity
        get() = activity as TabBarActivity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setCancelable(true)
                .setPositiveButton("Выйти") { dialog, id ->
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        tabBarActivity.finish()
                }
                .setNegativeButton("Отмена") { dialog, id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}