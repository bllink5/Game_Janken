package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserData

interface IFUserRepository {
    suspend fun addUserData(userData: UserData): RepositoryResult<String>
    suspend fun updateUserData(newUserData: UserData): RepositoryResult<String>
    suspend fun updateUserData(mapUserData: Map<String,String>): RepositoryResult<String>
    suspend fun getUserData(): RepositoryResult<UserData>
    suspend fun getTopScoreUser(): RepositoryResult<List<UserData>>
    suspend fun getUserDataByUid(uid:String): RepositoryResult<UserData>
    suspend fun updateFrameUrl(frameUrlMap: Map<String, String>): RepositoryResult<String>
    suspend fun updateUserPoint(userDataMap: Map<String, Int>): RepositoryResult<String>
}