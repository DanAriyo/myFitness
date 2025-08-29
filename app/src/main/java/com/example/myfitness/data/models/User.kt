package com.example.myfitness.data.models

import com.example.myfitness.data.models.local.ThemeSettings

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val email: String = "",
    val theme: String = ThemeSettings.SYSTEM.name
)
