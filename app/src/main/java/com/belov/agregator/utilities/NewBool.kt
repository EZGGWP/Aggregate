package com.belov.agregator.utilities

import java.io.Serializable

class NewBool(var listener: NewBoolListener) {
    private var value = false

    fun set(value: Boolean) {
        this.value = value
        listener.onValueChanged(value)
    }

    fun get(): Boolean {
        return value
    }


}

interface NewBoolListener : Serializable {
    fun onValueChanged(value: Boolean)

}