package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.repository.AuthRepository
import com.example.jankenteamb.utils.FirebaseTaskExtension.awaits
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun login(email: String, password: String): RepositoryResult<String> {
        return when (val query = auth.signInWithEmailAndPassword(email, password).awaits()) {
            is RepositoryResult.Success -> RepositoryResult.Success("Login success")
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> query.exception.let {
                RepositoryResult.Canceled(it)
            }
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): RepositoryResult<String> {
        return when (val query = auth.createUserWithEmailAndPassword(email, password).awaits()) {
            is RepositoryResult.Success -> {
                auth.currentUser?.let {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    it.updateProfile(profileUpdate)
                }
                RepositoryResult.Success("Register berhasil")
            }
            is RepositoryResult.Error -> RepositoryResult.Error(query.exception)
            is RepositoryResult.Canceled -> query.exception.let {
                RepositoryResult.Canceled(it)
            }
        }
    }


}