package com.example.habitmate.model

import java.io.Serializable

data class Habit(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val periodicity: String = "",
    val days: List<Int> = emptyList(),
    val startDate: String = "",
    val time: String = "",
    var progress: MutableMap<String, Boolean> = mutableMapOf(),
    val userId: String = ""
) : Serializable