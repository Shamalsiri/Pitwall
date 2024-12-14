package com.ssiriwardana.pitwall.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ssiriwardana.pitwall.model.Driver
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

const val DRIVERS_URL = "https://api.openf1.org/v1/"

private val retrofit = Retrofit.Builder()
    .baseUrl(DRIVERS_URL)
    .addConverterFactory(Json { ignoreUnknownKeys = true }
        .asConverterFactory("application/json"
        .toMediaType()))
    .build()

interface DriversApiService {
    @GET("drivers")
    suspend fun getDrivers(): List<Driver>
}

object DriversApi {
    val retrofitService: DriversApiService by lazy {
        retrofit.create(DriversApiService::class.java)
    }
}