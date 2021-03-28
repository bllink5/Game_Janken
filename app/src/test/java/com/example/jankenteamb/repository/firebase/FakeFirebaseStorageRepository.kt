package com.example.jankenteamb.repository.firebase

import android.net.Uri
import com.example.jankenteamb.repository.firebase.repository.StorageRepository

class FakeFirebaseStorageRepository: StorageRepository {
    override suspend fun downloadPhotoProfile(uid: String): Uri {
        return Uri.parse("uid")
    }

    override suspend fun uploadPhotoProfile(uid: String, photoUri: Uri, onResult: (Uri) -> Unit) {
        onResult(photoUri)
    }

    override suspend fun downloadFrameProfile(frameUrl: String): Uri {
        return Uri.parse(frameUrl)
    }
}