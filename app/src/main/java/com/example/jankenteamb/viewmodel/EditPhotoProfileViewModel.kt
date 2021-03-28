package com.example.jankenteamb.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestoreAdapter
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.FirebaseFrameCollectionRepository
import com.example.jankenteamb.repository.firebase.FirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.FrameCollectionRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.firebase.repository.StorageRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPhotoProfileViewModel(
    private val firebaseStorageRepository: StorageRepository,
    private val firebaseFrameCollectionRepository: FrameCollectionRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val roomUserRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _photoUriLiveData = MutableLiveData<Uri>()
    val photoUriLiveData: LiveData<Uri> get() = _photoUriLiveData

    private val _frameListLiveData = MutableLiveData<List<ShopListDataFirestoreAdapter>>()
    val frameListLiveData: LiveData<List<ShopListDataFirestoreAdapter>> get() = _frameListLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

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

    fun getUserFrameList() {
        val listFrame = mutableListOf<ShopListDataFirestoreAdapter>()
        lateinit var userData: UserData
        CoroutineScope(dispatcher.io()).launch {
            try {
                when (val result = firebaseUserRepository.getUserData()) {
                    is RepositoryResult.Success -> {
                        userData = result.data
                    }
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(
                            it
                        )
                    }
                }

                when (val result =
                    firebaseFrameCollectionRepository.getUserFrameList()) {
                    is RepositoryResult.Success -> {
                        result.data.forEach { frame ->
                            if (frame.frameUrl != userData.frameUrl) {
                                val frameUri =
                                    firebaseStorageRepository.downloadFrameProfile(
                                        frame.frameUrl
                                    )

                                val shopAdapter =
                                    ShopListDataFirestoreAdapter(
                                        frame.frameUrl,
                                        frameUri,
                                        0,
                                        frame.title,
                                        false
                                    )
                                listFrame.add(shopAdapter)
                            }else{
                                val frameUri =
                                    firebaseStorageRepository.downloadFrameProfile(
                                        frame.frameUrl
                                    )

                                val shopAdapter =
                                    ShopListDataFirestoreAdapter(
                                        frame.frameUrl,
                                        frameUri,
                                        0,
                                        frame.title,
                                        true
                                    )
                                listFrame.add(shopAdapter)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            _frameListLiveData.postValue(listFrame)
                        }
                    }
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(
                            it
                        )
                    }
                }
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message.toString())
                Log.d("getUserFrameList: ", e.message.toString())
            }
        }
    }

    fun uploadPhotoProfile(photoUri: Uri) {
        val uid = auth.currentUser?.uid.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseStorageRepository.uploadPhotoProfile(uid, photoUri, onResult = { uri ->
                    _photoUriLiveData.postValue(uri)
                })
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message)
                Log.d("uploadPhotoProfile: ", e.message.toString())
            }
        }
    }

    fun updateFrameUrl(frameUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                roomUserRepository.updateUserFrame(frameUrl, onResult = {
                    Log.d("updateFrame", "Berhasil")
                }, onError = {
                    _errorLiveData.postValue(it.message)
                })
                val frameUrlMap = mapOf("frameUrl" to frameUrl)
                firebaseUserRepository.updateFrameUrl(frameUrlMap)
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message)
            }
        }
    }

    class Factory(
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val firebaseFrameCollectionRepository: FirebaseFrameCollectionRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val roomUserRepository: RoomUserRepository,
        private val auth: FirebaseAuth,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditPhotoProfileViewModel(
                firebaseStorageRepository,
                firebaseFrameCollectionRepository,
                firebaseUserRepository,
                roomUserRepository,
                auth,
                dispatcher
            ) as T
        }
    }

}