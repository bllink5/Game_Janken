package com.example.jankenteamb.viewmodel


import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FakeFirebaseAchievementRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseAuthRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserDataCollectionRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseUserRepository
import com.example.jankenteamb.repository.room.FakeRoomAchievementRepository
import com.example.jankenteamb.repository.room.FakeRoomUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    @get:Rule
    val testCoroutineRule = MainCoroutineRule()

    private var auth = mock<FirebaseAuth>()
    private val firebaseAuthRepository = FakeFirebaseAuthRepository()
    private lateinit var roomUserRepository: FakeRoomUserRepository
    private lateinit var roomAchievementRepository: FakeRoomAchievementRepository
    private lateinit var firebaseUserRepository: FakeFirebaseUserRepository
    private lateinit var firebaseAchievementRepository: FakeFirebaseAchievementRepository
    private lateinit var firebaseUserDataCollectionRepository: FakeFirebaseUserDataCollectionRepository
    private lateinit var viewModel: LoginViewModel

    private val uid = "xxx"

    @Before
    fun setup() {
        roomUserRepository = FakeRoomUserRepository()
        roomAchievementRepository = FakeRoomAchievementRepository()
        firebaseUserRepository = FakeFirebaseUserRepository()
        firebaseAchievementRepository = FakeFirebaseAchievementRepository()
        firebaseUserDataCollectionRepository = FakeFirebaseUserDataCollectionRepository()

        firebaseAuthRepository.setup(uid)
        roomAchievementRepository.setup(uid)
        roomAchievementRepository.setup(uid)
        firebaseUserRepository.setup(uid)
        firebaseAchievementRepository.setup(uid)
        firebaseUserDataCollectionRepository.setup(uid)

        viewModel = LoginViewModel(
            auth,
            firebaseAuthRepository,
            roomUserRepository,
            roomAchievementRepository,
            firebaseUserRepository,
            firebaseAchievementRepository,
            firebaseUserDataCollectionRepository,
            testCoroutineRule
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
//    fun loginSuccess() {
//        testCoroutineRule.runBlockingTest {
//            val email = "makojima"
//            val password = "makojima"
//            firebaseAuthRepository.setScenario(RepositoryResult.Success(""))
//            firebaseUserRepository.setScenario(RepositoryResult.Success(""))
//            firebaseUserDataCollectionRepository.setScenario(RepositoryResult.Success(""))
//
//            viewModel.loginToFirebase(email, password)
//
//            val successLiveData = viewModel.successLiveData.getOrAwaitValue()
//
//            assertThat(successLiveData, `is`("Login success"))
//
//        }
//    }

    @Test
    fun login_error() = testCoroutineRule.runBlockingTest {
        val email = "makojima"
        val password = "makojima"
        firebaseAuthRepository.setScenario(RepositoryResult.Error(Exception("Error")))

        viewModel.loginToFirebase(email, password)

        val errorLiveData = viewModel.errorLiveData.getOrAwaitValue()

        assertThat(errorLiveData, `is`("Error"))
    }
}


