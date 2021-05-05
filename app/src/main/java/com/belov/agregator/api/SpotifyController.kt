package com.belov.agregator.api

import android.util.Log
import com.belov.agregator.storage.SpotifyDataStorage
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class SpotifyController(val apiKey: String, val storage: SpotifyDataStorage): Callback<JsonObject> {

    var spotify: SpotifyApiClient
    var totalLikedTracks = 0
    var totalFollowedArtists = 0
    var totalPlaylists = 0
    var completedReqs: Int by Delegates.observable(0) { _: KProperty<*>, i: Int, i1: Int ->
        onReqsComplete?.invoke(i, i1)
    }
    var onReqsComplete: ((Int, Int) -> Unit)? = null

    init {
        val retrofit = Retrofit.Builder().baseUrl("https://api.spotify.com/v1/").addConverterFactory(
            GsonConverterFactory.create(GsonBuilder().create())
        ).build()
        spotify = retrofit.create(SpotifyApiClient::class.java)
    }

    fun getLikedTracks() {
        val call = spotify.getLikedTracks("Bearer $apiKey")
        call.enqueue(this)
    }

    fun getFollowedArtists() {
        val call = spotify.getFollowedArtists("Bearer $apiKey", "artist")
        call.enqueue(this)
    }

    fun getPlaylists() {
        val call = spotify.getPlaylists("Bearer $apiKey")
        call.enqueue(this)
    }

    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
        if (response?.body() != null) {
            if (response.body().has("artists")) {
                //response.body().get("artists").asJsonObject.get("href").asString.contains("following") -> {
                storage.totalFollowedArtists = response.body().get("artists").asJsonObject.get("total").asInt
                completedReqs++;
                Log.d("Followed _______________________________________", totalFollowedArtists.toString())
                //}
            } else {
                when {
                    response.body().get("href").asString.contains("tracks") -> {
                        storage.totalLikedTracks = response.body().get("total").asInt
                        completedReqs++;
                        Log.d("Liked _______________________________________", totalLikedTracks.toString()
                        )
                    }

                    response.body().get("href").asString.contains("playlists") -> {
                        storage.totalPlaylists = response.body().get("total").asInt
                        completedReqs++;
                        Log.d("Playlists _______________________________________", totalPlaylists.toString()
                        )
                    }

                }
            }

        }
    }

    override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
        TODO("Not yet implemented")
    }

    fun clearData() {
        totalLikedTracks = 0
        totalPlaylists = 0
        totalFollowedArtists = 0
        storage.clearData()
    }

}