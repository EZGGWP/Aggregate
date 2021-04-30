package com.belov.agregator.database

import android.app.AlertDialog
import com.belov.agregator.MainActivity
import com.belov.agregator.utilities.NewBool
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.postgresql.util.PSQLException
import java.sql.Connection
import java.sql.DriverManager
import kotlin.properties.Delegates

class DatabaseManager(private val parent: MainActivity) {

    private lateinit var username: String;
    private lateinit var passwordHash: String;
    private var unique by Delegates.notNull<Boolean>();
    var state: NewBool = NewBool(parent)

    lateinit var connection: Connection;
    init {
        GlobalScope.launch {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://192.168.3.18:5432/agregator", "postgres", "azl41kmng85!_")

            } catch (e: PSQLException) {
                if (e.serverErrorMessage == null) {
                    parent.runOnUiThread {
                        parent.onValueChanged(false)
                        val builder = AlertDialog.Builder(parent)
                        val checkDialog = builder.setCancelable(false).setTitle("Сервер недоступен").setMessage("Сервер недоступен. Извиняемся за неудобства.").create()
                        checkDialog.show()
                    }
                }
            }
            parent.runOnUiThread {
                state.set(true)
            }
        }
    }

    fun getUserPasswordHash() {
        runBlocking {
            val prepStatement = connection.prepareStatement("SELECT \"password\" FROM \"users\" WHERE \"username\" = ?")
            prepStatement.setString(1, username)
            val coroutine = GlobalScope.launch {
                val result = prepStatement.executeQuery()
                if (result.next()) {
                    passwordHash = result.getString(1)
                }
            }
            coroutine.join()
        }
    }

    fun setCurrentUserName(username: String) {
        this.username = username
        getUserPasswordHash()
    }

    fun addUser(username: String, password: String) {
        val prepStatement = connection.prepareStatement("INSERT INTO \"users\" VALUES (DEFAULT, ?, ?, NOW())")
        prepStatement.setString(1, username)
        prepStatement.setString(2, password)
        GlobalScope.launch {
            prepStatement.execute()
        }
    }

    fun getHash(): String? {
        return if (this::passwordHash.isInitialized) {
            this.passwordHash
        } else {
            null
        }
    }

    fun getName(): String? {
        return if (this::username.isInitialized) {
            this.username
        } else {
            null
        }
    }

    fun getNameCount(username: String) {
        runBlocking {
            val prepStatement = connection.prepareStatement("SELECT count(*) FROM \"users\" WHERE \"username\" = ?")
            prepStatement.setString(1, username)
            val coroutine = GlobalScope.launch {
                val resultSet = prepStatement.executeQuery()
                if (resultSet.next()) {
                    unique = when (resultSet.getInt(1)) {0 -> true
                        else -> false
                    }
                }
            }
            coroutine.join()
        }
    }

    fun checkUniqueName(username: String): Boolean {
        getNameCount(username)
        return unique
    }

}