package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.GameHistoryFirestoreData
import com.example.jankenteamb.repository.firebase.repository.HistoryRepository
import com.google.firebase.Timestamp
import java.util.*

class FakeFirebaseHistoryRepository : HistoryRepository {
    var historyServiceData: LinkedHashMap<Int, GameHistoryFirestoreData> = LinkedHashMap()
    private val observableHistory = MutableLiveData<List<GameHistoryFirestoreData>>()

    private lateinit var resultState: RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))

    private var historyId = 0
    private var userUid = ""

    fun setup(uid:String) {
        userUid = uid
        val histories = listOf(
            GameHistoryFirestoreData("Single Player", "Player Win", Timestamp(Date(20200506))),
            GameHistoryFirestoreData("Single Player", "Player Lose", Timestamp(Date(20200506))),
            GameHistoryFirestoreData("Single Player", "Player Draw", Timestamp(Date(20200506)))
        )
        for (history in histories) {
            historyServiceData[historyId] = history
            historyId++
        }
    }

    fun setScenario(scenario: RepositoryResult<Any>) {
        resultState = scenario
    }

    fun refreshData() {
        observableHistory.value = historyServiceData.values.toList()
    }

    override suspend fun addHistory(gameMode: String,
                                    gameResult: String): RepositoryResult<String> {
        val history = GameHistoryFirestoreData(gameMode, gameResult, Timestamp(Date(20200506)))
        return when (resultState) {
            is RepositoryResult.Success -> {
                historyServiceData[historyId] = history
                historyId++
                refreshData()
                RepositoryResult.Success("Add data baru berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun getHistoryFromFirebase(): RepositoryResult<List<GameHistoryFirestoreData>> {
        return when(resultState){
            is RepositoryResult.Success -> RepositoryResult.Success(historyServiceData.values.toList())
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

}