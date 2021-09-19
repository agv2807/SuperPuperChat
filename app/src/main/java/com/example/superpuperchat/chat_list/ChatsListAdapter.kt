package com.example.superpuperchat.chat_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.squareup.picasso.Picasso

data class ChatMessage(val user: User, var message: UserMessage? = null)

class ChatsListAdapter(private var parentActivity: ChatsListActivityInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = arrayListOf<ChatMessage>()

    fun setData(messagesArray: ArrayList<ChatMessage>) {
        items = messagesArray
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderDialog(LayoutInflater.from(parent.context).inflate(R.layout.dialog_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderCustom = holder as ViewHolderDialog
        holderCustom.bind(items.elementAt(position))

        holder.itemView.setOnClickListener {
            parentActivity.routeToUser(user = items.elementAt(position).user)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolderDialog(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: ChatMessage) {
            val userName = itemView.findViewById<TextView>(R.id.item_user_name)
            val lastMessage = itemView.findViewById<TextView>(R.id.last_message)
            val photo = itemView.findViewById<ImageView>(R.id.user_photo)
            userName.text = item.user.name
            lastMessage.text = item.message?.text
            setPhoto(item.user.uri, photo)
        }
    }

    private fun setPhoto(image: String?, view: ImageView) {
        Picasso.with(parentActivity.localContext)
            .load(image)
            .placeholder(R.drawable.man)
            .into(view)
    }
}