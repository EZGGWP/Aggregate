package com.belov.agregator.profil

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.R
import com.google.android.material.tabs.TabLayout

class Profile: Fragment() {

    lateinit var currentUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUser = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString("authedUser", "")!!


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.profile_layout, container, false)
        layout.findViewById<TextView>(R.id.profile_name).text = "Здравствуйте, $currentUser!"
        layout.findViewById<Button>(R.id.log_out_btn).setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Вы точно хотите выйти?")
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.edit()?.putString("authedUser", "")?.apply()
                        activity?.finish()
                    }
                    .setNegativeButton("Нет", null)
                    .show()
        }


        return layout
    }
}