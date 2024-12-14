package com.ssiriwardana.pitwall.model

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Driver(
    // "broadcast_name": "M VERSTAPPEN",
    // "country_code": "NED",
    // "driver_number": 1,
    // "first_name": "Max",
    // "full_name": "Max VERSTAPPEN",
    // "headshot_url": "https://www.formula1.com/content/dam/fom-website/drivers/M/MAXVER01_Max_Verstappen/maxver01.png.transform/1col/image.png",
    // "last_name": "Verstappen",
    // "meeting_key": 1219,
    // "name_acronym": "VER",
    // "session_key": 9158,
    // "team_colour": "3671C6",
    // "team_name": "Red Bull Racing"
    @SerialName("broadcast_name")
    val broadcastName: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("driver_number")
    val driverNumber: Int? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("headshot_url")
    val headshotUrl: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("meeting_key")
    val meetingKey: Int? = null,
    @SerialName("name_acronym")
    val nameAcronym: String? = null,
    @SerialName("session_key")
    val sessionKey: Int? = null,
    @SerialName("team_colour")
    val teamColor: String? = null,
    @SerialName("team_name")
    val teamName: String? = null,
    var headshotFilePath: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other ) return true
        if (other !is Driver) return false
        return fullName.equals(other.fullName, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return fullName?.lowercase().hashCode() ?: 0
    }

    companion object {
        fun List<Driver>.getLatestDriverSortedByMeetingId() : List<Driver> {
            return this
                .groupBy { it.fullName }
                .mapNotNull { (_, drivers) ->
                    drivers.maxByOrNull {
                        maxOf(it.meetingKey ?: 0)
                    }
                }
        }

        suspend fun List<Driver>.fetchHeadshotAndStore(context: Context): List<Driver>? {
            try {
                this.forEach {
                    driver ->
                    val headShotFilePath = downloadHeadshot(context, driver)
                    driver.headshotFilePath = headShotFilePath.toString()
                }
            } catch (e: Exception) {
                return null
            }
            return this
        }

        private suspend fun downloadHeadshot(context: Context, driver: Driver): File? {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(driver.headshotUrl)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable
            result?.let {
                val image = File(context.cacheDir, "images/${driver.nameAcronym}.png")
                image.parentFile?.mkdirs()
                image.outputStream().use { outputStream ->
                    (result as? android.graphics.drawable.BitmapDrawable)?.bitmap?.compress(
                        android.graphics.Bitmap.CompressFormat.PNG,100, outputStream
                    )
                }
                Log.d("SSIRI", "Headshot Downloaded: ${driver.nameAcronym}")
                return image.absoluteFile
            }
            return null
        }
    }
}
