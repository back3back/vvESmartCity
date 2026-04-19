package com.example.vvesmartcity.supermarket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupermarketMainScreen(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onAddProduct: () -> Unit,
    onScanCode: () -> Unit,
    onVideoMonitor: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf(ProductDataSource.searchProducts("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
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
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        Text("搜索商品名称...", fontSize = 14.sp, color = Color(0xFFB0BEC5))
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddProduct,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("添加商品", fontSize = 13.sp)
                }
                Button(
                    onClick = onScanCode,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("拍码购物", fontSize = 13.sp)
                }
                Button(
                    onClick = onVideoMonitor,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("视频监控", fontSize = 13.sp)
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
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onBuyClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onBuyClick: () -> Unit) {
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
                    text = "ID: ${product.id}  |  库存: ${product.quantity}",
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

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("购买", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ProductPurchaseScreen(
    productId: String,
    onBack: () -> Unit,
    onPurchaseSuccess: (Order) -> Unit
) {
    val product = ProductDataSource.getProductById(productId)
    var purchaseQuantity by remember { mutableIntStateOf(1) }
    var showOrder by remember { mutableStateOf(false) }
    var lastOrder by remember { mutableStateOf<Order?>(null) }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("商品不存在", fontSize = 16.sp, color = Color(0xFF78909C))
        }
        return
    }

    if (showOrder && lastOrder != null) {
        OrderDetailScreen(order = lastOrder!!, onBack = onBack)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = "购买商品",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF43A047), Color(0xFF66BB6A))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "商品信息",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = product.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "商品ID: ${product.id}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "库存: ${product.quantity}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Text(
                                text = String.format("原价: ¥%.1f", product.unitPrice),
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = String.format("折扣价: ¥%.1f", product.discountedPrice),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFEB3B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "购买数量",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { if (purchaseQuantity > 1) purchaseQuantity-- },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("-", fontSize = 20.sp, color = Color(0xFF424242))
                        }
                        Text(
                            text = purchaseQuantity.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238),
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { if (purchaseQuantity < product.quantity) purchaseQuantity++ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("+", fontSize = 20.sp, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "合计金额:",
                            fontSize = 16.sp,
                            color = Color(0xFF78909C)
                        )
                        Text(
                            text = String.format("¥%.1f", product.discountedPrice * purchaseQuantity),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val order = ProductDataSource.createOrder(product, purchaseQuantity)
                    lastOrder = order
                    showOrder = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = product.quantity > 0
            ) {
                Text("确定购买", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun OrderDetailScreen(order: Order, onBack: () -> Unit) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = "购买成功",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF43A047), Color(0xFF66BB6A))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "订单详情",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OrderInfoRow("订单编号", order.orderId)
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("商品名称", order.product.name)
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("商品ID", order.product.id)
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("购买数量", "${order.purchaseQuantity}")
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("单价", String.format("¥%.1f", order.product.discountedPrice))
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("订单金额", String.format("¥%.1f", order.totalAmount))
                        Spacer(modifier = Modifier.height(12.dp))
                        OrderInfoRow("下单时间", timeFormat.format(Date(order.timestamp)))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("返回商超", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun AddEditProductScreen(
    productId: String?,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val editingProduct = productId?.let { ProductDataSource.getProductById(it) }
    var name by remember { mutableStateOf(editingProduct?.name ?: "") }
    var id by remember { mutableStateOf(editingProduct?.id ?: "") }
    var quantityStr by remember { mutableStateOf(editingProduct?.quantity?.toString() ?: "") }
    var unitPriceStr by remember { mutableStateOf(editingProduct?.unitPrice?.toString() ?: "") }
    var discountedPriceStr by remember { mutableStateOf(editingProduct?.discountedPrice?.toString() ?: "") }
    var showList by remember { mutableStateOf(false) }

    if (showList) {
        onSaveSuccess()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = if (editingProduct != null) "编辑商品" else "添加商品",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProductInputField("商品名称", name) { name = it }
            ProductInputField("商品ID", id) { id = it }
            ProductInputField("商品数量", quantityStr) { quantityStr = it }
            ProductInputField("单价", unitPriceStr) { unitPriceStr = it }
            ProductInputField("折扣后单价", discountedPriceStr) { discountedPriceStr = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val quantity = quantityStr.toIntOrNull() ?: 0
                    val unitPrice = unitPriceStr.toFloatOrNull() ?: 0f
                    val discountedPrice = discountedPriceStr.toFloatOrNull() ?: 0f
                    if (editingProduct != null) {
                        ProductDataSource.updateProduct(
                            Product(id, name, null, quantity, unitPrice, discountedPrice)
                        )
                    } else {
                        ProductDataSource.addProduct(
                            Product(id, name, null, quantity, unitPrice, discountedPrice)
                        )
                    }
                    showList = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("保存商品", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            if (editingProduct != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        ProductDataSource.deleteProduct(editingProduct.id)
                        showList = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("删除商品", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ProductInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF263238)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF43A047),
                focusedLabelColor = Color(0xFF43A047)
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun VideoMonitorScreen(onBack: () -> Unit) {
    var isLive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
                )
            )
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF1A237E)
                )
            }
            Text(
                text = "视频监控",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isLive = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLive) Color(0xFFE53935) else Color(0xFFE3F2FD),
                        contentColor = if (isLive) Color.White else Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("实时监控", fontSize = 13.sp)
                }
                Button(
                    onClick = { isLive = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLive) Color(0xFF1E88E5) else Color(0xFFE3F2FD),
                        contentColor = if (!isLive) Color.White else Color(0xFF1E88E5)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("过往视频", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLive) {
                LiveVideoView()
            } else {
                HistoryVideoView()
            }
        }
    }
}

@Composable
fun LiveVideoView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF263238))
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF37474F)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📹",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "实时监控画面",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "摄像头 1 - 入口区域",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "● 直播中",
                    fontSize = 13.sp,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5)
                )
            }
        }
    }
}

