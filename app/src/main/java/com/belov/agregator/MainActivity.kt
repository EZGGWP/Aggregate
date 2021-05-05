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
import com.belov.agregator.utilities.NewBoolListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : FragmentActivity(), NewBoolListener {
    lateinit var checkDialog: AlertDialog
    lateinit var app: App
    var uiInitialized = false
    var currentUser = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = applicationContext as App

        setContentView(R.layout.activity_main)

        currentUser = getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)?.getString("authedUser", "")!!;


        val builder = AlertDialog.Builder(this)
        checkDialog = builder.setCancelable(false).setView(R.layout.preloader_layout).create()
        checkDialog.show()
        
        app.databaseManager = DatabaseManager(this)



    }

    override fun onValueChanged(value: Boolean) {
        checkDialog.hide()
        checkCurrentUser()
        app.databaseManager.getNamesAndIDs()
    }

    override fun onDestroy() {
        super.onDestroy()
        checkDialog.dismiss()
    }

    fun checkCurrentUser() {
        if (currentUser!!.isNotEmpty()) {
            app.databaseManager.setCurrentUserName(currentUser)
            val intent = Intent(this, ProfileBase::class.java)
            startActivityForResult(intent, 1)
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