package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.repository.firebase.repository.UserDataCollectionRepository
import java.util.*

class FakeFirebaseUserDataCollectionRepository: UserDataCollectionRepository {
    var allUserServiceData: LinkedHashMap<String, FriendListData> = LinkedHashMap()
    private val observableAllUser = MutableLiveData<List<FriendListData>>()

    private lateinit var resultState : RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))

    private var userUid = ""

    fun setup(uid:String) {
        userUid = uid
        val fakeUser1 = FriendListData("photoUrl1", "username1", "uiddsd1")
        val fakeUser2 = FriendListData("photoUrl2", "username2", "uidasda2")
        val fakeUser3 = FriendListData("photoUrl3", "username3", "uidasfa3")
        val fakeUser4 = FriendListData("photoUrl4", "username4", "uidasda4")

        allUserServiceData[fakeUser1.uid] = fakeUser1
        allUserServiceData[fakeUser2.uid] = fakeUser2
        allUserServiceData[fakeUser3.uid] = fakeUser3
        allUserServiceData[fakeUser4.uid] = fakeUser4

    }

    fun setScenario(scenario:RepositoryResult<Any>){
        resultState = scenario
    }

    fun refreshData(){
        observableAllUser.value = allUserServiceData.values.toList()
    }

    override suspend fun addUserToUserData(newUserData: FriendListData): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                allUserServiceData[newUserData.uid] = newUserData
                RepositoryResult.Success("Add new user data success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun getAllUserData(): RepositoryResult<List<FriendListData>> {
        return when(resultState){
            is RepositoryResult.Success -> RepositoryResult.Success(allUserServiceData.values.toList())
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateUserData(newUserData: FriendListData): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                allUserServiceData[newUserData.uid] = newUserData
                refreshData()
                RepositoryResult.Success("Update berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun updateUserData(userDataMap: MutableMap<String, String>): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                allUserServiceData[userUid]?.username = userDataMap["username"]!!
                refreshData()
                RepositoryResult.Success("Update berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }
}