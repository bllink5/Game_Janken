package com.example.jankenteamb.api

import com.example.jankenteamb.model.api.PayloadNotif
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServices {
    @POST("fcm/send")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun pushNotif(
        @Header("Authorization") token: String,
        @Body payloadNotif: PayloadNotif
    ): Single<String>
}