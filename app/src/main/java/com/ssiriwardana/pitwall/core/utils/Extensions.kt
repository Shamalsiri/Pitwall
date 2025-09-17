package com.ssiriwardana.pitwall.core.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Common Operations
 */

fun LocalDate.toDisplayFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    return this.format(formatter)
}

/**
 * Converts hex (with and without # prefix) color string to android color
 */
fun String.toColorInt(): Int {
    val hex = if (this.startsWith("#")) this else "#$this"
    return try {
        android.graphics.Color.parseColor(hex)
    } catch (e: Exception) {
        android.graphics.Color.GRAY
    }
}

/**
 * Capitalize first Char of each word
 */
fun String.toTitleCase(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
             word.lowercase().replaceFirstChar {
                 if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                 else it.toString()
             }
        }
}