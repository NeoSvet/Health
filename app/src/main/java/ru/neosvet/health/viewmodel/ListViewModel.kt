package ru.neosvet.health.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import ru.neosvet.health.data.FakeRepository
import ru.neosvet.health.data.Repository
import ru.neosvet.health.list.Health
import java.text.SimpleDateFormat
import java.util.*

class ListViewModel : ViewModel() {
    companion object {
        private const val MIN_PRESSURE = 110
        private const val MAX_PRESSURE = 130
        private const val CRITICAL_PRESSURE = 150
    }

    private val repository: Repository = FakeRepository()
    val userIntent = Channel<ListIntent>(Channel.UNLIMITED)
    private val _state: MutableLiveData<ListState> = MutableLiveData()
    val state: LiveData<ListState> = _state
    private val dateFormat = SimpleDateFormat("dd MMMM")
    private val timeFormat = SimpleDateFormat("HH:mm")

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is ListIntent.GetList -> loadList()
                }
            }
        }
    }

    private suspend fun loadList() {
        val list = mutableListOf<Health>()
        repository.getList().forEach {
            val dt = getDateTime(it.time)
            val color = getColorByPressure(it.highPressure)
            val item = Health(
                date = dt.date,
                time = dt.time,
                highPressure = it.highPressure,
                lowPressure = it.lowPressure,
                pulse = it.pulse,
                color = color
            )
            list.add(item)
        }
        _state.postValue(ListState.Success(list))
    }

    private fun getColorByPressure(pressure: Int): Int {
        //TODO сделать оттенки в соответствии степени отклонения
        return when {
            pressure in MIN_PRESSURE until MAX_PRESSURE -> Color.GREEN
            pressure > CRITICAL_PRESSURE -> Color.RED
            else -> Color.YELLOW
        }
    }

    private fun getDateTime(time: Long): DateTime {
        val date = Date(time)
        return DateTime(
            date = dateFormat.format(date),
            time = timeFormat.format(date)
        )
    }
}