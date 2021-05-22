package com.belov.agregator

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.belov.agregator.auth.AuthPagerAdapter
import com.belov.agregator.database.DatabaseManager
import com.belov.agregator.profile.ProfileBase
import com.belov.agregator.utilities.NewBool
import com.belov.agregator.utilities.NewBoolListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import java.lang.RuntimeException


class MainActivity : FragmentActivity(), NewBoolListener {
    lateinit var app: App
    var uiInitialized = false
    var currentUser = ""
    lateinit var mFirebaseAnalytics: FirebaseAnalytics



    override fun onCreate(savedInstanceState: Bundle?) {
        app = applicationContext as App
        app.mainActivity = this

        super.onCreate(savedInstanceState)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        app.databaseManager.state = NewBool(this)

        if (app.databaseManager.hasErrorOccurred) {
            val builder = AlertDialog.Builder(applicationContext)
            val unreachableDialog = builder.setCancelable(false).setTitle("Сервер недоступен").setMessage("Сервер недоступен. Извиняемся за неудобства.").create()
            unreachableDialog.show()
        } else {

            setContentView(R.layout.activity_main)

            currentUser = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString("authedUser", "")!!;


            if (!app.isConnectInitialized() && !app.databaseManager.hasErrorOccurred) {
                val builder = AlertDialog.Builder(this)
                app.checkDialog = builder.setCancelable(false).setView(R.layout.preloader_layout).create()
                app.checkDialog.show()
            }

            if (!uiInitialized && app.isConnectInitialized()) checkCurrentUser()
        }


    }

    override fun onValueChanged(value: Boolean) {
        if (value && app.isCheckDialogInitialized()) {
            runOnUiThread {
                app.checkDialog.hide()
                checkCurrentUser()
            }
            app.databaseManager.getNamesAndIDs()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (app.isCheckDialogInitialized()) {
            app.checkDialog.cancel()
        }
    }

    fun checkCurrentUser() {
        if (currentUser.isNotEmpty()) {
            app.databaseManager.setCurrentUserName(currentUser)
            val intent = Intent(this, ProfileBase::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivityForResult(intent, 1)
            finish()
        } else {
            initUi()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resultCode) {
            initUi()
        }
    }

    fun initUi() {
        if (!uiInitialized) {
            val viewPager = findViewById<ViewPager2>(R.id.viewPager);
            viewPager.adapter = AuthPagerAdapter(this)
            val tabs = findViewById<TabLayout>(R.id.tabLayout)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Вход"
                    1 -> tab.text = "Регистрация"
                }
            }.attach()
        }
        uiInitialized = true
    }


}