package com.example.vvesmartcity.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class WeatherState(
    val currentWeather: WeatherRecord? = null,
    val historyData: List<WeatherRecord> = emptyList(),
    val selectedDays: Int = 1,
    val isLoading: Boolean = false
)

class WeatherViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeatherState(
        currentWeather = WeatherDataSource.getCurrentData(),
        historyData = WeatherDataSource.getHistoryData(1)
    ))
    val state: StateFlow<WeatherState> = _state.asStateFlow()
    
    private var pollingJob: Job? = null

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                refreshCurrentWeather()
                delay(5000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun refreshCurrentWeather() {
        _state.update { it.copy(
            currentWeather = WeatherDataSource.getCurrentData()
        ) }
    }

    fun setSelectedDays(days: Int) {
        _state.update { it.copy(
            selectedDays = days,
            historyData = WeatherDataSource.getHistoryData(days)
        ) }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