@Composable
fun HistoryVideoView() {
    val historyItems = listOf(
        "2026-04-19 14:30 - 入口区域",
        "2026-04-19 10:15 - 收银台",
        "2026-04-18 16:45 - 货架区域",
        "2026-04-18 09:20 - 入口区域"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        historyItems.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶",
                            fontSize = 20.sp,
                            color = Color(0xFF1E88E5)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        color = Color(0xFF263238)
                    )
                }
            }
        }
    }
}

@Composable
fun ScanCodeScreen(
    onBack: () -> Unit,
    onScanResult: (String) -> Unit
) {
    var isScanning by remember { mutableStateOf(true) }
    var scanLineY by remember { mutableStateOf(0f) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            while (isScanning) {
                kotlinx.coroutines.delay(3000)
                isScanning = false
                onScanResult("P003")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
            Text(
                text = "拍码购物",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1a1a2e))
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val boxSize = canvasWidth * 0.6f
                val boxX = (canvasWidth - boxSize) / 2
                val boxY = (canvasHeight - boxSize) / 2

                drawRect(
                    color = Color(0xFF1a1a2e),
                    size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight)
                )

                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(canvasWidth, boxY)
                )
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, boxY + boxSize),
                    size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight - boxY - boxSize)
                )
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, boxY),
                    size = androidx.compose.ui.geometry.Size(boxX, boxSize)
                )
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - boxX - boxSize, boxSize)
                )

                val cornerLength = boxSize * 0.15f
                val strokeWidth = 4f

                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX, boxY + cornerLength),
                    end = androidx.compose.ui.geometry.Offset(boxX, boxY),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX, boxY),
                    end = androidx.compose.ui.geometry.Offset(boxX + cornerLength, boxY),
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX + boxSize - cornerLength, boxY),
                    end = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY),
                    end = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY + cornerLength),
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX, boxY + boxSize - cornerLength),
                    end = androidx.compose.ui.geometry.Offset(boxX, boxY + boxSize),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX, boxY + boxSize),
                    end = androidx.compose.ui.geometry.Offset(boxX + cornerLength, boxY + boxSize),
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX + boxSize - cornerLength, boxY + boxSize),
                    end = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY + boxSize),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFF43A047),
                    start = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY + boxSize - cornerLength),
                    end = androidx.compose.ui.geometry.Offset(boxX + boxSize, boxY + boxSize),
                    strokeWidth = strokeWidth
                )

                val scanLineCurrentY = boxY + (boxSize * ((System.currentTimeMillis() % 3000).toFloat() / 3000f))
                drawLine(
                    color = Color(0xFF43A047).copy(alpha = 0.8f),
                    start = androidx.compose.ui.geometry.Offset(boxX, scanLineCurrentY),
                    end = androidx.compose.ui.geometry.Offset(boxX + boxSize, scanLineCurrentY),
                    strokeWidth = 2f
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
            ) {
                Text(
                    text = "将条形码/二维码对准框内",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "识别成功后将自动跳转",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
