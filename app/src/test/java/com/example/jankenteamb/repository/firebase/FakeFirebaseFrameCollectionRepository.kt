package com.example.jankenteamb.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.firestore.shop.FrameUserData
import com.example.jankenteamb.model.firestore.shop.ShopListDataFirestore
import com.example.jankenteamb.model.firestore.shop.ShopListFirestore
import com.example.jankenteamb.repository.firebase.repository.FrameCollectionRepository
import java.util.*

class FakeFirebaseFrameCollectionRepository : FrameCollectionRepository {
    var userFrameCollectionServiceData: LinkedHashMap<String, FrameUserData> = LinkedHashMap()
    var allFrameCollectionServiceData: LinkedHashMap<Int, ShopListFirestore> = LinkedHashMap()
    private val observableUserFrame = MutableLiveData<List<FrameUserData>>()

    private lateinit var resultState: RepositoryResult<Any>
    private val dummyError = RepositoryResult.Error(Exception("Error"))
    private val dummyCanceled = RepositoryResult.Canceled(Exception("Canceled"))

    private var userUid: String? = null

    fun setup(uid:String) {
        userUid = uid
        val framesData = listOf(
            ShopListDataFirestore("frameUrl1", 10, "title1"),
            ShopListDataFirestore("frameUrl2", 20, "title2"),
            ShopListDataFirestore("frameUrl3", 30, "title3"),
            ShopListDataFirestore("frameUrl4", 40, "title4"),
            ShopListDataFirestore("frameUrl5", 50, "title5")
        )
        allFrameCollectionServiceData[0] = ShopListFirestore(framesData as MutableList<ShopListDataFirestore>)
        val frames = listOf(
            FrameUserData("frameUrl1", "title1"),
            FrameUserData("frameUrl2", "title2"),
            FrameUserData("frameUrl3", "title3"),
            FrameUserData("frameUrl4", "title4"),
            FrameUserData("frameUrl5", "title5")
        )
        for (frame in frames) {
            userFrameCollectionServiceData[frame.title] = frame
        }
    }

    fun setScenario(scenario: RepositoryResult<Any>) {
        resultState = scenario
    }

    fun refreshData() {
        observableUserFrame.value = userFrameCollectionServiceData.values.toList()
    }

    override suspend fun getAllFrameList(): RepositoryResult<ShopListFirestore> {
        return when (resultState) {
            is RepositoryResult.Success -> {
                RepositoryResult.Success(allFrameCollectionServiceData[0]) as RepositoryResult<ShopListFirestore>
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun getUserFrameList(): RepositoryResult<List<FrameUserData>> {
        return when (resultState) {
            is RepositoryResult.Success -> {
                RepositoryResult.Success(observableUserFrame.value?.toList()) as RepositoryResult<List<FrameUserData>>
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }

    override suspend fun addFrame(frame: FrameUserData): RepositoryResult<String> {
        return when (resultState) {
            is RepositoryResult.Success -> {
                userFrameCollectionServiceData[frame.title] = frame
                refreshData()
                RepositoryResult.Success("Add data baru berhasil")
            }
            is RepositoryResult.Error -> dummyError
            is RepositoryResult.Canceled -> dummyCanceled
        }
    }
}