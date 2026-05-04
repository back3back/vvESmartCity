package com.example.vvesmartcity.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object DataPersistenceManager {
    private const val PREFS_NAME = "smart_city_data"
    private const val FARM_PREFS_NAME = "zhcs"
    
    private const val KEY_DEVICES = "devices"
    private const val KEY_PRODUCTS = "products"
    private const val KEY_WARNINGS = "warnings"
    private const val KEY_INITIALIZED = "data_initialized"
    
    private const val KEY_TEMP_MIN = "temp_min"
    private const val KEY_TEMP_MAX = "temp_max"
    private const val KEY_HUMIDITY_MIN = "humidity_min"
    private const val KEY_HUMIDITY_MAX = "humidity_max"
    private const val KEY_LIGHT_INTENSITY = "light_intensity"
    private const val KEY_CO_THRESHOLD = "co_threshold"
    
    private lateinit var prefs: SharedPreferences
    private lateinit var farmPrefs: SharedPreferences
    private lateinit var database: ShoppingDatabase
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        farmPrefs = context.getSharedPreferences(FARM_PREFS_NAME, Context.MODE_PRIVATE)
        database = ShoppingDatabase.getInstance(context)
    }
    
    fun getShoppingDao(): ShoppingDao {
        return database.shoppingDao()
    }
    
    fun isInitialized(): Boolean {
        return prefs.getBoolean(KEY_INITIALIZED, false)
    }
    
    fun markInitialized() {
        prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
    }
    
    fun saveDevices(devices: List<DeviceData>) {
        val jsonArray = JSONArray()
        devices.forEach { device ->
            val json = JSONObject().apply {
                put("id", device.id)
                put("name", device.name)
                put("type", device.type)
                put("isOn", device.isOn)
                put("location", device.location)
            }
            jsonArray.put(json)
        }
        prefs.edit().putString(KEY_DEVICES, jsonArray.toString()).apply()
    }
    
    fun loadDevices(): List<DeviceData> {
        val jsonStr = prefs.getString(KEY_DEVICES, null) ?: return emptyList()
        val devices = mutableListOf<DeviceData>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                devices.add(DeviceData(
                    id = json.getString("id"),
                    name = json.getString("name"),
                    type = json.getString("type"),
                    isOn = json.getBoolean("isOn"),
                    location = json.getString("location")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return devices
    }
    
    fun saveProducts(products: List<ProductData>) {
        val jsonArray = JSONArray()
        products.forEach { product ->
            val json = JSONObject().apply {
                put("id", product.id)
                put("name", product.name)
                put("quantity", product.quantity)
                put("unitPrice", product.unitPrice)
                put("discountedPrice", product.discountedPrice)
            }
            jsonArray.put(json)
        }
        prefs.edit().putString(KEY_PRODUCTS, jsonArray.toString()).apply()
    }
    
    fun loadProducts(): List<ProductData> {
        val jsonStr = prefs.getString(KEY_PRODUCTS, null) ?: return emptyList()
        val products = mutableListOf<ProductData>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                products.add(ProductData(
                    id = json.getString("id"),
                    name = json.getString("name"),
                    quantity = json.getInt("quantity"),
                    unitPrice = json.getDouble("unitPrice").toFloat(),
                    discountedPrice = json.getDouble("discountedPrice").toFloat()
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return products
    }
    
    fun saveWarnings(warnings: List<WarningData>) {
        val jsonArray = JSONArray()
        warnings.forEach { warning ->
            val json = JSONObject().apply {
                put("id", warning.id)
                put("type", warning.type)
                put("level", warning.level)
                put("location", warning.location)
                put("description", warning.description)
                put("timestamp", warning.timestamp)
                put("isHandled", warning.isHandled)
            }
            jsonArray.put(json)
        }
        prefs.edit().putString(KEY_WARNINGS, jsonArray.toString()).apply()
    }
    
    fun loadWarnings(): List<WarningData> {
        val jsonStr = prefs.getString(KEY_WARNINGS, null) ?: return emptyList()
        val warnings = mutableListOf<WarningData>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                warnings.add(WarningData(
                    id = json.getString("id"),
                    type = json.getString("type"),
                    level = json.getString("level"),
                    location = json.getString("location"),
                    description = json.getString("description"),
                    timestamp = json.getLong("timestamp"),
                    isHandled = json.getBoolean("isHandled")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return warnings
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun saveFarmSettings(
        tempMin: Float,
        tempMax: Float,
        humidityMin: Float,
        humidityMax: Float,
        lightIntensity: Float,
        coThreshold: Float
    ) {
        farmPrefs.edit().apply {
            putFloat(KEY_TEMP_MIN, tempMin)
            putFloat(KEY_TEMP_MAX, tempMax)
            putFloat(KEY_HUMIDITY_MIN, humidityMin)
            putFloat(KEY_HUMIDITY_MAX, humidityMax)
            putFloat(KEY_LIGHT_INTENSITY, lightIntensity)
            putFloat(KEY_CO_THRESHOLD, coThreshold)
            apply()
        }
    }

    fun readFarmSettings(): FarmSettings? {
        return if (farmPrefs.contains(KEY_TEMP_MIN)) {
            FarmSettings(
                tempMin = farmPrefs.getFloat(KEY_TEMP_MIN, 0f),
                tempMax = farmPrefs.getFloat(KEY_TEMP_MAX, 0f),
                humidityMin = farmPrefs.getFloat(KEY_HUMIDITY_MIN, 0f),
                humidityMax = farmPrefs.getFloat(KEY_HUMIDITY_MAX, 0f),
                lightIntensity = farmPrefs.getFloat(KEY_LIGHT_INTENSITY, 0f),
                coThreshold = farmPrefs.getFloat(KEY_CO_THRESHOLD, 0f)
            )
        } else {
            null
        }
    }

    fun clearFarmSettings() {
        farmPrefs.edit().clear().apply()
    }
}

data class DeviceData(
    val id: String,
    val name: String,
    val type: String,
    val isOn: Boolean,
    val location: String
)

data class ProductData(
    val id: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Float,
    val discountedPrice: Float
)

data class WarningData(
    val id: String,
    val type: String,
    val level: String,
    val location: String,
    val description: String,
    val timestamp: Long,
    val isHandled: Boolean
)

data class FarmSettings(
    val tempMin: Float,
    val tempMax: Float,
    val humidityMin: Float,
    val humidityMax: Float,
    val lightIntensity: Float,
    val coThreshold: Float
)
