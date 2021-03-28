package com.example.jankenteamb.repository.firebase

import android.net.Uri
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository(private val mStorageReference: StorageReference) :
    StorageRepository {
    override suspend fun downloadPhotoProfile(uid: String) : Uri {
        val ref = mStorageReference.child("profilePicture/$uid.jpg")
        return ref.downloadUrl.await()
    }

    override suspend fun uploadPhotoProfile(uid: String, photoUri: Uri, onResult: (Uri) -> Unit) {
        val ref = mStorageReference.child("profilePicture/$uid.jpg")
        ref.putFile(photoUri)
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (downloadUri != null) {
                        onResult(downloadUri)
                    }
                }
            }
    }

    override suspend fun downloadFrameProfile(frameUrl: String) :Uri {
        val ref = mStorageReference.child("frame/$frameUrl")
        return ref.downloadUrl.await()
    }
}