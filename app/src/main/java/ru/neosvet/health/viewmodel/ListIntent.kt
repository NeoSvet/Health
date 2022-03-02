package ru.neosvet.health.viewmodel

sealed class ListIntent {
    object GetList: ListIntent()
    data class Delete(val id: String): ListIntent()
}