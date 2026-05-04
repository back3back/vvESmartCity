package com.example.vvesmartcity.farm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.data.FarmSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FarmState(
    val sensorRecords: List<SensorRecord> = emptyList(),
    val devices: List<FarmDevice> = emptyList(),
    val thresholds: List<WarningThreshold> = emptyList(),
    val searchQuery: String = "",
    val farmSettings: FarmSettings? = null,
    val settingsMessage: String = ""
)

class FarmViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(FarmState(
        sensorRecords = FarmDataSource.getAllRecords(),
        devices = FarmDataSource.getDevices(),
        thresholds = FarmDataSource.getThresholds()
    ))
    val state: StateFlow<FarmState> = _state.asStateFlow()

    val currentSensorData: List<SensorRecord>
        get() = FarmDataSource.getCurrentSensorData()

    val abnormalRecords: List<SensorRecord>
        get() = FarmDataSource.getAbnormalRecords()

    fun searchRecords(query: String) {
        _state.update { it.copy(
            sensorRecords = FarmDataSource.searchRecords(query),
            searchQuery = query
        ) }
    }

    fun addRecord(record: SensorRecord) {
        FarmDataSource.addRecord(record)
        refreshRecords()
    }

    fun updateRecord(record: SensorRecord) {
        FarmDataSource.updateRecord(record)
        refreshRecords()
    }

    fun deleteRecord(id: String) {
        FarmDataSource.deleteRecord(id)
        refreshRecords()
    }

    fun toggleDevice(id: String) {
        FarmDataSource.toggleDevice(id)
        refreshDevices()
    }

    fun updateDevice(device: FarmDevice) {
        FarmDataSource.updateDevice(device)
        refreshDevices()
    }

    fun addDevice(device: FarmDevice) {
        FarmDataSource.addDevice(device)
        refreshDevices()
    }

    fun deleteDevice(id: String) {
        FarmDataSource.deleteDevice(id)
        refreshDevices()
    }

    fun updateThreshold(threshold: WarningThreshold) {
        FarmDataSource.updateThreshold(threshold)
        refreshThresholds()
    }

    fun saveFarmSettings(
        tempMin: Float,
        tempMax: Float,
        humidityMin: Float,
        humidityMax: Float,
        lightIntensity: Float,
        coThreshold: Float
    ) {
        viewModelScope.launch {
            DataPersistenceManager.saveFarmSettings(
                tempMin = tempMin,
                tempMax = tempMax,
                humidityMin = humidityMin,
                humidityMax = humidityMax,
                lightIntensity = lightIntensity,
                coThreshold = coThreshold
            )
            _state.update { it.copy(
                farmSettings = DataPersistenceManager.readFarmSettings(),
                settingsMessage = "保存成功"
            ) }
        }
    }

    fun readFarmSettings() {
        viewModelScope.launch {
            val settings = DataPersistenceManager.readFarmSettings()
            _state.update { it.copy(
                farmSettings = settings,
                settingsMessage = if (settings != null) "读取成功" else "暂无已保存的数据"
            ) }
        }
    }

    fun clearFarmSettings() {
        viewModelScope.launch {
            DataPersistenceManager.clearFarmSettings()
            _state.update { it.copy(
                farmSettings = null,
                settingsMessage = "已清空"
            ) }
        }
    }

    fun clearSettingsMessage() {
        _state.update { it.copy(settingsMessage = "") }
    }

    private fun refreshRecords() {
        _state.update { it.copy(
            sensorRecords = FarmDataSource.searchRecords(_state.value.searchQuery)
        ) }
    }

    private fun refreshDevices() {
        _state.update { it.copy(
            devices = FarmDataSource.getDevices()
        ) }
    }

    private fun refreshThresholds() {
        _state.update { it.copy(
            thresholds = FarmDataSource.getThresholds()
        ) }
    }
}
