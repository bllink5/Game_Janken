package com.example.jankenteamb.repository.firebase

import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.user.UserData
import com.example.jankenteamb.repository.firebase.repository.AuthRepository
import java.util.*

class FakeFirebaseAuthRepository : AuthRepository {
    var userServiceData: LinkedHashMap<String, UserData> = LinkedHashMap()

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
            userServiceData[user.username] = user
        }
    }

    fun setScenario(scenario:RepositoryResult<Any>){
        resultState = scenario
    }

    override suspend fun login(email: String, password: String): RepositoryResult<String> {
        return when (resultState) {
            is RepositoryResult.Success -> {
                RepositoryResult.Success("Login Success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): RepositoryResult<String> {
        return when (resultState) {
            is RepositoryResult.Success -> {
                RepositoryResult.Success("Register Success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }
}