package com.example.vvesmartcity.farm

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.data.DeviceData
import java.util.UUID

enum class SensorType(val label: String, val icon: String, val unit: String) {
    TEMPERATURE("温度", "🌡", "°C"),
    HUMIDITY("湿度", "💧", "%"),
    CO("CO浓度", "☁", "ppm"),
    LIGHT("光照", "☀", "lux"),
    AIR_QUALITY("空气质量", "🌬", "AQI"),
    SOIL_MOISTURE("土壤湿度", "🌱", "%"),
    MOTION("人体感应", "🚶", "")
}

data class SensorRecord(
    val id: String,
    val type: SensorType,
    val value: Double,
    val thresholdMin: Double,
    val thresholdMax: Double,
    val timestamp: Long,
    val location: String
) {
    val isNormal: Boolean
        get() = value in thresholdMin..thresholdMax
}

data class FarmDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val isOn: Boolean,
    val location: String
)

enum class DeviceType(val label: String, val icon: String) {
    AC("空调", "❄"),
    FAN("风扇", "🌀"),
    LIGHT("灯", "💡"),
    IRRIGATION("灌溉", "🚿")
}

data class WarningThreshold(
    val sensorType: SensorType,
    val min: Double,
    val max: Double
)

object FarmDataSource {
    private val sensorRecords = mutableStateListOf<SensorRecord>()
    private val devices = mutableStateListOf<FarmDevice>()
    private val thresholds = mutableStateListOf<WarningThreshold>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        
        val savedDevices = DataPersistenceManager.loadDevices()
        if (savedDevices.isNotEmpty()) {
            savedDevices.forEach { deviceData ->
                val deviceType = when (deviceData.type) {
                    "AC" -> DeviceType.AC
                    "FAN" -> DeviceType.FAN
                    "LIGHT" -> DeviceType.LIGHT
                    "IRRIGATION" -> DeviceType.IRRIGATION
                    else -> DeviceType.AC
                }
                devices.add(FarmDevice(
                    id = deviceData.id,
                    name = deviceData.name,
                    type = deviceType,
                    isOn = deviceData.isOn,
                    location = deviceData.location
                ))
            }
        } else {
            initMockDevices()
        }
        
