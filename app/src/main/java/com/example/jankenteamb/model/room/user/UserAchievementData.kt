package com.example.jankenteamb.model.room.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.jankenteamb.model.room.game.AchievementData

@Entity(
    tableName = "user_achievement_progress",
    foreignKeys = [ForeignKey(
        entity = AchievementData::class,
        parentColumns = ["achievement_id"],
        childColumns = ["achievement_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserAchievementData(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Long? = null,
    @ColumnInfo(name = "achievement_id", index = true) val achievementId: Int = 0,
    @ColumnInfo(name = "uid") val uid: String = "",
    @ColumnInfo(name = "achievement_progress") var achievementProgress: Int = 0,
    @ColumnInfo(name = "achievement_claim") var claim: String = ""
)