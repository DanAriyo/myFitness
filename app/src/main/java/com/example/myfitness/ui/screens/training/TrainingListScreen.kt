package com.example.myfitness.ui.screens.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.data.models.Training
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.ui.composables.TopBar // ✅ Importa TopBar

@Composable
fun TrainingListScreen(
    state: TrainingListState,
    actions: TrainingListActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    LaunchedEffect(Unit) {
        val userId = authViewModel.actions.getCurrentUserId()
        if (userId.isNotEmpty()) {
            actions.loadTrainings(userId)
        }
    }

    Scaffold(
        topBar = { TopBar(title = "La Tua Lista di Allenamenti") }, // ✅ Aggiungi la TopBar
        bottomBar = { BottomBar(navController) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp), // Aggiungi padding orizzontale
            horizontalAlignment = Alignment.CenterHorizontally // Centra orizzontalmente i contenuti
        ) {
            // Rimosso il Text "La Tua Lista di Allenamenti" duplicato

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp)) // Padding per la progress bar
            }
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            }
            if (state.trainings.isEmpty() && !state.isLoading && state.errorMessage == null) {
                Text(
                    text = "Nessun allenamento trovato. Inizia a crearne uno!",
                    modifier = Modifier.padding(top = 16.dp), // Padding per il messaggio
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp), // Aumenta lo spazio tra le card
                contentPadding = PaddingValues(vertical = 12.dp) // Aggiungi padding verticale alla lista
            ) {
                items(state.trainings) { training ->
                    TrainingCard(
                        training = training,
                        onClick = {
                            navController.navigate("trainingDetail/${training.id}")
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun TrainingCard(training: Training, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Allenamento",
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = training.titolo,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}