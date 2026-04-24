package com.example.vvesmartcity.supermarket

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupermarketMainScreen(
    userRole: String,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    onCustomerScan: () -> Unit,
    onAdminManage: () -> Unit
) {
    val context = LocalContext.current
    val isAdmin = userRole == "管理员"
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf(ProductDataSource.searchProducts("")) }
    var cartCount by remember { mutableStateOf(CartDataSource.itemCount) }
    var cartTotal by remember { mutableStateOf(CartDataSource.totalAmount) }
    var refreshKey by remember { mutableStateOf(0) }

    fun refreshCart() {
        cartCount = CartDataSource.itemCount
        cartTotal = CartDataSource.totalAmount
        refreshKey++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = "智能商超",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            
            if (cartCount > 0) {
                Button(
                    onClick = onCartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("🛒 购物车($cartCount)", fontSize = 13.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text("搜索商品名称或条码...", fontSize = 14.sp, color = Color(0xFFB0BEC5))
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            products = ProductDataSource.searchProducts(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF263238))
                    )
                }
                Button(
                    onClick = {
                        products = ProductDataSource.searchProducts(searchQuery)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("查询", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCustomerScan,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("扫码购物", fontSize = 13.sp)
                    }
                    Button(
                        onClick = onAdminManage,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("商品管理", fontSize = 13.sp)
                    }
                }
            } else {
                Button(
                    onClick = onCustomerScan,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("扫码购物", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "商品列表 (${products.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products, key = { "${it.id}_$refreshKey" }) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            if (product.quantity <= 0) {
                                Toast.makeText(context, "商品库存不足", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = CartDataSource.addToCart(product, 1)
                                if (success) {
                                    refreshCart()
                                } else {
                                    Toast.makeText(context, "库存不足", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onRemoveFromCart = {
                            val currentQty = CartDataSource.getQuantity(product.id)
                            if (currentQty > 0) {
                                CartDataSource.updateQuantity(product.id, currentQty - 1)
                                refreshCart()
                            }
                        }
                    )
                }
            }
        }

        if (cartCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "已选 $cartCount 件商品",
                            fontSize = 14.sp,
                            color = Color(0xFF78909C)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "合计: ",
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = String.format("¥%.2f", cartTotal),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935)
                            )
                        }
                    }
                    
                    Button(
                        onClick = onCartClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth(0.4f)
                    ) {
                        Text("去结算", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
) {
    val cartQuantity = CartDataSource.getQuantity(product.id)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "条码: ${product.id}  |  库存: ${product.quantity}",
                    fontSize = 12.sp,
                    color = Color(0xFF78909C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = String.format("¥%.1f", product.unitPrice),
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5),
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("¥%.1f", product.discountedPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }
            }

            if (cartQuantity > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onRemoveFromCart,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                    }
                    
                    Text(
                        text = cartQuantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (cartQuantity < product.quantity) Color(0xFF43A047) else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        enabled = cartQuantity < product.quantity
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (product.quantity > 0) Color(0xFF43A047) else Color(0xFFB0BEC5)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    enabled = product.quantity > 0
                ) {
                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
