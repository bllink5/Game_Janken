package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.repository.IFUserRepository
import java.util.*

class FakeFirebaseUserRepository: IFUserRepository {
    var userServiceData: LinkedHashMap<String, UserData> = LinkedHashMap()
    private val observableUser = MutableLiveData<List<UserData>>()

    private lateinit var resultState : RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))

    private var userUid: String? = null

    fun setup(uid:String) {
        userUid = uid
        val users = listOf(
            UserData(1, "username1","kfakg3865", 1, 0,0,0,0,0,"photoUrl1","frameUrl1", "tokenNotif1"),
            UserData(2, "username2","kfakgasd34", 1, 0,0,0,0,0,"photoUrl2","frameUrl2", "tokenNotif2"),
            UserData(3, "username2", userUid!!, 1, 0,0,0,0,0,"photoUrl3","frameUrl3", "tokenNotif3")
        )
        for (user in users) {
            userServiceData[user.uid] = user
        }
    }

    fun setScenario(scenario:RepositoryResult<Any>){
        resultState = scenario
    }

    override suspend fun addUserData(userData: UserData): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                userServiceData[userData.uid] = userData
                RepositoryResult.Success("Add user data success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateUserData(newUserData: UserData): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                userServiceData[newUserData.uid] = newUserData
                RepositoryResult.Success("Update user data success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateUserData(mapUserData: Map<String, String>): RepositoryResult<String> {
        TODO("Never used")
    }

    override suspend fun getUserData(): RepositoryResult<UserData> {
        return when(resultState){
            is RepositoryResult.Success -> {
                RepositoryResult.Success(userServiceData[userUid]) as RepositoryResult<UserData>
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun getTopScoreUser(): RepositoryResult<List<UserData>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDataByUid(uid: String): RepositoryResult<UserData> {
        return when(resultState){
            is RepositoryResult.Success -> {
                RepositoryResult.Success(userServiceData[uid]) as RepositoryResult<UserData>
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateFrameUrl(frameUrlMap: Map<String, String>): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                userServiceData[userUid]?.frameUrl = frameUrlMap["frameUrl"]!!
                RepositoryResult.Success("Update frame url success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateUserPoint(userDataMap: Map<String, Int>): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                userServiceData[userUid]?.point = userDataMap["point"]!!
                RepositoryResult.Success("Update user point success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

}