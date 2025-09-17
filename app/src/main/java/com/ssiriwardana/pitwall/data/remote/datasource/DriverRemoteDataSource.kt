package com.ssiriwardana.pitwall.data.remote.datasource

import android.util.Log
import com.ssiriwardana.pitwall.data.remote.api.JolpicaF1Api
import com.ssiriwardana.pitwall.data.remote.api.OpenF1Api
import com.ssiriwardana.pitwall.data.remote.dto.JolpicaDriverDto
import com.ssiriwardana.pitwall.data.remote.dto.OpenF1DriverDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Remote data source for drivers
 */
class DriverRemoteDataSource(
    private val jolpicaF1Api: JolpicaF1Api,
    private val openF1Api: OpenF1Api
) {
    companion object {
        val TAG = DriverRemoteDataSource::class.simpleName
    }

    /**
     * Fetch current drivers from both APIs and combine the data
     */
    suspend fun getCurrentDrivers(): CombineDriverData = withContext(Dispatchers.IO) {
        coroutineScope {

            val deferredJolpica = async {
                try {
                    jolpicaF1Api.getCurrentDrivers().mrData.driverTable.drivers
                } catch (e: Exception) {
                    Log.e(TAG, "getCurrentDrivers: Error fetching Jolpica data", e)
                    emptyList()
                }
            }

            val deferredOpenF1 = async {
                try {
                    openF1Api.getDrivers(sessionKey = "latest")
                } catch (e: Exception){
                    Log.e(TAG, "getCurrentDrivers: Error fetching open F1 data", e)
                    emptyList()
                }
            }

            val jolpicaDrivers = deferredJolpica.await()
            val openF1Drivers = deferredOpenF1.await()

            CombineDriverData(jolpicaDrivers, openF1Drivers)
        }
    }

    /**
     * Fetches detailed information for specific driver
     */
    suspend fun getDriverDetails(driverId:String): CombineDriverData = withContext(Dispatchers.IO) {
        val allDrivers = getCurrentDrivers()

        CombineDriverData(
            jolpicaDrivers = allDrivers.jolpicaDrivers.filter { it.driverId == driverId },
            openF1Drivers = allDrivers.openF1Drivers.filter { it.nameAcronym?.lowercase() == driverId.lowercase() ||
                    it.lastName?.lowercase() == driverId.lowercase()  }
        )
    }

}

/**
 * Container for combined driver data from both remote sources
 */
data class CombineDriverData(
    val jolpicaDrivers: List<JolpicaDriverDto>,
    val openF1Drivers: List<OpenF1DriverDto>
)