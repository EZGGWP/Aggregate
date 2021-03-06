package com.belov.agregator.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import com.belov.agregator.App
import com.belov.agregator.MainActivity
import com.belov.agregator.R
import com.belov.agregator.api.*
import com.belov.agregator.profil.Profile
import com.belov.agregator.storage.GithubDataStorage
import com.belov.agregator.storage.SpotifyDataStorage
import com.belov.agregator.storage.SteamDataStorage
import com.belov.agregator.utilities.Achievement
import com.belov.agregator.utilities.NewListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.profile_base_layout.*
import kotlin.system.exitProcess

class ProfileBase: FragmentActivity() {
    private lateinit var currentUser: String
    private val CLIENT_ID = "c78eb210e9e447c1bbb7db66886e372e"
    private val REDIRECT_URI = "com.belov.agregator://callback"
    private val REQUEST_CODE = 30
    private val SCOPES = "user-follow-read,playlist-read-private,playlist-read-collaborative,user-library-read"
    private lateinit var  SPOTIFY_ACCESS_TOKEN: String

    lateinit var app: App

    private val steamStorage = SteamDataStorage()
    private val spotifyStorage = SpotifyDataStorage()
    private val githubStorage = GithubDataStorage()


    var isGithubReady = false
    var isSteamReady = false
    var isSpotifyReady = false
    var isSteamGamesReady = false
    var isGithubReposReady = false

    var profile = Profile()
    var achList = AchievementList()
    var profileKeys = ProfileKeys()
    var friends = Friends()

    var steamKey = ""
    var steamId = ""
    var githubKey = ""

    lateinit var noKeysDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = (applicationContext as App)

        setContentView(R.layout.profile_base_layout)

