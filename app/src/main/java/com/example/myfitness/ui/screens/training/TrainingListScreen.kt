package com.example.myfitness.ui.screens.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.data.models.Training

@Composable
fun TrainingListScreen(
    state: TrainingListState,
    actions: TrainingListActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {


    // Carica gli allenamenti quando la schermata viene visualizzata
    LaunchedEffect(Unit) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            // We use the 'actions' parameter directly, which is passed from the ViewModel
            actions.loadTrainings(userId)
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (state.trainings.isEmpty() && !state.isLoading && state.errorMessage == null) {
                Text(text = "Nessun allenamento trovato. Inizia a crearne uno!")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.trainings) { training ->
                    TrainingCard(training = training)
                }
            }
        }
    }
}

@Composable
fun TrainingCard(training: Training) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = training.titolo, style = MaterialTheme.typography.headlineSmall)
        }
    }
}