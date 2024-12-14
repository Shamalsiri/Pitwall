package com.ssiriwardana.pitwall.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssiriwardana.pitwall.model.Driver
import com.ssiriwardana.pitwall.model.Driver.Companion.fetchHeadshotAndStore
import com.ssiriwardana.pitwall.model.Driver.Companion.getLatestDriverSortedByMeetingId
import com.ssiriwardana.pitwall.network.DriversApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface PitwallUIState {
    data class Success(val drivers: String) : PitwallUIState
    object Error : PitwallUIState
    object Loading : PitwallUIState
}

class PitwallViewModel: ViewModel() {
    private val TAG = "PitwallViewModel"

    var pitwallUiState : PitwallUIState by mutableStateOf(PitwallUIState.Loading)
    private set

    fun getDrivers(context: Context) {
        viewModelScope.launch {
            pitwallUiState = PitwallUIState.Loading
            pitwallUiState = try {
                var listDrivers = DriversApi.retrofitService.getDrivers()
                listDrivers = listDrivers.getLatestDriverSortedByMeetingId()
                listDrivers = getUniqueDriversByFullName(drivers = listDrivers)
                listDrivers = listDrivers.sortedBy{ it.meetingKey }
                listDrivers = listDrivers.fetchHeadshotAndStore(context)!!
                listDrivers.forEach { 
                    driver ->

                    Log.d(TAG, "${driver.driverNumber} \t ${driver.fullName}")
                    Log.d(TAG, "mId: ${driver.meetingKey} \t sId: ${driver.sessionKey}")
                }
                    
                PitwallUIState.Success("Success: ${listDrivers.size} drivers retrieved")
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                PitwallUIState.Error
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error", e)
                PitwallUIState.Error
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error", e)
                PitwallUIState.Error
            }
        }
    }

    private fun getUniqueDriversByFullName(drivers: List<Driver>): List<Driver> {
        return drivers.toSet().toList()
    }
}
