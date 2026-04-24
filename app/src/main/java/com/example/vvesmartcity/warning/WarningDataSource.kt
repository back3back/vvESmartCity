package com.example.vvesmartcity.warning

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.data.WarningData
import java.util.UUID

enum class WarningType(val label: String, val icon: String) {
    FLAME("火焰预警", "🔥"),
    SMOKE("烟雾预警", "💨"),
    MOTION("人体感应", "🚶")
}

enum class WarningLevel(val label: String, val color: Long) {
    HIGH("高危", 0xFFE53935),
    MEDIUM("中危", 0xFFFF9800),
    LOW("低危", 0xFFFFC107)
}

data class WarningRecord(
    val id: String,
    val type: WarningType,
    val level: WarningLevel,
    val location: String,
    val description: String,
    val timestamp: Long,
    var isHandled: Boolean
)

object WarningDataSource {
    private val warnings = mutableStateListOf<WarningRecord>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        
        val savedWarnings = DataPersistenceManager.loadWarnings()
        if (savedWarnings.isNotEmpty()) {
            savedWarnings.forEach { warningData ->
                val warningType = when (warningData.type) {
                    "FLAME" -> WarningType.FLAME
                    "SMOKE" -> WarningType.SMOKE
                    "MOTION" -> WarningType.MOTION
                    else -> WarningType.FLAME
                }
                val warningLevel = when (warningData.level) {
                    "HIGH" -> WarningLevel.HIGH
                    "MEDIUM" -> WarningLevel.MEDIUM
                    "LOW" -> WarningLevel.LOW
                    else -> WarningLevel.LOW
                }
                warnings.add(WarningRecord(
                    id = warningData.id,
                    type = warningType,
                    level = warningLevel,
                    location = warningData.location,
                    description = warningData.description,
                    timestamp = warningData.timestamp,
                    isHandled = warningData.isHandled
                ))
            }
        } else {
            initMockData()
        }
        isInitialized = true
    }
    
    private fun saveWarnings() {
        val warningDataList = warnings.map { warning ->
            WarningData(
                id = warning.id,
                type = warning.type.name,
                level = warning.level.name,
                location = warning.location,
                description = warning.description,
                timestamp = warning.timestamp,
                isHandled = warning.isHandled
            )
        }
        DataPersistenceManager.saveWarnings(warningDataList)
    }

    fun getCurrentWarnings(): List<WarningRecord> {
        return warnings.filter { !it.isHandled }.take(5)
    }

    fun getAllWarnings(): List<WarningRecord> = warnings.toList()

    fun searchWarnings(query: String): List<WarningRecord> {
        if (query.isBlank()) return warnings.toList()
        return warnings.filter {
            it.location.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true) ||
            it.type.label.contains(query, ignoreCase = true)
        }
    }

    fun addWarning(warning: WarningRecord) {
        warnings.add(0, warning)
        saveWarnings()
    }

    fun updateWarning(warning: WarningRecord) {
        val index = warnings.indexOfFirst { it.id == warning.id }
        if (index >= 0) {
            warnings[index] = warning
            saveWarnings()
        }
    }

    fun deleteWarning(id: String) {
        warnings.removeAll { it.id == id }
        saveWarnings()
    }

    fun markAsHandled(id: String) {
        val index = warnings.indexOfFirst { it.id == id }
        if (index >= 0) {
            warnings[index] = warnings[index].copy(isHandled = true)
            saveWarnings()
        }
    }

    private fun initMockData() {
        val now = System.currentTimeMillis()
        warnings.addAll(
            listOf(
                WarningRecord(
                    id = "W001",
                    type = WarningType.FLAME,
                    level = WarningLevel.HIGH,
                    location = "客厅",
                    description = "检测到火焰，温度异常升高",
                    timestamp = now - 300000,
                    isHandled = false
                ),
                WarningRecord(
                    id = "W002",
                    type = WarningType.SMOKE,
                    level = WarningLevel.HIGH,
                    location = "厨房",
                    description = "烟雾浓度超标，请注意安全",
                    timestamp = now - 600000,
                    isHandled = false
                ),
                WarningRecord(
                    id = "W003",
                    type = WarningType.MOTION,
                    level = WarningLevel.MEDIUM,
                    location = "卧室",
                    description = "检测到异常人体移动",
                    timestamp = now - 900000,
                    isHandled = false
                ),
                WarningRecord(
                    id = "W004",
                    type = WarningType.SMOKE,
                    level = WarningLevel.LOW,
                    location = "阳台",
                    description = "轻微烟雾，已自动通风",
                    timestamp = now - 3600000,
                    isHandled = true
                ),
                WarningRecord(
                    id = "W005",
                    type = WarningType.FLAME,
                    level = WarningLevel.MEDIUM,
                    location = "车库",
                    description = "温度传感器异常波动",
                    timestamp = now - 7200000,
                    isHandled = true
                ),
                WarningRecord(
                    id = "W006",
                    type = WarningType.MOTION,
                    level = WarningLevel.LOW,
                    location = "走廊",
                    description = "夜间检测到移动信号",
                    timestamp = now - 86400000,
                    isHandled = true
                )
            )
        )
        saveWarnings()
    }
}
