package com.example.myfitness.data.models

data class Training(
    val id: String = "",
    val titolo: String = "",
    val esercizi: List<Exercise> = emptyList()
)