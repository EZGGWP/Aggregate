package com.belov.agregator.utilities

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.belov.agregator.R

class AddFriendDialog: DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.add_friend_dialog, container)
        val metrics = resources.displayMetrics
        this.dialog?.window?.setLayout((metrics.widthPixels * 6)/7, (metrics.heightPixels * 4)/5)
        return layout
    }
}