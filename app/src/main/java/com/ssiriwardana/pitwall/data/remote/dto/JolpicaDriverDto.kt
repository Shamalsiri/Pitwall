package com.ssiriwardana.pitwall.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO to represent Jolpica API driver response
 */
data class JolpicaDriverDto (

    @SerializedName("driverId")
    val driverId: String,

    @SerializedName("permanentNumber")
    val permanentNumber: String?,

    @SerializedName("code")
    val code: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("givenName")
    val givenName: String,

    @SerializedName("familyName")
    val familyName: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String?,

    @SerializedName("nationality")
    val nationality: String,
)

/**
 * Wrappers for response with multiple drivers
 */
data class JolpicaDriversResponse (
    @SerializedName("MRData")
    val mrData: MRData
)

data class MRData(
    @SerializedName("DriverTable")
    val driverTable: DriverTable
)

data class DriverTable(
    @SerializedName("Drivers")
    val drivers: List<JolpicaDriverDto>
)