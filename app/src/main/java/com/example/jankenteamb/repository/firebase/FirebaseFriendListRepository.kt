package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.repository.firebase.repository.FriendListRepository
import com.example.jankenteamb.utils.FRIENDLIST_COLLECTION
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.utils.JANKEN_USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseFriendListRepository(private val auth: FirebaseAuth) : FriendListRepository {

    override suspend fun getFriendList(): RepositoryResult<List<FriendListData>> {
        val docRef = Firebase.firestore
            .collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$FRIENDLIST_COLLECTION")
        val friendlist = mutableListOf<FriendListData>()
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                if (query.data.isEmpty) {
                    RepositoryResult.Success(friendlist)
                } else {
                    query.data.documents.forEach { doc ->
                        doc.toObject<FriendListData>()?.let {
                            friendlist.add(it)
                        }
                    }
                    RepositoryResult.Success(friendlist)
                }
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun deleteFriend(friendUid: String): RepositoryResult<String> {
        val docRef = Firebase.firestore
            .document("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$FRIENDLIST_COLLECTION/$friendUid")
        return when (val query = docRef.delete().awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Delete success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun addFriendToUser(
        userData: FriendListData,
        friendListData: FriendListData
    ): RepositoryResult<String> {
        val docRef =
            Firebase.firestore.document("$JANKEN_USER_COLLECTION/${userData.uid}/$FRIENDLIST_COLLECTION/${friendListData.uid}")
        return when (val query = docRef.set(friendListData).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add friend success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun addUserToFriend(userData: FriendListData, friendListData: FriendListData):
            RepositoryResult<String> {
        val docRef =
            Firebase.firestore.document("$JANKEN_USER_COLLECTION/${friendListData.uid}/$FRIENDLIST_COLLECTION/${userData.uid}")
        return when (val query = docRef.set(userData).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add user to friend's friendlist success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }
}
