package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData

interface UserDataCollectionRepository {
    suspend fun addUserToUserData(newUserData: FriendListData): RepositoryResult<String>

    suspend fun getAllUserData(): RepositoryResult<List<FriendListData>>

    suspend fun updateUserData(newUserData: FriendListData): RepositoryResult<String>

    suspend fun updateUserData(userDataMap: MutableMap<String, String>): RepositoryResult<String>
}