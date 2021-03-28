package com.example.jankenteamb.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.jankenteamb.model.room.game.AchievementData
import io.reactivex.Observable

@Dao
interface AchievementDao {
    //Room + RxJava returnnya ganti dengan Observerable<>
    //Tidak perlu menggunakan suspend kalau menggunakan RxJava
    //Work -  Tested
    @Query("SELECT * FROM achievement_data_table")
    fun getAllAchievementData(): Observable<MutableList<AchievementData>>

    //Room non RxJava, error kalau pakai RxJava :|
    //perlu corountine
    //Work - tested - need upgrade
    @Insert(onConflict = REPLACE)
    fun insertAchievement(achievementData: AchievementData)
}