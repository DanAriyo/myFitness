package com.example.myfitness.ui.screens.training

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.data.models.Exercise
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun TrainingScreen(
    state: TrainingState,
    actions: TrainingActions,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

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
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
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

            // ✅ Sezione per Calorie e Data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Input per le Calorie
                OutlinedTextField(
                    value = state.calories,
                    onValueChange = { actions.onCaloriesChange(it) },
                    label = { Text("Calorie Bruciate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                // Input per la Data con DatePicker
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showDatePicker(context) { date ->
                                actions.onDateChange(date)
                            }
                        }
                ) {
                    OutlinedTextField(
                        value = state.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        onValueChange = {}, // L'utente non può digitare qui
                        label = { Text("Data Allenamento") },
                        readOnly = true, // Impedisce la modifica manuale
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // LazyColumn per la lista di esercizi
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

// ✅ Funzione per mostrare il DatePickerDialog
private fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            onDateSelected(LocalDate.of(selectedYear, selectedMonth + 1, selectedDay))
        },
        year, month, day
    )
    datePickerDialog.show()
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