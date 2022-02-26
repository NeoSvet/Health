package ru.neosvet.health.viewmodel

import ru.neosvet.health.list.DataItem

sealed class ListState {
    object Loading : ListState()
    data class Success(val list: List<DataItem>) : ListState()
    data class Error(val error: String?) : ListState()
}
