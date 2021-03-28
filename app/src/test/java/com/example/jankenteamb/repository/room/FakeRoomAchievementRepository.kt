package com.example.jankenteamb.repository.room

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.repository.room.repository.AchievementRepository

class FakeRoomAchievementRepository : AchievementRepository {
    var achievementServiceData: LinkedHashMap<Int, UserAchievementData> = LinkedHashMap()
    var joinAchievementServiceData: LinkedHashMap<Int, JoinAchievementData> = LinkedHashMap()
    private val observableAchievement = MutableLiveData<List<UserAchievementData>>()
    private var isError = false
    private var userUid: String? = null

    fun setup(uid: String) {
        userUid = uid
        val achievements = listOf(
            UserAchievementData(null, 1, userUid!!, 0, "unclaimed"),
            UserAchievementData(null, 2, userUid!!, 0, "unclaimed"),
            UserAchievementData(null, 3, userUid!!, 0, "unclaimed"),
            UserAchievementData(null, 4, userUid!!, 0, "unclaimed"),
            UserAchievementData(null, 5, userUid!!, 0, "unclaimed")
        )
        for (achievement in achievements) {
            val tempAchievement = JoinAchievementData(
                "title${achievement.achievementId}",
                1 * achievement.achievementId,
                10 * achievement.achievementId,
                userUid!!,
                achievement.uid,
                achievement.achievementProgress,
                achievement.claim,
                achievement.achievementId
            )
            joinAchievementServiceData[achievement.achievementId] = tempAchievement
            achievementServiceData[achievement.achievementId] = achievement
        }

    }

    fun serError(error: Boolean) {
        isError = error
    }

    fun refreshData() {
        observableAchievement.value = achievementServiceData.values.toList()
    }

    override fun insertUserAchievement(
        achievementData: UserAchievementData,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError) {
            onError(Throwable("Error"))
        } else {
            achievementServiceData[achievementData.achievementId] = achievementData
            onResult()
        }
    }

    override fun getUserAchievementProgressForRecycleView(
        onResult: (MutableList<JoinAchievementData>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError) {
            onError(Throwable("Error"))
        } else {
            onResult(joinAchievementServiceData.values.toList() as MutableList<JoinAchievementData>)
        }
    }

    override fun getUserAchievementProgress(
        onResult: (MutableList<UserAchievementData>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError) {
            onError(Throwable("Error"))
        } else {
            observableAchievement.value = achievementServiceData.values.toList()
            onResult(observableAchievement.value as MutableList<UserAchievementData>)
        }
    }

    override fun claimAchievement(
        achievementId: Int,
        onResult: (Int) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError) {
            onError(Throwable("Error"))
        } else {
            val achievement = UserAchievementData(1, achievementId, userUid!!, 0, "claimed")
            achievementServiceData[achievementId] = achievement
//            refreshData()
            onResult(achievementId)

        }

    }

    override fun updateUserAchievementByAchievementId(
        newProgress: Int,
        achievementId: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError) {
            onError(Throwable("Error"))
        } else {
            val achievement =
                UserAchievementData(null, achievementId, userUid!!, newProgress, "claimed")
            achievementServiceData[achievementId] = achievement
            refreshData()
            onResult()
        }
    }

    override fun deleteUserAchievement(achievementData: UserAchievementData) {
        achievementServiceData.remove(achievementData.achievementId)
        refreshData()
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }


}