package com.ssiriwardana.pitwall.data.local.datasource

import com.ssiriwardana.pitwall.data.local.dao.DriverDao
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

/**
 * Local data source for drivers
 */
class DriverLocalDataSource(
    private val driverDao: DriverDao
) {

    /**
     * 1 hr in ms for cache validity
     */
    private val CACHE_VALIDITY_MS = 60 * 60 * 1000L

    suspend fun insertDrivers(drivers: List<DriverEntity>) = driverDao.insertDrivers(drivers)

    suspend fun clearAndInsertDrivers(drivers: List<DriverEntity>) {
        driverDao.deleteAllDrivers()
        driverDao.insertDrivers(drivers)
    }

    fun getAllDrivers(): Flow<List<DriverEntity>> = driverDao.getAllDrivers()

    suspend fun getAllDriversOneShot(): List<DriverEntity> = driverDao.getAllDriversOneShot()

    suspend fun getDriverById(driverId: String): DriverEntity? = driverDao.getDriverById(driverId)

    fun searchDrivers(query: String): Flow<List<DriverEntity>> = driverDao.searchDrivers(query)

    /**
     * Check if the cached driver data is valid
     */
    suspend fun isCachedDataValid(): Boolean {
        val oldestUpdate = driverDao.getOldestUpdateTimestamp() ?: return false
        val currentTime = System.currentTimeMillis()
        return (currentTime - oldestUpdate) < CACHE_VALIDITY_MS
    }
}