package com.example.vvesmartcity.supermarket

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProductState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val orders: List<Order> = emptyList()
)

class ProductViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProductState(
        products = ProductDataSource.getAllProducts(),
        orders = ProductDataSource.getOrders()
    ))
    val state: StateFlow<ProductState> = _state.asStateFlow()
    
    val products: List<Product> get() = _state.value.products

    fun searchProducts(query: String) {
        val results = ProductDataSource.searchProducts(query)
        _state.update { it.copy(
            products = results,
            searchQuery = query
        ) }
    }

    fun getProductById(id: String): Product? {
        return ProductDataSource.getProductById(id)
    }

    fun addProduct(product: Product) {
        ProductDataSource.addProduct(product)
        refreshProducts()
    }

    fun updateProduct(product: Product) {
        ProductDataSource.updateProduct(product)
        refreshProducts()
    }

    fun updateQuantity(id: String, newQuantity: Int) {
        ProductDataSource.updateQuantity(id, newQuantity)
        refreshProducts()
    }

    fun deleteProduct(id: String) {
        ProductDataSource.deleteProduct(id)
        refreshProducts()
    }

    fun createOrder(product: Product, quantity: Int): Order {
        val order = ProductDataSource.createOrder(product, quantity)
        refreshProducts()
        refreshOrders()
        return order
    }

    fun refreshProducts() {
        _state.update { it.copy(
            products = ProductDataSource.searchProducts(_state.value.searchQuery)
        ) }
    }

    fun refreshOrders() {
        _state.update { it.copy(
            orders = ProductDataSource.getOrders()
        ) }
    }
}
