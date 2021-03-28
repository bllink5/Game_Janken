package com.example.jankenteamb.model

import kotlin.Exception

sealed class RepositoryResult<out R> {
    data class Success<out T>(val data: T) : RepositoryResult<T>() // Status success and data of the result
    data class Error(val exception: Exception) : RepositoryResult<Nothing>() // Status Error an error message
    data class Canceled(val exception: Exception?) : RepositoryResult<Nothing>() // Status Canceled

    // string method to display a result for debugging
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled[exception=$exception]"
        }
    }
}