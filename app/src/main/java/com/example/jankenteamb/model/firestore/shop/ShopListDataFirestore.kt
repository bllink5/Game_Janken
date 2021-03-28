package com.example.jankenteamb.model.firestore.shop

import android.net.Uri

data class ShopListDataFirestore (
    var frameUrl : String = "",
    var price : Int = 0,
    var title : String = ""
)

data class ShopListDataFirestoreAdapter (
    var frameUrl : String,
    var frameUri : Uri,
    var price : Int = 0,
    var title : String = "",
    var used : Boolean = false
)