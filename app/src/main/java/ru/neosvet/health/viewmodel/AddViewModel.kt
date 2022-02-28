package ru.neosvet.health.viewmodel

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
import java.text.SimpleDateFormat
import java.util.*

class AddViewModel : ViewModel() {
    private val repository: Repository = FakeRepository()
    val userIntent = Channel<AddIntent>(Channel.UNLIMITED)
    private val _state: MutableLiveData<AddState> = MutableLiveData()
    val state: LiveData<AddState> = _state
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is AddIntent.Add -> addData(it)
                }
            }
        }
    }

    private suspend fun addData(data: AddIntent.Add) {
        val tz = TimeZone.getDefault().rawOffset
        val time = timeFormat.parse(data.time)!!.time + tz
        val date = dateFormat.parse(data.date)!!.time
        repository.add(
            time = time + date,
            highPressure = data.highPressure,
            lowPressure = data.lowPressure,
            pulse = data.pulse
        )
        _state.postValue(AddState.Success)
    }
}