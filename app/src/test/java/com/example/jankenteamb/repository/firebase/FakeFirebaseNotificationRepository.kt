package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.api.PayloadNotif
import com.example.jankenteamb.repository.firebase.repository.NotificationRepository
import io.reactivex.Single

class FakeFirebaseNotificationRepository: NotificationRepository {
    override suspend fun pushNotification(
        fromUid: String,
        toUid: String,
        payloadNotif: PayloadNotif
    ): Single<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addNotificationToFirestore(
        toUid: String,
        payloadNotif: PayloadNotif
    ): RepositoryResult<String> {
        TODO("Not yet implemented")
    }
}