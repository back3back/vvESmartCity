package com.example.vvesmartcity.supermarket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vvesmartcity.data.DataPersistenceManager
import com.example.vvesmartcity.data.ShoppingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalAmount: Float = 0f,
    val itemCount: Int = 0,
    val dbOrders: List<ShoppingEntity> = emptyList()
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(CartState(
        items = CartDataSource.items,
        totalAmount = CartDataSource.totalAmount,
        itemCount = CartDataSource.itemCount
    ))
    val state: StateFlow<CartState> = _state.asStateFlow()
    
    val items: List<CartItem> get() = _state.value.items
    val totalAmount: Float get() = _state.value.totalAmount
    val itemCount: Int get() = _state.value.itemCount

    init {
        observeDbOrders()
    }

    private fun observeDbOrders() {
        viewModelScope.launch {
            DataPersistenceManager.getShoppingDao().getAll().collect { orders ->
                _state.update { it.copy(dbOrders = orders) }
            }
        }
    }

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

    fun insertOrder(item: ShoppingEntity) {
        viewModelScope.launch {
            DataPersistenceManager.getShoppingDao().insert(item)
        }
    }

    fun updateOrder(item: ShoppingEntity) {
        viewModelScope.launch {
            DataPersistenceManager.getShoppingDao().update(item)
        }
    }

    fun deleteOrder(item: ShoppingEntity) {
        viewModelScope.launch {
            DataPersistenceManager.getShoppingDao().delete(item)
        }
    }

    private fun refreshCart() {
        _state.update { CartState(
            items = CartDataSource.items,
            totalAmount = CartDataSource.totalAmount,
            itemCount = CartDataSource.itemCount,
            dbOrders = _state.value.dbOrders
        ) }
    }
}
