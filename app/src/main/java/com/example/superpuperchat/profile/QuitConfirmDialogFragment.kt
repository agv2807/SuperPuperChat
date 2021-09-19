package com.example.superpuperchat.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.superpuperchat.sign.SignActivity

class QuitConfirmDialogFragment: DialogFragment() {

    private val signActivity: SignActivity
        get() = activity as SignActivity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setCancelable(true)
                .setPositiveButton("Выйти") { dialog, id ->
                        signActivity.quitFromAcc()
                }
                .setNegativeButton("Отмена") { dialog, id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}