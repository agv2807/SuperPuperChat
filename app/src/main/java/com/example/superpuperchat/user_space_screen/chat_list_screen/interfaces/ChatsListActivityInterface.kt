package com.example.superpuperchat.user_space_screen.chat_list_screen.interfaces

import android.content.Context
import com.example.superpuperchat.models.User

interface ChatsListActivityInterface {
    var localContext: Context?
    fun routeToUser(user: User)
}