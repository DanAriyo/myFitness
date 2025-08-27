package com.example.myfitness.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import com.example.myfitness.ui.composables.BottomBar

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    navController: NavController
) {
    Scaffold(
        containerColor = Color.Black,
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(ScrollState(0))
                .background(Color.Black)
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
                    color = Color.White
                )
                IconButton(onClick = { /* azione futura */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi", tint = Color.Green)
                }
            }

            // Calendario orizzontale
            CalendarRow()

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Green
                )
            }

            state.errorMessage?.let {
                Text(text = it, color = Color.Red)
            }

            // Card passi circolare
            CircularStatCard(
                label = "Passi di oggi",
                current = state.steps,
                max = 10000,
                color = Color.Green
            )

            // Card calorie circolare
            CircularStatCard(
                label = "Calorie bruciate",
                current = state.calories,
                max = 2500,
                color = Color.Red
            )

            // Card allenamento del giorno (rettangolare)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Allenamento del giorno", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(
                        if (state.workoutOfTheDay.isNotBlank()) state.workoutOfTheDay
                        else "Nessun allenamento disponibile",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { actions.loadDashboard() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("Aggiorna dati", color = Color.Black)
            }
        }
    }
}

@Composable
fun CircularStatCard(label: String, current: Int, max: Int, color: Color) {
    val progress = (current.toFloat() / max).coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(Color.DarkGray)
    ) {
        // Cerchio progress
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize().padding(12.dp),
            color = color,
            strokeWidth = 12.dp,
            trackColor = ProgressIndicatorDefaults.circularTrackColor,
            strokeCap = StrokeCap.Round,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text("$current / $max", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalendarRow() {
    val today = LocalDate.now()
    val daysInMonth = (1..today.lengthOfMonth()).map { day ->
        today.withDayOfMonth(day)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (day in daysInMonth) {
            val isToday = day == today
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isToday) Color.Green else Color.DarkGray)
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    color = Color.White
                )
            }
        }
    }

}
