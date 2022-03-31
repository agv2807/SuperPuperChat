package com.example.superpuperchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.user_space_screen.chat_list_screen.interfaces.ChatsListActivityInterface
import com.example.superpuperchat.models.ChatMessage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatsListAdapter(var parentActivity: ChatsListActivityInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val time = itemView.findViewById<TextView>(R.id.time)
            val countNonRead = itemView.findViewById<TextView>(R.id.count_non_read)
            userName.text = item.user.name
            lastMessage.text = item.message?.text
            setPhoto(item.user.uri, photo)
            time.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                Date(item.message?.time?.toLong()!!*1)
            )
            if (item.countNonRead != 0) {
                countNonRead.visibility = View.VISIBLE
                countNonRead.text = item.countNonRead.toString()
            } else {
                countNonRead.visibility = View.GONE
                countNonRead.text = item.countNonRead.toString()
            }
        }
    }

    private fun setPhoto(image: String?, view: ImageView) {
        Picasso.with(parentActivity.localContext)
            .load(image)
            .placeholder(R.drawable.man)
            .into(view)
    }
}