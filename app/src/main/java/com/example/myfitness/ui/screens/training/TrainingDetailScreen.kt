package com.example.myfitness.ui.screens.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

    var isEditing by remember { mutableStateOf(false) }
    val editableExercises = remember { mutableStateListOf<Exercise>() }

    // Lancia l'azione di caricamento solo una volta quando la schermata viene visualizzata
    LaunchedEffect(Unit) {
        if (userId.isNotBlank()) {
            viewModel.actions.loadTraining(userId, trainingId)
        }
    }

    // Quando lo stato dell'allenamento cambia, aggiorna la lista modificabile
    LaunchedEffect(state.training) {
        state.training?.let {
            editableExercises.clear()
            editableExercises.addAll(it.esercizi)
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
                state.training != null -> {
                    TrainingDetailContent(
                        training = state.training!!,
                        isEditing = isEditing,
                        editableExercises = editableExercises,
                        onEditClick = { isEditing = true },
                        onSaveClick = {
                            viewModel.actions.updateTrainingExercises(
                                userId,
                                trainingId,
                                editableExercises
                            )
                            isEditing = false
                        },
                        onAddExerciseClick = { editableExercises.add(Exercise()) }, // ✅ New click handler
                        onExerciseNameChange = { index, name ->
                            editableExercises[index] = editableExercises[index].copy(name = name)
                        },
                        onExerciseSetsChange = { index, sets ->
                            editableExercises[index] = editableExercises[index].copy(sets = sets)
                        },
                        onExerciseRepsChange = { index, reps ->
                            editableExercises[index] = editableExercises[index].copy(reps = reps)
                        },
                        onExerciseDurationChange = { index, duration ->
                            editableExercises[index] = editableExercises[index].copy(duration = duration)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrainingDetailContent(
    training: Training,
    isEditing: Boolean,
    editableExercises: List<Exercise>,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddExerciseClick: () -> Unit, // ✅ New parameter
    onExerciseNameChange: (Int, String) -> Unit,
    onExerciseSetsChange: (Int, Int) -> Unit,
    onExerciseRepsChange: (Int, Int) -> Unit,
    onExerciseDurationChange: (Int, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Esercizi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (editableExercises.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(editableExercises) { index, exercise ->
                    EditableExerciseCard(
                        exercise = exercise,
                        isEditing = isEditing,
                        onNameChange = { onExerciseNameChange(index, it) },
                        onSetsChange = { onExerciseSetsChange(index, it) },
                        onRepsChange = { onExerciseRepsChange(index, it) },
                        onDurationChange = { onExerciseDurationChange(index, it) }
                    )
                }

                // ✅ Add "Add Exercise" button when in edit mode
                if (isEditing) {
                    item {
                        Button(
                            onClick = onAddExerciseClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aggiungi Esercizio")
                            Spacer(Modifier.width(8.dp))
                            Text("Aggiungi Esercizio")
                        }
                    }
                }
            }
        } else {
            Text(text = "Nessun esercizio aggiunto.", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                enabled = !isEditing,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Modifica Allenamento")
                Spacer(Modifier.width(4.dp))
                Text("Modifica Dati")
            }
            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f),
                enabled = isEditing,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Done, contentDescription = "Salva Allenamento")
                Spacer(Modifier.width(4.dp))
                Text("Salva Dati")
            }
        }
    }
}

@Composable
fun EditableExerciseCard(
    exercise: Exercise,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onDurationChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = exercise.name,
                onValueChange = onNameChange,
                label = { Text("Nome Esercizio") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
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
                    modifier = Modifier.weight(1f),
                    readOnly = !isEditing
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
                    modifier = Modifier.weight(1f),
                    readOnly = !isEditing
                )
                OutlinedTextField(
                    value = exercise.duration,
                    onValueChange = onDurationChange,
                    label = { Text("Durata") },
                    modifier = Modifier.weight(1f),
                    readOnly = !isEditing
                )
            }
        }
    }
}