package com.example.myfitness.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    navController: NavController
) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(ScrollState(0))
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con +
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dashboard Fitness",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = {
                        navController.navigate("train")
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aggiungi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Calendario orizzontale
            CalendarRow()

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            // Card passi circolare
            CircularStatCard(
                label = "Passi di oggi",
                current = state.steps,
                max = 10000,
                color = MaterialTheme.colorScheme.primary
            )

            // Card calorie circolare
            CircularStatCard(
                label = "Calorie bruciate",
                current = state.calories,
                max = 2500,
                color = MaterialTheme.colorScheme.tertiary
            )

            // Card allenamento del giorno (rettangolare)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Allenamento del giorno",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (state.workoutOfTheDay.isNotBlank()) state.workoutOfTheDay
                        else "Nessun allenamento disponibile",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { actions.loadDashboard() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Aggiorna dati", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun CircularStatCard(label: String, current: Int, max: Int, color: androidx.compose.ui.graphics.Color) {
    val progress = (current.toFloat() / max).coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Cerchio progress
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            color = color,
            strokeWidth = 12.dp,
            trackColor = ProgressIndicatorDefaults.circularTrackColor,
            strokeCap = StrokeCap.Round,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Text("$current / $max", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalendarRow() {
    val today = LocalDate.now()
    val startOfMonth = today.withDayOfMonth(1)

    val daysInMonth = (0 until today.lengthOfMonth()).map {
        startOfMonth.plusDays(it.toLong())
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(daysInMonth) { day ->
            val isToday = day == today
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}