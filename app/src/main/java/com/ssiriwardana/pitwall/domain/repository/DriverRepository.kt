package com.ssiriwardana.pitwall.domain.repository

import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for all Driver data needs
 */
interface DriverRepository {

    /**
     * Fetch all drivers
     *
     * @param forceRefresh if true, download drivers from the remote source
     * @return Flow of the Resource state
     */
    suspend fun getAllDrivers(forceRefresh: Boolean = false): Flow<Resource<List<Driver>>>

    /**
     * Fetch specific driver
     *
     * @param driverId Unique driver id
     * @return Flow of the Resource state with driver details
     */
    suspend fun getDriverDetails(driverId: String): Flow<Resource<Driver>>

    /**
     * Search Drivers by name
     *
     * @param query Search query to match driver names
     * @return Flow of Resource state with filtered list of drivers
     */
    suspend fun searchDrivers(query: String): Flow<Resource<List<Driver>>>
}