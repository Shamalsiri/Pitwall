package com.ssiriwardana.pitwall.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for driver entity
 */
@Dao
interface DriverDao {

    /**
     * Insert multiple drivers to the table
     * @param drivers List of DriverEntity to be inserted to the table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    /**
     * Insert single driver to the table
     * @param driver DriverEntity to be inserted to the table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    /**
     * Observe all drivers in the db
     */
    @Query("SELECT * FROM drivers ORDER BY lastName ASC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    /**
     * Get all drivers from db (No Observing)
     */
    @Query("SELECT * FROM drivers ORDER BY lastName ASC")
    suspend fun getAllDriversOneShot(): List<DriverEntity>

    /**
     * Get driver by driverId
     * @param driverId unique id of the driver
     */
    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun  getDriverById(driverId: String): DriverEntity?

    /**
     * Search driver by query
     * @param query first name, last name, full name, or the driver acronym of the driver
     */
    @Query("SELECT * FROM drivers WHERE " +
            "firstName LIKE '%' || :query || '%' OR " +
            "lastName LIKE '%' || :query || '%' OR " +
            "fullName LIKE '%' || :query || '%' OR " +
            "code LIKE '%' || :query || '%' " +
            "ORDER By lastName ASC")
    fun searchDrivers(query: String): Flow<List<DriverEntity>>

    /**
     * Delete all drivers from the driver table
     */
    @Query("DELETE FROM drivers")
    suspend fun deleteAllDrivers()

    /**
     * Check last update time of the data to make sure the data is not relevant
     */
    @Query("SELECT MIN (lastUpdated) FROM drivers")
    suspend fun getOldestUpdateTimestamp(): Long?

    /**
     * Get the total number of drivers in the db
     */
    @Query("SELECT COUNT(*) FROM drivers")
    suspend fun  getDriverCount(): Int

}