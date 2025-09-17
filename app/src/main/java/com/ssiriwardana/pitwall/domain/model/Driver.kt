package com.ssiriwardana.pitwall.domain.model

import java.time.LocalDate
import java.time.Period

/**
 * Domain model representing the F1 Driver
 * @property id Unique identifier for the driver
 * @property permanentNumber The permanent racing number assigned to the driver
 * @property code Three-letter driver code (e.g., "VER", "HAM")
 * @property firstName Driver's first name
 * @property lastName Driver's last name
 * @property fullName Complete name of the driver
 * @property dateOfBirth Driver's birth date
 * @property nationality Driver's nationality
 * @property teamName Current team name
 * @property teamColor Team's color in hex format (RRGGBB)
 * @property headshotUrl URL to driver's headshot image
 * @property wikiUrl URL to driver's Wikipedia page
 */
data class Driver (
    val id: String,
    val permanentNumber: String?,
    val code: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val dateOfBirth: LocalDate?,
    val nationality: String,
    val teamName: String,
    val teamColor: String?,
    val headshotUrl: String?,
    val wikiUrl: String?
){

    /**
     * Use date of birth to calculate the age of the driver
     */
    val age: Int?
        get() = dateOfBirth?.let { Period.between (it, LocalDate.now()).years }

    /**
     * Combine the full name + acronym/code to generate the displayName
     */
    val displayName: String
        get() = "$fullName ($code)"
}