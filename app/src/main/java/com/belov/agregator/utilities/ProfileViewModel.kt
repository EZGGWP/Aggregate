package com.belov.agregator.utilities

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.activity.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.belov.agregator.api.GithubController
import com.belov.agregator.api.GithubUserController
import com.belov.agregator.api.SpotifyController
import com.belov.agregator.api.SteamController
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class ProfileViewModel: ViewModel() {

    private val CLIENT_ID = "c78eb210e9e447c1bbb7db66886e372e"
    private val REDIRECT_URI = "com.belov.agregator://callback"
    private val REQUEST_CODE = 30
    private val SCOPES = "user-follow-read,playlist-read-private,playlist-read-collaborative,user-library-read"

    lateinit var githubUserController: GithubUserController
    lateinit var spotifyController: SpotifyController
    lateinit var githubController: GithubController
    lateinit var steamController: SteamController

    var isGithubReady = false
    var isSteamReady = false
    var isSpotifyReady = false
    var isSteamGamesReady = false
    var isGithubReposReady = false


    var achievementList: List<Achievement> = arrayListOf()
    lateinit var listener: SwipeRefreshLayout.OnRefreshListener


    /*
    private fun authenticateSpotify() {
        val reqBuilder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        reqBuilder.setScopes(arrayOf(SCOPES))
        val request = reqBuilder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    fun getSpotifyData() {
        spotifyController.getLikedTracks()
        spotifyController.getFollowedArtists()
        spotifyController.getPlaylists()
    }

    fun getGithubData() {
        githubController.getRepos()
        githubController.getGists()
    }

    fun getSteamData() {
        steamController.getFriends()
        steamController.getGames()
    }

    fun getApiData() {
        if (githubController.isKeySet) {
            githubController.onReqsComplete = { _, new ->
                if (new == 3) {
                    isGithubReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        initUi()
                    }
                }
            }

            githubController.onReposComplete = { _, new ->
                Log.d("REPO COUNTER DEBUG____________________________", "$new/${githubController.storage.repoCount}")
                if (new == githubController.storage.repoCount) {
                    isGithubReposReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        initUi()
                    }
                    githubController.storage.repoCount = 0;
                }
            }
        } else {
            isGithubReady = true
            isGithubReposReady = true
        }

        spotifyController.onReqsComplete = { _, new ->
            if (new == 3) {
                isSpotifyReady = true
                if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                    initUi()
                }
            }
        }


        if (steamController.isKeySet) {
            steamController.onReqsComplete = { _, new ->
                if (new == 3) {
                    isSteamReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        initUi()
                    }
                }
            }

            steamController.onGamesComplete = { _, new ->
                Log.d("GAME COUNTER DEBUG____________________________", "$new/${steamController.storage.gamesCount}")
                if (new == steamController.storage.gamesCount) {
                    isSteamGamesReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        initUi()
                    }
                    steamController.storage.gamesCount = 0;
                }
            }
        } else {
            isSteamReady = true
            isSteamGamesReady = true
        }

        getGithubData()
        getSpotifyData()
        getSteamData()


    }

    */

}