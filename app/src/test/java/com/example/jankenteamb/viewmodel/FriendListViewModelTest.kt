package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FakeFirebaseFriendListRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseStorageRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FriendListViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var firebaseFriendListRepository: FakeFirebaseFriendListRepository
    private lateinit var firebaseStorageRepository: FakeFirebaseStorageRepository
    private lateinit var dispatcher: DispatcherProvider

    private lateinit var viewModel: FriendListViewModel

    private val uid = "xxx"

    @Before
    fun setupViewModel() {

        firebaseFriendListRepository = FakeFirebaseFriendListRepository()
        firebaseStorageRepository = FakeFirebaseStorageRepository()
        dispatcher = coroutineRule
        firebaseFriendListRepository.setup(uid)


        viewModel =
            FriendListViewModel(
                firebaseStorageRepository,
                firebaseFriendListRepository, dispatcher
            )

        InstantRuleExecution.start()
        TrampolineSchedulerRx.start()
    }

    @After
    fun cleanUp() {
        InstantRuleExecution.tearDown()
        TrampolineSchedulerRx.tearDown()
    }

//    @Test
//    fun getFriendList(){
//        firebaseFriendListRepository.setScenario(RepositoryResult.Success(""))
//
//        viewModel.getFriendList()
//
////        val friendListliveData = viewModel.friendListLiveData.getOrAwaitValue()
//        val errorliveData = viewModel.errorLiveData.getOrAwaitValue()
//
////        assertThat(friendListliveData.size, `is`(3))
//        assertThat(errorliveData, `is`("error"))
//    }

    @Test
    fun deleteFriend(){
        firebaseFriendListRepository.setScenario(RepositoryResult.Success(""))
        viewModel.deleteFriend("uid1")

        assertThat(firebaseFriendListRepository.friendListServiceData.size, `is`(0))
    }
}