        currentUser = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE).getString("authedUser", "")!!

        app.databaseManager.setCurrentUserName(currentUser)
        app.databaseManager.getUserId()
        app.databaseManager.getUserAchievementsJson()



        val keys = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString(
            currentUser,
            ""
        )
        if (keys?.length != 0) {
            val keysList = keys?.split(";")
            steamKey = keysList!![0]
            steamId = keysList[1]
            githubKey = keysList[2]
        }

        if (steamKey.isEmpty() && steamId.isEmpty() && githubKey.isEmpty() && !app.isMissingKeysWarningShown) {
            val builder = AlertDialog.Builder(this)
            noKeysDialog = builder.setMessage("???? ???? ???????????????? ???????????? Steam ?? GitHub. ?????????????????? ???????????? ?? ???????? ???????????????? ?????????? ????????????????????").setPositiveButton("??????????????") { dialog: DialogInterface, _: Int ->
                app.isMissingKeysWarningShown = true
                dialog.cancel() }.create()
            noKeysDialog.show()
        }
        if (!app.isGithubUserControllerInitialized && !app.isGithubControllerInitialized) {
            setGithubUserController()
            setGithubController()
        } else {
            if (app.githubUserController.initializedFor != currentUser) {
                setGithubUserController()
            }
            if (app.githubController.initializedFor != currentUser) {
                setGithubController()
            }
        }

        if (!app.isSteamControllerInitialized) {
            setSteamController()
        } else {
            if (app.steamController.initializedFor != currentUser) {
                setSteamController()
            }
        }

        bottom_navigation.selectedItemId = when (app.selectedPage) {
            "??????????????" -> R.id.prof_nav_item
            "????????????????????" -> R.id.ach_nav_item
            "??????????" -> R.id.keys_nav_item
            "????????????" -> R.id.friends_nav_item
            else ->
                R.id.prof_nav_item
        }

        initUi()



        val bundle = Bundle()
        val bundle2 = Bundle()
        bundle.putString("user", currentUser)
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        app.listener = refreshListener
        bundle2.putParcelableArrayList("list", ArrayList<Achievement>(app.achievementList))

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle




    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        app = (applicationContext as App)
    }

    fun setGithubController() {
        if (app.githubUserController.getUser() != null && app.githubUserController.checkUser()) {
            app.githubController = GithubController(
                app.githubUserController.currentGithubUser,
                githubStorage,
                githubKey
            )
            app.githubController.initializedFor = currentUser
        } else {
            app.githubController = GithubController("", githubStorage, githubKey)
            app.githubController.initializedFor = currentUser
        }
    }

    fun setGithubUserController() {
        app.githubUserController = GithubUserController(githubKey)
        app.githubUserController.initializedFor = currentUser
    }

    fun setSteamController() {
        app.steamController = SteamController(steamKey, steamId, steamStorage)
        app.steamController.initializedFor = currentUser
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        Log.d("", "")
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            when(response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d("Succ", response.accessToken)
                    SPOTIFY_ACCESS_TOKEN = response.accessToken
                    app.spotifyController = SpotifyController(SPOTIFY_ACCESS_TOKEN, spotifyStorage)
                    getApiData()
                }

                AuthenticationResponse.Type.ERROR -> {
                    Toast.makeText(applicationContext, "??????-???? ?????????? ???? ??????...", Toast.LENGTH_SHORT).show()
                    Log.d("Error", response.error)
                }

                AuthenticationResponse.Type.EMPTY -> {
                    Toast.makeText(applicationContext, "??????-???? ?????????? ???? ??????...", Toast.LENGTH_SHORT).show()
                    authenticateSpotify()
                }

                else -> {
                    Log.d("Unknown", "Unknown response")
                }
            }
        }
    }

    fun authenticateSpotify() {
        val reqBuilder = AuthenticationRequest.Builder(
            CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        reqBuilder.setScopes(arrayOf(SCOPES))
        val request = reqBuilder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    fun getSpotifyData() {
        app.spotifyController.getLikedTracks()
        app.spotifyController.getFollowedArtists()
        app.spotifyController.getPlaylists()
    }

    fun getGithubData() {
        app.githubController.getRepos()
        app.githubController.getGists()
    }

    fun getSteamData() {
        app.steamController.getFriends()
        app.steamController.getGames()
    }

    fun getApiData() {
        if (app.githubController.isKeySet) {
            app.githubController.onReqsComplete = { _, new ->
                if (new == 3) {
                    isGithubReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                }
            }

            app.githubController.onReposComplete = { _, new ->
                Log.d("REPO COUNTER DEBUG", "$new/${app.githubController.storage.repoCount}")
                if (new == app.githubController.storage.repoCount) {
                    isGithubReposReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                }
            }
        } else {
            isGithubReady = true
            isGithubReposReady = true
        }

        if (app.spotifyController.isKeySet) {
            app.spotifyController.onReqsComplete = { _, new ->
                if (new == 3) {
                    isSpotifyReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                }
            }
        } else {
            isSpotifyReady = true
        }


        if (app.steamController.isKeySet) {
            app.steamController.onReqsComplete = { _, new ->
                if (new == 3) {
                    isSteamReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                }
            }

            app.steamController.onGamesComplete = { _, new ->
                Log.d("GAME COUNTER DEBUG", "$new/${app.steamController.storage.gamesCount}")
                if (new == app.steamController.storage.gamesCount) {
                    isSteamGamesReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
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

    fun initUi() {
        if (app.achievementList.isEmpty()) {
            app.achievementList.addAll(steamStorage.createAchievements())
            app.achievementList.addAll(spotifyStorage.createAchievement())
            app.achievementList.addAll(githubStorage.createAchievements())
            app.achievementList = ArrayList(app.achievementList.sortedBy { it.id })
        }


        val bundle = Bundle()
        bundle.putString("user", currentUser)
        val bundle2 = Bundle()
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        app.listener = refreshListener
        bundle2.putParcelableArrayList("list", app.achievementList)
        profile = Profile()
        achList = AchievementList()
        profileKeys = ProfileKeys()
        friends = Friends()

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.title) {
                "????????????????????" -> {
                    app.selectedPage = "????????????????????"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, achList)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "??????????????" -> {
                    app.selectedPage = "??????????????"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, profile)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "??????????" -> {
                    app.selectedPage = "??????????"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, profileKeys)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "????????????" -> {
                    app.selectedPage = "????????????"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, friends)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

        when (app.selectedPage) {
            "????????????????????" -> {
                app.selectedPage = "????????????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, achList)
                }

            }

            "??????????????" -> {
                app.selectedPage = "??????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profile)
                }
            }

            "??????????" -> {
                app.selectedPage = "??????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profileKeys)
                }
            }

            "????????????" -> {
                app.selectedPage = "????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, friends)
                }
            }
        }
    }

    fun updateUi() {
        app.achievementList.clear()
        app.achievementList.addAll(steamStorage.createAchievements())
        app.achievementList.addAll(spotifyStorage.createAchievement())
        app.achievementList.addAll(githubStorage.createAchievements())
        app.achievementList = ArrayList(app.achievementList.sortedBy { it.id })

        val bundle = Bundle()
        bundle.putString("user", currentUser)
        val bundle2 = Bundle()
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        app.listener = refreshListener
        bundle2.putParcelableArrayList("list", app.achievementList)
        profile = Profile()
        achList = AchievementList()
        profileKeys = ProfileKeys()
        friends = Friends()

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle

        when (app.selectedPage) {
            "????????????????????" -> {
                app.selectedPage = "????????????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, achList)
                }

            }

            "??????????????" -> {
                app.selectedPage = "??????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profile)
                }
            }

            "??????????" -> {
                app.selectedPage = "??????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profileKeys)
                }
            }

            "????????????" -> {
                app.selectedPage = "????????????"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, friends)
                }
            }
        }
    }

    fun cleanAndFinish(resultCode: Int) {
        app.achievementList.clear()
        getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.edit()?.putString(
            "authedUser",
            ""
        )?.apply()
        setResult(resultCode)
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitProcess(0)

    }

    override fun onDestroy() {
        if (this::noKeysDialog.isInitialized) {
            noKeysDialog.cancel()
        }
        super.onDestroy()

    }

}