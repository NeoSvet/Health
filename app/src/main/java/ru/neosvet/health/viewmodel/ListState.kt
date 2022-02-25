package ru.neosvet.health.viewmodel

import ru.neosvet.health.list.Health

sealed class ListState {
    object Loading : ListState()
    data class Success(val list: List<Health>) : ListState()
    data class Error(val error: String?) : ListState()
}
