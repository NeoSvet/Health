package ru.neosvet.health.data

data class HealthEntity(
    val id: String,
    val time: Long,
    val highPressure: Int,
    val lowPressure: Int,
    val pulse: Int
)
