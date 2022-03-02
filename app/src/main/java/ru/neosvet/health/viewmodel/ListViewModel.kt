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
import ru.neosvet.health.data.Repository
import ru.neosvet.health.list.DataItem
import java.text.SimpleDateFormat
import java.util.*

class ListViewModel(
    private val repository: Repository
) : ViewModel() {
    companion object {
        private const val PRESSURE_LEVEL1 = 130
        private const val PRESSURE_LEVEL2 = 140
        private const val PRESSURE_LEVEL3 = 150
        private val colorNormal = Color.parseColor("#b9e7b9")
        private val colorLevel1 = Color.parseColor("#e5efb0")
        private val colorLevel2 = Color.parseColor("#faeaae")
        private val colorLevel3 = Color.parseColor("#ff6446")
    }

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
                    is ListIntent.Delete -> deleteItem(it.id)
                }
            }
        }
    }

    private suspend fun deleteItem(id: String) {
        repository.delete(id)
    }

    private suspend fun loadList() {
        _state.postValue(ListState.Loading)
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
                id = it.id,
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
        return when {
            pressure > PRESSURE_LEVEL3 -> colorLevel3
            pressure > PRESSURE_LEVEL2 -> colorLevel2
            pressure > PRESSURE_LEVEL1 -> colorLevel1
            else -> colorNormal
        }
    }
}