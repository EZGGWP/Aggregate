package com.belov.agregator.storage

import com.belov.agregator.utilities.Achievement

class SteamDataStorage {
    var totalAchievements = 0
    var friendsCount = 0
    var gamesCount = 0

    fun createAchievements(): List<Achievement> {
        val listOfAchievements = arrayListOf<Achievement>()
        listOfAchievements.add(SteamAchievement("Заработать 250 достижений", 250, totalAchievements, 4))
        listOfAchievements.add(SteamAchievement("Добавить 20 друзей", 20, friendsCount, 5))
        listOfAchievements.add(SteamAchievement("Добавьте в библиотеку 300 игр", 300, gamesCount, 6))
        return listOfAchievements
    }

    class SteamAchievement(override var name: String, override var goal: Int, override var progress: Int, override var id: Int) : Achievement {

    }
}