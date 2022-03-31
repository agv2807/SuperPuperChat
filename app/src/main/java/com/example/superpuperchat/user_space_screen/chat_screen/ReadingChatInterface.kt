package com.example.superpuperchat.user_space_screen.chat_screen

import com.example.superpuperchat.models.UserMessage

interface ReadingChatInterface {
    fun readMessages(message: UserMessage)
}