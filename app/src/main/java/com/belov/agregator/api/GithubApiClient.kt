package com.belov.agregator.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiClient {

    // Parameter header MUST contain 'token ***'
    @GET("/user/repos?type=public")
    fun getRepos(@Header("Authorization") token: String): Call<JsonArray>

    @GET("/gists")
    fun getGists(@Header("Authorization") token: String) : Call<JsonArray>

    @GET("/repos/{owner}/{repo}/pulls")
    fun getPulls(@Header("Authorization") token: String, @Path("owner") owner: String, @Path("repo") repo: String): Call<JsonArray>

    @GET("/user")
    fun getUser(@Header("Authorization") token: String) : Call<JsonObject>
}