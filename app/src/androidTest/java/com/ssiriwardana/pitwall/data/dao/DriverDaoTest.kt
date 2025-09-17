package com.ssiriwardana.pitwall.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.ssiriwardana.pitwall.data.local.dao.DriverDao
import com.ssiriwardana.pitwall.data.local.database.PitwallDB
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DriverDaoTest {

    private lateinit var database: PitwallDB
    private lateinit var driverDao: DriverDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PitwallDB::class.java
        ).allowMainThreadQueries().build()

        driverDao = database.driverDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun validateInsertAndGetDrivers() = runTest {
        val drivers = listOf(
            createDriverEntity("VER", "Max", "Verstappen"),
            createDriverEntity("RIC", "Daniel", "Riccardo"),
            createDriverEntity("HAM", "Lewis", "Hamilton")
        )

        driverDao.insertDrivers(drivers)

        driverDao.getAllDrivers().test{
            val res = awaitItem()
            assertEquals("HAM", res[0].code)
            assertEquals("RIC", res[1].code)
            assertEquals("VER", res[2].code)

        }
    }

    @Test
    fun validateGetDriverById() = runTest {
        val driver = createDriverEntity("RIC", "Daniel", "Riccardo")
        driverDao.insertDriver(driver)

        val res = driverDao.getDriverById("ric")

        assertNotNull(res)
        assertEquals("Daniel", res?.firstName)
    }

    @Test
    fun validateSearchFilter() = runTest {
        val drivers = listOf(
            createDriverEntity("VER", "Max", "Verstappen"),
            createDriverEntity("RIC", "Daniel", "Riccardo"),
            createDriverEntity("BEA", "Oliver", "Bearman"),
            createDriverEntity("HAM", "Lewis", "Hamilton")
        )
        driverDao.insertDrivers(drivers)

        driverDao.searchDrivers("ver").test {
            val res = awaitItem()

            assertEquals(res.size, 2)
            assertTrue(res.any() { it.code == "VER" })
            assertTrue(res.any() { it.code == "BEA" })
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun validateDeleteDBClearsDrivers() = runTest {
        val drivers = listOf(
            createDriverEntity("VER", "Max", "Verstappen"),
            createDriverEntity("RIC", "Daniel", "Riccardo"),
            createDriverEntity("BEA", "Oliver", "Bearman"),
            createDriverEntity("HAM", "Lewis", "Hamilton")
        )
        driverDao.insertDrivers(drivers)

        driverDao.deleteAllDrivers()

        val res = driverDao.getDriverCount()
        assertEquals(0, res)
    }

    @Test
    fun validateDriverReplaceStrategy() = runTest {
        val driver = createDriverEntity("RIC", "Daniel", "Riccardo")
        val updatedDriver = createDriverEntity("RIC", "Dan", "Riccardo")

        driverDao.insertDriver(driver)
        driverDao.insertDriver(updatedDriver)

        val res = driverDao.getDriverById("ric")
        assertEquals(res?.firstName, "Dan")
    }

    @Test
    fun validateGetOldestUpdateTime() = runTest {
        val oldDriver = createDriverEntity("RIC", "Daniel", "Riccardo").copy(
            lastUpdated = System.currentTimeMillis() - 20000
        )
        val newDriver = createDriverEntity("SIR", "Shamal", "Siriwardana").copy(
            lastUpdated = System.currentTimeMillis()
        )

        driverDao.insertDrivers(listOf(oldDriver, newDriver))

        val res = driverDao.getOldestUpdateTimestamp()
        assertNotNull(res)
        assertEquals(res, oldDriver.lastUpdated)
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

}