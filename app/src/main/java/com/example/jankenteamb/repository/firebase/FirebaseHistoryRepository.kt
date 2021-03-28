package com.example.jankenteamb.repository.firebase

import android.util.Log
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData
import com.example.jankenteamb.repository.firebase.repository.HistoryRepository
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.utils.GAME_HISTORY_COLLECTION
import com.example.jankenteamb.utils.JANKEN_USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseHistoryRepository(private val auth: FirebaseAuth) : HistoryRepository {

    override suspend fun addHistory(gameMode: String,
                                    gameResult: String): RepositoryResult<String> {
        val docRef = Firebase.firestore.collection(JANKEN_USER_COLLECTION)
            .document(auth.currentUser?.uid!!).collection(
                GAME_HISTORY_COLLECTION
            )
        val history = mapOf("gameMode" to gameMode,
            "gameResult" to gameResult, "gameDate" to FieldValue.serverTimestamp())
        Log.d("addHistory", history.toString())
        return when (val query = docRef.add(history).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add history success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getHistoryFromFirebase(): RepositoryResult<List<GameHistoryFirestoreData>> {
        val docref = Firebase.firestore
            .collection("$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$GAME_HISTORY_COLLECTION")
            .orderBy("gameDate")
        val historyList = mutableListOf<GameHistoryFirestoreData>()
        return when (val query = docref.get().awaits()) {
            is RepositoryResult.Success -> {
                query.data.documents.forEach {doc ->
                    doc.toObject<GameHistoryFirestoreData>()?.let {
                        historyList.add(it)
                    }
                }
                RepositoryResult.Success(historyList)
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }
}