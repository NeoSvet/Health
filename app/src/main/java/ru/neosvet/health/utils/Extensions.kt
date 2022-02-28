package ru.neosvet.health.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import ru.neosvet.health.R

fun TextInputEditText.isCorrect(): Boolean {
    return text.toString().length > 1
}

fun TextInputEditText.toInt(): Int {
    return text.toString().toInt()
}

fun View.Snackbar(text: String): Snackbar {
    return  Snackbar.make(this, text, Snackbar.LENGTH_SHORT)
}