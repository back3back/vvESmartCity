package com.example.vvesmartcity.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object DataPersistenceManager {
    private const val PREFS_NAME = "smart_city_data"
    
    private const val KEY_DEVICES = "devices"
    private const val KEY_PRODUCTS = "products"
    private const val KEY_WARNINGS = "warnings"
    private const val KEY_INITIALIZED = "data_initialized"
    
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
