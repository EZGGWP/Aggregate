package com.belov.agregator.utilities

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

interface Achievement : Parcelable {
    var name: String;
    var goal: Int;
    var progress: Int;
    var id: Int;


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(goal)
        parcel.writeInt(progress)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

}