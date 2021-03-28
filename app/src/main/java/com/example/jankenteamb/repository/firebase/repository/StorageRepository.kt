package com.example.jankenteamb.repository.firebase.repository

import android.net.Uri

interface StorageRepository {
    suspend fun downloadPhotoProfile(uid: String): Uri

    suspend fun uploadPhotoProfile(uid: String, photoUri: Uri, onResult: (Uri) -> Unit)

    suspend fun downloadFrameProfile(frameUrl: String): Uri
}