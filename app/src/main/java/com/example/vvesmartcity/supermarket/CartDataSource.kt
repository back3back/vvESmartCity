package com.example.vvesmartcity.supermarket

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

data class CartItem(
    val product: Product,
    var quantity: Int
) {
    val totalPrice: Float
        get() = product.discountedPrice * quantity
}

object CartDataSource {
    private val cartItems = mutableStateListOf<CartItem>()
    
    val items: List<CartItem> get() = cartItems.toList()
    
    val totalAmount: Float
        get() = cartItems.sumOf { it.totalPrice.toDouble() }.toFloat()
    
    val itemCount: Int
        get() = cartItems.sumOf { it.quantity }

    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val existingItem = cartItems.find { it.product.id == product.id }
        
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + quantity
            if (newQuantity > product.quantity) {
                return false
            }
            existingItem.quantity = newQuantity
        } else {
            if (quantity > product.quantity) {
                return false
            }
            cartItems.add(CartItem(product, quantity))
        }
        return true
    }

    fun updateQuantity(productId: String, quantity: Int): Boolean {
        val item = cartItems.find { it.product.id == productId } ?: return false
        if (quantity <= 0) {
            cartItems.remove(item)
            return true
        }
        if (quantity > item.product.quantity) {
            return false
        }
        item.quantity = quantity
        return true
    }

    fun removeFromCart(productId: String) {
        cartItems.removeAll { it.product.id == productId }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getQuantity(productId: String): Int {
        return cartItems.find { it.product.id == productId }?.quantity ?: 0
    }

    fun isInCart(productId: String): Boolean {
        return cartItems.any { it.product.id == productId }
    }

    fun checkout(): List<Order> {
        val orders = mutableListOf<Order>()
        
        cartItems.forEach { item ->
            val order = ProductDataSource.createOrder(item.product, item.quantity)
            orders.add(order)
        }
        
        cartItems.clear()
        return orders
    }
}
