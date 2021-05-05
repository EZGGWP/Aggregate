package com.belov.agregator.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.api.*
import com.belov.agregator.profil.Profile
import com.belov.agregator.storage.GithubDataStorage
import com.belov.agregator.storage.SpotifyDataStorage
import com.belov.agregator.storage.SteamDataStorage
import com.belov.agregator.utilities.Achievement
import com.belov.agregator.utilities.NewListener
import com.belov.agregator.utilities.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.profile_base_layout.*

class ProfileBase: FragmentActivity() {
    private lateinit var currentUser: String
    private val CLIENT_ID = "c78eb210e9e447c1bbb7db66886e372e"
    private val REDIRECT_URI = "com.belov.agregator://callback"
    private val REQUEST_CODE = 30
    private val SCOPES = "user-follow-read,playlist-read-private,playlist-read-collaborative,user-library-read"
    private lateinit var  SPOTIFY_ACCESS_TOKEN: String
    private var STEAM_ACCESS_TOKEN = "CCB89104478056EE33A85A792CF24EE3"
    private var STEAM_ID = "76561198113053343"

    lateinit var app: App

    private val steamStorage = SteamDataStorage()
    private val spotifyStorage = SpotifyDataStorage()
    private val githubStorage = GithubDataStorage()


    var isGithubReady = false
    var isSteamReady = false
    var isSpotifyReady = false
    var isSteamGamesReady = false
    var isGithubReposReady = false

    lateinit var viewModel: ProfileViewModel

    //var app.achievementList = arrayListOf<Achievement>()

    var profile = Profile()
    var achList = AchievementList()
    var profileKeys = ProfileKeys()
    var friends = Friends()

    var steamKey = ""
    var steamId = ""
    var githubKey = ""

    /*lateinit var app.githubUserController: GithubUserController
    lateinit var app.spotifyController: SpotifyController
    lateinit var app.githubController: GithubController
    lateinit var app.steamController: SteamController*/

