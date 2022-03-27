package com.example.superpuperchat.user_space_screen.search_friends_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superpuperchat.R
import com.example.superpuperchat.adapters.ChatsListAdapter
import com.example.superpuperchat.models.User
import com.example.superpuperchat.models.UserMessage
import com.example.superpuperchat.user_space_screen.chat_list_screen.interfaces.ChatsListActivityInterface
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SearchFriendsAdapter(var parentActivity: ChatsListActivityInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = arrayListOf<User>()

    fun setData(messagesArray: ArrayList<User>) {
        items = messagesArray
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friends_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderCustom = holder as MyViewHolder
        holderCustom.bind(items.elementAt(position))
    }

    override fun getItemCount(): Int = items.size

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            val userName = itemView.findViewById<TextView>(R.id.search_list_user_name)
            val image = itemView.findViewById<CircleImageView>(R.id.search_list_user_photo)
            val chatIc = itemView.findViewById<ImageView>(R.id.chat_ic)
            userName.text = user.name
            setPhoto(user.uri, image)

            chatIc.setOnClickListener {
                parentActivity.routeToUser(user = items.elementAt(position))
            }
        }

        private fun setPhoto(image: String?, view: ImageView) {
            Picasso.with(parentActivity.localContext)
                .load(image)
                .placeholder(R.drawable.man)
                .into(view)
        }
    }
}