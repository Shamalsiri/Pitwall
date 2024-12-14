@file:OptIn(ExperimentalMaterial3Api::class)

package com.ssiriwardana.pitwall

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssiriwardana.pitwall.ui.screens.HomeScreen
import com.ssiriwardana.pitwall.ui.screens.PitwallViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PitwallApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { PitwallTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface (
            modifier = Modifier.fillMaxSize()
        ) {
            val pitwallViewModel: PitwallViewModel = viewModel()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                pitwallViewModel.getDrivers(context)
            }
            HomeScreen(
                pitwallUIState = pitwallViewModel.pitwallUiState,
                contentPadding = it
            )
        }
    }
}

@Composable
fun PitwallTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}