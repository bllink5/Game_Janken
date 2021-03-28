package com.example.jankenteamb.viewmodel

import com.example.jankenteamb.helper.InstantRuleExecution
import com.example.jankenteamb.helper.MainCoroutineRule
import com.example.jankenteamb.helper.TrampolineSchedulerRx
import com.example.jankenteamb.helper.getOrAwaitValue
import com.example.jankenteamb.model.RepositoryResult
import com.example.jankenteamb.repository.firebase.FakeFirebaseHistoryRepository
import com.example.jankenteamb.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MenuHistoryViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var firebaseHistoryRepository: FakeFirebaseHistoryRepository
    private lateinit var dispatcher: DispatcherProvider

    private lateinit var viewModel: MenuHistoryViewModel

    val uid = "xxx"

    @Before
    fun setupViewModel() {
        firebaseHistoryRepository = FakeFirebaseHistoryRepository()
        dispatcher = coroutineRule

        firebaseHistoryRepository.setup("xxx")

        viewModel = MenuHistoryViewModel(
            firebaseHistoryRepository, dispatcher
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
    fun getHistoryfromFireBase_success(){
        firebaseHistoryRepository.setScenario(RepositoryResult.Success(""))

        viewModel.getHistoryFromFirebase()

        val historyLiveData = viewModel.historyLiveData.getOrAwaitValue()

        assertThat(historyLiveData.size, `is`(3))
    }

    @Test
    fun getHistoryfromFireBase_error(){
        firebaseHistoryRepository.setScenario(RepositoryResult.Error(Exception("")))

        viewModel.getHistoryFromFirebase()

        val errorLiveData = viewModel.errorLiveData.getOrAwaitValue()

        assertThat(errorLiveData, `is`("Error"))
    }

}