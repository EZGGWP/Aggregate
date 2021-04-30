package com.belov.agregator.storage

import android.os.Parcel
import android.os.Parcelable
import com.belov.agregator.utilities.Achievement

class GithubDataStorage {
    var repoCount = 0
    var gistCount = 0
    var pullCount = 0

    fun createAchievements(): List<Achievement> {
        val listOfAchievements = arrayListOf<Achievement>()
        listOfAchievements.add(GithubAchievement("Создать 20 репозиториев", 20, repoCount, 7))
        listOfAchievements.add(GithubAchievement("Создать 10 гистов", 10, gistCount, 8))
        listOfAchievements.add(GithubAchievement("Сделать 5 пулл-реквестов к своим репозиториям", 300, pullCount, 9))
        return listOfAchievements
    }


    class GithubAchievement(override var name: String, override var goal: Int, override var progress: Int, override var id: Int) : Achievement {
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

        companion object CREATOR : Parcelable.Creator<GithubAchievement> {
            override fun createFromParcel(parcel: Parcel): GithubAchievement {
                return GithubAchievement(parcel)
            }

            override fun newArray(size: Int): Array<GithubAchievement?> {
                return arrayOfNulls(size)
            }
        }
    }

}