package com.example.vvesmartcity.supermarket

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalAmount: Float = 0f,
    val itemCount: Int = 0
)

class CartViewModel : ViewModel() {
    private val _state = MutableStateFlow(CartState(
        items = CartDataSource.items,
        totalAmount = CartDataSource.totalAmount,
        itemCount = CartDataSource.itemCount
    ))
    val state: StateFlow<CartState> = _state.asStateFlow()
    
    val items: List<CartItem> get() = _state.value.items
    val totalAmount: Float get() = _state.value.totalAmount
    val itemCount: Int get() = _state.value.itemCount

    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val success = CartDataSource.addToCart(product, quantity)
        if (success) {
            refreshCart()
        }
        return success
    }

    fun updateQuantity(productId: String, quantity: Int): Boolean {
        val success = CartDataSource.updateQuantity(productId, quantity)
        if (success) {
            refreshCart()
        }
        return success
    }

    fun removeFromCart(productId: String) {
        CartDataSource.removeFromCart(productId)
        refreshCart()
    }

    fun clearCart() {
        CartDataSource.clearCart()
        refreshCart()
    }

    fun getQuantity(productId: String): Int {
        return CartDataSource.getQuantity(productId)
    }

    fun checkout(): List<Order> {
        val orders = CartDataSource.checkout()
        refreshCart()
        return orders
    }

    private fun refreshCart() {
        _state.update { CartState(
            items = CartDataSource.items,
            totalAmount = CartDataSource.totalAmount,
            itemCount = CartDataSource.itemCount
        ) }
    }
}
