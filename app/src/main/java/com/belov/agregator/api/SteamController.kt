package com.belov.agregator.api

import android.content.Context
import android.content.res.Resources
import android.os.AsyncTask
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.belov.agregator.R
import com.belov.agregator.storage.SteamDataStorage
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.create
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class SteamController(var apiKey: String, var steamId: String, val storage: SteamDataStorage): Callback<JsonObject> {
    var isKeySet = false

    var initializedFor = ""

    var steam:  SteamApiClient
    lateinit var gamesArray: JsonArray
    lateinit var currentGame: String
    var gamesMap: MutableMap<String, Int> = mutableMapOf()
    var completedReqs: Int by Delegates.observable(0) { _: KProperty<*>, i: Int, i1: Int ->
        onReqsComplete?.invoke(i, i1)
    }
    var onReqsComplete: ((Int, Int) -> Unit)? = null

    var completedGameReqs: Int by Delegates.observable(0) {_, i, i1 ->
        onGamesComplete?.invoke(i, i1)
    }
    var onGamesComplete: ((Int, Int) -> Unit)? = null



    init {

        if (apiKey.isNotEmpty()) {
            isKeySet = true;
        }

        val retrofit = Retrofit.Builder().baseUrl("https://api.steampowered.com").addConverterFactory(
            create(GsonBuilder().create())
        ).build()
        steam = retrofit.create(SteamApiClient::class.java)
    }

    fun getFriends() {
        if (isKeySet) {
            val call = steam.getFriends(apiKey, steamId)
            call.enqueue(this)
        }
    }

    private fun getAchievements() {
        if (isKeySet) {
            val call = steam.getAchievements(currentGame, apiKey, steamId)
            call.enqueue(this)
        }
    }

    fun getGames() {
        if (isKeySet) {
            val call = steam.getGames(apiKey, steamId)
            call.enqueue(this)
        }
    }

    fun checkKey(key: String) : Boolean {
        var isKeyValid = false
        runBlocking {
            val kek = GlobalScope.launch {
                val call = steam.checkKey(key)
                val res = call.execute()
                if (res.code() == 200) {
                    isKeyValid = true
                }
            }
            kek.join()
        }
        return isKeyValid
    }

    fun countAllAchievements() {
        GlobalScope.launch {
            storage.totalAchievements = 0
            for (item in gamesArray) {
                currentGame = item.asJsonObject.get("appid").asString
                gamesMap[currentGame] = 0
                getAchievements()

            }
        }
        completedReqs++;
    }

    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
        if (response != null && response.isSuccessful) {
            //Log.d("RESPONSE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", response.body().toString())
            when {
                response.body().has("friendslist") -> {
                    val body = response.body().get("friendslist").asJsonObject
                    val array = body.get("friends").asJsonArray
                    storage.friendsCount = array.size()
                    completedReqs++;
                    //Log.d("DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", array.size().toString())
                }
                response.body().has("response") -> {
                    val body = response.body().get("response").asJsonObject
                    val games = body.get("game_count").asInt
                    gamesArray = body.get("games").asJsonArray
                    storage.gamesCount = games
                    completedReqs++;
                    //Log.d("DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", games.toString())
                    countAllAchievements()

                }
                response.body().has("playerstats") -> {

                    // Неизвестно, нужно ли оно, раз мы синхроним
                    val body = response.body().get("playerstats").asJsonObject
                    if (body.has("achievements")) {
                        val achievements = body.get("achievements").asJsonArray
                        var counter = 0
                        for (item in achievements) {
                            if (item.asJsonObject.get("achieved").asInt == 1) {
                                counter++
                                storage.totalAchievements++;
                            }
                        }
                        gamesMap[currentGame] = counter
                        //Log.d("DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", achievements.size().toString())
                    } else {
                        gamesMap[currentGame] = 0
                    }
                    completedGameReqs++;

                }
            }
        } else {
            completedGameReqs++;
        }
    }

    override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {

        Log.d("DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", t?.message!!)
    }

    fun clearData() {
        completedGameReqs = 0
        completedReqs = 0
        gamesMap.clear()
        storage.clearData()
    }

}