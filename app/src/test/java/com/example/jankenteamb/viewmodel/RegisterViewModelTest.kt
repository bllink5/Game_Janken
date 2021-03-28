package com.example.jankenteamb.viewmodel

import android.net.Uri
import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FakeFirebaseAuthRepository
import com.example.jankenteamb.repository.firebase.FakeFirebaseStorageRepository
import com.example.jankenteamb.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {
    @get: Rule
    val testCoroutineRule = MainCoroutineRule()

    private var auth = mock<FirebaseAuth>()
    private var uri = mock<Uri>()
    private lateinit var firebaseAuthRepository: FakeFirebaseAuthRepository
    private lateinit var firebaseStorageRepository: FakeFirebaseStorageRepository
    private lateinit var dispatcher: DispatcherProvider
    private lateinit var viewModel: RegisterViewModel

    private val uid = "xxx"

    @Before
    fun setup() {
        firebaseAuthRepository = FakeFirebaseAuthRepository()
        firebaseStorageRepository = FakeFirebaseStorageRepository()
        dispatcher = testCoroutineRule

        firebaseAuthRepository.setup(uid)

        viewModel = RegisterViewModel(
            auth,
            firebaseAuthRepository,
            firebaseStorageRepository,
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
//    fun register_success() = testCoroutineRule.runBlockingTest {
//        val email = "makojima"
//        val username = "makojima"
//        val password = "makojima"
//        firebaseAuthRepository.setScenario(RepositoryResult.Success(""))
//
//        viewModel.registerToFirebase(email, password, username, uri)
//
//        val successLiveData = viewModel.successLiveData.getOrAwaitValue()
//
//        assertThat(successLiveData, `is`("Register berhasil"))
//    }

    @Test
    fun register_error() = testCoroutineRule.runBlockingTest {
        val email = "makojima"
        val username = "makojima"
        val password = "makojima"
        firebaseAuthRepository.setScenario(RepositoryResult.Error(Exception("")))


        viewModel.registerToFirebase(email, password, username, uri)

        val errorLiveData = viewModel.errorLiveData.getOrAwaitValue()

        assertThat(errorLiveData, `is`("Error"))
    }
}