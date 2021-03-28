package com.example.jankenteamb.model.firestore

data class JankenUserFirestoreData(
    var uid: String = "",
    var level: Int = 0,
    var exp: Int = 0,
    var point: Int = 0,
    var photoUrl: String = "",
    var frameUrl: String = "",
    var win: Int = 0,
    var lose: Int = 0,
    var draw: Int = 0

)