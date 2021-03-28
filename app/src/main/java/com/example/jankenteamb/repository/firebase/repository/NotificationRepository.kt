package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.api.PayloadNotif
import io.reactivex.Single

interface NotificationRepository {
    suspend fun pushNotification(fromUid: String, toUid: String, payloadNotif: PayloadNotif) : Single<String>
    suspend fun addNotificationToFirestore(toUid: String, payloadNotif: PayloadNotif) : RepositoryResult<String>
}