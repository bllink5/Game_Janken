package com.example.jankenteamb.repository.firebase

import android.util.Log
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.repository.firebase.repository.IFAchievementRepository
import com.example.jankenteamb.utils.ACHIEVEMENT_DATA_COLLECTION
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.utils.JANKEN_USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseAchievementRepository(private val auth: FirebaseAuth) : IFAchievementRepository {

    override suspend fun getAllAchievement(): RepositoryResult<List<UserAchievementData>> {
        val docRef = Firebase.firestore
            .collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid!!}/$ACHIEVEMENT_DATA_COLLECTION")
        val listAchievement = mutableListOf<UserAchievementData>()
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                if (query.data.isEmpty) {
                    RepositoryResult.Success(listAchievement)
                } else {
                    query.data.documents.forEach { doc ->
                        val achievement = doc.toObject<UserAchievementData>()
                        achievement?.let {
                            listAchievement.add(it)
                        }
                    }
                    RepositoryResult.Success(listAchievement)
                }
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getAchievementByAchievementId(
        achievementId: Int
    ): RepositoryResult<UserAchievementData> {
        val docRef =
            Firebase.firestore.collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$ACHIEVEMENT_DATA_COLLECTION")
                .whereEqualTo("achievementId", achievementId)
                .whereEqualTo("uid", auth.currentUser?.uid!!)
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                lateinit var userAchievementData: UserAchievementData
                query.data.documents.forEach { doc ->
                    doc.toObject<UserAchievementData>()?.let {
                        userAchievementData = it
                    }
                }
                RepositoryResult.Success(userAchievementData)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }


    override suspend fun updateAchievementToClaimed(achievementId: Int): RepositoryResult<String> {
        val firebaseAchievementId = achievementId.toString().plus(auth.currentUser?.uid!!)
        val achievementMap = mapOf("claim" to "claimed")
        Log.d("updateAchievement", "$achievementId $achievementMap")
        val docRef =
            Firebase.firestore.document("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$ACHIEVEMENT_DATA_COLLECTION/$firebaseAchievementId")

        return when (val query = docRef.set(achievementMap, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun updateAchievementDataByAchievementId(
        newProgress: Map<String, Int>,
        achievementId: Int
    ): RepositoryResult<String> {
        val docRef =
            Firebase.firestore.collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$ACHIEVEMENT_DATA_COLLECTION")
                .document("$achievementId${auth.currentUser?.uid}")
        return when (val query = docRef.set(newProgress, SetOptions.merge()).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Update success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun addAchievementData(newAchievementData: UserAchievementData): RepositoryResult<String> {
        val docRef =
            Firebase.firestore.collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$ACHIEVEMENT_DATA_COLLECTION")
                .document("${newAchievementData.achievementId}${auth.currentUser?.uid}")
        return when (val query = docRef.set(newAchievementData).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add data success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }
}
