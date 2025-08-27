package com.example.myfitness.ui.screens.exercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar

@Composable
fun ExerciseScreen(
    state: ExerciseState,
    actions: ExerciseActions,
    navController: NavController,
    trainingId: String
) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Esercizi",
                style = MaterialTheme.typography.headlineSmall
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.esercizi) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 🔹 Qui potresti navigare a una schermata di dettaglio esercizio
                                // esempio: navController.navigate("exerciseDetail/${exercise.id}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(exercise.nome, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                exercise.descrizione,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Serie: ${exercise.serie} - Ripetizioni: ${exercise.ripetizioni}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (exercise.categoria.isNotBlank()) {
                                Text(
                                    "Categoria: ${exercise.categoria}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { actions.loadExercises(trainingId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Aggiorna Esercizi")
            }
        }
    }
}
