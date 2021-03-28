package com.example.jankenteamb.repository.firebase.repository

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserAchievementData

interface IFAchievementRepository {
    suspend fun getAllAchievement(): RepositoryResult<List<UserAchievementData>>

    suspend fun getAchievementByAchievementId(
        achievementId: Int
    ): RepositoryResult<UserAchievementData>

    suspend fun updateAchievementToClaimed(achievementId: Int): RepositoryResult<String>

    suspend fun updateAchievementDataByAchievementId(
        newProgress: Map<String, Int>,
        achievementId: Int
    ):RepositoryResult<String>

    suspend fun addAchievementData(newAchievementData: UserAchievementData):RepositoryResult<String>
}