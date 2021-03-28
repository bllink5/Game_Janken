package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.shop.FrameUserData
import com.example.jankenteamb.model.firestore.shop.ShopListFirestore
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.example.jankenteamb.repository.firebase.repository.FrameCollectionRepository
import com.example.jankenteamb.utils.FRAME_DATA_COLLECTION
import com.example.jankenteamb.utils.FRAME_DATA_DOCUMENT
import com.example.jankenteamb.utils.GAME_DATA_COLLECTION
import com.example.jankenteamb.utils.JANKEN_USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseFrameCollectionRepository(private val auth: FirebaseAuth) :
    FrameCollectionRepository {

    override suspend fun getAllFrameList(): RepositoryResult<ShopListFirestore> {
        val docRef = Firebase.firestore.collection(GAME_DATA_COLLECTION)
            .document(FRAME_DATA_DOCUMENT)

        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                val data = query.data.toObject<ShopListFirestore>()
                @Suppress("UNCHECKED_CAST")
                RepositoryResult.Success(data) as RepositoryResult<ShopListFirestore>
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }

    override suspend fun getUserFrameList(): RepositoryResult<List<FrameUserData>> {

        val docRef = Firebase.firestore
            .collection("/$JANKEN_USER_COLLECTION/${auth.currentUser?.uid}/$FRAME_DATA_COLLECTION")
        return when (val query = docRef.get().awaits()) {
            is RepositoryResult.Success -> {
                val frameUserList = mutableListOf<FrameUserData>()
                if (query.data.isEmpty) {
                    RepositoryResult.Success(frameUserList)
                } else {
                    query.data.documents.forEach { doc ->
                        doc.toObject<FrameUserData>()?.let {
                            frameUserList.add(it)
                        }
                    }
                    RepositoryResult.Success(frameUserList)
                }
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }
    }


    override suspend fun addFrame(frame: FrameUserData): RepositoryResult<String> {
        val docRef = Firebase.firestore.collection(JANKEN_USER_COLLECTION)
            .document(auth.currentUser?.uid!!).collection(
                FRAME_DATA_COLLECTION
            )
        return when (val query = docRef.add(frame).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Add frame success!")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> RepositoryResult.Canceled(query.exception)
        }

    }
}