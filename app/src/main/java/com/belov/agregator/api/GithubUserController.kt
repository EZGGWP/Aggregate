package com.belov.agregator.api

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.belov.agregator.R
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubUserController(var githubKey: String) : Callback<JsonObject> {
    var isKeySet = false;
    lateinit var currentGithubUser: String
    var github: GithubApiClient

    var initializedFor = ""

    init {

        if (githubKey.isNotEmpty()) {
            isKeySet = true;
        }

        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com").addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
        ).build()
        github = retrofit.create(GithubApiClient::class.java)
    }

    fun getUser() : String? {
        if (isKeySet) {
            runBlocking {
                val kek = GlobalScope.launch {
                    val call = github.getUser("token $githubKey")
                    val res = call.execute()
                    currentGithubUser = res.body().get("login").asString
                }
                kek.join()
            }
            return currentGithubUser
        } else return null
    }

    fun checkKey(key: String) : Boolean {
        var isKeyValid = false
        runBlocking {
            val kek = GlobalScope.launch {
                val call = github.getUser("token $key")
                val res = call.execute()
                if (res.code() == 200) {
                    isKeyValid = true
                }
            }
            kek.join()
        }
        return isKeyValid
    }

    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
        /*if (response != null) {
            currentGithubUser = response.body().get("login").asString
        }*/
    }

    override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
        Log.d("______________________________", t?.message)
    }

    fun checkUser() : Boolean {
        return ::currentGithubUser.isInitialized
    }

}