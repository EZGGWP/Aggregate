package com.belov.agregator.storage

import com.belov.agregator.utilities.Achievement

class SpotifyDataStorage {

    var totalLikedTracks = 0
    var totalFollowedArtists = 0
    var totalPlaylists = 0

    fun createAchievement(): List<Achievement> {
        val listOfAchievements = arrayListOf<Achievement>()
        listOfAchievements.add(SpotifyAchievement("Добавьте 250 треков в \"Понравивешееся\"", 250, totalLikedTracks, 1))
        listOfAchievements.add(SpotifyAchievement("Подпишитесь на 10 исполнителей", 10, totalFollowedArtists, 2))
        listOfAchievements.add(SpotifyAchievement("Создайте 5 плейлистов", 5, totalPlaylists, 3))
        return listOfAchievements
    }


    class SpotifyAchievement(override var name: String, override var goal: Int, override var progress: Int, override var id: Int): Achievement {


    }
}