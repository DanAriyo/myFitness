package com.example.myfitness.ui.screens.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myfitness.data.models.Exercise
import com.example.myfitness.data.models.Training
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrainingDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    trainingId: String,
    viewModel: TrainingDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userId = authViewModel.actions.getCurrentUserId()

    // Lancia l'azione di caricamento solo una volta quando la schermata viene visualizzata
    LaunchedEffect(Unit) {
        if (userId.isNotBlank()) {
            viewModel.actions.loadTraining(userId, trainingId)
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
            val currentState = state
            when {
                currentState.isLoading -> {
                    CircularProgressIndicator()
                }
                currentState.errorMessage != null -> {
                    Text(text = currentState.errorMessage, color = MaterialTheme.colorScheme.error)
                }
                currentState.training != null -> {
                    TrainingDetailContent(training = currentState.training)
                }
            }
        }
    }
}

@Composable
fun TrainingDetailContent(training: Training) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Esercizi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (training.esercizi.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(training.esercizi) { exercise ->
                    ExerciseCard(exercise = exercise)
                }
            }
        } else {
            Text(text = "Nessun esercizio aggiunto.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Sets: ${exercise.sets}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Reps: ${exercise.reps}", style = MaterialTheme.typography.bodyMedium)
            // You can also add duration if needed
            Text(text = "Durata: ${exercise.duration}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}