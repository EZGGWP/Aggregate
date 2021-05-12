package com.belov.agregator.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.utilities.Achievement
import com.belov.agregator.utilities.NewListener

class AchievementList() : Fragment() {
    lateinit var list: List<Achievement>
    lateinit var listener: SwipeRefreshLayout.OnRefreshListener
    lateinit var currentUser: String
    lateinit var refresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*currentUser = arguments?.getString("user")!!
        list = (arguments?.getParcelableArrayList<Achievement>("list")?.toList())!!
        listener = arguments?.getSerializable("listener") as NewListener*/

        currentUser = arguments?.getString("user")!!
        list = (arguments?.getParcelableArrayList<Achievement>("list")?.toList())!!
        //listener = arguments?.getSerializable("listener") as NewListener
        listener = (activity as ProfileBase).app.listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)



        val layout = inflater.inflate(R.layout.ach_list_layout, container, false)
        val recycler = layout.findViewById<RecyclerView>(R.id.ach_recycler)
        refresher = layout.findViewById(R.id.ach_list_refresher)
        if (refresher.isRefreshing) {
            refresher.isRefreshing = false
        }

        //refresher.setOnRefreshListener(viewModel.listener)
        refresher.setOnRefreshListener(listener)
        //recycler.adapter = AchievementAdapter(viewModel.achievementList)
        recycler.adapter = AchievementAdapter(list, requireActivity().application as App)
        recycler.layoutManager = LinearLayoutManager(context)
        return layout
    }

}