        initMockSensorData()
        initMockThresholds()
        isInitialized = true
    }
    
    private fun saveDevices() {
        val deviceDataList = devices.map { device ->
            DeviceData(
                id = device.id,
                name = device.name,
                type = device.type.name,
                isOn = device.isOn,
                location = device.location
            )
        }
        DataPersistenceManager.saveDevices(deviceDataList)
    }

    fun getCurrentSensorData(): List<SensorRecord> {
        return sensorRecords.groupBy { it.type }.mapNotNull { (_, records) ->
            records.maxByOrNull { it.timestamp }
        }
    }

    fun getAllRecords(): List<SensorRecord> = sensorRecords.toList()

    fun searchRecords(query: String): List<SensorRecord> {
        if (query.isBlank()) return sensorRecords.toList()
        return sensorRecords.filter {
            it.location.contains(query, ignoreCase = true) ||
            it.type.label.contains(query, ignoreCase = true)
        }
    }

    fun addRecord(record: SensorRecord) {
        sensorRecords.add(0, record)
    }

    fun updateRecord(record: SensorRecord) {
        val index = sensorRecords.indexOfFirst { it.id == record.id }
        if (index >= 0) {
            sensorRecords[index] = record
        }
    }

    fun deleteRecord(id: String) {
        sensorRecords.removeAll { it.id == id }
    }

    fun getDevices(): List<FarmDevice> = devices.toList()

    fun toggleDevice(id: String) {
        val index = devices.indexOfFirst { it.id == id }
        if (index >= 0) {
            devices[index] = devices[index].copy(isOn = !devices[index].isOn)
            saveDevices()
        }
    }

    fun updateDevice(device: FarmDevice) {
        val index = devices.indexOfFirst { it.id == device.id }
        if (index >= 0) {
            devices[index] = device
            saveDevices()
        }
    }

    fun addDevice(device: FarmDevice) {
        devices.add(device)
        saveDevices()
    }

    fun deleteDevice(id: String) {
        devices.removeAll { it.id == id }
        saveDevices()
    }

    fun getThresholds(): List<WarningThreshold> = thresholds.toList()

    fun updateThreshold(threshold: WarningThreshold) {
        val index = thresholds.indexOfFirst { it.sensorType == threshold.sensorType }
        if (index >= 0) {
            thresholds[index] = threshold
        } else {
            thresholds.add(threshold)
        }
    }

    fun getAbnormalRecords(): List<SensorRecord> {
        return sensorRecords.filter { !it.isNormal }
    }

    private fun initMockThresholds() {
        thresholds.addAll(
            listOf(
                WarningThreshold(SensorType.TEMPERATURE, 15.0, 35.0),
                WarningThreshold(SensorType.HUMIDITY, 30.0, 80.0),
                WarningThreshold(SensorType.CO, 0.0, 50.0),
                WarningThreshold(SensorType.LIGHT, 100.0, 10000.0),
                WarningThreshold(SensorType.AIR_QUALITY, 0.0, 100.0),
                WarningThreshold(SensorType.SOIL_MOISTURE, 20.0, 70.0)
            )
        )
    }

    private fun initMockSensorData() {
        val now = System.currentTimeMillis()

        sensorRecords.addAll(
            listOf(
                SensorRecord(
                    id = "S001",
                    type = SensorType.TEMPERATURE,
                    value = 25.5,
                    thresholdMin = 15.0,
                    thresholdMax = 35.0,
                    timestamp = now - 60000,
                    location = "大棚A"
                ),
                SensorRecord(
                    id = "S002",
                    type = SensorType.HUMIDITY,
                    value = 65.0,
                    thresholdMin = 30.0,
                    thresholdMax = 80.0,
                    timestamp = now - 60000,
                    location = "大棚A"
                ),
                SensorRecord(
                    id = "S003",
                    type = SensorType.CO,
                    value = 35.0,
                    thresholdMin = 0.0,
                    thresholdMax = 50.0,
                    timestamp = now - 120000,
                    location = "大棚B"
                ),
                SensorRecord(
                    id = "S004",
                    type = SensorType.LIGHT,
                    value = 5000.0,
                    thresholdMin = 100.0,
                    thresholdMax = 10000.0,
                    timestamp = now - 120000,
                    location = "大棚A"
                ),
                SensorRecord(
                    id = "S005",
                    type = SensorType.AIR_QUALITY,
                    value = 85.0,
                    thresholdMin = 0.0,
                    thresholdMax = 100.0,
                    timestamp = now - 180000,
                    location = "大棚B"
                ),
                SensorRecord(
                    id = "S006",
                    type = SensorType.SOIL_MOISTURE,
                    value = 45.0,
                    thresholdMin = 20.0,
                    thresholdMax = 70.0,
                    timestamp = now - 180000,
                    location = "大棚A"
                ),
                SensorRecord(
                    id = "S007",
                    type = SensorType.MOTION,
                    value = 1.0,
                    thresholdMin = 0.0,
                    thresholdMax = 1.0,
                    timestamp = now - 240000,
                    location = "大棚C"
                ),
                SensorRecord(
                    id = "S008",
                    type = SensorType.TEMPERATURE,
                    value = 38.0,
                    thresholdMin = 15.0,
                    thresholdMax = 35.0,
                    timestamp = now - 300000,
                    location = "大棚B"
                ),
                SensorRecord(
                    id = "S009",
                    type = SensorType.SOIL_MOISTURE,
                    value = 15.0,
                    thresholdMin = 20.0,
                    thresholdMax = 70.0,
                    timestamp = now - 360000,
                    location = "大棚C"
                ),
                SensorRecord(
                    id = "S010",
                    type = SensorType.HUMIDITY,
                    value = 85.0,
                    thresholdMin = 30.0,
                    thresholdMax = 80.0,
                    timestamp = now - 420000,
                    location = "大棚A"
                )
            )
        )
    }

    private fun initMockDevices() {
        devices.addAll(
            listOf(
                FarmDevice(
                    id = "D001",
                    name = "空调1号",
                    type = DeviceType.AC,
                    isOn = false,
                    location = "大棚A"
                ),
                FarmDevice(
                    id = "D002",
                    name = "风扇1号",
                    type = DeviceType.FAN,
                    isOn = false,
                    location = "大棚A"
                ),
                FarmDevice(
                    id = "D003",
                    name = "补光灯1号",
                    type = DeviceType.LIGHT,
                    isOn = false,
                    location = "大棚B"
                ),
                FarmDevice(
                    id = "D004",
                    name = "灌溉系统1号",
                    type = DeviceType.IRRIGATION,
                    isOn = false,
                    location = "大棚C"
                ),
                FarmDevice(
                    id = "D005",
                    name = "空调2号",
                    type = DeviceType.AC,
                    isOn = false,
                    location = "大棚B"
                ),
                FarmDevice(
                    id = "D006",
                    name = "风扇2号",
                    type = DeviceType.FAN,
                    isOn = false,
                    location = "大棚C"
                )
            )
        )
        saveDevices()
    }
}
