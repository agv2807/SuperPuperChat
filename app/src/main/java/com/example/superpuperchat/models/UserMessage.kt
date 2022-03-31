package com.example.superpuperchat.models

data class UserMessage(
    val text: String = "",
    val user: User = User(),
    var isMy: Boolean = true,
    val time: String = "",
    var isRead: Boolean = false)
