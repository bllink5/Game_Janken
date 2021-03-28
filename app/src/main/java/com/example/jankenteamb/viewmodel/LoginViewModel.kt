package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.repository.firebase.FirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FirebaseAuthRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserDataCollectionRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.AuthRepository
import com.example.jankenteamb.repository.firebase.repository.IFAchievementRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.firebase.repository.UserDataCollectionRepository
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.AchievementRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val auth: FirebaseAuth,
    private val firebaseAuthRepository: AuthRepository,
    private val roomUserRepository: UserRepository,
    private val roomAchievementRepository: AchievementRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val firebaseAchievementRepository: IFAchievementRepository,
    private val firebaseUserDataCollectionRepository: UserDataCollectionRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

    fun loginToFirebase(email: String, password: String) {
        CoroutineScope(dispatcher.io()).launch {
            try {
                when (val result = firebaseAuthRepository.login(email, password)) {
                    is RepositoryResult.Success -> {
                        getJankenUserDataFromFirestore()
                        getAchievementCollection()
                        withContext(dispatcher.main()) {
                            _successLiveData.value = "Login success"
                        }
                    }
                    is RepositoryResult.Error -> withContext(dispatcher.main()) {
                        _errorLiveData.value = result.exception.message
                    }
                    is RepositoryResult.Canceled -> withContext(dispatcher.main()) {
                        _errorLiveData.value = result.exception?.message
                    }
                }
            } catch (e: Exception) {
                withContext(dispatcher.main()) {
                    _errorLiveData.value = e.message!!
                }
            }
        }
    }

    //fungsi untuk mendapatkan data user level, win, lose, draw, point, dll
    //dalam tahap trial error
    private fun getJankenUserDataFromFirestore() {
        CoroutineScope(dispatcher.io()).launch {
            try {
                val tokenIntance = FirebaseInstanceId.getInstance().instanceId.await()
                when (val result = firebaseUserRepository.getUserData()) {
                    is RepositoryResult.Success -> {
                        val userData = result.data
                        if (userData.tokenNotif == "tokenIntance.token") {
                            userData.tokenNotif = tokenIntance.token

                            val friendListData = FriendListData(
                                "${auth.currentUser?.uid!!}.jpg",
                                auth.currentUser?.displayName!!,
                                auth.currentUser?.uid!!
                            )

                            //memasukkan data user baru ke jankenUsers
                            firebaseUserRepository.addUserData(userData)
                            //memasukkan data user baru ke userData
                            firebaseUserDataCollectionRepository.addUserToUserData(friendListData)
                        } else {
                            userData.tokenNotif = tokenIntance.token
                        }

                        //memasukkan data user dari firebase ke room
                        roomUserRepository.insertUserData(userData, onResult = {
                            Log.d("InsertUser", "DONE: $userData")
                        }, onError = {
                            Log.d("InsertUser", it.message!!)
                        })

                    }
                    is RepositoryResult.Error -> withContext(dispatcher.main()) {
                        _errorLiveData.postValue(result.exception.message)
                    }
                    is RepositoryResult.Canceled -> {
                        withContext(dispatcher.main()) {
                            result.exception?.message.let { _errorLiveData.postValue(it) }
                        }
                    }
                }
            } catch (e: Exception) {
//                Log.d("getDataFirebase", "errro: ${e.message}")
                withContext(dispatcher.main()) {
                    e.message?.let { _errorLiveData.value = it }
                }
            }
        }
    }

    //memasukkan progress achievement user ke firestore jika belum ada (user pertama kali login)
//mengambil data progress achievement dari firestore dan memasukkan ke room
    private fun getAchievementCollection() {
        //uid user
        val uidFirebase = auth.currentUser?.uid!!
        CoroutineScope(dispatcher.io()).launch {
            try {
                var achievementData = listOf(
                    UserAchievementData(null, 1, uidFirebase, 0, "unclaimed"),
                    UserAchievementData(null, 2, uidFirebase, 0, "unclaimed"),
                    UserAchievementData(null, 3, uidFirebase, 0, "unclaimed"),
                    UserAchievementData(null, 4, uidFirebase, 0, "unclaimed"),
                    UserAchievementData(null, 5, uidFirebase, 0, "unclaimed")
                )
                //check apakah collection achievement sudah ada
                when (val result = firebaseAchievementRepository.getAllAchievement()) {
                    is RepositoryResult.Success -> {
                        if (result.data.isEmpty()) {
                            achievementData.forEach {
                                Log.d("Add", it.toString())
                                firebaseAchievementRepository.addAchievementData(it)
                            }
                        } else {
                            achievementData = result.data
                        }

                        achievementData.forEach { achievement ->
                            Log.d("Add", achievement.toString())
                            roomAchievementRepository.insertUserAchievement(
                                achievement,
                                onResult = {
                                    Log.d("intoRoom", achievement.toString())
                                },
                                onError = {
                                    Log.d("intoRoomError", "$achievement ${it.message}")
                                })
                        }
                    }
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> {
                        result.exception?.message.let { _errorLiveData.postValue(it) }
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d("getAchievementError", e.message!!)
                _errorLiveData.value = e.message!!
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomAchievementRepository.onDestroy()
        roomUserRepository.onDestroy()
    }

    class Factory(
        private val auth: FirebaseAuth,
        private val firebaseAuthRepository: FirebaseAuthRepository,
        private val roomUserRepository: RoomUserRepository,
        private val roomAchievementRepository: RoomAchievementRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val firebaseAchievementRepository: FirebaseAchievementRepository,
        private val firebaseUserDataCollectionRepository: FirebaseUserDataCollectionRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel(
                auth,
                firebaseAuthRepository,
                roomUserRepository,
                roomAchievementRepository,
                firebaseUserRepository,
                firebaseAchievementRepository,
                firebaseUserDataCollectionRepository,
                dispatcher
            ) as T
        }
    }
}