package com.ssiriwardana.pitwall.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.usecase.GetAllDriversUseCase
import com.ssiriwardana.pitwall.domain.usecase.GetDriverDetailsUseCase
import com.ssiriwardana.pitwall.presentation.ui.state.SortOption
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class DriversViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DriverViewModel
    private lateinit var getAllDriversUseCase: GetAllDriversUseCase
    private lateinit var getDriverDetailsUseCase: GetDriverDetailsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllDriversUseCase = mock()
        getDriverDetailsUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateLoadsDrivers() = runTest {
        val drivers = listOf(createTestDriver("VER", "Max", "VERSTAPPEN"))
        whenever(getAllDriversUseCase(any(), any())).thenReturn(
            flowOf(Resource.Success(drivers))
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.drivers.size)
            assertEquals("Max", state.drivers.first().firstName)
        }
    }

    @Test
    fun freshFetch_updateStateOnForceRefresh() = runTest {

        val cachedDrivers = listOf(createTestDriver("SIR", "Shamal", "Siriwardana"))
        val refreshDrivers = listOf(
            createTestDriver("SIR", "Shamal", "Siriwardana"),
            createTestDriver("VER", "Max", "Verstappen")
        )

        whenever(getAllDriversUseCase(eq(false), any())).thenReturn(
            flowOf(Resource.Success(cachedDrivers))
        )
        whenever(getAllDriversUseCase(eq(true), any())).thenReturn(
            flowOf(
                Resource.Loading(cachedDrivers),
                Resource.Success(refreshDrivers)
            )
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.drivers.size)
        }

        verify(getAllDriversUseCase).invoke(eq(true), any())
    }

    @Test
    fun searchFiltersCorrectDrivers() = runTest {
        val cachedDrivers = listOf(
            createTestDriver("SIR", "Shamal", "Siriwardana", "Race SLK"),
            createTestDriver("VER", "Max", "Verstappen"),
            createTestDriver("TSU", "Yuki", "Tsunoda"),
            createTestDriver("HAM", "Lewis", "Hamilton", "Mercedes")
        )

        whenever(getAllDriversUseCase(any(), any())).thenReturn(
            flowOf(
                Resource.Success(cachedDrivers)
            )
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Red Bull")
        advanceTimeBy(400)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Red Bull", state.searchQuery)
            assertEquals(2, state.drivers.size)
            assertTrue(state.filteredDrivers.all { it.teamName == "Red Bull Racing"} == true)
        }

    }

    @Test
    fun selectDriver_fetchDetails() = runTest {
        val driver = createTestDriver("VER", "Max", "Verstappen")
        val detailedDriver = driver.copy(wikiUrl = "https://wiki.com/maxVerstappen" )


        whenever(getAllDriversUseCase(any(), any())).thenReturn(
            flowOf(Resource.Success(listOf(driver)))
        )
        whenever(getDriverDetailsUseCase("ver")).thenReturn(
            flowOf(Resource.Success(detailedDriver))
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.selectDriver(driver)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Max", state.selectedDriver?.firstName)
            assertEquals("https://wiki.com/maxVerstappen", state.selectedDriver?.wikiUrl)
        }
    }

    @Test
    fun sortUpdatesByDriverOrder() = runTest {
        val drivers =
            listOf(createTestDriver("SIR", "Shamal", "Siriwardana", "Race SLK", number = "55"),
            createTestDriver("VER", "Max", "Verstappen", number = "1"),
            createTestDriver("TSU", "Yuki", "Tsunoda", number = "24"),
            createTestDriver("HAM", "Lewis", "Hamilton", "Mercedes", number = "5"))


        whenever(getAllDriversUseCase(any(), any())).thenReturn(
            flowOf(Resource.Success(drivers))
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.changeSortOption(SortOption.NUMBER)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(SortOption.NUMBER, state.sortBy)
            assertEquals("1", state.drivers[0].permanentNumber)
            assertEquals("5", state.drivers[1].permanentNumber)
            assertEquals("24", state.drivers[2].permanentNumber)
            assertEquals("55", state.drivers[3].permanentNumber)
        }
    }

    @Test
    fun cacheDataUsedOnErrorState() = runTest {
        val cachedDrivers = listOf(createTestDriver("SIR", "Shamal", "Siriwardana"))
        val errorMessage = "Network Error"

        whenever(getAllDriversUseCase(any(), any())).thenReturn(
            flowOf(Resource.Loading(cachedDrivers),
            Resource.Error(errorMessage, cachedDrivers))
        )

        viewModel = DriverViewModel(getAllDriversUseCase, getDriverDetailsUseCase)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertEquals(1, state.drivers.size)
            assertTrue(state.hasData)
        }
    }


    private fun createTestDriver(code: String, firstName: String, lastName: String,
                                 teamName: String = "Red Bull Racing", wikiUrl: String? = null,
                                 number: String = "1")
    = Driver(
        id = code.lowercase(),
        permanentNumber = number,
        code = code,
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        dateOfBirth = LocalDate.parse("8/8/1996", DateTimeFormatter.ofPattern("M/d/yyyy")),
        nationality = "Dutch",
        teamName = teamName,
        teamColor = "3671C6",
        headshotUrl = null,
        wikiUrl = null
    )
}