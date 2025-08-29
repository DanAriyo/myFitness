package com.example.myfitness.ui.screens.training


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.theme.MyFitnessTheme
import com.example.myfitness.ui.screens.auth.AuthViewModel
import com.example.myfitness.data.models.Training
import com.example.myfitness.data.models.Exercise

@Composable
fun TrainingScreen(
    state: TrainingState,
    actions: TrainingActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {
        Scaffold(
            bottomBar = { BottomBar(navController) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val userId = authViewModel.actions.getCurrentUserId()
                        if (userId.isNotEmpty()) {
                            actions.saveTraining(userId)
                        }
                    },
                    modifier = Modifier.padding(bottom = 60.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Done, contentDescription = "Salva Allenamento")
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show loading indicator when the state is loading
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                // Show error message if it exists
                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // TextField per il nome dell'allenamento
                OutlinedTextField(
                    value = state.trainingName,
                    onValueChange = { actions.onTrainingNameChange(it) },
                    label = { Text("Nome Allenamento") },
                    modifier = Modifier.fillMaxWidth()
                )

                // LazyColumn per la lista di esercizi da creare
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(state.exercises) { index, exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onNameChange = { actions.onExerciseNameChange(index, it) },
                            onSetsChange = { actions.onExerciseSetsChange(index, it) },
                            onRepsChange = { actions.onExerciseRepsChange(index, it) },
                            onDurationChange = { actions.onExerciseDurationChange(index, it) }
                        )
                    }

                    item {
                        Button(
                            onClick = { actions.addExercise() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aggiungi Esercizio")
                            Spacer(Modifier.width(8.dp))
                            Text("Aggiungi Esercizio")
                        }
                    }
                }
            }
        }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onNameChange: (String) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onDurationChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = exercise.name,
                onValueChange = onNameChange,
                label = { Text("Nome Esercizio") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = exercise.sets.toString(),
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            val intValue = newValue.toIntOrNull() ?: 0
                            onSetsChange(intValue)
                        }
                    },
                    label = { Text("Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = exercise.reps.toString(),
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            val intValue = newValue.toIntOrNull() ?: 0
                            onRepsChange(intValue)
                        }
                    },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = exercise.duration,
                    onValueChange = onDurationChange,
                    label = { Text("Durata (es. 30s)") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}