package com.example.myfitness.data.models

// In your data.models package
data class Exercise(
    val id: String = "",
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val duration: String = ""
)