    //TODO: Разобраться с потерей данных, система друзей


    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //app = intent.getParcelableExtra("app")
        app = (applicationContext as App)


        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        setContentView(R.layout.profile_base_layout)
        currentUser = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE).getString("authedUser", "")!!

        val keys = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString(currentUser, "")
        if (keys?.length != 0) {
            val keysList = keys?.split(";")
            steamKey = keysList!![0]
            steamId = keysList[1]
            githubKey = keysList[2]
        }

        if (steamKey.isEmpty() && steamId.isEmpty() && githubKey.isEmpty() && !app.isMissingKeysWarningShown) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Вы не добавили данные Steam и GitHub. Получение данных с этих сервисов будет недоступно")
                    .setPositiveButton("Понятно") { _: DialogInterface, _: Int ->
                        app.isMissingKeysWarningShown = true
                    }
                    .show()
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
            "Профиль" -> R.id.prof_nav_item
            "Достижения" -> R.id.ach_nav_item
            "Ключи" -> R.id.keys_nav_item
            "Друзья" -> R.id.friends_nav_item
            else ->
                R.id.prof_nav_item
        }

        initUi()




        //val refreshListener = NewListener(this)

        //viewModel.listener = refreshListener

        val bundle = Bundle()
        val bundle2 = Bundle()
        bundle.putString("user", currentUser)
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        bundle2.putSerializable("listener", refreshListener)
        bundle2.putParcelableArrayList("list", ArrayList<Achievement>(app.achievementList))

        //achList.list = achievements2
        //achList.listener = refreshListener

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle




    }

    fun setGithubController() {
        if (app.githubUserController.getUser() != null && app.githubUserController.checkUser()) {
            app.githubController = GithubController(app.githubUserController.currentGithubUser, githubStorage, githubKey)
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
                    Log.d("Succ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", response.accessToken)
                    SPOTIFY_ACCESS_TOKEN = response.accessToken
                    app.spotifyController = SpotifyController(SPOTIFY_ACCESS_TOKEN, spotifyStorage)
                    getApiData()
                }

                AuthenticationResponse.Type.ERROR -> {
                    Log.d("Error ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", response.error)
                }
                else -> {
                    Log.d("Unknown ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", "Unknown response")
                }
            }
        }
    }

    fun authenticateSpotify() {
        val reqBuilder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
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
                Log.d("REPO COUNTER DEBUG____________________________", "$new/${app.githubController.storage.repoCount}")
                if (new == app.githubController.storage.repoCount) {
                    app.githubController.storage.repoCount = 0
                    isGithubReposReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                    app.githubController.storage.repoCount = 0;
                }
            }
        } else {
            isGithubReady = true
            isGithubReposReady = true
        }

        app.spotifyController.onReqsComplete = { _, new ->
            if (new == 3) {
                isSpotifyReady = true
                if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                    updateUi()
                }
            }
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
                Log.d("GAME COUNTER DEBUG____________________________", "$new/${app.steamController.storage.gamesCount}")
                if (new == app.steamController.storage.gamesCount) {
                    app.steamController.storage.gamesCount = 0
                    isSteamGamesReady = true
                    if (isGithubReady && isSpotifyReady && isSteamReady && isSteamGamesReady && isGithubReposReady) {
                        updateUi()
                    }
                    app.steamController.storage.gamesCount = 0;
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
        //TODO: где-то теряются данные...
        if (app.achievementList.isEmpty()) {
            //app.achievementList.clear()
            app.achievementList.addAll(steamStorage.createAchievements())
            app.achievementList.addAll(spotifyStorage.createAchievement())
            app.achievementList.addAll(githubStorage.createAchievements())
            app.achievementList = ArrayList(app.achievementList.sortedBy { it.id  })
        }


        //viewModel.achievementList = app.achievementList


        val bundle = Bundle()
        bundle.putString("user", currentUser)
        val bundle2 = Bundle()
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        bundle2.putSerializable("listener", refreshListener)
        bundle2.putParcelableArrayList("list", app.achievementList)
        profile = Profile()
        achList = AchievementList()
        profileKeys = ProfileKeys()
        friends = Friends()
        //achList.list = achievements2
        //achList.listener = refreshListener

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.title) {
                "Достижения" -> {
                    app.selectedPage = "Достижения"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, achList)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "Профиль" -> {
                    app.selectedPage = "Профиль"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, profile)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "Ключи" -> {
                    app.selectedPage = "Ключи"
                    supportFragmentManager.commitNow {
                        setReorderingAllowed(true)
                        replace(R.id.profile_fragment_container, profileKeys)
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                "Друзья" -> {
                    app.selectedPage = "Друзья"
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

        /*supportFragmentManager.commitNow {
            setReorderingAllowed(true)
            replace(R.id.profile_fragment_container, achList)
        }*/

        when (app.selectedPage) {
            "Достижения" -> {
                app.selectedPage = "Достижения"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, achList)
                }

            }

            "Профиль" -> {
                app.selectedPage = "Профиль"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profile)
                }
            }

            "Ключи" -> {
                app.selectedPage = "Ключи"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profileKeys)
                }
            }

            "Друзья" -> {
                app.selectedPage = "Друзья"
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
        app.achievementList = ArrayList(app.achievementList.sortedBy { it.id  })

        val bundle = Bundle()
        bundle.putString("user", currentUser)
        val bundle2 = Bundle()
        bundle2.putString("user", currentUser)
        val refreshListener = NewListener(this)
        bundle2.putSerializable("listener", refreshListener)
        bundle2.putParcelableArrayList("list", app.achievementList)
        profile = Profile()
        achList = AchievementList()
        profileKeys = ProfileKeys()
        friends = Friends()
        //achList.list = achievements2
        //achList.listener = refreshListener

        profile.arguments = bundle
        achList.arguments = bundle2
        profileKeys.arguments = bundle
        friends.arguments = bundle

        when (app.selectedPage) {
            "Достижения" -> {
                app.selectedPage = "Достижения"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, achList)
                }

            }

            "Профиль" -> {
                app.selectedPage = "Профиль"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profile)
                }
            }

            "Ключи" -> {
                app.selectedPage = "Ключи"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, profileKeys)
                }
            }

            "Друзья" -> {
                app.selectedPage = "Друзья"
                supportFragmentManager.commitNow {
                    setReorderingAllowed(true)
                    replace(R.id.profile_fragment_container, friends)
                }
            }
        }
    }

    fun cleanAndFinish(resultCode: Int) {
        app.achievementList.clear()
        getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.edit()?.putString("authedUser", "")?.apply()
        setResult(resultCode)
        finish()
    }

}