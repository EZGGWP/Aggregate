package com.belov.agregator.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import com.belov.agregator.R
import com.google.android.material.textfield.TextInputEditText

class ProfileKeys : Fragment() {
    lateinit var currentUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString("authedUser", "")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.profile_keys_layout, container, false)
        val steamKeyField = layout.findViewById<TextInputEditText>(R.id.steam_key_field)
        val steamIdField = layout.findViewById<TextInputEditText>(R.id.steam_id_field)
        val githubKeyField = layout.findViewById<TextInputEditText>(R.id.github_key_field)
        val saveKeysButton = layout.findViewById<Button>(R.id.save_keys_button)

        var steamKey = ""
        var steamId = ""
        var githubKey = ""
        val keys = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString(currentUser, "")
        if (keys?.length != 0) {
            val keysList = keys?.split(";")
            steamKey = keysList!![0]
            steamId = keysList[1]
            githubKey = keysList[2]
        }
        steamKeyField.setText(steamKey)
        steamIdField.setText(steamId)
        githubKeyField.setText(githubKey)


        //Packing keys in order STEAM_KEY;STEAM_ID;GITHUB_KEY
        saveKeysButton.setOnClickListener {
            if (steamKeyField.text?.length  == 32 || steamKeyField.text?.length == 0) {
                if (steamIdField.text?.length == 17 || steamIdField.text?.length == 0) {
                    if (githubKeyField.text?.length == 40 || githubKeyField.text?.length == 0) {
                        val keyString = "${steamKeyField.text.toString()};${steamIdField.text.toString()};${githubKeyField.text.toString()};"
                        activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.edit {
                            putString(currentUser, keyString)
                            apply()
                        }

                    } else {
                        Toast.makeText(context, "Ключ GitHub должен быть 40 символов в длину", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Укажите корректный SteamID64", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Ключ Steam должен быть 32 символа в длину", Toast.LENGTH_LONG).show()
            }
        }


        return layout
    }
}