package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData
import com.example.jankenteamb.repository.firebase.FirebaseHistoryRepository
import com.example.jankenteamb.repository.firebase.repository.HistoryRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MenuHistoryViewModel(
    private val firebaseHistoryRepository: HistoryRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _historyLiveData = MutableLiveData<List<GameHistoryFirestoreData>>()
    val historyLiveData: LiveData<List<GameHistoryFirestoreData>> get() = _historyLiveData

    fun getHistoryFromFirebase() {
        CoroutineScope(dispatcher.io()).launch {
            try {
                when(val result = firebaseHistoryRepository.getHistoryFromFirebase()){
                    is RepositoryResult.Success -> _historyLiveData.postValue(result.data)
                    is RepositoryResult.Error -> _errorLiveData.postValue(result.exception.message)
                    is RepositoryResult.Canceled -> result.exception?.message.let {
                        _errorLiveData.postValue(it)
                    }
                }
            } catch (e: Exception) {
                _errorLiveData.postValue(e.message)
                Log.d("getHistoryFirebase: ", e.message.toString())
            }
        }
    }

    class Factory(
        private val firebaseHistoryRepository: FirebaseHistoryRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MenuHistoryViewModel(
                firebaseHistoryRepository, dispatcher
            ) as T
        }
    }
}