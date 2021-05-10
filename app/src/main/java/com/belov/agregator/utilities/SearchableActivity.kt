package com.belov.agregator.utilities

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.R

class SearchableActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        handleIntent(intent)
        setContentView(R.layout.friend_search_dialog_layout)
        setFinishOnTouchOutside(false)
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        handleIntent(intent!!)

        super.onNewIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Toast.makeText(this, "Вы ищете $query", Toast.LENGTH_LONG).show()
        }
    }


}