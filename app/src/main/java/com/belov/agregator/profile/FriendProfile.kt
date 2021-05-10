package com.belov.agregator.profile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.utilities.User
import com.google.gson.Gson
import com.google.gson.JsonObject

class FriendProfile: Activity() {

    var userId = -1
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent.getIntExtra("id", -1)
        user = (application as App).databaseManager.getUserById(userId)
        setContentView(R.layout.friend_profile_layout)

        val nameView = findViewById<TextView>(R.id.name)
        val achView = findViewById<TextView>(R.id.complete_achs)
        val regView = findViewById<TextView>(R.id.registered)

        nameView.text = "Профиль пользователя ${user.username}"
        regView.text = "Зарегистрирован ${user.regDate}"

        val achJson = Gson().fromJson(user.json, JsonObject::class.java)
        val completeAchs = arrayListOf<Int>()
        val steamArray = achJson.getAsJsonObject("achievements").getAsJsonArray("Steam")
        for (item in steamArray) {
            completeAchs.add(item.toString().toInt())
        }
        val githubArray = achJson.getAsJsonObject("achievements").getAsJsonArray("GitHub")
        for (item in githubArray) {
            completeAchs.add(item.toString().toInt())
        }
        val spotifyArray = achJson.getAsJsonObject("achievements").getAsJsonArray("Spotify")
        for (item in spotifyArray) {
            completeAchs.add(item.toString().toInt())
        }

        var achievementText = "Полученные достижения: \n"
        if (completeAchs.size == 0) {
            achievementText = "У пользователя пока нет достижений"
        } else {
            for (item in completeAchs) {
                val name = (application as App).achievementList.find {
                    it.id == item
                }!!.name
                achievementText = achievementText.plus(name).plus(System.getProperty("line.separator"))
            }
        }

        achView.text = achievementText

    }


}