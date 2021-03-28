package com.example.jankenteamb.model.firestore.achievementData

import com.example.jankenteamb.model.room.game.AchievementData

data class AchievementDataFirestore(
    var achievementList: MutableList<AchievementData> = mutableListOf()
)
