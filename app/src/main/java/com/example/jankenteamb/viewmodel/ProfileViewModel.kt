package com.example.jankenteamb.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserDataCollectionRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.repository.firebase.repository.UserDataCollectionRepository
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.AchievementRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val roomUserRepository: UserRepository,
    private val roomAchievementRepository: AchievementRepository,
    private val firebaseStorageRepository: StorageRepository,
    private val firebaseUserDataCollectionRepository: UserDataCollectionRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val auth: FirebaseAuth,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _userLiveData = MutableLiveData<UserData>()
    val userLiveData: LiveData<UserData> get() = _userLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

    private val _topScoreLiveData = MutableLiveData<List<UserData>>()
    val topScoreLiveData: LiveData<List<UserData>> get() = _topScoreLiveData

    private val _frameUriLiveData = MutableLiveData<Uri>()
    val frameUriLiveData: LiveData<Uri> get() = _frameUriLiveData

    private val _photoUriLiveData = MutableLiveData<Uri>()
    val photoUriLiveData: LiveData<Uri> get() = _photoUriLiveData

    private val _usernameUpdateLiveData = MutableLiveData<String>()
    val usernameUpdateLiveData: LiveData<String> = _usernameUpdateLiveData

    fun getUserDataFromRoom() {
        CoroutineScope(dispatcher.main()).launch {
            roomUserRepository.getObservableUserData(onResult = { data ->
                _userLiveData.postValue(data)
                Log.d("getUserDataFromRoom: ", data.toString())
            }, onError = { error ->
                _errorLiveData.postValue(error.message.toString())
                Log.d("getUserDataFromRoom: ", error.message.toString())
            })
        }
    }

    fun getTopScoreFromFirebase() {
        CoroutineScope(dispatcher.io()).launch {
            try {
                when (val result = firebaseUserRepository.getTopScoreUser()) {
                    is RepositoryResult.Success -> _topScoreLiveData.postValue(result.data)
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(it)
                    }
                }
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message)
                Log.d("getTopScoreFirebase: ", e.message.toString())
            }
        }
    }

    fun updateUsername(username: String) {
        auth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username).build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    user.updateProfile(profileUpdates).addOnCompleteListener { task: Task<Void> ->
                        task.addOnFailureListener {
                            _errorLiveData.postValue(it.message.toString())
                        }

                        task.addOnSuccessListener {
                            _successLiveData.postValue("$user username update success")
                        }
                    }

                    val map = mutableMapOf<String, String>()
                    map["username"] = username
                    when (val result = firebaseUserDataCollectionRepository.updateUserData(map)) {
                        is RepositoryResult.Success -> _usernameUpdateLiveData.postValue(username)
                        is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                        is RepositoryResult.Canceled -> result.exception?.message.let {
                            _errorLiveData.postValue(
                                it
                            )
                        }
                    }

                    when (val result = firebaseUserRepository.updateUserData(map)) {
                        is RepositoryResult.Success -> _usernameUpdateLiveData.postValue(username)
                        is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                        is RepositoryResult.Canceled -> result.exception?.message.let {
                            _errorLiveData.postValue(
                                it
                            )
                        }
                    }

                    auth.currentUser?.let {
                        val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        it.updateProfile(profileUpdate)
                    }
                } catch (e: Exception) {
                    _errorLiveData.postValue(e.message.toString())
                }
            }
        }
    }

    fun downloadPhotoProfile() {
        CoroutineScope(dispatcher.io()).launch {
            try {
                val photoUri =
                    firebaseStorageRepository.downloadPhotoProfile(auth.currentUser?.uid!!)
                _photoUriLiveData.postValue(photoUri)
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message.toString())
            }
        }
    }

    fun downloadFrameProfile(frameUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val frameUri = firebaseStorageRepository.downloadFrameProfile(frameUrl)
                _frameUriLiveData.postValue(frameUri)
            } catch (e: Exception) {
                Log.d("downloadFrameProfile: ", e.message.toString())
                _errorLiveData.postValue(e.message.toString())
            }
        }
    }

    fun deleteAchievementData() {
        roomAchievementRepository.getUserAchievementProgress(onResult = {
            it.forEach { userAchievementData ->
//                Log.d("delete", userAchievementData.toString())
                roomAchievementRepository.deleteUserAchievement(userAchievementData)
            }
        }, onError = {
            _errorLiveData.value = it.message!!
        })
    }

    fun deleteUserData() {
        roomUserRepository.getUserDataByUid(onResult = {
//            Log.d("delete", it.toString())
            roomUserRepository.deleteUserData(it)
        }, onError = {
            CoroutineScope(dispatcher.main()).launch {
                _errorLiveData.value = it.message!!
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        roomUserRepository.onDestroy()
    }

    class Factory(
        private val roomUserRepository: RoomUserRepository,
        private val roomAchievementRepository: RoomAchievementRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val firebaseUserDataCollectionRepository: FirebaseUserDataCollectionRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val auth: FirebaseAuth,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                roomUserRepository,
                roomAchievementRepository,
                firebaseStorageRepository,
                firebaseUserDataCollectionRepository,
                firebaseUserRepository,
                auth,
                dispatcher
            ) as T
        }
    }
}