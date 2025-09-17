package com.ssiriwardana.pitwall.core.utils

/**
 * Generic wrapper class to handle different states of data operations
 * Keeps handling different states uniform
 */
sealed class Resource<T> (
    val data: T? = null,
    val message: String? = null
) {

    /**
     * Represents Loading state
     */
    class Loading<T>(data: T? = null): Resource<T>(data)

    /**
     * Represent successful data retrieval
     */
    class Success<T>(data: T): Resource<T>(data)

    /**
     * Represent Failed data retrieval (error state)
     */
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}