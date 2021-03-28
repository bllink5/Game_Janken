package com.example.jankenteamb.repository.room

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.room.repository.UserRepository

class FakeRoomUserRepository: UserRepository {
    var userServiceData: LinkedHashMap<String, UserData> = LinkedHashMap()

    private val observableUser = MutableLiveData<List<UserData>>()
    private var isError = false
    private var userUid: String? = null

    fun setup(uid:String) {
        userUid = uid
        val users = listOf(
            UserData(1, "username1","kfakg3865", 1, 0,0,0,0,0,"photoUrl1","frameUrl1", "tokenNotif1"),
            UserData(2, "username2","kfakgasd34", 1, 0,0,0,0,0,"photoUrl2","frameUrl2", "tokenNotif2"),
            UserData(3, "username3", userUid!!, 1, 0,0,0,0,0,"photoUrl3","frameUrl3", "tokenNotif3")
        )
        for (user in users) {
            userServiceData[user.uid] = user
        }
    }

    fun setError(error: Boolean){
        isError = error
    }

    fun refreshData(){
        observableUser.value = userServiceData.values.toList()
    }

    override fun insertUserData(
        userData: UserData,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            userServiceData[userData.uid] = userData
            refreshData()
            onResult()
        }
    }

    override fun getUserPointDataByUid(onResult: (Int) -> Unit, onError: (Throwable) -> Unit) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            val userData = userServiceData[userUid]
            onResult(userData?.point!!)
        }
    }

    override fun updateUserPointByUid(
        newPoint: Int,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            val userData = userServiceData[userUid]
            userData?.point = newPoint
            userServiceData[userUid!!] = userData!!
            refreshData()
            onResult()
        }
    }

    override fun getUserDataByUid(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            val userData = userServiceData[userUid]
            refreshData()
            onResult(userData!!)
        }
    }

    override fun getObservableUserData(onResult: (UserData) -> Unit, onError: (Throwable) -> Unit) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            val userData = userServiceData[userUid]
            refreshData()
            onResult(userData!!)
        }
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
        if (isError){
            onError(Throwable("Error"))
        }else{
            userServiceData[userUid]?.level = level
            userServiceData[userUid]?.win = win
            userServiceData[userUid]?.draw = draw
            userServiceData[userUid]?.lose = lose
            userServiceData[userUid]?.exp = exp
            refreshData()
            onResult()
        }
    }

    override fun updateUserFrame(
        frameUrl: String,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (isError){
            onError(Throwable("Error"))
        }else{
            userServiceData[userUid]?.frameUrl = frameUrl
            refreshData()
            onResult()
        }
    }

    override fun deleteUserData(userData: UserData) {
        userServiceData.remove(userData.uid)
        refreshData()
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }


}