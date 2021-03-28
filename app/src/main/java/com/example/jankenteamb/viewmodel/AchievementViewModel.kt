package com.example.jankenteamb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.model.room.query.PointData
import com.example.jankenteamb.repository.firebase.FirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.IFAchievementRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.AchievementRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AchievementViewModel(
    private val roomUserRepository: UserRepository,
    private val roomAchievementRepository: AchievementRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val firebaseAchievementRepository: IFAchievementRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _successLiveData = MutableLiveData<String>()
    val successLiveData: LiveData<String> get() = _successLiveData

    private val _showPointLiveData = MutableLiveData<Int>()
    val showPointLiveData: LiveData<Int> get() = _showPointLiveData

    private val _getAchievementLiveData = MutableLiveData<MutableList<JoinAchievementData>>()
    val getAchievementLiveData: LiveData<MutableList<JoinAchievementData>> get() = _getAchievementLiveData

    fun getPointData() {
        val jumlahPoint = MutableLiveData<PointData>()
        roomUserRepository.getUserPointDataByUid(onResult = {
            CoroutineScope(dispatcher.main()).launch {
                _showPointLiveData.value = it
            }
//            Log.d("getPointResult", jumlahPoint.toString())
        }, onError = {
//            Log.d("getPointError", it.message!!)
            _errorLiveData.value = it.message
        }
        )
    }

    fun getAchievementProgress() {
        roomAchievementRepository.getUserAchievementProgressForRecycleView(onResult = {
            Log.d("Get Achievement", it.toString())
            CoroutineScope(dispatcher.main()).launch {
                _getAchievementLiveData.value = it
            }
        }, onError = {
//            Log.d("Get Achievement", "Error")
            _errorLiveData.value = it.message
        })
    }

    fun claimAchievement(achievement: JoinAchievementData) {
        CoroutineScope(dispatcher.io()).launch {
            roomAchievementRepository.claimAchievement(achievement.achievement_id, onResult = {
//                CoroutineScope(dispatcher.main()).launch {
//                    _successLiveData.value = "Berhasil claim achievement!"
//                }
            }, onError = {
                CoroutineScope(dispatcher.main()).launch {
                    _errorLiveData.postValue(it.message)
                }
            })

            when (val data =
                firebaseAchievementRepository.updateAchievementToClaimed(achievement.achievement_id)) {
                is RepositoryResult.Success -> _successLiveData.postValue("Berhasil claim achievement!")
                is RepositoryResult.Error -> _errorLiveData.postValue(data.exception.message)
                is RepositoryResult.Canceled -> {
                    data.exception?.message.let { _errorLiveData.postValue(it) }
                }
            }
        }
    }

    fun updatePoint(point: Int) {
        CoroutineScope(dispatcher.io()).launch {
            roomUserRepository.updateUserPointByUid(point, onResult = {
                CoroutineScope(dispatcher.main()).launch {
                    _showPointLiveData.value = point
//                    Log.d("updateRoom", "Berhasil")
                }
            }, onError = {
                CoroutineScope(dispatcher.main()).launch {
                    _errorLiveData.value = it.message!!
                }
            })
            val pointMap = mapOf("point" to point)
            firebaseUserRepository.updateUserPoint(pointMap)
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomAchievementRepository.onDestroy()
        roomUserRepository.onDestroy()
    }

    class Factory(
        private val roomUserRepository: RoomUserRepository,
        private val roomAchievementRepository: RoomAchievementRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val firebaseAchievementRepository: FirebaseAchievementRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AchievementViewModel(
                roomUserRepository,
                roomAchievementRepository,
                firebaseUserRepository,
                firebaseAchievementRepository,
                dispatcher
            ) as T
        }

    }
}
