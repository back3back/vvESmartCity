package com.example.vvesmartcity.farm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FarmState(
    val sensorRecords: List<SensorRecord> = emptyList(),
    val devices: List<FarmDevice> = emptyList(),
    val thresholds: List<WarningThreshold> = emptyList(),
    val searchQuery: String = ""
)

class FarmViewModel : ViewModel() {
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
