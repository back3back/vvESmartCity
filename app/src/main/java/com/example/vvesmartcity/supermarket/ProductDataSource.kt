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
        return products.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.id.contains(query, ignoreCase = true)
        }
    }

    fun getProductById(id: String): Product? = products.find { it.id == id }

    fun addProduct(product: Product) {
        val existingIndex = products.indexOfFirst { it.id == product.id }
        if (existingIndex >= 0) {
            products[existingIndex] = product.copy(
                quantity = products[existingIndex].quantity + product.quantity
            )
        } else {
            products.add(product)
        }
        saveProducts()
    }

    fun updateProduct(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            products[index] = product
            saveProducts()
        }
    }

    fun updateQuantity(id: String, newQuantity: Int) {
        val index = products.indexOfFirst { it.id == id }
        if (index >= 0) {
            products[index] = products[index].copy(quantity = newQuantity)
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
                Product("6901234567890", "有机纯牛奶 250ml", null, 100, 12.5f, 9.9f),
                Product("6901234567891", "全麦面包 400g", null, 50, 8.0f, 6.5f),
                Product("6901234567892", "红富士苹果 1kg", null, 200, 15.8f, 12.8f),
                Product("6901234567893", "进口橄榄油 500ml", null, 30, 68.0f, 55.0f),
                Product("6901234567894", "鲜鸡蛋 30枚", null, 80, 25.0f, 19.9f),
                Product("6901234567895", "挪威三文鱼 200g", null, 20, 88.0f, 72.0f),
                Product("6901234567896", "农夫山泉矿泉水 550ml", null, 150, 2.0f, 2.0f),
                Product("6901234567897", "康师傅红烧牛肉面", null, 60, 4.5f, 4.5f),
                Product("6920734800101", "可口可乐 330ml", null, 100, 3.0f, 2.5f),
                Product("6903148040215", "奥利奥原味饼干 116g", null, 40, 9.9f, 8.5f)
            )
        )
        saveProducts()
    }
}
