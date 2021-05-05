package com.belov.agregator.storage

import android.os.Parcel
import android.os.Parcelable
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

        companion object CREATOR : Parcelable.Creator<SteamAchievement> {
            override fun createFromParcel(parcel: Parcel): SteamAchievement {
                return SteamAchievement(parcel)
            }

            override fun newArray(size: Int): Array<SteamAchievement?> {
                return arrayOfNulls(size)
            }
        }

    }

    fun clearData() {
        totalAchievements = 0
        friendsCount = 0
        gamesCount = 0
    }
}