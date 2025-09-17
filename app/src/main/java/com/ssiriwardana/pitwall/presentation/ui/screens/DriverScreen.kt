package com.ssiriwardana.pitwall.presentation.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ssiriwardana.pitwall.presentation.ui.components.DriverCard
import com.ssiriwardana.pitwall.presentation.ui.components.DriverDetailDialog
import com.ssiriwardana.pitwall.presentation.ui.state.SortOption
import com.ssiriwardana.pitwall.presentation.viewModel.DriverViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverScreen(
    viewModel: DriverViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val gridColumn = if (isLandscape) 2 else 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("F1 Drivers") },
                actions = {
                    var showSearch by remember { mutableStateOf(false) }

                    if (showSearch) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("Search drivers... ") },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(end = 8.dp),
                            singleLine = true
                        )
                    }

                    IconButton(onClick = {
                        showSearch = !showSearch
                        if (!showSearch) {
                            viewModel.onSearchQueryChanged("")
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    var showSortMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort")
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name.capitalize(Locale.ROOT)) },
                                onClick = {
                                    viewModel.changeSortOption(option)
                                    showSortMenu = false
                                }

                            )
                        }
                    }

                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isRefreshing),
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(paddingValues)
        ) {
            when {
                uiState.isLoading && !uiState.hasData -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null && !uiState.hasData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = viewModel::refresh) {
                            Text("Retry")
                        }
                    }
                }

                uiState.displayedDrivers.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No drivers found matching \"${uiState.searchQuery}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center

                        )
                    }
                }

                else -> {
                    // Display drivers grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumn),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.displayedDrivers,
                            key = { it.id },

                        ) { driver ->
                            DriverCard(
                                driver = driver,
                                isFav = uiState.isFave(driver.id),
                                onClick = { viewModel.selectDriver(driver) }
                            )
                        }
                    }

                }
            }

            if (uiState.error != null && uiState.hasData) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.error.toString())
                }
            }

            uiState.selectedDriver?.let { driver ->
                DriverDetailDialog(
                    driver = driver,
                    onDismiss = viewModel::clearSelectedDriver,
                    isFav = uiState.isFave(driver.id),
                    onFavToggle = {
                        viewModel.toggleFavDriver(driver.id)
                    }
                )
            }
        }
    }
}

