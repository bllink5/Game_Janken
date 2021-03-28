package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.api.Data
import com.example.jankenteamb.model.api.PayloadNotif
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import com.example.jankenteamb.repository.firebase.*
import com.example.jankenteamb.repository.firebase.repository.*
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendSuggestViewModel(
    private val firebaseFriendListRepository: FriendListRepository,
    private val auth: FirebaseAuth,
    private val firebaseUserDataCollectionRepository: UserDataCollectionRepository,
    private val firebaseStorageRepository: StorageRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val firebaseNotificationRepository: NotificationRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _onErrorLiveData = MutableLiveData<String>()
    val onErrorLiveData: LiveData<String> get() = _onErrorLiveData

    private val _friendSuggestLiveData = MutableLiveData<List<FriendListDataWithUri>>()
    val friendSuggestLiveData: LiveData<List<FriendListDataWithUri>> get() = _friendSuggestLiveData

    private val _onSuccessLiveData = MutableLiveData<String>()
    val onSuccessLiveData: LiveData<String> get() = _onSuccessLiveData

    fun getFriendSuggest() {
        val friendSuggest = mutableListOf<FriendListDataWithUri>()
        CoroutineScope(dispatcher.io()).launch {
            try {
                //variable untuk menampung uid friendList user
                val friendUidList = mutableListOf<String>()
                when (val data = firebaseFriendListRepository.getFriendList()) {
                    is RepositoryResult.Success -> {
                        if (data.data.isNotEmpty()) {
                            data.data.forEach {
                                friendUidList.add(it.uid)
                            }
                            friendUidList.add(auth.currentUser?.uid!!)
                        } else {
                            friendUidList.add(auth.currentUser?.uid!!)
                        }
                    }
                    is RepositoryResult.Error -> _onErrorLiveData.postValue(data.exception.message)
                    is RepositoryResult.Canceled -> data.exception?.message.let {
                        _onErrorLiveData.postValue(it)
                    }
                }

                when (val data = firebaseUserDataCollectionRepository.getAllUserData()) {
                    is RepositoryResult.Success -> {
                        data.data.forEach { singleUserData ->
                            if (singleUserData.uid !in friendUidList) {
                                val photoUri =
                                    firebaseStorageRepository.downloadPhotoProfile(
                                        singleUserData.uid
                                    )
                                Log.d("DLphotoUri", "succ: $photoUri")
                                val friendWithUri = FriendListDataWithUri(
                                    photoUri,
                                    singleUserData.photoUrl,
                                    singleUserData.username,
                                    singleUserData.uid
                                )
                                friendSuggest.add(friendWithUri)
                            }
                        }
                        withContext(dispatcher.main()) {
                            _friendSuggestLiveData.value = friendSuggest
                        }
                    }
                    is RepositoryResult.Error -> _onErrorLiveData.postValue(data.exception.message)
                    is RepositoryResult.Canceled -> data.exception?.message.let {
                        _onErrorLiveData.postValue(it)
                    }
                }
            } catch (e: Exception) {
                _onErrorLiveData.postValue(e.message)
            }
        }
    }

    fun addFriend(photoUrl: String, username: String, uid: String) {
        val friendListData = FriendListData(photoUrl, username, uid)
        val userListData = FriendListData(
            "${auth.currentUser?.uid}.jpg",
            auth.currentUser?.displayName!!,
            auth.currentUser?.uid!!
        )
        lateinit var modelNotif: PayloadNotif
        CoroutineScope(dispatcher.io()).launch {
            try {
                //mengambil token user dan membuat template notifikasi
                when (val result = firebaseUserRepository.getUserDataByUid(friendListData.uid)) {
                    is RepositoryResult.Success -> {
                        modelNotif = PayloadNotif(
                            to = result.data.tokenNotif,
                            data = Data(
                                "${auth.currentUser?.displayName} menambahkan anda sebagai temannya."
                            )
                        )
                    }
                    is RepositoryResult.Error -> _onErrorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _onErrorLiveData.postValue(it)
                    }
                }

                //add data teman ke firestore
                when (val data =
                    firebaseFriendListRepository.addFriendToUser(userListData, friendListData)) {
                    is RepositoryResult.Success -> {
                    }
                    is RepositoryResult.Error -> _onErrorLiveData.postValue(data.exception.message)
                    is RepositoryResult.Canceled -> data.exception?.message.let {
                        _onErrorLiveData.postValue(it)
                    }
                }

                when (val data =
                    firebaseFriendListRepository.addUserToFriend(userListData, friendListData)) {
                    is RepositoryResult.Success -> {
                    }
                    is RepositoryResult.Error -> _onErrorLiveData.postValue(data.exception.message)
                    is RepositoryResult.Canceled -> data.exception?.message.let {
                        _onErrorLiveData.postValue(it)
                    }
                }

                compositeDisposable.add(
                    firebaseNotificationRepository.pushNotification(
                        userListData.uid,
                        friendListData.uid,
                        modelNotif
                    ).subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe({

                        }, {
                            _onErrorLiveData.postValue(it.message)
                        })
                )

                firebaseNotificationRepository.addNotificationToFirestore(
                    friendListData.uid,
                    modelNotif
                )

                _onSuccessLiveData.postValue("Berhasil menambahkan $username sebagai teman")

            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    _onErrorLiveData.value = e.message!!
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    class Factory(
        private val firebaseFriendListRepository: FirebaseFriendListRepository,
        private val auth: FirebaseAuth,
        private val firebaseUserDataCollectionRepository: FirebaseUserDataCollectionRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val firebaseNotificationRepository: FirebaseNotificationRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FriendSuggestViewModel(
                firebaseFriendListRepository,
                auth,
                firebaseUserDataCollectionRepository,
                firebaseStorageRepository,
                firebaseUserRepository,
                firebaseNotificationRepository,
                dispatcher
            ) as T
        }
    }
}