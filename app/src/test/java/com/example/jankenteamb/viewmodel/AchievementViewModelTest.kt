package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.model.room.query.JoinAchievementData
import com.example.jankenteamb.repository.firebase.FakeFirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserRepository
import com.example.jankenteamb.repository.room.FakeRoomAchievementRepository
import com.example.jankenteamb.repository.room.FakeRoomUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AchievementViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AchievementViewModel
    private lateinit var roomUserRepository: FakeRoomUserRepository
    private lateinit var roomAchievementRepository: FakeRoomAchievementRepository
    private lateinit var firebaseUserRepository: FakeFirebaseUserRepository
    private lateinit var firebaseAchievementRepository: FakeFirebaseAchievementRepository

    private val uid = "xxx"

    @Before
    fun setupViewModel() {
        roomUserRepository = FakeRoomUserRepository()
        roomAchievementRepository = FakeRoomAchievementRepository()
        firebaseAchievementRepository = FakeFirebaseAchievementRepository()
        firebaseUserRepository = FakeFirebaseUserRepository()

        roomAchievementRepository.setup(uid)
        firebaseAchievementRepository.setup(uid)
        roomUserRepository.setup(uid)
        firebaseUserRepository.setup(uid)

        viewModel = AchievementViewModel(
            roomUserRepository,
            roomAchievementRepository,
            firebaseUserRepository,
            firebaseAchievementRepository,
            dispatcher = coroutineRule
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
    fun getPointDataFromRoom_success() = coroutineRule.runBlockingTest {
        //When
        viewModel.getPointData()

        //observePointData
        val point = viewModel.showPointLiveData.getOrAwaitValue()

        //check if Point equal to user's point
        assertThat(point, `is`(roomUserRepository.userServiceData[uid]?.point))
    }

    @Test
    fun getPointDataFromRoom_error() = coroutineRule.runBlockingTest {
        //set scenario to error
        roomUserRepository.setError(true)

        //when getPointData
        viewModel.getPointData()

        //observe errorLiveData
        val error = viewModel.errorLiveData.getOrAwaitValue()

        //check errorLiveData's value
        assertThat(error, `is`("Error"))
    }

    @Test
    fun getAchievementProgress_success() = coroutineRule.runBlockingTest {
        //when getAchievementProgress
        viewModel.getAchievementProgress()

        //observe getAchievementLiveData
        val getAchievementLiveData = viewModel.getAchievementLiveData.getOrAwaitValue()

        //check getAchievementProgress data
        assertThat(getAchievementLiveData, `is`(roomAchievementRepository.joinAchievementServiceData.values.toList()))
    }

    @Test
    fun claimAchievement_success() = coroutineRule.runBlockingTest {
        //set achievementData
        val achievementData =
            JoinAchievementData("Title", 5, 50, uid, "achievementUrl", 2, "unclaimed", 1)

        firebaseAchievementRepository.setScenario(RepositoryResult.Success(""))

        //run claimAchievementData
        viewModel.claimAchievement(achievementData)

        //observe successLiveData
        val success = viewModel.successLiveData.getOrAwaitValue()

        //check if successLiveData change
        assertThat(success, `is`("Berhasil claim achievement!"))
        //check if data in roomAchievementRepositoy change
        assertThat(roomAchievementRepository.achievementServiceData[achievementData.achievement_id]?.claim, `is`("claimed"))
        //check if data in firebaseAchievementRepository change
        assertThat(firebaseAchievementRepository.achievementServiceData[achievementData.achievement_id]?.claim, `is`("claimed"))
    }

    @Test
    fun claimAchievement_error() = coroutineRule.runBlockingTest {
        //set scenario to error
        roomAchievementRepository.serError(true)
        firebaseAchievementRepository.setScenario(RepositoryResult.Error(Exception()))

        //dummy achievementData
        val achievementData =
            JoinAchievementData("Title", 5, 50, uid, "achievementUrl", 2, "unclaimed", 1)

        //run claimAchievement
        viewModel.claimAchievement(achievementData)

        //observe errorLiveData
        val error = viewModel.errorLiveData.getOrAwaitValue()

        //check if error
        assertThat(error, `is`("Error"))
    }

    //error return Void type
    @Test
    fun updateUserPoint_success() = coroutineRule.runBlockingTest {
        //set scenario
        firebaseUserRepository.setScenario(RepositoryResult.Success(""))
        //set newPoint
        val newPoint = 100

        //run updatePoint
        viewModel.updatePoint(newPoint)

        //observer pointData
        val point = viewModel.showPointLiveData.getOrAwaitValue()

        //compare pointLiveData and newPoint
        assertThat(point, `is`(newPoint))
    }
}