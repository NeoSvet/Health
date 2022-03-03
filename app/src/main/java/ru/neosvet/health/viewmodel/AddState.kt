package ru.neosvet.health.viewmodel

sealed class AddState {
    object Loading : AddState()
    object Success : AddState()
    data class Error(val error: String?) : AddState()
}
