package com.belov.agregator.profile

import android.app.AlertDialog
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

        var isSteamKeyChecked = false
        var isGithubKeyChecked = false

        var steamKey = ""
        var steamId = ""
        var githubKey = ""
        val keys = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString(currentUser, "")
        if (keys?.length != 0) {
            val keysList = keys?.split(";")
            steamKey = keysList!![0]
            isSteamKeyChecked = true
            steamId = keysList[1]
            githubKey = keysList[2]
            isGithubKeyChecked = true

        }
        steamKeyField.setText(steamKey)
        steamIdField.setText(steamId)
        githubKeyField.setText(githubKey)


        //Packing keys in order STEAM_KEY;STEAM_ID;GITHUB_KEY
        saveKeysButton.setOnClickListener {
            if (steamKeyField.text?.length  == 32 || steamKeyField.text?.length == 0) {
                if (steamIdField.text?.length == 17 || steamIdField.text?.length == 0) {
                    if (githubKeyField.text?.length == 40 || githubKeyField.text?.length == 0) {
                        val app = (activity as ProfileBase).app
                        var isSteamKeyValid = false
                        var isGithubKeyValid = false
                        var wasAnythingChanged = false

                        if (!isSteamKeyChecked && steamKeyField.text!!.isNotEmpty()) {
                            isSteamKeyValid = app.steamController.checkKey(steamKeyField.text.toString())
                            wasAnythingChanged = true
                        }

                        if (!isGithubKeyChecked && githubKeyField.text!!.isNotEmpty()) {
                            isGithubKeyValid = app.githubUserController.checkKey(githubKeyField.text.toString())
                            wasAnythingChanged = true
                        }

                        if ((isSteamKeyChecked || isGithubKeyChecked) && (steamKeyField.text!!.isEmpty() || githubKeyField.text!!.isEmpty())) {
                            wasAnythingChanged = true
                        }

                        var warningString = ""
                        if (!isSteamKeyValid && steamKeyField.text!!.isNotEmpty() && !isSteamKeyChecked) {
                            warningString = "Указанные ключи для Steam некорректны."
                        } else if (!isGithubKeyValid && githubKeyField.text!!.isNotEmpty() && !isGithubKeyChecked) {
                            warningString = "Указанные ключи для Github некорректны."
                        } else if (!isGithubKeyValid && !isSteamKeyValid && githubKeyField.text!!.isNotEmpty() && steamKeyField.text!!.isNotEmpty() && !isSteamKeyChecked && !isGithubKeyChecked) {
                            warningString = "Указанные ключи для Steam и Github некорректны."
                        }

                        if (warningString.isNotEmpty()) {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage(warningString).setPositiveButton("Понятно", null).show()
                        } else if (wasAnythingChanged) {
                            val keyString = "${steamKeyField.text.toString()};${steamIdField.text.toString()};${githubKeyField.text.toString()};"
                            activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.edit {
                                putString(currentUser, keyString)
                                apply()
                            }

                            if (steamKeyField.text!!.isEmpty()) {
                                app.steamController.apiKey = ""
                                app.steamController.isKeySet = false
                            } else {
                                app.steamController.apiKey = steamKeyField.text.toString()
                                app.steamController.isKeySet = true
                            }
                            if (githubKeyField.text!!.isEmpty()) {
                                app.githubUserController.githubKey = ""
                                app.githubController.githubKey = ""
                                app.githubUserController.isKeySet = false
                                app.githubController.isKeySet = false
                            } else {
                                app.githubUserController.githubKey = githubKeyField.text.toString()
                                app.githubUserController.isKeySet = true

                                app.githubController.githubKey = githubKeyField.text.toString()
                                app.githubController.isKeySet = true
                            }


                            Toast.makeText(context, "Данные успешно сохранены", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Вы не внесли изменений", Toast.LENGTH_LONG).show()
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