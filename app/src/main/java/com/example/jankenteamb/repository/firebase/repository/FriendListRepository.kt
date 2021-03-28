package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData

interface FriendListRepository {
    suspend fun getFriendList(): RepositoryResult<List<FriendListData>>

    suspend fun deleteFriend(friendUid: String): RepositoryResult<String>

    suspend fun addFriendToUser(
        userData: FriendListData,
        friendListData: FriendListData
    ): RepositoryResult<String>

    suspend fun addUserToFriend(
        userData: FriendListData,
        friendListData: FriendListData
    ): RepositoryResult<String>
}