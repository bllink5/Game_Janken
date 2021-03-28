package com.example.jankenteamb.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.jankenteamb.model.room.query.PointData
import com.example.jankenteamb.model.room.user.UserData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user_data WHERE uid = :uid")
    fun observeableUserData(uid:String): Observable<UserData>

    @Query("SELECT point FROM user_data WHERE uid = :uid")
    fun observeableUserPoint(uid:String): Observable<PointData>

    @Query("SELECT * FROM user_data WHERE uid = :uid")
    fun getUserData(uid: String): Single<UserData>

    @Insert(onConflict = REPLACE)
    fun insertUserData(userData: UserData) : Completable

    @Delete
    fun deleteUserData(userData: UserData)

    @Query("UPDATE user_data SET level = :level, win = :win, draw = :draw, lose = :lose, exp = :exp WHERE uid = :uid")
    fun updateUserData(uid:String, level:Int, win:Int, draw:Int, lose:Int, exp:Int) : Completable

    @Query("UPDATE user_data SET photoUrl = :photoUrl, frameUrl = :frameUrl WHERE uid = :uid")
    fun updatePhotoFrameUrl(uid:String, photoUrl: String, frameUrl:String) : Completable

    @Query("UPDATE user_data SET point = :point WHERE uid = :uid")
    fun updatePointUser(uid:String, point: Int) : Completable
}