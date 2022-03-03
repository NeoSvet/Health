package ru.neosvet.health.viewmodel

sealed class AddIntent {
    data class Add(
        val time: String,
        val date: String,
        val highPressure: Int,
        val lowPressure: Int,
        val pulse: Int
    ) : AddIntent()
}