package com.ssiriwardana.pitwall.domain.usecase

import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.repository.DriverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for fetching all F1 drivers
 */
class GetAllDriversUseCase(
    private val repository: DriverRepository
) {

    /**
     * Executes use case to get all drivers
     *
     * @param forceRefresh if true, get fresh data from remote source
     * @param sortByTeam if true, sort drivers by team
     * @return Flow of resource containing list of drivers
     */
    suspend operator fun invoke(
        forceRefresh: Boolean = false,
        sortByTeam: Boolean = false
    ): Flow<Resource<List<Driver>>> {
        return repository.getAllDrivers(forceRefresh).map { resource ->
            when (resource) {

                // Getting Drivers Successful
                is Resource.Success -> {
                    var drivers = resource.data ?: emptyList()
                    drivers = drivers.filter { drivers -> drivers.teamName != "N/A" }
                    val sortedDriver = if (sortByTeam) {
                        drivers.sortedBy { it.teamName }
                    } else {
                        drivers.sortedBy { it.lastName }
                    }
                    Resource.Success(sortedDriver)
                }

                // Getting Drivers loading
                is Resource.Loading -> {
                    Resource.Loading(resource.data)
                }

                // Error getting Drivers
                is Resource.Error -> Resource.Error(
                    resource.message ?: "Unknown Error",
                    resource.data
                )
            }
        }
    }
}
