package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListDataWithUri
import com.example.jankenteamb.repository.firebase.FirebaseFriendListRepository
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.repository.FriendListRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val firebaseStorageRepository: StorageRepository,
    private val firebaseFriendListRepository: FriendListRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _successDeleteFriendLiveData = MutableLiveData<String>()
    val successDeleteFriendLiveData: LiveData<String> get() = _successDeleteFriendLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _friendListLiveData = MutableLiveData<List<FriendListDataWithUri>>()
    val friendListLiveData: LiveData<List<FriendListDataWithUri>> get() = _friendListLiveData

    fun getFriendList() {
        val friendListWithUri = mutableListOf<FriendListDataWithUri>()
        CoroutineScope(dispatcher.io()).launch {
            try {
                when (val data = firebaseFriendListRepository.getFriendList()) {
                    is RepositoryResult.Success -> {
                        data.data.forEach { friend ->
                            val photoUri =
                                firebaseStorageRepository.downloadPhotoProfile(friend.uid)
                            Log.d("dlPhotoProfile", "uri: $photoUri")
                            val friendWithUri = FriendListDataWithUri(
                                photoUri,
                                friend.photoUrl,
                                friend.username,
                                friend.uid
                            )
                            friendListWithUri.add(friendWithUri)
                        }
                        _friendListLiveData.postValue(friendListWithUri)
                    }
                    is RepositoryResult.Error -> _errorLiveData.postValue(data.exception.message)
                    is RepositoryResult.Canceled -> data.exception?.message.let{_errorLiveData.postValue(it)}
                }
            } catch (e: Exception) {
                _errorLiveData.value = e.message!!
            }
        }
    }

    fun deleteFriend(friendUid: String) {
        CoroutineScope(dispatcher.io()).launch {
            when(val data =firebaseFriendListRepository.deleteFriend(friendUid)){
                is RepositoryResult.Success -> {
                    _successDeleteFriendLiveData.postValue("Berhasil menghapus teman")
                    getFriendList()
                }
                is RepositoryResult.Error -> _errorLiveData.postValue(data.exception.message)
                is RepositoryResult.Canceled -> data.exception?.message.let{_errorLiveData.postValue(it)}
            }
        }
    }

    class Factory(
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val firebaseFriendListRepository: FirebaseFriendListRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FriendListViewModel(
                firebaseStorageRepository,
                firebaseFriendListRepository,
                dispatcher
            ) as T
        }
    }
}