package com.example.vvesmartcity.weather

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeatherRecord(
    val timestamp: Long,
    val temperature: Float,
    val humidity: Float
)

object WeatherDataSource {
    private val historyRecords: MutableList<WeatherRecord> = mutableListOf()

    init {
        generateMockHistory()
    }

    fun getCurrentData(): WeatherRecord {
        // 现在是模拟数据，后面用真实数据，就只要把这个方法改一下即可
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val baseTemp = when {
            hour in 6..9 -> 18f
            hour in 10..13 -> 24f
            hour in 14..16 -> 28f
            hour in 17..19 -> 22f
            else -> 16f
        }
        return WeatherRecord(
            timestamp = System.currentTimeMillis(),
            temperature = baseTemp + (Math.random() * 4 - 2).toFloat(),
            humidity = 55f + (Math.random() * 20 - 10).toFloat()
        )
    }

    fun getHistoryData(days: Int): List<WeatherRecord> {
        return historyRecords.takeLast(days * 4)
    }

    private fun generateMockHistory() {
        val calendar = Calendar.getInstance()
        repeat(7 * 4) { index ->
            calendar.add(Calendar.HOUR_OF_DAY, -6)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val baseTemp = when {
                hour in 6..9 -> 18f
                hour in 10..13 -> 24f
                hour in 14..16 -> 28f
                hour in 17..19 -> 22f
                else -> 16f
            }
            historyRecords.add(
                WeatherRecord(
                    timestamp = calendar.timeInMillis,
                    temperature = baseTemp + (Math.random() * 4 - 2).toFloat(),
                    humidity = 55f + (Math.random() * 20 - 10).toFloat()
                )
            )
        }
        historyRecords.sortBy { it.timestamp }
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object WeatherAdvice {
    fun getClothingAdvice(temperature: Float): Pair<String, String> {
        return when {
            temperature < 5 -> "厚羽绒服" to "气温较低，注意保暖"
            temperature < 15 -> "外套/毛衣" to "天气偏凉，建议穿外套"
            temperature < 25 -> "薄外套/长袖" to "温度适宜，穿着舒适"
            temperature < 30 -> "短袖/薄衫" to "天气较热，注意防晒"
            else -> "短袖/短裤" to "高温天气，注意防暑降温"
        }
    }

    fun getCarWashAdvice(humidity: Float, temperature: Float): Pair<String, String> {
        return when {
            humidity > 80 -> "不适宜" to "湿度较大，洗车后容易再次弄脏"
            temperature < 0 -> "不适宜" to "气温过低，洗车后可能结冰"
            humidity < 40 -> "适宜" to "天气干燥，适合洗车"
            else -> "较适宜" to "天气条件尚可，可以洗车"
        }
    }

    fun getTravelAdvice(temperature: Float, humidity: Float): Pair<String, String> {
        val comfortIndex = temperature - humidity / 10
        return when {
            comfortIndex >= 15 && comfortIndex <= 22 -> "适宜出行" to "天气舒适，适合户外活动"
            comfortIndex > 25 -> "注意防暑" to "天气较热，建议避开高温时段出行"
            comfortIndex < 10 -> "注意保暖" to "天气偏冷，外出注意添衣"
            else -> "可以出行" to "天气一般，注意适时增减衣物"
        }
    }
}
