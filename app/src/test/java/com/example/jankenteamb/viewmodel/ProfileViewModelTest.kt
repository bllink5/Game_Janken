package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.repository.firebase.FakeFirebaseStorageRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserDataCollectionRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserRepository
import com.example.jankenteamb.repository.room.FakeRoomAchievementRepository
import com.example.jankenteamb.repository.room.FakeRoomUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var roomUserRepository: FakeRoomUserRepository
    private lateinit var roomAchievementRepository: FakeRoomAchievementRepository
    private lateinit var firebaseStorageRepository: FakeFirebaseStorageRepository
    private lateinit var firebaseUserDataCollectionRepository: FakeFirebaseUserDataCollectionRepository
    private lateinit var firebaseUserRepository: FakeFirebaseUserRepository
    private lateinit var auth: FirebaseAuth

    private val uid = "xxx"

    @Before
    fun setupViewModel() {

        roomUserRepository = FakeRoomUserRepository()
        roomAchievementRepository = FakeRoomAchievementRepository()
        firebaseStorageRepository = FakeFirebaseStorageRepository()
        firebaseStorageRepository= FakeFirebaseStorageRepository()
        firebaseUserDataCollectionRepository= FakeFirebaseUserDataCollectionRepository()
        firebaseUserRepository= FakeFirebaseUserRepository()

        auth = mock()

        roomAchievementRepository.setup(uid)
        roomUserRepository.setup(uid)

        viewModel = ProfileViewModel(
            roomUserRepository,
            roomAchievementRepository,
            firebaseStorageRepository,
            firebaseUserDataCollectionRepository,
            firebaseUserRepository,
            auth,
            coroutineRule
        )

        InstantRuleExecution.start()
        TrampolineSchedulerRx.start()
    }

    @After
    fun cleanUp() {
        InstantRuleExecution.tearDown()
        TrampolineSchedulerRx.tearDown()
    }

    @Test
    fun getUserDataFromRoom_success() = coroutineRule.runBlockingTest {
        //run getUserDataFromRoom
        viewModel.getUserDataFromRoom()

        //observer userLiveData
        val userLiveData = viewModel.userLiveData.getOrAwaitValue()

        //check if userLiveData = userData from roomUserRepository
        assertThat(
            userLiveData,
            CoreMatchers.`is`(roomUserRepository.userServiceData[uid])
        )
//        assertThat(roomUserRepository.userServiceData.size, `is`(0))
    }

    @Test
    fun getUserDataFromRoom_error() = coroutineRule.runBlockingTest {
        //set scenario to error
        roomUserRepository.setError(true)

        //run getUserDataFromRoom
        viewModel.getUserDataFromRoom()

        //observer userLiveData
        val errorLiveData = viewModel.errorLiveData.getOrAwaitValue()

        //check if userLiveData = userData
        assertThat(errorLiveData, CoreMatchers.`is`("Error"))
    }

    @Test
    fun deleteUserData_success() = coroutineRule.runBlockingTest{
        val size = roomUserRepository.userServiceData.size
        //delete user data
        viewModel.deleteUserData()
        //check if userData has been deleted
        assertThat(roomUserRepository.userServiceData.size, `is`(size.minus(1)))
    }

    @Test
    fun deleteUserData_error() = coroutineRule.runBlockingTest{
        val size = roomUserRepository.userServiceData.size
        //set scenario to error
        roomUserRepository.setError(true)

        //delete user data
        viewModel.deleteUserData()

        //observe errorLiveData
        val error = viewModel.errorLiveData.getOrAwaitValue()

        //check userData still not deleted
        assertThat(error, `is`("Error"))
        assertThat(roomUserRepository.userServiceData.size, `is`(size))
    }

    @Test
    fun deleteAchievementData_success(){
        val size = roomAchievementRepository.achievementServiceData.size
        //deleteAchievementData
        viewModel.deleteAchievementData()

        //check if achievementData has been deleted
        assertThat(roomAchievementRepository.achievementServiceData.size, `is`(0))
    }

    @Test
    fun deleteAchievementData_error(){
        //set scenario to error
        roomAchievementRepository.serError(true)

        //deleteAchievementData
        viewModel.deleteAchievementData()

        //observe errorLiveData
        val error = viewModel.errorLiveData.getOrAwaitValue()

        //check achievementData still not deleted
        assertThat(error, `is`("Error"))
        assertThat(roomAchievementRepository.achievementServiceData.size, `is`(5))
    }

}