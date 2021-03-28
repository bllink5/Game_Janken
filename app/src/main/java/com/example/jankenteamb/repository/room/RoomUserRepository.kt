package com.example.jankenteamb.repository.room

import com.example.jankenteamb.api.observer.ApiCompletableObserver
import com.example.jankenteamb.api.observer.ApiObserver
import com.example.jankenteamb.api.observer.ApiSingleObserver
import com.example.jankenteamb.db.AppDatabase
import com.example.jankenteamb.model.room.query.PointData
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.room.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RoomUserRepository(private val database: AppDatabase, private val auth: FirebaseAuth):
    UserRepository {
    private val userDataDao = database.userDataDao()
    private val compositeDisposable = CompositeDisposable()

    override fun insertUserData(userData: UserData, onResult: () -> Unit, onError: (Throwable) -> Unit){
        userDataDao.insertUserData(userData)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : ApiCompletableObserver(compositeDisposable){
                override fun onApiSuccess() {
                    onResult()
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun getUserPointDataByUid(onResult: (Int) -> Unit, onError: (Throwable) -> Unit) {
        userDataDao.observeableUserPoint(auth.currentUser?.uid!!)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiObserver<PointData>(compositeDisposable) {
                override fun onApiSuccess(data: PointData) {
                    onResult(data.point)
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }
            })
    }

    override fun updateUserPointByUid(
        newPoint: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        userDataDao.updatePointUser(
            auth.currentUser?.uid!!,
            newPoint
        )
            .observeOn(AndroidSchedulers.mainThread())
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

    override fun getUserDataByUid(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit) {
        userDataDao.getUserData(auth.currentUser?.uid!!)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiSingleObserver<UserData>(compositeDisposable) {
                override fun onApiSuccess(data: UserData) {
                    onResult(data)
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }
            })
    }

    override fun getObservableUserData(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit){
        userDataDao.observeableUserData(auth.currentUser?.uid!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiObserver<UserData>(compositeDisposable){
                override fun onApiSuccess(data: UserData) {
                    onResult(data)
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun updateUserData(
        level: Int,
        win: Int,
        draw: Int,
        lose: Int,
        exp: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        userDataDao.updateUserData(
            auth.currentUser?.uid!!, level,
            win, draw,
            lose, exp
        ).observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCompletableObserver(compositeDisposable){
                override fun onApiSuccess() {
                    onResult()
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun updateUserFrame(frameUrl: String, onResult: () -> Unit, onError: (Throwable) -> Unit){
        userDataDao.updatePhotoFrameUrl(auth.currentUser?.uid!!, "${auth.currentUser?.uid!!}.jpg", frameUrl)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ApiCompletableObserver(compositeDisposable){
                override fun onApiSuccess() {
                    onResult()
                }

                override fun onApiError(error: Throwable) {
                    onError(error)
                }

            })
    }

    override fun deleteUserData(userData: UserData){
        userDataDao.deleteUserData(userData)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}