package com.ssiriwardana.pitwall.domain.usecase

import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.repository.DriverRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching driver details
 */
class GetDriverDetailsUseCase(
    private val repository: DriverRepository
) {

    /**
     * Execute the use case for getting driver details
     *
     * @param driverId unique driver id
     * @return Flow of Resource containing driver details
     */
    suspend operator fun invoke(driverId: String): Flow<Resource<Driver>> {
        return repository.getDriverDetails(driverId)
    }
}