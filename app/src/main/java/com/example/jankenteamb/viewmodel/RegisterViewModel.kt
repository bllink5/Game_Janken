package com.example.jankenteamb.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FirebaseAuthRepository
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.repository.AuthRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val auth: FirebaseAuth,
    private val firebaseAuthRepository: AuthRepository,
    private val firebaseStorageRepository: StorageRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

    fun registerToFirebase(email: String, password: String, username: String, uri: Uri) {
        CoroutineScope(dispatcher.main()).launch {
            try {
                when (val result = firebaseAuthRepository.register(username, email, password)) {
                    is RepositoryResult.Success -> {
                        firebaseStorageRepository.uploadPhotoProfile(
                            auth.currentUser?.uid!!,
                            uri, onResult = {
                                _successLiveData.value = "Register berhasil"
                            }
                        )
                    }
                    is RepositoryResult.Error -> {

                        _errorLiveData.postValue(result.exception.message)

                    }
                    is RepositoryResult.Canceled -> {

                        _errorLiveData.postValue(result.exception?.message)

                    }
                }
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message)
            }
        }
    }

    class Factory(
        private val auth: FirebaseAuth,
        private val firebaseAuthRepository: FirebaseAuthRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RegisterViewModel(
                auth,
                firebaseAuthRepository,
                firebaseStorageRepository,
                dispatcher
            ) as T
        }
    }
}