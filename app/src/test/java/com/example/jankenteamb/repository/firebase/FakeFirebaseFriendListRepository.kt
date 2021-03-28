package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.friendlist.FriendListData
import com.example.jankenteamb.repository.firebase.repository.FriendListRepository
import java.util.*

class FakeFirebaseFriendListRepository: FriendListRepository {
    var friendListServiceData: LinkedHashMap<String, FriendListData> = LinkedHashMap()
    private val observableFriendlist = MutableLiveData<List<FriendListData>>()

    private lateinit var resultState : RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))


    fun setup(uid:String) {
        val friends = listOf(
            FriendListData("photoUrl1","Username1", "uid1")
        )
        for (friend in friends) {
            friendListServiceData[friend.uid] = friend
        }
    }

    fun setScenario(scenario:RepositoryResult<Any>){
        resultState = scenario
    }

    fun refreshData(){
        observableFriendlist.value = friendListServiceData.values.toList()
    }

    override suspend fun getFriendList(): RepositoryResult<List<FriendListData>> {
        return when(resultState){
            is RepositoryResult.Success -> RepositoryResult.Success(friendListServiceData.values.toList())
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun deleteFriend(friendUid: String): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                friendListServiceData.remove(friendUid)
                refreshData()
                RepositoryResult.Success("Delete friend success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun addFriendToUser(
        userData: FriendListData,
        friendListData: FriendListData
    ): RepositoryResult<String> {
        return when(resultState){
            is RepositoryResult.Success -> {
                friendListServiceData[friendListData.uid] = friendListData
                refreshData()
                RepositoryResult.Success("Add friend success")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun addUserToFriend(
        userData: FriendListData,
        friendListData: FriendListData
    ): RepositoryResult<String> {
        throw NotImplementedError()
    }
}