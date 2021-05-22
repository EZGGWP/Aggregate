package com.belov.agregator

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.res.Configuration
import com.belov.agregator.api.GithubController
import com.belov.agregator.api.GithubUserController
import com.belov.agregator.api.SpotifyController
import com.belov.agregator.api.SteamController
import com.belov.agregator.database.DatabaseManager
import com.belov.agregator.utilities.Achievement
import com.belov.agregator.utilities.NewBoolListener
import com.belov.agregator.utilities.NewListener

class App() : Application() {

    var selectedPage = "Профиль"
    var achievementList = arrayListOf<Achievement>()

    lateinit var githubUserController: GithubUserController
    lateinit var spotifyController: SpotifyController
    lateinit var githubController: GithubController
    lateinit var steamController: SteamController
    lateinit var checkDialog: Dialog
    lateinit var mainActivity: MainActivity
    lateinit var listener: NewListener

    lateinit var databaseManager: DatabaseManager

    var isGithubUserControllerInitialized: Boolean
        get() {
            return this::githubUserController.isInitialized
        }
        set(_) {}

    var isGithubControllerInitialized: Boolean
        get() {
            return this::githubController.isInitialized
        }
        set(_) {}

    var isSteamControllerInitialized: Boolean
        get() {
            return this::steamController.isInitialized
        }
        set(_) {}

    var isSpotifyControllerInitialized: Boolean
        get() {
            return this::spotifyController.isInitialized
        }
        set(_) {}

    var isMissingKeysWarningShown: Boolean = false


    override fun onCreate() {
        super.onCreate()

        databaseManager = DatabaseManager(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    fun isConnectInitialized(): Boolean {
        return databaseManager.isConnectInitialized()
    }

    fun isMainActivityInitialized() : Boolean {
        return this::mainActivity.isInitialized
    }

    fun isCheckDialogInitialized(): Boolean {
        return this::checkDialog.isInitialized
    }
}