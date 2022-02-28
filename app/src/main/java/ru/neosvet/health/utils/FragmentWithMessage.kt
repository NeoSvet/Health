package ru.neosvet.health.utils

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class FragmentWithMessage : Fragment() {
    protected var message: Snackbar? = null

    fun dismiss(): Boolean {
        if (message == null)
            return false
        message?.dismiss()
        message = null
        return true
    }
}