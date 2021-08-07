package com.example.superpuperchat.data_classes

data class UserMessage(val text: String = "", val user: User = User(), var isMy: Boolean = false, val time: String = "")

data class User(val id: String = "", var name: String = "", val uri: String? = null)