package com.example.jankenteamb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jankenteamb.model.room.game.AchievementData
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.model.room.user.UserData

@Database(
    entities = [
        AchievementData::class,
        UserAchievementData::class,
        UserData::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun userAchievementDao(): UserAchievementDao
    abstract fun userDataDao(): UserDataDao
}