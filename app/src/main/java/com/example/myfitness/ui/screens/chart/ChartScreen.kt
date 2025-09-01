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
import com.example.myfitness.ui.composables.MyBarChart
import com.example.myfitness.ui.screens.auth.AuthViewModel

@Composable
fun ChartScreen(
    state: ChartState,
    actions: ChartActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    LaunchedEffect(Unit) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            actions.loadTrainingData(userId)
        }
    }

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
                state.barChartEntries.isNotEmpty() -> {
                    MyBarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        entries = state.barChartEntries,
                        labels = state.xAxisLabels // ✅ Passa le etichette al grafico
                    )
                }
                else -> {
                    Text("Nessun dato disponibile.")
                }
            }
        }
    }
}