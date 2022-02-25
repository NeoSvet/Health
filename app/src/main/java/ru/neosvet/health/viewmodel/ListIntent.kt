package ru.neosvet.health.viewmodel

sealed class ListIntent {
    object GetList: ListIntent()
}