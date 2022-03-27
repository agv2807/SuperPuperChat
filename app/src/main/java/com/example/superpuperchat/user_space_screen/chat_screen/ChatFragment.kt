package com.example.superpuperchat.user_space_screen.chat_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.user_space_screen.activities.TabBarActivity
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment(friendId: String = "") : Fragment() {

    private var chatRv: RecyclerView? = null
    private var messageField: EditText? = null
    private var sendIcon: ImageView? = null
    private var userPhoto: CircleImageView? = null
    private var userName: TextView? = null
    private var backIc: ImageView? = null

    private val adapter = ChatAdapter()

    private val chatViewModel = ChatViewModel(friendId)

    private val tabBarActivity: TabBarActivity
        get() = activity as TabBarActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        setupViews(view)

        chatViewModel.messagesData.observe(requireActivity()) {
            adapter.setData(ArrayList(it))
            scrollToBottom(it)
        }

        chatViewModel.friendInfo.observe(requireActivity()) {
            setupFriendInfo(it)
        }

        chatRv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatRv?.adapter = adapter

        return view
    }

    private fun setupViews(view: View) {
        chatRv = view.findViewById(R.id.chat)
        messageField = view.findViewById(R.id.message_field)
        sendIcon = view.findViewById(R.id.send_icon)
        userPhoto = view.findViewById(R.id.chat_profile_photo)
        userName = view.findViewById(R.id.chat_username)
        backIc = view.findViewById(R.id.back_ic)

        sendIcon?.setOnClickListener {
            if (messageField?.text.toString().isEmpty()) {
                Toast.makeText(context, "Введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                chatViewModel.pushEndTime(messageField?.text.toString())
                messageField?.setText("")
            }
        }

        backIc?.setOnClickListener {
            tabBarActivity.routeToChats()
        }
    }

    private fun setupFriendInfo(user: User) {
        userName?.text = user.name
        Picasso.with(context)
            .load(user.uri)
            .placeholder(R.drawable.man)
            .into(userPhoto)
    }

    private fun scrollToBottom(messages: List<UserMessage>) {
        val indexToScroll = messages.size - 1
        if (indexToScroll > 0) {
            chatRv?.scrollToPosition(indexToScroll)
        }
    }
}