package com.example.jankenteamb.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.model.room.user.UserAchievementData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UserAchievementDao {
    //Room + RxJava returnnya ganti dengan Observerable<>
    //Tidak perlu menggunakan suspend kalau menggunakan RxJava
    //menggabungkan 2 table dengan join, hasilnya dipetakan ke data class
    //need testing
    @Query("SELECT achievement_url, achievement_title, achievement_point, achievement_max, achievement_progress, uid, achievement_claim, ach_tbl.achievement_id FROM achievement_data_table AS ach_tbl INNER JOIN user_achievement_progress AS usr_tbl ON ach_tbl.achievement_id = usr_tbl.achievement_id WHERE usr_tbl.uid = :uid")
    fun getAchievementProgressForRecycleView(uid:String): Observable<MutableList<JoinAchievementData>>

    //Menggambil data dari user_achievementProgress saja
    @Query("SELECT * FROM user_achievement_progress WHERE uid = :uid")
    fun getUserAchievementProgress(uid:String): Single<MutableList<UserAchievementData>>

    //update data achievement user
    //need testing
    @Query("UPDATE user_achievement_progress SET achievement_progress = :progress WHERE uid = :uid AND achievement_id = :achievementId")
    fun updateUserAchievement(uid:String, progress:Int, achievementId:Int) : Single<Int>

    //update data achievement user
    //need testing
    @Query("UPDATE user_achievement_progress SET achievement_claim = :achievementClaim WHERE uid = :uid AND achievement_id = :achievementId")
    fun updateClaimPoint(uid:String, achievementClaim:String, achievementId:Int) : Single<Int>


    //insert data achievement user
    //need testing
    @Insert(onConflict = REPLACE)
    fun insertUserAchievementToRoom(userAchievementData: UserAchievementData) : Completable

    //delete data
    @Delete
    fun deleteUserAchievementProgress(userAchievementData: UserAchievementData)
}