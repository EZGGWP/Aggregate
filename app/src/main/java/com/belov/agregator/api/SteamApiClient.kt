package com.belov.agregator.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamApiClient {

    // Key CCB89104478056EE33A85A792CF24EE3
    // Id  76561198113053343
    @GET("ISteamUser/GetFriendList/v0001/?relationship=friend")
    fun getFriends(@Query("key") key: String, @Query("steamid") id: String) : Call<JsonObject>

    @GET("IPlayerService/GetOwnedGames/v0001/?format=json")
    fun getGames(@Query("key") key: String, @Query("steamid") id: String) : Call<JsonObject>

    @GET("ISteamUserStats/GetPlayerAchievements/v0001/?")
    fun getAchievements(@Query("appId") app: String, @Query("key") key: String, @Query("steamid") id: String) : Call<JsonObject>

    @GET("ISteamUser/GetPlayerSummaries/v0002/?steamids=76561197960435530&")
    fun checkKey(@Query("key") key: String): Call<JsonObject>



}