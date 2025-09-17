package com.ssiriwardana.pitwall.domain.usecase

import app.cash.turbine.test
import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.repository.DriverRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetAllDriversUseCaseTest {

    private lateinit var useCase: GetAllDriversUseCase
    private lateinit var repository: DriverRepository

    @Before
    fun setup() {
        repository = mock()
        useCase = GetAllDriversUseCase(repository)
    }

    @Test
    fun invokeDefaultSortByTeam() = runTest {
        val drivers =
            listOf(
                createTestDriver("SIR", "Shamal", "Siriwardana", "Race SLK", number = "55"),
                createTestDriver("VER", "Max", "Verstappen", number = "1"),
                createTestDriver("TSU", "Yuki", "Tsunoda", number = "24"),
                createTestDriver("HAM", "Lewis", "Hamilton", "Ferrari", number = "5")
            )

        whenever(repository.getAllDrivers(any())).thenReturn(
            flowOf(Resource.Success(drivers))
        )

        useCase(forceRefresh = false, sortByTeam = true).test {
            val res = awaitItem()
            assertEquals("Ferrari", (res as Resource.Success).data!![0].teamName)
            assertEquals("Race SLK", res.data!![1].teamName)
            assertEquals("Red Bull Racing", res.data[2].teamName)
            assertEquals("Red Bull Racing", res.data[3].teamName)
            awaitComplete()
        }

    }

    @Test
    fun invokesPassThroughStates_LoadingError() = runTest {
        val drivers = listOf(
            createTestDriver("VER", "Max", "Verstappen", number = "1")
        )

        whenever(repository.getAllDrivers(true)).thenReturn(
            flowOf(
                Resource.Loading(listOf()),
                Resource.Success(drivers),
                Resource.Error("Network Error", drivers)
            )
        )

        useCase(true, true).test {
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("Max", success.data?.first()?.firstName)

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals(error.message, "Network Error")

            awaitComplete()
        }
    }


    private fun createTestDriver(
        code: String, firstName: String, lastName: String,
        teamName: String = "Red Bull Racing", wikiUrl: String? = null,
        number: String = "1"
    ) = Driver(
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