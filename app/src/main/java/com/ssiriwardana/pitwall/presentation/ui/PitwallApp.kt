package com.ssiriwardana.pitwall.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssiriwardana.pitwall.presentation.navigation.NavBar
import com.ssiriwardana.pitwall.presentation.navigation.NavRoutes
import com.ssiriwardana.pitwall.presentation.ui.screens.DriverScreen

@Composable
fun PitwallApp(
    driversViewModel: DriverViewModel,
    //todo add constructorViewModel here when ready
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: NavRoutes.DRIVERS

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavBar(
                currentRoute = currentRoute,
                onNavigation = {route ->
                    when (route) {
                     NavRoutes.SETTINGS -> {
                         // TODO: Start Settings Activity
//                         context.startActivity()
                        }
                        else -> {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    ) {
        paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.DRIVERS,
            modifier = Modifier.padding(paddingValues)
        ){
            composable(NavRoutes.DRIVERS) {
                DriverScreen(viewModel = driversViewModel)
            }

            composable(NavRoutes.CONSTRUCTORS) {
                DriverScreen(viewModel = driversViewModel)
            }
            composable(NavRoutes.CIRCUITS) {
                DriverScreen(viewModel = driversViewModel)
            }
            composable(NavRoutes.RACES) {
                DriverScreen(viewModel = driversViewModel)
            }


        }
    }

}