package com.example.jankenteamb.model.api

data class PayloadNotif(
    val data: Data? = null,
    val to: String? = null
)

data class Data(
//    val idTarget: String? = null,
//    val idPengirim: String? = null,
//    val title: String? = null,
    val message: String? = null
)
