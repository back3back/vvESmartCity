package com.example.vvesmartcity.supermarket

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.data.ProductData
import java.util.UUID

data class Product(
    val id: String,
    val name: String,
    val imageResId: Int?,
    val quantity: Int,
    val unitPrice: Float,
    val discountedPrice: Float
)

data class Order(
    val orderId: String,
    val product: Product,
    val purchaseQuantity: Int,
    val totalAmount: Float,
    val timestamp: Long
)

object ProductDataSource {
    private val products = mutableStateListOf<Product>()
    private val orders = mutableStateListOf<Order>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        
        val savedProducts = DataPersistenceManager.loadProducts()
        if (savedProducts.isNotEmpty()) {
            savedProducts.forEach { productData ->
                products.add(Product(
                    id = productData.id,
                    name = productData.name,
                    imageResId = null,
                    quantity = productData.quantity,
                    unitPrice = productData.unitPrice,
                    discountedPrice = productData.discountedPrice
                ))
            }
        } else {
            initMockData()
        }
        isInitialized = true
    }
    
    private fun saveProducts() {
        val productDataList = products.map { product ->
            ProductData(
                id = product.id,
                name = product.name,
                quantity = product.quantity,
                unitPrice = product.unitPrice,
                discountedPrice = product.discountedPrice
            )
        }
        DataPersistenceManager.saveProducts(productDataList)
    }

    fun getAllProducts(): List<Product> = products.toList()

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return products.toList()
        return products.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun getProductById(id: String): Product? = products.find { it.id == id }

    fun addProduct(product: Product) {
        products.add(product)
        saveProducts()
    }

    fun updateProduct(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            products[index] = product
            saveProducts()
        }
    }

    fun deleteProduct(id: String) {
        products.removeAll { it.id == id }
        saveProducts()
    }

    fun createOrder(product: Product, quantity: Int): Order {
        val order = Order(
            orderId = UUID.randomUUID().toString().substring(0, 8).uppercase(),
            product = product,
            purchaseQuantity = quantity,
            totalAmount = product.discountedPrice * quantity,
            timestamp = System.currentTimeMillis()
        )
        orders.add(order)
        val productIndex = products.indexOfFirst { it.id == product.id }
        if (productIndex >= 0) {
            products[productIndex] = product.copy(quantity = product.quantity - quantity)
            saveProducts()
        }
        return order
    }

    fun getOrders(): List<Order> = orders.toList()

    private fun initMockData() {
        products.addAll(
            listOf(
                Product("P001", "有机牛奶", null, 100, 12.5f, 9.9f),
                Product("P002", "全麦面包", null, 50, 8.0f, 6.5f),
                Product("P003", "新鲜苹果", null, 200, 6.0f, 4.8f),
                Product("P004", "进口橄榄油", null, 30, 68.0f, 55.0f),
                Product("P005", "鸡蛋(30枚)", null, 80, 25.0f, 19.9f),
                Product("P006", "三文鱼", null, 20, 88.0f, 72.0f)
            )
        )
        saveProducts()
    }
}
