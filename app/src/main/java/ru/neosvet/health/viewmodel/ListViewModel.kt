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
import ru.neosvet.health.list.DataItem
import java.text.SimpleDateFormat
import java.util.*

class ListViewModel : ViewModel() {
    companion object {
        private const val WARNINGS_PRESSURE = 130
        private const val CRITICAL_PRESSURE = 150
        private val colorNormal = Color.parseColor("#b9e7b9")
        private val colorWarnings = Color.parseColor("#faeaae")
        private val colorCritical = Color.parseColor("#ff6446")
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
        val list = mutableListOf<DataItem>()
        var lastDate: String? = null
        repository.getList().forEach {
            val d = Date(it.time)
            val date = dateFormat.format(d)
            val time = timeFormat.format(d)
            if (lastDate != date) {
                list.add(DataItem.Title(date))
                lastDate = date
            }
            val color = getColorByPressure(it.highPressure)
            val item = DataItem.Health(
                time = time,
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
            pressure > WARNINGS_PRESSURE -> colorWarnings
            pressure > CRITICAL_PRESSURE -> colorCritical
            else -> colorNormal
        }
    }
}