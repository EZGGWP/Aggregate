package com.belov.agregator.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.R

class Friends : Fragment() {
    lateinit var currentUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = arguments?.getString("user")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.friends_layout, container, false)
        val recycler = layout.findViewById<RecyclerView>(R.id.friends_recycler)
        recycler.adapter = FriendsAdapter()
        recycler.layoutManager = LinearLayoutManager(context)

        return layout
    }
}