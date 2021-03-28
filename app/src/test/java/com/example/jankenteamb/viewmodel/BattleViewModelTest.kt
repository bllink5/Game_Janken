package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FakeFirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseHistoryRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserRepository
import com.example.jankenteamb.repository.room.FakeRoomAchievementRepository
import com.example.jankenteamb.repository.room.FakeRoomUserRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BattleViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var roomUserRepository: FakeRoomUserRepository
    private lateinit var roomAchievementRepository: FakeRoomAchievementRepository
    private lateinit var firebaseHistoryRepository: FakeFirebaseHistoryRepository
    private lateinit var firebaseUserRepository: FakeFirebaseUserRepository
    private lateinit var firebaseAchievementRepository: FakeFirebaseAchievementRepository
    private lateinit var dispatcher: DispatcherProvider
    private lateinit var viewModel: BattleViewModel

    private val uid = "xxx"

    @Before
    fun setupViewModel() {

        roomUserRepository = FakeRoomUserRepository()
        roomAchievementRepository = FakeRoomAchievementRepository()
        firebaseAchievementRepository = FakeFirebaseAchievementRepository()
        firebaseUserRepository = FakeFirebaseUserRepository()
        firebaseHistoryRepository = FakeFirebaseHistoryRepository()
        dispatcher = coroutineRule

        roomAchievementRepository.setup(uid)
        firebaseAchievementRepository.setup(uid)

        roomUserRepository.setup(uid)
        firebaseUserRepository.setup(uid)

        viewModel = BattleViewModel(
            roomUserRepository,
            roomAchievementRepository,
            firebaseHistoryRepository,
            firebaseUserRepository,
            firebaseAchievementRepository,
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

    @Test
    fun getUserDataFromRoom_success(){
        //when
        viewModel.getUserDataFromRoom()
        //observer userLiveData
        val userLiveData = viewModel.userLiveData.getOrAwaitValue()
        //check if userLiveData equals to data from roomUserRepository
        assertThat(userLiveData, `is`(roomUserRepository.userServiceData[uid]))
    }

    @Test
    fun getUserDataFromRoom_error(){
        //set scenario
        roomUserRepository.setError(true)
        //when
        viewModel.getUserDataFromRoom()
        //observer userLiveData
        val errorLiveData = viewModel.errorLiveData.getOrAwaitValue()
        //check if userLiveData equals to data from roomUserRepository
        assertThat(errorLiveData, `is`("Error"))
    }

    @Test
    fun addHistoryToFirebase_success(){
        val historySum = firebaseHistoryRepository.historyServiceData.size
        firebaseHistoryRepository.setScenario(RepositoryResult.Success(""))

        //when
        viewModel.addHistoryToFirebase("SinglePlayer", "Player Win")

        //check if history is insert
        assertThat(firebaseHistoryRepository.historyServiceData.size, `is`(historySum.plus(1)))
    }

    @Test
    fun getResult_player1rock_player2rock_success(){
        //simpan data awal user
        val playerWin = roomUserRepository.userServiceData[uid]?.win
        val playerLose = roomUserRepository.userServiceData[uid]?.lose
        val playerDraw = roomUserRepository.userServiceData[uid]?.draw
        val playerExp = roomUserRepository.userServiceData[uid]?.exp
        val playerLevel = roomUserRepository.userServiceData[uid]?.level

        //simpan progress awal achievement
        val achievementProgressLevel = roomAchievementRepository.achievementServiceData[1]?.achievementProgress
        val achievementProgressLose = roomAchievementRepository.achievementServiceData[2]?.achievementProgress
        val achievementProgressWin = roomAchievementRepository.achievementServiceData[3]?.achievementProgress
        val achievementProgress15 = roomAchievementRepository.achievementServiceData[4]?.achievementProgress
        val achievementProgress30 = roomAchievementRepository.achievementServiceData[5]?.achievementProgress

        //set scenario
        firebaseAchievementRepository.setScenario(RepositoryResult.Success(""))
        firebaseUserRepository.setScenario(RepositoryResult.Success(""))

        //when
        viewModel.getResult("username1","username2","rock","rock")

        //observer
        val historyLiveData = viewModel.historyResult.getOrAwaitValue()
        val battleResultLiveData = viewModel.battleResult.getOrAwaitValue()

        //check live data
        assertThat(historyLiveData, `is`("Player Draw"))
        assertThat(battleResultLiveData, `is`("Draw"))

        //check semua data yang terkena efek
        assertThat(roomUserRepository.userServiceData[uid]?.win, `is`(playerWin))
        assertThat(roomUserRepository.userServiceData[uid]?.lose, `is`(playerLose))
        assertThat(roomUserRepository.userServiceData[uid]?.draw, `is`(playerDraw?.plus(1)))
        assertThat(roomUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(roomUserRepository.userServiceData[uid]?.exp, `is`(playerExp?.plus(3)))
        assertThat(firebaseUserRepository.userServiceData[uid]?.win, `is`(playerWin))
        assertThat(firebaseUserRepository.userServiceData[uid]?.lose, `is`(playerLose))
        assertThat(firebaseUserRepository.userServiceData[uid]?.draw, `is`(playerDraw?.plus(1)))
        assertThat(firebaseUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(firebaseUserRepository.userServiceData[uid]?.exp, `is`(playerExp?.plus(3)))

        //check perubahan di achievement
        assertThat(firebaseAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(firebaseAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose))
        assertThat(firebaseAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin))
        assertThat(firebaseAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(firebaseAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))

        assertThat(roomAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(roomAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose))
        assertThat(roomAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin))
        assertThat(roomAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(roomAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))
    }

    @Test
    fun updateUseData_player1rock_player2scissor_success(){
        //simpan data awal user
        val playerWin = roomUserRepository.userServiceData[uid]?.win
        val playerLose = roomUserRepository.userServiceData[uid]?.lose
        val playerDraw = roomUserRepository.userServiceData[uid]?.draw
        val playerExp = roomUserRepository.userServiceData[uid]?.exp
        val playerLevel = roomUserRepository.userServiceData[uid]?.level

        //simpan progress awal achievement
        val achievementProgressLevel = roomAchievementRepository.achievementServiceData[1]?.achievementProgress
        val achievementProgressLose = roomAchievementRepository.achievementServiceData[2]?.achievementProgress
        val achievementProgressWin = roomAchievementRepository.achievementServiceData[3]?.achievementProgress
        val achievementProgress15 = roomAchievementRepository.achievementServiceData[4]?.achievementProgress
        val achievementProgress30 = roomAchievementRepository.achievementServiceData[5]?.achievementProgress

        //set scenario
        firebaseAchievementRepository.setScenario(RepositoryResult.Success(""))
        firebaseUserRepository.setScenario(RepositoryResult.Success(""))

        //when
        viewModel.getResult("username1","username2","rock","scissor")

        //observer
        val historyLiveData = viewModel.historyResult.getOrAwaitValue()
        val battleResultLiveData = viewModel.battleResult.getOrAwaitValue()

        //check live data
        assertThat(historyLiveData, `is`("Player Win"))
        assertThat(battleResultLiveData, `is`("username1\nWin"))

        //check semua data yang terkena efek
        assertThat(roomUserRepository.userServiceData[uid]?.win, `is`(playerWin?.plus(1)))
        assertThat(roomUserRepository.userServiceData[uid]?.lose, `is`(playerLose))
        assertThat(roomUserRepository.userServiceData[uid]?.draw, `is`(playerDraw))
        assertThat(roomUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(roomUserRepository.userServiceData[uid]?.exp, `is`(playerExp?.plus(5)))
        assertThat(firebaseUserRepository.userServiceData[uid]?.win, `is`(playerWin?.plus(1)))
        assertThat(firebaseUserRepository.userServiceData[uid]?.lose, `is`(playerLose))
        assertThat(firebaseUserRepository.userServiceData[uid]?.draw, `is`(playerDraw))
        assertThat(firebaseUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(firebaseUserRepository.userServiceData[uid]?.exp, `is`(playerExp?.plus(5)))

        //check perubahan di achievement
        assertThat(firebaseAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(firebaseAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose))
        assertThat(firebaseAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin?.plus(1)))
        assertThat(firebaseAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(firebaseAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))

        assertThat(roomAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(roomAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose))
        assertThat(roomAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin?.plus(1)))
        assertThat(roomAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(roomAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))
    }

    @Test
    fun updateUseData_player1rock_player2paper_success(){
        //simpan data awal user
        val playerWin = roomUserRepository.userServiceData[uid]?.win
        val playerLose = roomUserRepository.userServiceData[uid]?.lose
        val playerDraw = roomUserRepository.userServiceData[uid]?.draw
        val playerExp = roomUserRepository.userServiceData[uid]?.exp
        val playerLevel = roomUserRepository.userServiceData[uid]?.level

        //simpan progress awal achievement
        val achievementProgressLevel = roomAchievementRepository.achievementServiceData[1]?.achievementProgress
        val achievementProgressLose = roomAchievementRepository.achievementServiceData[2]?.achievementProgress
        val achievementProgressWin = roomAchievementRepository.achievementServiceData[3]?.achievementProgress
        val achievementProgress15 = roomAchievementRepository.achievementServiceData[4]?.achievementProgress
        val achievementProgress30 = roomAchievementRepository.achievementServiceData[5]?.achievementProgress

        //set scenario
        firebaseAchievementRepository.setScenario(RepositoryResult.Success(""))
        firebaseUserRepository.setScenario(RepositoryResult.Success(""))

        //when
        viewModel.getResult("username1","username2","rock","paper")

        //observer
        val historyLiveData = viewModel.historyResult.getOrAwaitValue()
        val battleResultLiveData = viewModel.battleResult.getOrAwaitValue()

        //check live data
        assertThat(historyLiveData, `is`("Player Lose"))
        assertThat(battleResultLiveData, `is`("username2\nWin"))

        //check semua data yang terkena efek
        assertThat(roomUserRepository.userServiceData[uid]?.win, `is`(playerWin))
        assertThat(roomUserRepository.userServiceData[uid]?.lose, `is`(playerLose?.plus(1)))
        assertThat(roomUserRepository.userServiceData[uid]?.draw, `is`(playerDraw))
        assertThat(roomUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(roomUserRepository.userServiceData[uid]?.exp, `is`(playerExp))
        assertThat(firebaseUserRepository.userServiceData[uid]?.win, `is`(playerWin))
        assertThat(firebaseUserRepository.userServiceData[uid]?.lose, `is`(playerLose?.plus(1)))
        assertThat(firebaseUserRepository.userServiceData[uid]?.draw, `is`(playerDraw))
        assertThat(firebaseUserRepository.userServiceData[uid]?.level, `is`(playerLevel))
        assertThat(firebaseUserRepository.userServiceData[uid]?.exp, `is`(playerExp))

        //check perubahan di achievement
        assertThat(firebaseAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(firebaseAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose?.plus(1)))
        assertThat(firebaseAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin))
        assertThat(firebaseAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(firebaseAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))

        assertThat(roomAchievementRepository.achievementServiceData[1]?.achievementProgress, `is`(achievementProgressLevel))
        assertThat(roomAchievementRepository.achievementServiceData[2]?.achievementProgress, `is`(achievementProgressLose?.plus(1)))
        assertThat(roomAchievementRepository.achievementServiceData[3]?.achievementProgress, `is`(achievementProgressWin))
        assertThat(roomAchievementRepository.achievementServiceData[4]?.achievementProgress, `is`(achievementProgress15?.plus(1)))
        assertThat(roomAchievementRepository.achievementServiceData[5]?.achievementProgress, `is`(achievementProgress30?.plus(1)))
    }


}