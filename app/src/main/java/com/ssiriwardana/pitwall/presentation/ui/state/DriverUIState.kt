package com.ssiriwardana.pitwall.presentation.ui.state

import com.ssiriwardana.pitwall.domain.model.Driver

data class DriverUIState(
    val isLoading: Boolean = false,
    val drivers: List<Driver> = emptyList(),
    val error: String? = null,
    val selectedDriver: Driver? = null,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filteredDrivers: List<Driver> = emptyList(),
    val sortBy: SortOption = SortOption.NAME,
    val favDriverIds: Set<String> = emptySet()
) {
    /**
     * @return a list of drivers to be displayed
     */
    val displayedDrivers: List<Driver>
        get() = if (searchQuery.isNotEmpty()) filteredDrivers else drivers

    /**
     * check if data is available to be displayed
     */
    val hasData: Boolean
        get() = drivers.isNotEmpty()

    fun isFave(driverId: String): Boolean {
        return favDriverIds.contains(driverId)
    }
}

enum class SortOption {
    NAME,
    TEAM,
    NUMBER,
    AGE
}
