package com.belov.agregator.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiClient {

    @GET("me/tracks")
    fun getLikedTracks(@Header("Authorization") token: String): Call<JsonObject>

    @GET("me/following")
    fun getFollowedArtists(@Header("Authorization") token: String, @Query("type") type: String): Call<JsonObject>

    @GET("me/playlists")
    fun getPlaylists(@Header("Authorization") token: String): Call<JsonObject>
}