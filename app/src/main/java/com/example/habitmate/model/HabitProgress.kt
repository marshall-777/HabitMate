package com.example.habitmate.model

import java.io.Serializable
data class HabitProgress(
    val date: String = "", // формат: dd.MM.yyyy
    val completed: Boolean = false
) : Serializable
