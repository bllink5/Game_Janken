package com.example.jankenteamb.model.firestore

import com.google.firebase.Timestamp

data class GameHistoryFirestoreData(
    var gameMode: String = "",
    var gameResult: String = "",
    var gameDate: Timestamp? = null
)