package com.belov.agregator.profile

import android.net.sip.SipSession
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.belov.agregator.R
import com.belov.agregator.utilities.Achievement
import com.belov.agregator.utilities.NewListener
import com.belov.agregator.utilities.ProfileViewModel

class AchievementList() : Fragment() {
    lateinit var list: List<Achievement>
    lateinit var listener: SwipeRefreshLayout.OnRefreshListener
    lateinit var viewModel: ProfileViewModel
    lateinit var currentUser: String
    lateinit var refresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        /*currentUser = arguments?.getString("user")!!
        list = (arguments?.getParcelableArrayList<Achievement>("list")?.toList())!!
        listener = arguments?.getSerializable("listener") as NewListener*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)

        currentUser = arguments?.getString("user")!!
        list = (arguments?.getParcelableArrayList<Achievement>("list")?.toList())!!
        listener = arguments?.getSerializable("listener") as NewListener

        val layout = inflater.inflate(R.layout.ach_list_layout, container, false)
        val recycler = layout.findViewById<RecyclerView>(R.id.ach_recycler)
        refresher = layout.findViewById(R.id.ach_list_refresher)
        if (refresher.isRefreshing) {
            refresher.isRefreshing = false
        }

        //refresher.setOnRefreshListener(viewModel.listener)
        refresher.setOnRefreshListener(listener)
        //recycler.adapter = AchievementAdapter(viewModel.achievementList)
        recycler.adapter = AchievementAdapter(list)
        recycler.layoutManager = LinearLayoutManager(context)
        return layout
    }

}