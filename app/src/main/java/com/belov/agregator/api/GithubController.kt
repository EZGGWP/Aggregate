package com.belov.agregator.api

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.belov.agregator.R
import com.belov.agregator.storage.GithubDataStorage
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class GithubController(val currentGithubUser: String, val storage: GithubDataStorage, var githubKey: String): Callback<JsonArray> {
    var isKeySet = false;

    var github: GithubApiClient

    var initializedFor = ""

    var completedReqs: Int by Delegates.observable(0) { _, i, i1 ->
        onReqsComplete?.invoke(i, i1)
    }
    var onReqsComplete: ((Int, Int) -> Unit)? = null

    var completedReposReqs: Int by Delegates.observable(0) {_, i, i1 ->
        onReposComplete?.invoke(i, i1)
    }
    var onReposComplete: ((Int, Int) -> Unit)? = null

    var reposMap: MutableMap<String, Int> = mutableMapOf()



    init {

        if (githubKey.isNotEmpty() && currentGithubUser.isNotEmpty()) {
            isKeySet = true;
        }

        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com").addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
        ).build()
        github = retrofit.create(GithubApiClient::class.java)
    }

    fun getRepos() {
        if (isKeySet) {
            val call = github.getRepos("token $githubKey")
            call.enqueue(this)
        }
    }

    fun getGists() {
        if (isKeySet) {
            val call = github.getGists("token $githubKey")
            call.enqueue(this)
        }
    }

    fun getPulls(currentRepo: String) {
        if (isKeySet) {
            val call = github.getPulls("token $githubKey", currentGithubUser, currentRepo)
            call.enqueue(this)
        }
    }

    fun countAllPulls() {
        GlobalScope.launch {
            storage.pullCount = 0
            reposMap.forEach {
            val currentRepo = it.key.replace("\"", "")
                getPulls(currentRepo)
            }
        }
        completedReqs++;
    }


    override fun onResponse(call: Call<JsonArray>?, response: Response<JsonArray>?) {
        if (response != null) {
            if (response.body().size() != 0) {
                when {
                    // Repos
                    response.body()[0].asJsonObject.has("fork") -> {
                        storage.repoCount = response.body().size()
                        completedReqs++;
                        Log.d("___________________________________________", "Repos: ${storage.repoCount}")
                        response.body().forEach {
                            reposMap[it.asJsonObject.get("name").toString()] = 0
                        }
                        countAllPulls()

                    }
                    // Gists
                    response.body()[0].asJsonObject.has("files") -> {
                        storage.gistCount = response.body().size()
                        completedReqs++;
                        Log.d("___________________________________________", "Gists: ${storage.gistCount}")
                    }
                    // Pulls
                    response.body()[0].asJsonObject.has("state") -> {
                        storage.pullCount += response.body().size()
                        completedReposReqs++

                    }
                }
            } else {
                completedReposReqs++;
            }
        }
    }

    override fun onFailure(call: Call<JsonArray>?, t: Throwable?) {
        Log.d("_________________________________", t?.message!!)
    }

    fun clearData() {
        completedReposReqs = 0
        completedReqs = 0
        reposMap.clear()
        storage.clearData()
    }

}