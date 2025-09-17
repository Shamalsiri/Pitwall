package com.ssiriwardana.pitwall.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.usecase.GetAllDriversUseCase
import com.ssiriwardana.pitwall.domain.usecase.GetDriverDetailsUseCase
import com.ssiriwardana.pitwall.presentation.ui.state.DriverUIState
import com.ssiriwardana.pitwall.presentation.ui.state.SortOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverViewModel(
    private val getAllDriversUseCase: GetAllDriversUseCase,
    private val getDriverDetailsUseCase: GetDriverDetailsUseCase
) : ViewModel()
{

    private val _uiState = MutableStateFlow(DriverUIState())
    val uiState: StateFlow<DriverUIState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadDrivers()
    }

    fun loadDrivers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getAllDriversUseCase(
                forceRefresh = forceRefresh,
                sortByTeam = _uiState.value.sortBy == SortOption.TEAM
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = !currentState.hasData && !forceRefresh,
                                isRefreshing = forceRefresh,
                                drivers = resource.data ?: currentState.drivers,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message,
                                drivers = resource.data ?: currentState.drivers
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message,
                                drivers = resource.data ?: currentState.drivers
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Get details for the selected driver
     */
    fun selectDriver(driver: Driver) {
        _uiState.update { it.copy(selectedDriver = driver) }

        viewModelScope.launch {
            getDriverDetailsUseCase(driver.id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { detailDriver ->
                            _uiState.update {
                                it.copy(
                                    selectedDriver = detailDriver
                                )
                            }
                        }
                    }

                    else -> {
//                        TODO("Load basic info of the driver")
                    }
                }
            }
        }
    }

    /**
     * Reset the DriverUIState's selectedDriver value
     */
    fun clearSelectedDriver() {
        _uiState.update { it.copy(selectedDriver = null) }
    }

    /**
     * Handle search query change
     * wait 300ms after recieving the query
     */
    fun onSearchQueryChanged(query: String) {
//        TODO("Replace with kotlin flow debounce")
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    /**
     * Search a specific driver by filtering the data
     */
    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            _uiState.update { it.copy(filteredDrivers = emptyList()) }
            return
        }

        val filtered = _uiState.value.drivers.filter { driver ->
            driver.firstName.contains(query, ignoreCase = true) ||
                    driver.lastName.contains(query, ignoreCase = true) ||
                    driver.fullName.contains(query, ignoreCase = true) ||
                    driver.code.contains(query, ignoreCase = true) ||
                    driver.teamName.contains(query, ignoreCase = true) ||
                    driver.permanentNumber?.contains(query) == true
        }

        _uiState.update { it.copy(filteredDrivers = filtered) }
    }

    /**
     * Re-sort drivers when sort option is changed
     */
    fun changeSortOption(sortOption: SortOption) {
        _uiState.update { currentState ->
            val sortedDrivers = sortDrivers(currentState.drivers, sortOption)
            currentState.copy(
                sortBy = sortOption,
                drivers = sortedDrivers
            )

        }

        if (_uiState.value.searchQuery.isNotEmpty()) {
            performSearch(_uiState.value.searchQuery)
        }
    }

    /**
     * Sort drivers by SortOption
     */
    private fun sortDrivers(drivers: List<Driver>, sortBy: SortOption): List<Driver> {
        return when (sortBy) {
            SortOption.NAME -> drivers.sortedBy { it.lastName }

            SortOption.TEAM -> drivers.sortedWith(
                compareBy({ it.teamName ?: "ZZZ" }, { it.lastName })
            )

            SortOption.NUMBER -> drivers.sortedBy {
                it.permanentNumber?.toIntOrNull() ?: Int.MAX_VALUE
            }

            SortOption.AGE -> drivers.sortedBy { it.age ?: Int.MAX_VALUE }
        }
    }

    /**
     * Refreshed the driver list
     */
    fun refresh() {
        loadDrivers(forceRefresh = true)
    }

    /**
     * Clears any error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun toggleFavDriver(driverId: String) {
        _uiState.update { currentState ->
            val currentFavDrivers = currentState.favDriverIds
            val newFavDrivers =
                if (currentFavDrivers.contains(driverId)) {
                    currentFavDrivers - driverId
                } else {
                    currentFavDrivers + driverId
                }

            currentState.copy(favDriverIds = newFavDrivers)
        }
    }
}