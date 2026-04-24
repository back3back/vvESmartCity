package com.example.vvesmartcity.supermarket

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun AdminManageScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var products by remember { mutableStateOf(ProductDataSource.getAllProducts()) }

    fun refreshProducts() {
        products = ProductDataSource.getAllProducts()
    }

    fun handleBarcodeResult(barcode: String?) {
        if (barcode != null) {
            scannedBarcode = barcode
            val existingProduct = ProductDataSource.getProductById(barcode)
            if (existingProduct != null) {
                editingProduct = existingProduct
                showEditProductDialog = true
            } else {
                showAddProductDialog = true
            }
        } else {
            Toast.makeText(context, "未能识别条形码，请手动输入", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isProcessing = true
            scanBarcodeFromBitmapAdmin(bitmap) { barcode ->
                isProcessing = false
                handleBarcodeResult(barcode)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessing = true
            scanBarcodeFromUriAdmin(context, uri) { barcode ->
                isProcessing = false
                handleBarcodeResult(barcode)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
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
                    tint = Color(0xFFE53935)
                )
            }
            Text(
                text = "管理员 - 商品管理",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📷 扫码添加/编辑",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "扫描商品条形码，已存在则编辑，不存在则添加",
                        fontSize = 13.sp,
                        color = Color(0xFF78909C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("📷 拍照扫描", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !isProcessing
                ) {
                    Text("🖼️ 相册选择", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { 
                    scannedBarcode = null
                    showAddProductDialog = true 
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("✏️ 手动添加商品", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "商品列表 (${products.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            products.forEach { product ->
                AdminProductItem(
                    product = product,
                    onEdit = {
                        editingProduct = product
                        showEditProductDialog = true
                    },
                    onDelete = {
                        ProductDataSource.deleteProduct(product.id)
                        refreshProducts()
                        Toast.makeText(context, "已删除: ${product.name}", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showAddProductDialog) {
        AdminAddProductDialog(
            initialBarcode = scannedBarcode ?: "",
            onDismiss = { showAddProductDialog = false },
            onConfirm = { barcode, name, price, discountedPrice, quantity ->
                val newProduct = Product(
                    id = barcode,
                    name = name,
                    imageResId = null,
                    quantity = quantity,
                    unitPrice = price,
                    discountedPrice = discountedPrice
                )
                ProductDataSource.addProduct(newProduct)
                refreshProducts()
                showAddProductDialog = false
                Toast.makeText(context, "已添加: $name", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showEditProductDialog && editingProduct != null) {
        AdminEditProductDialog(
            product = editingProduct!!,
            onDismiss = { showEditProductDialog = false },
            onConfirm = { updatedProduct ->
                ProductDataSource.updateProduct(updatedProduct)
                refreshProducts()
                showEditProductDialog = false
                Toast.makeText(context, "已更新: ${updatedProduct.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AdminProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "条码: ${product.id}",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "库存: ${product.quantity}",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = String.format("¥%.1f", product.unitPrice),
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5),
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("¥%.1f", product.discountedPrice),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }
            }
            
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_edit),
                    contentDescription = "编辑",
                    tint = Color(0xFF1976D2)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_delete),
                    contentDescription = "删除",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun AdminAddProductDialog(
    initialBarcode: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Float, Float, Int) -> Unit
) {
    var barcode by remember { mutableStateOf(initialBarcode) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var discountedPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("10") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "➕ 添加商品",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("条形码ID *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("商品名称 *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("价格 *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = discountedPrice,
                    onValueChange = { discountedPrice = it },
                    label = { Text("折后价 (不填则无折扣)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.all { c -> c.isDigit() }) {
                            quantity = it
                        }
                    },
                    label = { Text("库存数量") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toFloatOrNull() ?: 0f
                    val discountValue = discountedPrice.toFloatOrNull() ?: priceValue
                    val quantityValue = quantity.toIntOrNull() ?: 10
                    
                    if (barcode.isNotBlank() && name.isNotBlank() && priceValue > 0) {
                        onConfirm(barcode, name, priceValue, discountValue, quantityValue)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("确认添加")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Text("取消", color = Color(0xFF616161))
            }
        }
    )
}

@Composable
fun AdminEditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.unitPrice.toString()) }
    var discountedPrice by remember { mutableStateOf(product.discountedPrice.toString()) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "✏️ 编辑商品",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "条形码: ${product.id}",
                            fontSize = 12.sp,
                            color = Color(0xFF78909C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("商品名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("价格") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = discountedPrice,
                    onValueChange = { discountedPrice = it },
                    label = { Text("折后价") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.all { c -> c.isDigit() }) {
                            quantity = it
                        }
                    },
                    label = { Text("库存数量") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toFloatOrNull() ?: product.unitPrice
                    val discountValue = discountedPrice.toFloatOrNull() ?: priceValue
                    val quantityValue = quantity.toIntOrNull() ?: product.quantity
                    
                    val updatedProduct = product.copy(
                        name = name,
                        unitPrice = priceValue,
                        discountedPrice = discountValue,
                        quantity = quantityValue
                    )
                    onConfirm(updatedProduct)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("保存修改")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Text("取消", color = Color(0xFF616161))
            }
        }
    )
}

private fun scanBarcodeFromBitmapAdmin(bitmap: Bitmap, onResult: (String?) -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val scanner = BarcodeScanning.getClient()
    
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                onResult(barcodes[0].rawValue)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            onResult(null)
        }
}

private fun scanBarcodeFromUriAdmin(context: android.content.Context, uri: Uri, onResult: (String?) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap != null) {
            scanBarcodeFromBitmapAdmin(bitmap, onResult)
        } else {
            onResult(null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}
