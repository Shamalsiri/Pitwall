package com.ssiriwardana.pitwall.data.remote

import com.ssiriwardana.pitwall.data.remote.api.JolpicaF1Api
import com.ssiriwardana.pitwall.data.remote.api.OpenF1Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun provideJolpicaApi(): JolpicaF1Api {
        return Retrofit.Builder()
            .baseUrl(JolpicaF1Api.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JolpicaF1Api::class.java)
    }

    fun provideOpenF1pi(): OpenF1Api {
        return Retrofit.Builder()
            .baseUrl(OpenF1Api.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenF1Api::class.java)
    }
}