package com.ssiriwardana.pitwall.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ssiriwardana.pitwall.Application
import com.ssiriwardana.pitwall.data.local.database.PitwallDB
import com.ssiriwardana.pitwall.data.local.datasource.DriverLocalDataSource
import com.ssiriwardana.pitwall.data.remote.NetworkModule
import com.ssiriwardana.pitwall.data.remote.datasource.DriverRemoteDataSource
import com.ssiriwardana.pitwall.data.repository.DriverRepositoryImpl
import com.ssiriwardana.pitwall.domain.usecase.GetAllDriversUseCase
import com.ssiriwardana.pitwall.domain.usecase.GetDriverDetailsUseCase
import com.ssiriwardana.pitwall.presentation.ui.screens.DriverScreen
import com.ssiriwardana.pitwall.presentation.ui.theme.PitwallTheme
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModel
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DriverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupApp()

        enableEdgeToEdge()
        setContent {
            PitwallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    PitwallApp(viewModel)
                    DriverScreen(viewModel = viewModel)
                    //Todo: add nav panel here. Replace DriverScreen with a Dynamic
                    // module that loads different screens
                }
            }
        }
    }

    private fun setupApp() {
        val db = PitwallDB.getInstance(applicationContext)
        val driverDao = db.driverDao()
        val localDataSource = DriverLocalDataSource(driverDao)

        val jolpicaApi = NetworkModule.provideJolpicaApi()
        val openF1Api = NetworkModule.provideOpenF1pi()
        val remoteDataSource = DriverRemoteDataSource(jolpicaApi, openF1Api)

        val repository = DriverRepositoryImpl(remoteDataSource, localDataSource)

        val getAllDriversUseCase = GetAllDriversUseCase(repository)
        val getDriverDetailsUseCase = GetDriverDetailsUseCase(repository)

        val viewModelFactory = DriverViewModelFactory(getAllDriversUseCase, getDriverDetailsUseCase)
        viewModel = ViewModelProvider(this, viewModelFactory)[DriverViewModel::class.java]
    }
}