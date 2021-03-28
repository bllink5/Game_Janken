package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.shop.FrameUserData
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestore
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import com.example.jankenteamb.repository.firebase.FirebaseFrameCollectionRepository
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.FrameCollectionRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopViewModel(
    private val firebaseFrameCollectionRepository: FrameCollectionRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val firebaseStorageRepository: StorageRepository,
    private val roomUserRepository: UserRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _userPointLiveData = MutableLiveData<Int>()
    val userPointLiveData: LiveData<Int> get() = _userPointLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> get() = _loadingLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

    private val _frameCollectionLiveData =
        MutableLiveData<MutableList<ShopListDataFirestoreAdapter>>()
    val frameCollectionLiveData: LiveData<MutableList<ShopListDataFirestoreAdapter>> get() = _frameCollectionLiveData

    fun getPointData() {
        CoroutineScope(dispatcher.main()).launch {
            roomUserRepository.getUserPointDataByUid(onResult = { data ->
                Log.d("getPointData", "point: $data")
                _userPointLiveData.postValue(data)
            }, onError = {
                Log.d("getPointData", "error: ${it.message}")
                _errorLiveData.value = it.message!!
            })
        }
    }

    fun getShopListFromFirebase() {
        _loadingLiveData.value = true
        val listFrame = mutableListOf<ShopListDataFirestoreAdapter>()
        var allFrameList = mutableListOf<ShopListDataFirestore>()
        val userFrameList = mutableListOf<String>()
        CoroutineScope(dispatcher.io()).launch {
            try {
                when (val result = firebaseFrameCollectionRepository.getAllFrameList()) {
                    is RepositoryResult.Success -> allFrameList = result.data.frame
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(
                            it
                        )
                    }
                }

                when (val result = firebaseFrameCollectionRepository.getUserFrameList()) {
                    is RepositoryResult.Success -> {
                        if (result.data.isNotEmpty()) {
                            result.data.forEach {
                                userFrameList.add(it.title)
                            }
                        }
                    }
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(
                            it
                        )
                    }
                }

                allFrameList.forEach {
                    if (it.title !in userFrameList) {
                        val frameUri = firebaseStorageRepository.downloadFrameProfile(it.frameUrl)
                        Log.d("frameUri", frameUri.path!!)
                        val shopAdapter = ShopListDataFirestoreAdapter(
                            it.frameUrl,
                            frameUri,
                            it.price,
                            it.title
                        )
                        Log.d("shopAdapter", shopAdapter.toString())
                        listFrame.add(shopAdapter)
                    }
                }

                CoroutineScope(dispatcher.main()).launch {
                    _frameCollectionLiveData.value = listFrame
                    _loadingLiveData.value = false
                }

            } catch (e: Exception) {
                CoroutineScope(dispatcher.main()).launch {
                    _errorLiveData.value = e.message!!
                }
            }
        }
    }

    fun addFrameUser(
        frameUrl: String,
        title: String,
        price: Int
    ) {
        val frame = FrameUserData(frameUrl, title)
        CoroutineScope(Dispatchers.IO).launch {
            roomUserRepository.getUserDataByUid(onResult = { data ->
//                userLiveData.value = data
                if (data.point >= price) {
                    try {
                        CoroutineScope(dispatcher.io()).launch {
                            when (val result = firebaseFrameCollectionRepository.addFrame(frame)) {
                                is RepositoryResult.Success -> {
                                    Log.d("addUserFrame", "list: ${result.data}")
//                                    _successLiveData.postValue(result.data)
                                }
                                is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                                is RepositoryResult.Canceled -> result.exception?.message.let {
                                    _errorLiveData.postValue(
                                        it
                                    )
                                }
                            }

                            roomUserRepository.updateUserPointByUid(data.point - price, onResult = {
                            }, onError = {
                                _errorLiveData.value = it.message!!
                            })

                            firebaseUserRepository.updateUserPoint(
                                mutableMapOf("point" to (data.point - price))
                            )
                            withContext(dispatcher.main()) {
                                _successLiveData.value = "Frame Dibeli"
                            }
                        }

                    } catch (e: Exception) {
                        CoroutineScope(dispatcher.main()).launch {
                            _errorLiveData.value = e.message
                        }
                    }
                } else {
                    CoroutineScope(dispatcher.main()).launch {
                        _errorLiveData.value = "Point yang anda miliki tidak cukup"
                    }
                }
            }, onError = { error ->
                CoroutineScope(dispatcher.main()).launch {
                    _errorLiveData.value = error.message!!
                }
            })
        }
    }

    //clear compositeDisposable di semua repository
    override fun onCleared() {
        super.onCleared()
        roomUserRepository.onDestroy()
    }

    class Factory(
        private val firebaseFrameCollectionRepository: FirebaseFrameCollectionRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val roomUserRepository: RoomUserRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ShopViewModel(
                firebaseFrameCollectionRepository,
                firebaseUserRepository,
                firebaseStorageRepository,
                roomUserRepository,
                dispatcher
            ) as T
        }
    }

}