package com.ssiriwardana.pitwall.data.mapper

import android.util.Log
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import com.ssiriwardana.pitwall.data.remote.datasource.CombineDriverData
import com.ssiriwardana.pitwall.data.remote.dto.JolpicaDriverDto
import com.ssiriwardana.pitwall.data.remote.dto.OpenF1DriverDto
import com.ssiriwardana.pitwall.domain.model.Driver
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper class for converting drivers between different representations
 */
object DriverMapper {

    private val TAG: String = DriverMapper::class.simpleName.toString()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Maps combined data from both API into a domain model
     */
    fun mapToDomainModel(combineData: CombineDriverData): List<Driver> {
        val jolpicaMap = combineData.jolpicaDrivers.associateBy { it.code }
        val openF1Map = combineData.openF1Drivers.associateBy { it.nameAcronym }

        val driversFromJolpica = combineData.jolpicaDrivers.map { jolpicaDriver ->
            val openF1Driver = openF1Map[jolpicaDriver.code]
            mapToDriver(jolpicaDriver, openF1Driver)

        }

        val additionalDrivers = combineData.openF1Drivers
            .filter { it.nameAcronym !in jolpicaMap.keys }
            .map { mapToDriver(null, it) }

        return driversFromJolpica + additionalDrivers
    }

    /**
     * Maps DTO to domain model
     */
    private fun mapToDriver(
        jolpicaDto: JolpicaDriverDto?,
        openF1Dto: OpenF1DriverDto?
    ): Driver {
        val id = jolpicaDto?.driverId ?: openF1Dto?.nameAcronym ?: "unknown"
        val firstName = jolpicaDto?.givenName ?: openF1Dto?.firstName ?: ""
        val lastName = jolpicaDto?.familyName ?: openF1Dto?.lastName ?: ""
        val code = jolpicaDto?.code ?: openF1Dto?.nameAcronym ?: "???"

        return Driver(
            id = id,
            permanentNumber = openF1Dto?.driverNumber ?: jolpicaDto?.permanentNumber?.toString(),
            code = code,
            firstName = firstName,
            lastName = lastName,
            fullName = openF1Dto?.fullName ?: "$firstName $lastName",
            dateOfBirth = jolpicaDto?.dateOfBirth?.let { parseDate(it) },
            nationality = jolpicaDto?.nationality ?: openF1Dto?.countryCode ?: "",
            teamName = openF1Dto?.teamName ?: "N/A",
            headshotUrl = openF1Dto?.headshotUrl,
            wikiUrl = jolpicaDto?.url,
            teamColor = openF1Dto?.teamColour
        )
    }

    /**
     * Maps domain model to db Entity
     */
    fun mapToEntity(driver: Driver): DriverEntity {
        return DriverEntity(
            id = driver.id,
            permanentNumber = driver.permanentNumber,
            code = driver.code,
            firstName = driver.firstName,
            lastName = driver.lastName,
            fullName = driver.fullName,
            dateOfBirth = driver.dateOfBirth?.format(dateFormatter),
            nationality = driver.nationality,
            teamName = driver.teamName,
            teamColor = driver.teamColor,
            headshotUrl = driver.headshotUrl,
            wikiUrl = driver.wikiUrl
        )
    }


    /**
     * Maps db Entity to Domain model
     */
    fun mapToDomain(entity: DriverEntity): Driver {
        return Driver(
            id = entity.id,
            permanentNumber = entity.permanentNumber,
            code = entity.code,
            firstName = entity.firstName,
            lastName = entity.lastName,
            fullName = entity.fullName,
            dateOfBirth = entity.dateOfBirth?.let { parseDate(it) },
            nationality = entity.nationality,
            teamName = entity.teamName.toString(),
            teamColor = entity.teamColor,
            headshotUrl = entity.headshotUrl,
            wikiUrl = entity.wikiUrl

        )
    }

    /**
     * Maps list of domain models to db Entities
     */
    fun mapToDomain(entities: List<DriverEntity>): List<Driver> {
        return entities.map { mapToDomain(it) }
    }

    /**
     * Maps list of db Entities to Domain models
     */
    fun mapToEntities(drivers: List<Driver>): List<DriverEntity> {
        return drivers.map { mapToEntity(it) }
    }

    /**
     * Parse date string to LocalDate
     */
    private fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: Exception) {
            Log.e(TAG, "parseDate: Failed to parse date", e)
            null
        }
    }


}