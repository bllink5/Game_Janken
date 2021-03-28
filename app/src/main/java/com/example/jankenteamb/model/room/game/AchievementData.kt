package com.example.jankenteamb.model.room.game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_data_table")
data class AchievementData(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "achievement_id",index = true) val achievementId: Int = 0,
    @ColumnInfo(name = "achievement_title") val achievementTitle: String = "",
    @ColumnInfo(name = "achievement_url") val achievementUrl: String = "",
    @ColumnInfo(name = "achievement_max") val achievementMax: Int = 0,
    @ColumnInfo(name = "achievement_point") val achievementPoint: Int = 0
)