package com.example.jankenteamb.model.firestore.friendlist

import android.net.Uri

data class FriendListDataWithUri (
    var photoUri: Uri,
    var photoUrl: String = "",
    var username: String = "",
    var uid: String = ""
)
