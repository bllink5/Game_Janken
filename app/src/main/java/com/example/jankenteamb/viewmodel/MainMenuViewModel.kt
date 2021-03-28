package com.example.jankenteamb.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainMenuViewModel(
    private val auth: FirebaseAuth,
    private val roomUserRepository: UserRepository,
    private val firebaseStorageRepository: StorageRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _userLiveData = MutableLiveData<UserData>()
    val userLiveData: LiveData<UserData> get() = _userLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _uriLiveData = MutableLiveData<Uri>()
    val uriLiveData: LiveData<Uri> get() = _uriLiveData


    //getUserDataFromRoom dari profilePresenter
    fun getUserDataFromRoom() {
        CoroutineScope(dispatcher.main()).launch {
            roomUserRepository.getObservableUserData(onResult = { data ->
                _userLiveData.value = data
            }, onError = { error ->
                _errorLiveData.value = error.message!!
            })
        }
    }

    fun downloadPhotoProfile() {
        CoroutineScope(dispatcher.main()).launch {
            try {
                val photoUri =
                    firebaseStorageRepository.downloadPhotoProfile(auth.currentUser?.uid!!)
                _uriLiveData.postValue(photoUri)
            } catch (e: Exception) {
                Log.d("downloadPhoto", "error: ${e.message}")
            }
        }
    }

    class Factory(
        private val auth: FirebaseAuth,
        private val roomUserRepository: RoomUserRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val roomAchievementRepository: RoomAchievementRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainMenuViewModel(
                auth,
                roomUserRepository,
                firebaseStorageRepository,
                dispatcher
            ) as T
        }
    }
}