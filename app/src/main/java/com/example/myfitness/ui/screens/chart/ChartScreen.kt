package com.example.myfitness.ui.screens.chart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.composables.TopBar
import com.example.myfitness.ui.composables.MyBarChart
import com.example.myfitness.ui.composables.MyLineChart
import com.example.myfitness.ui.screens.auth.AuthViewModel

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

    val title = when (selectedChartType) {
        ChartType.CALORIES -> "Calorie Bruciate per Allenamento"
        ChartType.WEEKLY_TRAININGS -> "Allenamenti Settimanali"
    }

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
        topBar = { TopBar(title = title) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChartTypeSelector(
                selectedChartType = selectedChartType,
                onChartTypeSelected = { selectedChartType = it },
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator()
                    }
                    !state.errorMessage.isNullOrEmpty() -> {
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
fun ChartTypeSelector(
    selectedChartType: ChartType,
    onChartTypeSelected: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChartTab(
                text = "Calorie",
                isSelected = selectedChartType == ChartType.CALORIES,
                onClick = { onChartTypeSelected(ChartType.CALORIES) },
                modifier = Modifier.weight(1f)
            )
            ChartTab(
                text = "Settimanale",
                isSelected = selectedChartType == ChartType.WEEKLY_TRAININGS,
                onClick = { onChartTypeSelected(ChartType.WEEKLY_TRAININGS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ChartTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}