package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.repository.firebase.*
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class FriendSuggestViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var firebaseFriendListRepository: FakeFirebaseFriendListRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUserDataCollectionRepository: FakeFirebaseUserDataCollectionRepository
    private lateinit var firebaseStorageRepository: FakeFirebaseStorageRepository
    private lateinit var firebaseUserRepository: FakeFirebaseUserRepository
    private lateinit var firebaseNotificationRepository: FakeFirebaseNotificationRepository
    private lateinit var dispatcher: DispatcherProvider
    private lateinit var viewModel: FriendSuggestViewModel

    private val uid = "xxx"

    @Before
    fun setupViewModel() {
        auth = mock()
        firebaseFriendListRepository = FakeFirebaseFriendListRepository()
        firebaseUserDataCollectionRepository = FakeFirebaseUserDataCollectionRepository()
        firebaseStorageRepository = FakeFirebaseStorageRepository()
        firebaseUserRepository = FakeFirebaseUserRepository()
        firebaseNotificationRepository = FakeFirebaseNotificationRepository()
        dispatcher = coroutineRule
        firebaseFriendListRepository.setup(uid)
        firebaseUserDataCollectionRepository.setup(uid)
        firebaseUserRepository.setup(uid)

        viewModel = FriendSuggestViewModel(
            firebaseFriendListRepository,
            auth,
            firebaseUserDataCollectionRepository,
            firebaseStorageRepository,
            firebaseUserRepository,
            firebaseNotificationRepository,
            dispatcher
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
//    fun getFriendSuggest_success(){
//        firebaseUserDataCollectionRepository.setScenario(RepositoryResult.Success(""))
//        firebaseFriendListRepository.setScenario(RepositoryResult.Success(""))
//
////        firebaseUserDataCollectionRepository.setScenario(RepositoryResult.Error(Exception("Error")))
////        firebaseFriendListRepository.setScenario(RepositoryResult.Error(Exception("Error")))
//
//        viewModel.getFriendSuggest()
//
////        val friendSuggestLiveData = viewModel.friendSuggestLiveData.getOrAwaitValue()
//        val errorLiveData = viewModel.onErrorLiveData.getOrAwaitValue()
//
////        assertThat(friendSuggestLiveData.size, `is`(3))
//        assertThat(errorLiveData, `is`("Error"))
//    }

//    @Test
//    fun addFriend_success(){
//        val photoUrl = "photourl1"
//        val username = "username1"
//        val uid = "uid1"
//
//        viewModel.addFriend(photoUrl, username, uid)
//
//        val onSuccessLiveData = viewModel.onSuccessLiveData.getOrAwaitValue()
//
//        assertThat(onSuccessLiveData, `is`("Berhasil menambahkan username1"))
//    }
}