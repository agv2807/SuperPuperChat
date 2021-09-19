package com.example.superpuperchat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment(private val viewModel: ChatViewModel): Fragment() {

    private var chatRv: RecyclerView? = null
    private var messageField: EditText? = null
    private var sendIcon: ImageView? = null
    private var userPhoto: CircleImageView? = null
    private var userName: TextView? = null
    private var backIc: ImageView? = null

    private val adapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_chat, container, false)

        setupViews(view)

        chatRv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        chatRv?.adapter = adapter

        viewModel.items.observe(requireActivity(), {
            adapter.setData(it)
            val index = viewModel.indexToScroll
            if (index > 0) {
                chatRv?.smoothScrollToPosition(index)
            }
        })

        viewModel.friendUser.observe(requireActivity(), {
            setupFriendInfo(it)
        })

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
            val textMessage = messageField?.text.toString()
            if (textMessage.replace("\n", "").replace(" ", "").isEmpty()) {
                Toast.makeText(context, "Введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendMessage(textMessage)
                messageField?.setText("")
                val index = viewModel.indexToScroll
                if (index > 0) {
                    chatRv?.smoothScrollToPosition(index)
                }
            }
        }

        backIc?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this@ChatFragment).commit()
        }
    }

    private fun setupFriendInfo(user: User) {
        userName?.text = user.name
        Picasso.with(context)
            .load(user.uri)
            .placeholder(R.drawable.man)
            .into(userPhoto)
    }

}