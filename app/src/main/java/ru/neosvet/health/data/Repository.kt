package ru.neosvet.health.data

interface Repository {
    suspend fun getList(): List<HealthEntity>
}