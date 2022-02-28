package ru.neosvet.health.utils

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.isCorrect(): Boolean {
    return text.toString().length > 1
}

fun TextInputEditText.toInt(): Int {
    return text.toString().toInt()
}