package com.example.myfitness.data.models
import com.google.firebase.Timestamp
import java.time.LocalDate

data class Training(
    val id: String = "",
    val titolo: String = "",
    val esercizi: List<Exercise> = emptyList(),
    val data: Timestamp = Timestamp.now(), // ✅ Usa un tipo di dato Date o Instant
    val calorie: Int = 0
)