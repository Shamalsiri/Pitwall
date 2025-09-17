package com.ssiriwardana.pitwall.data.remote.api

import com.ssiriwardana.pitwall.data.remote.dto.OpenF1DriverDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Open F1 API
 */
interface OpenF1Api {

    companion object {
        val BASE_URL = "https://api.openf1.org/v1/"
    }

    /**
     * Fetch drivers of a specific meeting or session
     */
    @GET("drivers")
    suspend fun getDrivers(
        @Query("session_key") sessionKey: String? = "latest",
        @Query("session_key") meetingKey: String? = null,
        @Query("session_key") driverNumber: String? = null
    ): List<OpenF1DriverDto>
}