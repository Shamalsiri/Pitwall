package com.ssiriwardana.pitwall.data.repository

import android.util.Log
import app.cash.turbine.test
import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.data.local.datasource.DriverLocalDataSource
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import com.ssiriwardana.pitwall.data.remote.datasource.CombineDriverData
import com.ssiriwardana.pitwall.data.remote.datasource.DriverRemoteDataSource
import com.ssiriwardana.pitwall.data.remote.dto.JolpicaDriverDto
import com.ssiriwardana.pitwall.data.remote.dto.OpenF1DriverDto
import io.mockk.every
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DriverRepositoryTest {
    private lateinit var repository: DriverRepositoryImpl
    private lateinit var remoteDataSource: DriverRemoteDataSource
    private lateinit var localDataSource: DriverLocalDataSource

    @Before
    fun setup(){
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any<Throwable>()) } returns 0

        remoteDataSource = mock()
        localDataSource = mock()
        repository = DriverRepositoryImpl(remoteDataSource, localDataSource)

    }

    /**
     * Valid data in the cache
     * Confirm the flow when getting all drivers follows loading with data, success with data
     *
     * Finally verify getCurrentDrivers on the remoteDataSource was not called.
     */
    @Test
    fun getAllDrivers_getsCachedDataFirst() = runTest {
        val cachedEntity = createDriverEntity("VER", "Max", "Verstappen")
        whenever(localDataSource.getAllDriversOneShot()).thenReturn(listOf(cachedEntity))
        whenever(localDataSource.isCachedDataValid()).thenReturn(true)

        repository.getAllDrivers(forceRefresh = false).test {

            val loadingEmission = awaitItem()
            assertTrue(loadingEmission is Resource.Loading)
            assertEquals("Max", loadingEmission.data?.firstOrNull()?.firstName)

            val successEmission = awaitItem()
            assertTrue(successEmission is Resource.Success)
            assertEquals("Max", successEmission.data?.firstOrNull()?.firstName)

            awaitComplete()
        }

        verify(remoteDataSource, never()).getCurrentDrivers()
    }

    @Test
    fun getAllDrivers_getsRemoteData_invalidCache() = runTest {
        val cachedEntity = createDriverEntity("RIC", "Daniel", "Riccardo")
        val remoteData = CombineDriverData(jolpicaDrivers = listOf(createJolpicaDriver("VER", "Max", "Verstappen")),
            openF1Drivers = listOf(createOpenF1Driver("HAM", "Lewis", "Hamilton"))
        )

        whenever(localDataSource.getAllDriversOneShot()).thenReturn(listOf(cachedEntity))
        whenever(localDataSource.isCachedDataValid()).thenReturn(false)
        whenever(remoteDataSource.getCurrentDrivers()).thenReturn(remoteData)

        repository.getAllDrivers(forceRefresh = false).test {
            val loadingEmission = awaitItem()
            assertTrue(loadingEmission is Resource.Loading)

            val successEmission = awaitItem()
            assertTrue(successEmission is Resource.Success)
            assertEquals("Max", successEmission.data?.firstOrNull()?.firstName)

            awaitComplete()
        }

        verify(localDataSource).clearAndInsertDrivers(any())
    }

    @Test
    fun getAllDrivers_getCacheData_remoteError() = runTest {
        val cachedEntity = createDriverEntity("SAI", "Carlos", "Sainz")
        whenever(localDataSource.getAllDriversOneShot()).thenReturn(listOf(cachedEntity))
        whenever(localDataSource.isCachedDataValid()).thenReturn(false)
        whenever(remoteDataSource.getCurrentDrivers()).thenThrow(RuntimeException("Network error"))

        repository.getAllDrivers(forceRefresh = true).test {
            val loadingEmission = awaitItem()
             assertTrue(loadingEmission is Resource.Loading)

            val errorEmission = awaitItem()
            assertTrue(errorEmission is Resource.Error)
            assertEquals("Carlos", errorEmission.data?.firstOrNull()?.firstName)
            assertTrue(errorEmission.message?.contains("Network error") == true)

            awaitComplete()
        }

    }

    private fun createDriverEntity(code: String, firstName: String, lastName: String) = DriverEntity(
        id = code.lowercase(),
        permanentNumber = "1",
        code = code,
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        dateOfBirth = "1997-09-30",
        nationality = "Dutch",
        teamName = "Red Bull Racing",
        teamColor = "3671C6",
        headshotUrl = null,
        wikiUrl = null
    )

    private fun createJolpicaDriver(code: String, firstName: String, lastName: String) = JolpicaDriverDto(
        driverId = code.lowercase(),
        permanentNumber = "1",
        code = code,
        url = null,
        givenName = firstName,
        familyName = lastName,
        dateOfBirth = "1997-09-30",
        nationality = "Dutch"
    )

    private fun createOpenF1Driver(code: String, firstName: String, lastName: String) = OpenF1DriverDto(
        broadcastName = "$firstName $lastName",
        countryCode = "NL",
        driverNumber = "1",
        firstName = firstName,
        fullName = "$firstName $lastName",
        headshotUrl = null,
        lastName = lastName,
        meetingKey = "1234",
        nameAcronym = code,
        sessionKey = "5678",
        teamColour = "3671C6",
        teamName = "Red Bull Racing"
    )
}