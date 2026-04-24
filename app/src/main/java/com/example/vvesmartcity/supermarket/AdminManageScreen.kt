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
    var showProductDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var scannedProduct by remember { mutableStateOf<BarcodeProduct?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    fun handleBarcodeResult(barcode: String?) {
        if (barcode != null) {
            scannedBarcode = barcode
            val product = BarcodeProductDatabase.findByBarcode(barcode)
            if (product != null) {
                scannedProduct = product
                showProductDialog = true
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
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFE53935).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📦",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "添加/入库商品",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "扫码或手动添加商品到库存",
                fontSize = 14.sp,
                color = Color(0xFF78909C)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                        text = "📷 扫码添加",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "扫描商品条形码，自动识别商品信息",
                        fontSize = 13.sp,
                        color = Color(0xFF78909C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("识别中...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(
                        text = "📷 拍照扫描",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isProcessing
            ) {
                Text(
                    text = "🖼️ 从相册选择",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "✏️ 手动添加",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "手动输入商品信息添加新商品",
                        fontSize = 13.sp,
                        color = Color(0xFF78909C)
                    )
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
                    .height(52.dp)
            ) {
                Text(
                    text = "✏️ 手动添加商品",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "使用提示",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            text = "扫码时如果商品已存在则增加库存，不存在则添加新商品",
                            fontSize = 12.sp,
                            color = Color(0xFF795548)
                        )
                    }
                }
            }
        }
    }

    if (showProductDialog && scannedProduct != null) {
        AdminProductStockDialog(
            product = scannedProduct!!,
            barcode = scannedBarcode ?: "",
            onDismiss = { showProductDialog = false },
            onConfirm = { quantity ->
                val existingProduct = ProductDataSource.getProductById(scannedBarcode ?: "")
                if (existingProduct != null) {
                    ProductDataSource.updateQuantity(scannedBarcode!!, existingProduct.quantity + quantity)
                    Toast.makeText(context, "已入库 ${quantity} 件 ${scannedProduct!!.name}", Toast.LENGTH_SHORT).show()
                } else {
                    val newProduct = Product(
                        id = scannedBarcode ?: "P${System.currentTimeMillis()}",
                        name = scannedProduct!!.name,
                        imageResId = null,
                        quantity = quantity,
                        unitPrice = scannedProduct!!.price,
                        discountedPrice = scannedProduct!!.price
                    )
                    ProductDataSource.addProduct(newProduct)
                    Toast.makeText(context, "已添加新商品: ${scannedProduct!!.name}", Toast.LENGTH_SHORT).show()
                }
                showProductDialog = false
            }
        )
    }

    if (showAddProductDialog) {
        AdminAddProductDialog(
            initialBarcode = scannedBarcode ?: "",
            onDismiss = { showAddProductDialog = false },
            onConfirm = { barcode, name, price, category, brand ->
                val newBarcodeProduct = BarcodeProduct(barcode, name, price, category, brand)
                BarcodeProductDatabase.addProduct(newBarcodeProduct)
                
                val newProduct = Product(
                    id = barcode,
                    name = name,
                    imageResId = null,
                    quantity = 100,
                    unitPrice = price,
                    discountedPrice = price
                )
                ProductDataSource.addProduct(newProduct)
                showAddProductDialog = false
                Toast.makeText(context, "已添加新商品: $name", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AdminProductStockDialog(
    product: BarcodeProduct,
    barcode: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf("10") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📦 商品入库",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(
                                text = "品牌: ${product.brand}",
                                fontSize = 13.sp,
                                color = Color(0xFF78909C)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "分类: ${product.category}",
                                fontSize = 13.sp,
                                color = Color(0xFF78909C)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "条码: $barcode",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "¥${String.format("%.2f", product.price)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53935)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.all { c -> c.isDigit() }) {
                            quantity = it
                        }
                    },
                    label = { Text("入库数量") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 10
                    if (qty > 0) {
                        onConfirm(qty)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("确认入库")
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
fun AdminAddProductDialog(
    initialBarcode: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Float, String, String) -> Unit
) {
    var barcode by remember { mutableStateOf(initialBarcode) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "➕ 添加新商品",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
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
                    label = { Text("条码 *") },
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
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("分类") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("品牌") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (barcode.isNotBlank() && name.isNotBlank() && price.isNotBlank()) {
                        onConfirm(barcode, name, price.toFloatOrNull() ?: 0f, category, brand)
                    }
                },
                enabled = barcode.isNotBlank() && name.isNotBlank() && price.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("确认添加")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("取消")
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
