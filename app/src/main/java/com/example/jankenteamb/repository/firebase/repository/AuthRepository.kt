package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult

interface AuthRepository {
    suspend fun login(email: String, password: String): RepositoryResult<String>
    suspend fun register(username:String, email: String, password: String): RepositoryResult<String>
}