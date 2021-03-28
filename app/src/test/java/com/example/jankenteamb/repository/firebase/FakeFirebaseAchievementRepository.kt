package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.repository.firebase.repository.IFAchievementRepository
import java.util.*

class FakeFirebaseAchievementRepository : IFAchievementRepository {
    var achievementServiceData: LinkedHashMap<Int, UserAchievementData> = LinkedHashMap()
    private val observableAchievement = MutableLiveData<List<UserAchievementData>>()

    private lateinit var resultState : RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))

    private var userUid: String? = null

    fun setup(uid:String) {
        userUid = uid
        val achievements = listOf(
            UserAchievementData(null, 1, userUid!!,0,"unclaimed"),
            UserAchievementData(null, 2, userUid!!,0,"unclaimed"),
            UserAchievementData(null, 3, userUid!!,0,"unclaimed"),
            UserAchievementData(null, 4, userUid!!,0,"unclaimed"),
            UserAchievementData(null, 5, userUid!!,0,"unclaimed")
        )
        for (achievement in achievements) {
            achievementServiceData[achievement.achievementId] = achievement
        }
    }

    fun setScenario(scenario:RepositoryResult<Any>){
        resultState = scenario
    }

    private fun refreshData(){
        observableAchievement.value = achievementServiceData.values.toList()
    }

    override suspend fun getAllAchievement(): RepositoryResult<List<UserAchievementData>> {
        return when(resultState){
            is RepositoryResult.Success -> RepositoryResult.Success(observableAchievement.value) as RepositoryResult<List<UserAchievementData>>
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun getAchievementByAchievementId(achievementId: Int): RepositoryResult<UserAchievementData> {
        return when(resultState){
            is RepositoryResult.Success -> RepositoryResult.Success(achievementServiceData[achievementId]) as RepositoryResult<UserAchievementData>
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateAchievementToClaimed(achievementId: Int): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                achievementServiceData[achievementId]?.claim = "claimed"
                refreshData()
                RepositoryResult.Success("Update berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateAchievementDataByAchievementId(
        newProgress: Map<String, Int>,
        achievementId: Int
    ): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                 newProgress["achievementProgress"]?.let {
                     achievementServiceData[achievementId]?.achievementProgress = it
                }
                refreshData()
                RepositoryResult.Success("Update berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun addAchievementData(newAchievementData: UserAchievementData): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                achievementServiceData[newAchievementData.achievementId] = newAchievementData
                refreshData()
                RepositoryResult.Success("Add data baru berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }
}