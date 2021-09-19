package com.example.superpuperchat.chat

class ChatAssembly {

    fun chatActivity(friendId: String): ChatFragment {
        val viewModel = ChatViewModel(friendId)

        return ChatFragment(viewModel)
    }

}