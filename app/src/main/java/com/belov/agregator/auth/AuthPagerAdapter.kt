package com.belov.agregator.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.belov.agregator.App
import com.belov.agregator.database.DatabaseManager

class AuthPagerAdapter(private val activity: FragmentActivity): FragmentStateAdapter(activity) {

    lateinit var app: App
    lateinit var db: DatabaseManager

    init {

    }

    class PageHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val frag = AuthFragment()
                //frag.parent = activity
                //frag.db = db
                var bundle = Bundle()
                bundle.putBoolean("regMode", false)
                frag.arguments = bundle
                frag
            }
            1 -> {
                val frag = AuthFragment()
                //frag.parent = activity
                //frag.db = db
                var bundle = Bundle()
                bundle.putBoolean("regMode", true)
                frag.arguments = bundle
                frag
            }
            else -> Fragment()
        }
    }

    fun die() {

    }


}
