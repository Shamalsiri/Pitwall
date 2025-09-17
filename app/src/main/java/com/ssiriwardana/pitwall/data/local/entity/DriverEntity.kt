package com.ssiriwardana.pitwall.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room db entity for drivers
 */
@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
    val id: String,
    val permanentNumber: String?,
    val code: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val dateOfBirth: String?,
    val nationality: String,
    val teamName: String?,
    val teamColor: String?,
    val headshotUrl: String?,
    val wikiUrl: String?,
    val lastUpdated: Long = System.currentTimeMillis(),
    )