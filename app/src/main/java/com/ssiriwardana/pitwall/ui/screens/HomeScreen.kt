package com.ssiriwardana.pitwall.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    pitwallUIState: PitwallUIState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    when (pitwallUIState) {
        is PitwallUIState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is PitwallUIState.Success -> ResultScreen (
            pitwallUIState.drivers, modifier = modifier.fillMaxSize()
        )

        PitwallUIState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun ErrorScreen(modifier: Modifier) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center) {
        Text(text = "Error Screen")
    }
}

@Composable
fun ResultScreen(drivers: String, modifier: Modifier) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center) {
        Column {
            Text(text = "Result Screen")
            Text(text = "Drivers: $drivers")

        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center) {
        Text(text = "Loading Screen")
    }
}
