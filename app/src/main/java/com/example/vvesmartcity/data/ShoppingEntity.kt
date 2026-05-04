package com.example.vvesmartcity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Info")
data class ShoppingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: String,
    val productName: String,
    val unitPrice: Float,
    val quantity: Int,
    val discountedPrice: Float
)
