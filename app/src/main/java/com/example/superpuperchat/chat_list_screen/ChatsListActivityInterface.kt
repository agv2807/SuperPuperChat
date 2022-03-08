package com.example.superpuperchat.chat_list_screen

import android.content.Context
import com.example.superpuperchat.models.User

interface ChatsListActivityInterface {
    var localContext: Context?
    fun routeToUser(user: User)
}