package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.shop.FrameUserData
import com.example.jankenteamb.model.firestore.shop.ShopListFirestore

interface FrameCollectionRepository {
    suspend fun getAllFrameList(): RepositoryResult<ShopListFirestore>

    suspend fun getUserFrameList(): RepositoryResult<List<FrameUserData>>

    suspend fun addFrame(frame: FrameUserData): RepositoryResult<String>
}