package com.example.jankenteamb.repository.room

import android.util.Log
import com.example.jankenteamb.api.observer.ApiCompletableObserver
import com.example.jankenteamb.api.observer.ApiObserver
import com.example.jankenteamb.api.observer.ApiSingleObserver
import com.example.jankenteamb.db.AppDatabase
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.model.room.user.UserAchievementData
import com.example.jankenteamb.repository.room.repository.AchievementRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RoomAchievementRepository(private val database: AppDatabase, private val auth: FirebaseAuth):
    AchievementRepository {
    private val userAchievementDao = database.userAchievementDao()
    private val compositeDisposable = CompositeDisposable()

    override fun insertUserAchievement(achievementData: UserAchievementData, onResult: () -> Unit, onError: (Throwable) -> Unit) {
        userAchievementDao.insertUserAchievementToRoom(achievementData)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCompletableObserver(compositeDisposable) {
                override fun onApiSuccess() {
                    onResult()
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun getUserAchievementProgressForRecycleView(
        onResult: (MutableList<JoinAchievementData>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        userAchievementDao.getAchievementProgressForRecycleView(auth.currentUser?.uid!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiObserver<MutableList<JoinAchievementData>>(compositeDisposable) {
                override fun onApiSuccess(data: MutableList<JoinAchievementData>) {
                    onResult(data)
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun getUserAchievementProgress(
        onResult: (MutableList<UserAchievementData>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        userAchievementDao.getUserAchievementProgress(auth.currentUser?.uid!!)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object :
                ApiSingleObserver<MutableList<UserAchievementData>>(compositeDisposable) {
                override fun onApiSuccess(data: MutableList<UserAchievementData>) {
                    onResult(data)
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })

    }

    override fun claimAchievement(
        achievementId: Int,
        onResult: (Int) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        userAchievementDao.updateClaimPoint(
            auth.currentUser?.uid!!, "claimed",
            achievementId
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiSingleObserver<Int>(compositeDisposable) {
                override fun onApiSuccess(data: Int) {
                    Log.d(
                        "claimUpdate",
                        "${auth.currentUser?.uid} claimed $achievementId"
                    )
                    onResult(data)
                }

                override fun onApiError(error: Throwable) {
                    Log.d("claimUpdateError", error.message!!)
                    onError(error)
                }

            })
    }

    override fun updateUserAchievementByAchievementId(
        newProgress: Int,
        achievementId: Int, onResult: () -> Unit, onError: (Throwable) -> Unit
    ) {
        userAchievementDao.updateUserAchievement(
            auth.currentUser?.uid!!,
            newProgress, achievementId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiSingleObserver<Int>(compositeDisposable) {
                override fun onApiSuccess(data: Int) {
                    onResult()
                    Log.d(
                        "updateAchievement",
                        "uid : ${auth.currentUser?.uid} achievementId : $achievementId newProgress : $newProgress"
                    )
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                    Log.d("updateAchievement", "Error: ${error.message}")
                }

            })

    }

    override fun deleteUserAchievement(achievementData: UserAchievementData) {
        userAchievementDao.deleteUserAchievementProgress(achievementData)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}