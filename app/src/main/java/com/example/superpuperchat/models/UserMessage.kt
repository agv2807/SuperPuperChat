package com.example.superpuperchat.models

data class UserMessage(val text: String = "", val user: User = User(), var isMy: Boolean = false, val time: String = "")

