package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.repository.firebase.repository.UserDataCollectionRepository
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.utils.USERDATA_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseUserDataCollectionRepository(private val auth: FirebaseAuth) :
    UserDataCollectionRepository {

    override suspend fun addUserToUserData(newUserData: FriendListData): RepositoryResult<String> {
        val docRef = Firebase.firestore
            .document("$USERDATA_COLLECTION/${newUserData.uid}")
        return when (val query = docRef.set(newUserData).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add user to UserData collection success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getAllUserData(): RepositoryResult<List<FriendListData>> {
        val docRef = Firebase.firestore
            .collection(USERDATA_COLLECTION)
        val allUserData = mutableListOf<FriendListData>()
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                query.data.documents.forEach { doc ->
                    val singleData = doc.toObject<FriendListData>()
                    singleData?.let { allUserData.add(it) }
                }
                RepositoryResult.Success(allUserData)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateUserData(newUserData: FriendListData): RepositoryResult<String> {
        val docref = Firebase.firestore
            .document("$USERDATA_COLLECTION/${newUserData.uid}")
        return when (val query = docref.set(newUserData, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update user data in UserData coleection success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateUserData(userDataMap: MutableMap<String, String>): RepositoryResult<String> {
        val docRef = Firebase.firestore
            .document("/$USERDATA_COLLECTION/${auth.currentUser?.uid}")
        return when (val query = docRef.set(userDataMap, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update user data in UserData coleection success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }
}
