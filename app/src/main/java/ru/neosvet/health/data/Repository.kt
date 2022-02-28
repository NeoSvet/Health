package ru.neosvet.health.data

interface Repository {
    suspend fun getList(): List<HealthEntity>
    suspend fun add(time: Long, highPressure: Int, lowPressure: Int, pulse: Int)
}