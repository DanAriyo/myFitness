// ui/screens/chart/ChartScreen.kt

package com.example.myfitness.ui.screens.chart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import org.koin.androidx.compose.koinViewModel
import com.example.myfitness.ui.composables.MyBarChart // Assuming you placed the composable here

@Composable
fun ChartScreen(
    viewModel: ChartViewModel = koinViewModel(),
    navController: NavController
) {
    // ✅ Use the state from the ViewModel
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // ✅ Access the correct data property
                state.barChartEntries.isNotEmpty() -> {
                    // Assuming MyBarChart is the composable you created
                    MyBarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        entries = state.barChartEntries
                    )
                }
                else -> {
                    Text("Nessun dato disponibile.")
                }
            }
        }
    }
}