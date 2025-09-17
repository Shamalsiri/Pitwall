package com.ssiriwardana.pitwall.data.remote.api

import com.ssiriwardana.pitwall.data.remote.dto.JolpicaDriversResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for Jolpica F1 API
 */
interface JolpicaF1Api {

    companion object {
        const val BASE_URL = "https://api.jolpi.ca/ergast/f1/"
    }

    /**
     * Fetch drivers of the current season
     */
    @GET("current/drivers.json")
    suspend fun getCurrentDrivers(): JolpicaDriversResponse

    /**
     * Fetch drivers of a given year
     */
    @GET("{year}/drivers.json")
    suspend fun getDriversByYear(
        @Path("year") year: Int): JolpicaDriversResponse

    /**
     * Fetah all drivers since the start of F1
     */
    @GET("drivers.json")
    suspend fun getAllDrivers(): JolpicaDriversResponse
}