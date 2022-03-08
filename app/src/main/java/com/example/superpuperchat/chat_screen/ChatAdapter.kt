package com.example.superpuperchat.chat_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.models.UserMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = arrayListOf<UserMessage>()

    fun setData(messagesArray: ArrayList<UserMessage>) {
        items = messagesArray
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = if (items.elementAt(position).isMy) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0)
            ViewHolderMyMessage(LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false))
        else
            ViewHolderOtherMessage(LayoutInflater.from(parent.context).inflate(R.layout.other_message, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items.elementAt(position).isMy) {
            val holderCustom = holder as ViewHolderMyMessage
            holderCustom.bind(items.elementAt(position))
        } else {
            val holderCustom = holder as ViewHolderOtherMessage
            holderCustom.bind(items.elementAt(position))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolderMyMessage(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(message: UserMessage) {
            val messageTextView = itemView.findViewById<TextView>(R.id.my_message_text)
            val timeTextView = itemView.findViewById<TextView>(R.id.my_time)
            messageTextView.text = message.text
            timeTextView.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                Date(message.time.toLong()*1))
        }
    }

    inner class ViewHolderOtherMessage(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(message: UserMessage) {
            val messageTextView = itemView.findViewById<TextView>(R.id.other_message_text)
            val userNameTextView = itemView.findViewById<TextView>(R.id.other_message_user_name)
            val timeTextView = itemView.findViewById<TextView>(R.id.other_time)
            messageTextView.text = message.text
            userNameTextView.text = message.user.name
            timeTextView.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                Date(message.time.toLong()*1))
        }
    }
}