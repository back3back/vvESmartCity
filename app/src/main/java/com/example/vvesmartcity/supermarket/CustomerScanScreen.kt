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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun CustomerScanScreen(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val context = LocalContext.current
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var showProductDialog by remember { mutableStateOf(false) }
    var showNotFoundDialog by remember { mutableStateOf(false) }
    var scannedProduct by remember { mutableStateOf<BarcodeProduct?>(null) }
    var inventoryProduct by remember { mutableStateOf<Product?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    fun handleBarcodeResult(barcode: String?) {
        if (barcode != null) {
            scannedBarcode = barcode
            val product = BarcodeProductDatabase.findByBarcode(barcode)
            val inventory = ProductDataSource.getProductById(barcode)
            
            if (product != null && inventory != null && inventory.quantity > 0) {
                scannedProduct = product
                inventoryProduct = inventory
                showProductDialog = true
            } else {
                showNotFoundDialog = true
            }
        } else {
            Toast.makeText(context, "未能识别条形码", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isProcessing = true
            scanBarcodeFromBitmapCustomer(bitmap) { barcode ->
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
            scanBarcodeFromUriCustomer(context, uri) { barcode ->
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
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = "扫码购物",
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color(0xFF43A047).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛒",
                    fontSize = 56.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "扫描商品条码",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "扫码查找商品并购买",
                fontSize = 14.sp,
                color = Color(0xFF78909C)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "支持识别的条形码类型",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BarcodeTypeChipCustomer("EAN-13")
                        BarcodeTypeChipCustomer("UPC-A")
                        BarcodeTypeChipCustomer("Code128")
                        BarcodeTypeChipCustomer("QR码")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                        text = "📷 拍照扫描条形码",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isProcessing
            ) {
                Text(
                    text = "🖼️ 从相册选择图片",
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
                            text = "扫描商品条码可快速查找并购买",
                            fontSize = 12.sp,
                            color = Color(0xFF795548)
                        )
                    }
                }
            }
        }
    }

    if (showProductDialog && scannedProduct != null && inventoryProduct != null) {
        CustomerProductDialog(
            product = scannedProduct!!,
            inventory = inventoryProduct!!,
            barcode = scannedBarcode ?: "",
            onDismiss = { showProductDialog = false },
            onBuy = {
                showProductDialog = false
                onProductClick(inventoryProduct!!.id)
            }
        )
    }

    if (showNotFoundDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNotFoundDialog = false },
            title = {
                Text(
                    text = "❌ 未找到商品",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "条码: ${scannedBarcode ?: ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF78909C)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "该商品暂未上架或库存不足",
                        fontSize = 16.sp,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "请联系工作人员添加商品",
                        fontSize = 14.sp,
                        color = Color(0xFF78909C)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotFoundDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("知道了")
                }
            }
        )
    }
}

@Composable
fun BarcodeTypeChipCustomer(type: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = type,
            fontSize = 11.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CustomerProductDialog(
    product: BarcodeProduct,
    inventory: Product,
    barcode: String,
    onDismiss: () -> Unit,
    onBuy: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "✅ 找到商品",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "库存: ${inventory.quantity} 件",
                            fontSize = 13.sp,
                            color = Color(0xFF43A047)
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
            }
        },
        confirmButton = {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Text("立即购买")
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

private fun scanBarcodeFromBitmapCustomer(bitmap: Bitmap, onResult: (String?) -> Unit) {
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

private fun scanBarcodeFromUriCustomer(context: android.content.Context, uri: Uri, onResult: (String?) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap != null) {
            scanBarcodeFromBitmapCustomer(bitmap, onResult)
        } else {
            onResult(null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}
