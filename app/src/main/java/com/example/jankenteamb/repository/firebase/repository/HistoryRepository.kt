package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData

interface HistoryRepository {
    suspend fun addHistory(gameMode: String,
                           gameResult: String): RepositoryResult<String>

    suspend fun getHistoryFromFirebase(): RepositoryResult<List<GameHistoryFirestoreData>>
}