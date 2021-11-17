package com.example.katakan.data.network

import com.example.katakan.data.network.Status.LOADING
import com.example.katakan.data.network.Status.SUCCESS
import com.example.katakan.data.network.Status.ERROR

data class Resources<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resources<T> =
            Resources(status = SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resources<T> =
            Resources(status = ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resources<T> =
            Resources(status = LOADING, data = data, message = null)
    }
}