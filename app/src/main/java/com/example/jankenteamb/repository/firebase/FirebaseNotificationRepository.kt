package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.api.ApiResource
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.api.PayloadNotif
import com.example.jankenteamb.repository.firebase.repository.NotificationRepository
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseNotificationRepository(private val auth: FirebaseAuth) : NotificationRepository {
    override suspend fun pushNotification(
        fromUid: String,
        toUid: String,
        payloadNotif: PayloadNotif
    ) =
        ApiResource.pushNotif(payloadNotif)


    override suspend fun addNotificationToFirestore(
        toUid: String,
        payloadNotif: PayloadNotif
    ): RepositoryResult<String> {
        val docRef = Firebase.firestore.collection("NotifList/$toUid/notif")

        return when (val query = docRef.add(payloadNotif).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add notification to firestore success")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

}