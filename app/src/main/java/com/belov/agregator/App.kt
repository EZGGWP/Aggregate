package com.belov.agregator

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.belov.agregator.api.GithubController
import com.belov.agregator.api.GithubUserController
import com.belov.agregator.api.SpotifyController
import com.belov.agregator.api.SteamController
import com.belov.agregator.database.DatabaseManager
import com.belov.agregator.utilities.Achievement
import java.io.Serializable

class App() : Application() {

    var selectedPage = "Профиль"
    var achievementList = arrayListOf<Achievement>()

    lateinit var githubUserController: GithubUserController
    lateinit var spotifyController: SpotifyController
    lateinit var githubController: GithubController
    lateinit var steamController: SteamController

    lateinit var databaseManager: DatabaseManager

    var isGithubUserControllerInitialized: Boolean
        get() {
            return this::githubUserController.isInitialized
        }
        set(value) {}

    var isGithubControllerInitialized: Boolean
        get() {
            return this::githubController.isInitialized
        }
        set(value) {}

    var isSteamControllerInitialized: Boolean
        get() {
            return this::steamController.isInitialized
        }
        set(value) {}

    var isSpotifyControllerInitialized: Boolean
        get() {
            return this::spotifyController.isInitialized
        }
        set(value) {}

    var isMissingKeysWarningShown: Boolean = false


    override fun onCreate() {
        super.onCreate()
        Log.d("_____________________________________________", "APP IS STARTED!!!!!!!!!!!!!!!!!!!!!!!")
        /*val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("app", this)
        startActivity(intent, bundle)*/
    }



}