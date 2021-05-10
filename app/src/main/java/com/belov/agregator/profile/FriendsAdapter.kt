package com.belov.agregator.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.utilities.Friendship
import kotlin.coroutines.coroutineContext

class FriendsAdapter(var friendsList: ArrayList<Friendship>, var currentUser: String, var context: Context, val app: App) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendName = view.findViewById<TextView>(R.id.friend_name)
        val icon = view.findViewById<ImageView>(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friend, parent, false))
        holder.itemView.setOnClickListener {
            if (friendsList[holder.bindingAdapterPosition].sUsername == currentUser) {
                val intent = Intent(context, FriendProfile::class.java)
                intent.putExtra("id", friendsList[holder.bindingAdapterPosition].rId)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, FriendProfile::class.java)
                intent.putExtra("id", friendsList[holder.bindingAdapterPosition].sId)
                context.startActivity(intent)
            }
         }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (friendsList[position].status) {
            holder.icon.setImageResource(R.drawable.outline_check_24)
            holder.icon.setOnClickListener {
                Toast.makeText(context, "Теперь вы друзья", Toast.LENGTH_SHORT).show()
            }
            holder.icon.setColorFilter(Color.GREEN)
        } else if (!friendsList[position].status && friendsList[position].rUsername == currentUser) {
            holder.icon.setImageResource(R.drawable.outline_thumb_up_24)
            holder.icon.setOnClickListener {
                val friendship = friendsList[position]
                app.databaseManager.acceptFriendship(friendship.sId, friendship.rId)
                holder.icon.setImageResource(R.drawable.outline_check_24)
                holder.icon.setColorFilter(Color.GREEN)
                Toast.makeText(context, "Вы приняли заявку", Toast.LENGTH_SHORT).show()
            }
        } else if (!friendsList[position].status && friendsList[position].sUsername == currentUser) {
            holder.icon.setImageResource(R.drawable.outline_help_outline_24)
            holder.icon.setColorFilter(Color.YELLOW)
            holder.icon.setOnClickListener {
                Toast.makeText(context, "Вы отправили заявку", Toast.LENGTH_SHORT).show()
            }
        }

        if (friendsList[position].rUsername == currentUser) {
            holder.friendName.text = friendsList[position].sUsername
        } else {
            holder.friendName.text = friendsList[position].rUsername
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }


}