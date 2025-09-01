package com.example.myfitness.ui.screens.chart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.composables.MyBarChart
import com.example.myfitness.ui.composables.MyLineChart // Importa il grafico a linee
import com.example.myfitness.ui.screens.auth.AuthViewModel

// Definizione dell'enum per i tipi di grafico
enum class ChartType {
    CALORIES,
    WEEKLY_TRAININGS
}

@Composable
fun ChartScreen(
    state: ChartState,
    actions: ChartActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var selectedChartType by remember { mutableStateOf(ChartType.CALORIES) }

    LaunchedEffect(selectedChartType) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            when (selectedChartType) {
                ChartType.CALORIES -> actions.loadTrainingData(userId)
                ChartType.WEEKLY_TRAININGS -> actions.loadWeeklyTrainingData(userId)
            }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Segmented Button per la selezione del grafico
            SegmentedButton(
                selectedChartType = selectedChartType,
                onChartTypeSelected = { selectedChartType = it },
                modifier = Modifier.padding(16.dp)
            )

            // Contenuto dinamico in base alla selezione
            Box(
                modifier = Modifier
                    .fillMaxSize(),
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
                    else -> {
                        when (selectedChartType) {
                            ChartType.CALORIES -> {
                                if (state.barChartEntries.isNotEmpty()) {
                                    MyBarChart(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        entries = state.barChartEntries,
                                        labels = state.xAxisLabels
                                    )
                                } else {
                                    Text("Nessun dato sulle calorie.")
                                }
                            }
                            ChartType.WEEKLY_TRAININGS -> {
                                if (state.lineChartEntries.isNotEmpty()) {
                                    MyLineChart(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        entries = state.lineChartEntries,
                                        labels = listOf("Sett. 4", "Sett. 3", "Sett. 2", "Corr.")
                                    )
                                } else {
                                    Text("Nessun dato sugli allenamenti settimanali.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedButton(
    selectedChartType: ChartType,
    onChartTypeSelected: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { onChartTypeSelected(ChartType.CALORIES) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedChartType == ChartType.CALORIES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Calorie")
        }
        Button(
            onClick = { onChartTypeSelected(ChartType.WEEKLY_TRAININGS) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedChartType == ChartType.WEEKLY_TRAININGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Settimanale")
        }
    }
}