package ru.neosvet.health.list

import androidx.annotation.ColorInt

sealed class DataItem {
    data class Health(
        val id: String,
        val time: String,
        val highPressure: Int,
        val lowPressure: Int,
        val pulse: Int,
        @ColorInt val color: Int
    ) : DataItem()

    data class Title(val title: String) : DataItem()
}
