package com.belov.agregator.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.R
import com.belov.agregator.utilities.Friend

class FriendsAdapter(var friendsList: ArrayList<Friend>) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendName = view.findViewById<TextView>(R.id.friend_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friend, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.friendName.text = friendsList[position].username

    }

    override fun getItemCount(): Int {
        return friendsList.size
    }
}