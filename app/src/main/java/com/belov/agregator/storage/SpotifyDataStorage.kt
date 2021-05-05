package com.belov.agregator.storage

import android.os.Parcel
import android.os.Parcelable
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
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeString(name)
            parcel.writeInt(goal)
            parcel.writeInt(progress)
            parcel.writeInt(id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SpotifyAchievement> {
            override fun createFromParcel(parcel: Parcel): SpotifyAchievement {
                return SpotifyAchievement(parcel)
            }

            override fun newArray(size: Int): Array<SpotifyAchievement?> {
                return arrayOfNulls(size)
            }
        }

    }

    fun clearData() {
        totalLikedTracks = 0
        totalFollowedArtists = 0
        totalPlaylists = 0
    }
}