package com.example.vvesmartcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.farm.FarmDataSource
import com.example.vvesmartcity.navigation.SmartCityApp
import com.example.vvesmartcity.supermarket.ProductDataSource
import com.example.vvesmartcity.ui.theme.VvESmartCityTheme
import com.example.vvesmartcity.warning.WarningDataSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        DataPersistenceManager.init(applicationContext)
        FarmDataSource.init(applicationContext)
        ProductDataSource.init(applicationContext)
        WarningDataSource.init(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            VvESmartCityTheme {
                SmartCityApp()
            }
        }
    }
}
