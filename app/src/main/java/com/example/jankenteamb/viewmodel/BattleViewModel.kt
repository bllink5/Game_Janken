package com.example.jankenteamb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.model.battlevs.Battle
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.FirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FirebaseHistoryRepository
import com.example.jankenteamb.repository.firebase.FirebaseUserRepository
import com.example.jankenteamb.repository.firebase.repository.HistoryRepository
import com.example.jankenteamb.repository.firebase.repository.IFAchievementRepository
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.repository.room.repository.AchievementRepository
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BattleViewModel(
    private val roomUserRepository: UserRepository,
    private val roomAchievementRepository: AchievementRepository,
    private val firebaseHistoryRepository: HistoryRepository,
    private val firebaseUserRepository: IFUserRepository,
    private val firebaseAchievementRepository: IFAchievementRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel(), Battle {
    private val _userLiveData = MutableLiveData<UserData>()
    val userLiveData: LiveData<UserData> get() = _userLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _battleResult = MutableLiveData<String>()
    val battleResult: LiveData<String> get() = _battleResult

    private val _historyResult = MutableLiveData<String>()
    val historyResult: LiveData<String> get() = _historyResult

    private var isLevelChange = false
    private var isWinChange = false
    private var isLoseChange = false

    //getDataFromRoom
    fun getUserDataFromRoom() {
        CoroutineScope(dispatcher.main()).launch {
            roomUserRepository.getObservableUserData(onResult = { data ->
                _userLiveData.value = data
            }, onError = { error ->
                _errorLiveData.value = error.message!!
            })
        }
    }

    //getResult untuk dipanggil di activity, menggantikan result.theResult
    fun getResult(
        playerOne: String,
        playerTwo: String, pone: String, ptwo: String
    ) {
        val result = theResult(playerOne, playerTwo, pone, ptwo)
        _battleResult.value = result["showResult"]
        _historyResult.value = result["historyResult"]
        updateUserData(result.getValue("historyResult"))
    }

    //addHistoryToFirebase dari presenter
    fun addHistoryToFirebase(
        gameMode: String,
        gameResult: String
    ) {
        CoroutineScope(dispatcher.io()).launch {
            firebaseHistoryRepository.addHistory(gameMode, gameResult)
        }
    }

    private fun changeUserData(userData: UserData, gameResult: String): UserData {
        when (gameResult) {
            "Player Win" -> {
                userData.win += 1
                userData.exp += 5
                isWinChange = true
            }
            "Player Draw" -> {
                userData.draw += 1
                userData.exp += 3
            }
            else -> {
                userData.lose += 1
                userData.exp += 0
                isLoseChange = true
            }
        }

        if (userData.level != userLevel(userData.exp)) {
            userData.level = userLevel(userData.exp)
            isLevelChange = true
        }
        return userData
    }

    //updateUserData dari battlePresenter
    fun updateUserData(gameResult: String) {

        roomUserRepository.getUserDataByUid(onResult = { data ->
            val userData = changeUserData(data, gameResult)

            //log perubahan data user untuk memastikan tidak ada data yang salah sebelum diupdate
//            Log.d("userDataChange", userData.toString())
            CoroutineScope(dispatcher.io()).launch {
                roomUserRepository.updateUserData(
                    userData.level, userData.win,
                    userData.draw, userData.lose, userData.exp, onResult = {
//                        Log.d("updateUserData", "Success : $userData")
                    },
                    onError = {
                        _errorLiveData.postValue(it.message)
                    }
                )

                firebaseUserRepository.updateUserData(userData)

                //mengupdate progress achievement level jika level user berubah dan level user belum lebih dari 5
                if (userData.level <= 5 && isLevelChange) {
//                    Log.d("updateProgress", userData.level.toString())
                    roomAchievementRepository.updateUserAchievementByAchievementId(
                        userData.level, 1, onResult = {

                        }, onError = {
                            _errorLiveData.value = it.message!!
                        })

                    firebaseAchievementRepository.updateAchievementDataByAchievementId(
                        mapOf("achievementProgress" to userData.level), 1
                    )
                }

                //mengupdate progress achievement Lose user jika user baru saja mandapatkan result lose dan jumlah lose belum lebih dari 3
                if (userData.lose <= 3 && isLoseChange) {
//                    Log.d("updateProgress", userData.lose.toString())
                    roomAchievementRepository.updateUserAchievementByAchievementId(
                        userData.lose, 2, onResult = {

                        }, onError = {
                            _errorLiveData.value = it.message!!
                        })

                    firebaseAchievementRepository.updateAchievementDataByAchievementId(
                        mapOf("achievementProgress" to userData.lose), 2
                    )
                }

                //mengupdate progress achievement Win user jika user baru saja mendapatkan result win dan jumlah Win belum lebih dari 3
                if (userData.win <= 3 && isWinChange) {
                    roomAchievementRepository.updateUserAchievementByAchievementId(
                        userData.win, 3, onResult = {

                        }, onError = {
                            _errorLiveData.value = it.message!!
                        })

                    firebaseAchievementRepository.updateAchievementDataByAchievementId(
                        mapOf("achievementProgress" to userData.win), 3
                    )
                }


                //mengupdate progress achievement bermain 15 kali selama jumlah pertandingan belum lewat dari 15
                if (userData.lose + userData.win + userData.draw <= 15) {
//                    Log.d(
//                        "updateProgress",
//                        (userData.lose + userData.win + userData.draw).toString()
//                    )

                    roomAchievementRepository.updateUserAchievementByAchievementId(
                        userData.lose + userData.win + userData.draw, 4, onResult = {

                        }, onError = {
                            _errorLiveData.value = it.message!!
                        })

                    firebaseAchievementRepository.updateAchievementDataByAchievementId(
                        mapOf("achievementProgress" to userData.lose + userData.win + userData.draw),
                        4
                    )
                }


                //mengupdate progress achievement bermain 30 kali selama jumlah pertandingan belum lewat dari 30
                if (userData.lose + userData.win + userData.draw <= 30) {

                    roomAchievementRepository.updateUserAchievementByAchievementId(
                        userData.lose + userData.win + userData.draw, 5, onResult = {

                        }, onError = {
                            _errorLiveData.value = it.message!!
                        })

                    firebaseAchievementRepository.updateAchievementDataByAchievementId(
                        mapOf("achievementProgress" to userData.lose + userData.win + userData.draw),
                        5
                    )
                }
            }
        }, onError = {
            _errorLiveData.value = it.message!!
        })
    }

    //clear compositeDisposable di semua repository
    override fun onCleared() {
        super.onCleared()
        roomAchievementRepository.onDestroy()
        roomUserRepository.onDestroy()
    }

    class Factory(
        private val roomUserRepository: RoomUserRepository,
        private val roomAchievementRepository: RoomAchievementRepository,
        private val firebaseHistoryRepository: FirebaseHistoryRepository,
        private val firebaseUserRepository: FirebaseUserRepository,
        private val firebaseAchievementRepository: FirebaseAchievementRepository,
        private val dispatcher: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BattleViewModel(
                roomUserRepository,
                roomAchievementRepository,
                firebaseHistoryRepository,
                firebaseUserRepository,
                firebaseAchievementRepository,
                dispatcher
            ) as T
        }
    }

}