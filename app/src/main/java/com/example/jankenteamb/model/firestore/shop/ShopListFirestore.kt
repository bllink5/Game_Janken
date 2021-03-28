package com.example.jankenteamb.model.firestore.shop

data class ShopListFirestore(
    var frame : MutableList<ShopListDataFirestore> = mutableListOf()
)