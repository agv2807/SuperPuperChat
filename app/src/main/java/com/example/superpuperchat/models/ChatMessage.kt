package com.example.superpuperchat.models

data class ChatMessage(
    val user: User,
    var message: UserMessage? = UserMessage(),
    var countNonRead: Int = 0)
