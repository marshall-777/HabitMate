package com.example.habitmate.model

import java.io.Serializable

// Уже готов: Habit.kt

// Класс пользователя

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = ""
) : Serializable
