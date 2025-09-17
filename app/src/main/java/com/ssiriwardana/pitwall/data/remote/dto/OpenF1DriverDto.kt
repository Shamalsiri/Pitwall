package com.ssiriwardana.pitwall.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO to represent Open F1 API driver response
 */
data class OpenF1DriverDto(

    @SerializedName("broadcast_name")
    val broadcastName: String?,

    @SerializedName("country_code")
    val countryCode: String?,

    @SerializedName("driver_number")
    val driverNumber: String?,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("headshot_url")
    val headshotUrl: String?,

    @SerializedName("last_name")
    val lastName: String?,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("meeting_key")
    val meetingKey: String?,

    @SerializedName("name_acronym")
    val nameAcronym: String?,

    @SerializedName("session_key")
    val sessionKey: String?,

    @SerializedName("team_colour")
    val teamColour: String?,

    @SerializedName("team_name")
    val teamName: String?
)
