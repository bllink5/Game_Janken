package com.example.jankenteamb.repository.room.repository

import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.model.room.user.UserAchievementData

interface AchievementRepository {

    fun insertUserAchievement(achievementData: UserAchievementData, onResult: () -> Unit, onError: (Throwable) -> Unit)

    fun getUserAchievementProgressForRecycleView(onResult: (MutableList<JoinAchievementData>) -> Unit,
                                                 onError: (Throwable) -> Unit)

    fun getUserAchievementProgress(onResult: (MutableList<UserAchievementData>) -> Unit,
                                   onError: (Throwable) -> Unit)

    fun claimAchievement(achievementId: Int,
                         onResult: (Int) -> Unit,
                         onError: (Throwable) -> Unit)

    fun updateUserAchievementByAchievementId(
        newProgress: Int,
        achievementId: Int, onResult: () -> Unit, onError: (Throwable) -> Unit
    )

    fun deleteUserAchievement(achievementData: UserAchievementData)

    fun onDestroy()
}