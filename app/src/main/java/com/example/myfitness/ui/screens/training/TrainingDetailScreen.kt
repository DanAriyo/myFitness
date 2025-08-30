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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myfitness.data.models.Exercise
import com.example.myfitness.data.models.Training
import com.example.myfitness.ui.composables.BottomBar
import com.example.myfitness.ui.screens.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun TrainingDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    trainingId: String,
    viewModel: TrainingDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userId = authViewModel.actions.getCurrentUserId()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }

    // Launches the loading action only once when the screen is displayed.
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
                        state = state,
                        isEditing = isEditing,
                        onEditClick = { isEditing = true },
                        onSaveClick = {
                            viewModel.actions.saveChanges(userId, trainingId)
                            isEditing = false
                        },
                        // ✅ Pass the addExercise action
                        onAddExerciseClick = viewModel.actions::addExercise,
                        onTitleChange = viewModel.actions::onTitleChange,
                        onCaloriesChange = viewModel.actions::onCaloriesChange,
                        onDateChange = viewModel.actions::onDateChange,
                        // ✅ Pass the new exercise modification actions
                        onExerciseNameChange = viewModel.actions::onExerciseNameChange,
                        onExerciseSetsChange = viewModel.actions::onExerciseSetsChange,
                        onExerciseRepsChange = viewModel.actions::onExerciseRepsChange,
                        onExerciseDurationChange = viewModel.actions::onExerciseDurationChange
                    )
                }
            }
        }
    }
}

@Composable
fun TrainingDetailContent(
    state: TrainingDetailState,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    // ✅ Add the new callback parameters
    onExerciseNameChange: (Int, String) -> Unit,
    onExerciseSetsChange: (Int, Int) -> Unit,
    onExerciseRepsChange: (Int, Int) -> Unit,
    onExerciseDurationChange: (Int, String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = state.trainingTitle,
            onValueChange = onTitleChange,
            label = { Text("Nome Allenamento") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !isEditing
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.calories,
                onValueChange = onCaloriesChange,
                label = { Text("Calorie Bruciate") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                readOnly = !isEditing
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = isEditing) {
                        showDatePicker(context) { date ->
                            onDateChange(date)
                        }
                    }
            ) {
                OutlinedTextField(
                    value = state.date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = {},
                    label = { Text("Data Allenamento") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Text(
            text = "Esercizi",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ✅ Use state.exercises instead of state.training.esercizi to get the list to display
        if (state.exercises.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.exercises) { index, exercise ->
                    EditableExerciseCard(
                        exercise = exercise,
                        isEditing = isEditing,
                        // ✅ Pass the index and the new value to the callbacks
                        onNameChange = { onExerciseNameChange(index, it) },
                        onSetsChange = { onExerciseSetsChange(index, it) },
                        onRepsChange = { onExerciseRepsChange(index, it) },
                        onDurationChange = { onExerciseDurationChange(index, it) }
                    )
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