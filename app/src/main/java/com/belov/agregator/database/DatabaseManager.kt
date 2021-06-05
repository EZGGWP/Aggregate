package com.belov.agregator.database

import com.belov.agregator.App
import com.belov.agregator.utilities.Friendship
import com.belov.agregator.utilities.User
import com.belov.agregator.utilities.NewBool
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.postgresql.util.PGobject
import org.postgresql.util.PSQLException
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import kotlin.properties.Delegates

class DatabaseManager(private val parent: App) {

    private lateinit var username: String;
    private lateinit var passwordHash: String;
    var userID by Delegates.notNull<Int>();
    lateinit var state: NewBool
    var hasErrorOccurred = false
    val users: ArrayList<User> = arrayListOf()
    var isUsersReady = false

    lateinit var achJson: JsonObject

    lateinit var connection: Connection;

    init {
        if (parent.isMainActivityInitialized()) {
            state = NewBool(parent.mainActivity)
        }

        getNewConnection()


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

    fun isAchJsonInitialized(): Boolean {
        return this::achJson.isInitialized
    }

    fun setCurrentUserNameAndGetHash(username: String) {
        this.username = username
        getUserPasswordHash()
    }

    fun setCurrentUserName(username: String) {
        this.username = username
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

    fun getNameCount(username: String): Boolean {
        var unique = false
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
        return unique
    }

    fun getUserId(): Int {
        var id = -1
        val prepStatement = connection.prepareStatement("SELECT \"id\" FROM \"users\" WHERE \"username\" = ?")
        prepStatement.setString(1, username)
        runBlocking {
            val coroutine = GlobalScope.launch {
                val resultSet = prepStatement.executeQuery()
                if (resultSet.next()) {
                    id = resultSet.getInt(1)
                }
            }
            coroutine.join()
        }
        userID = id
        return id
    }

    //TODO: Переделать под асинк?
    fun getFriends(): ArrayList<Friendship> {
        val friendsList = arrayListOf<Friendship>()
        if (userID != -1) {
            val prepStatement = connection.prepareStatement("SELECT \"sId\", \"rId\", \"stat\" FROM \"friends\" WHERE (\"rId\" = ?) OR (\"sId\" = ?)")
            prepStatement.setInt(1, userID)
            prepStatement.setInt(2, userID)
            runBlocking {
                val coroutine = GlobalScope.launch {
                    val resultSet = prepStatement.executeQuery()
                    while (resultSet.next()) {
                        friendsList.add(Friendship(resultSet.getBoolean(3), resultSet.getInt(1), resultSet.getInt(2)))
                    }
                }
                coroutine.join()
            }
        }
        return friendsList
    }

    fun getNamesAndIDs(): Int {
        isUsersReady = false
        users.clear()
        val prepStatement = connection.prepareStatement("SELECT \"id\", \"username\" FROM \"users\"")
        GlobalScope.launch {
            val resultSet = prepStatement.executeQuery()
            while (resultSet.next()) {
                val user = User(resultSet.getInt(1), resultSet.getString(2))
                users.add(user)
            }
            isUsersReady = true
        }
        return users.size
    }

    fun addFriendRequest(rId: Int) {
        GlobalScope.launch {
            val prepStatement = connection.prepareStatement("INSERT INTO \"friends\" VALUES (?, ?, false)")
            prepStatement.setInt(1, userID)
            prepStatement.setInt(2, rId)
            prepStatement.execute()
        }
    }

    fun acceptFriendship(sId: Int, rId: Int) {
        GlobalScope.launch {
            val prepStatement = connection.prepareStatement("UPDATE \"friends\" SET \"stat\" = true WHERE \"sId\" = ? AND \"rId\" = ?")
            prepStatement.setInt(1, sId)
            prepStatement.setInt(2, rId)
            prepStatement.execute()
        }
    }

    fun getUserAchievementsJson() {
        runBlocking {
            val coroutine = GlobalScope.launch {
                val prepStatement = connection.prepareStatement("SELECT \"achievements\" FROM \"users\" WHERE \"username\" = ?")
                prepStatement.setString(1, username)
                val resultSet = prepStatement.executeQuery()
                if (resultSet.next()) {
                    val json = resultSet.getString(1)
                    achJson = Gson().fromJson(json, JsonObject::class.java)
                }
            }
            coroutine.join()
        }
    }

    fun saveUserAchievementsJson() {
        GlobalScope.launch {
            val prepStatement = connection.prepareStatement("UPDATE \"users\" SET \"achievements\" = ? WHERE \"username\" = ?")
            val pgJson = PGobject()
            pgJson.type = "json"
            pgJson.value = achJson.toString()
            prepStatement.setObject(1, pgJson)
            prepStatement.setString(2, username)

            prepStatement.executeUpdate()
        }
    }

    fun getUserById(id: Int): User {
        var json = ""
        var regDate = Date.valueOf("2009-09-09")
        var username = ""
        val prepStatement = connection.prepareStatement("SELECT \"achievements\", \"regDate\", \"username\" FROM \"users\" WHERE \"id\" = ?")
        prepStatement.setInt(1, id)
        runBlocking {
            val coroutine = GlobalScope.launch{
                val resultSet = prepStatement.executeQuery()
                if (resultSet.next()) {
                    json = resultSet.getString(1)
                    regDate = resultSet.getDate(2)
                    username = resultSet.getString(3)
                }
            }
            coroutine.join()
        }
        return User(id, username, json, regDate)
    }

    fun isConnectInitialized(): Boolean {
        return this::connection.isInitialized
    }

    fun getNewConnection() {
        GlobalScope.launch {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://188.134.66.115:5432/agregator", "postgres", "azl41kmng85!_")

            } catch (e: PSQLException) {
                if (e.serverErrorMessage == null) {
                    if (this@DatabaseManager::state.isInitialized) {
                        state.set(false)
                    }

                    hasErrorOccurred = true
                    if (parent.isMainActivityInitialized()) {
                        parent.mainActivity.onValueChanged(false)
                    }
                }
            }
            if (this@DatabaseManager::state.isInitialized) {
                state.set(true)
            }
            hasErrorOccurred = false
        }
    }
}