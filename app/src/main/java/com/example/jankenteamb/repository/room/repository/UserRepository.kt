package com.example.jankenteamb.repository.room.repository

import com.example.jankenteamb.model.room.user.UserData

interface UserRepository {
    fun insertUserData(userData: UserData, onResult: () -> Unit, onError: (Throwable) -> Unit)

    fun getUserPointDataByUid(onResult: (Int) -> Unit, onError: (Throwable) -> Unit)

    fun updateUserPointByUid(
        newPoint: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun getUserDataByUid(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit)

    fun getObservableUserData(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit)
    fun updateUserData(
        level: Int,
        win: Int,
        draw: Int,
        lose: Int,
        exp: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun updateUserFrame(frameUrl: String, onResult: () -> Unit, onError: (Throwable) -> Unit)

    fun deleteUserData(userData: UserData)
    fun onDestroy()
}