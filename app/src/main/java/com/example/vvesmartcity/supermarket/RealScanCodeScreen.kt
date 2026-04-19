package com.example.vvesmartcity.supermarket

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun RealScanCodeScreen(
    onBack: () -> Unit,
    onScanResult: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
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
                .background(Color.Black.copy(alpha = 0.7f))
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
                text = "扫码购物",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        val executor = Executors.newSingleThreadExecutor()

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val barcodeScanner = BarcodeScanning.getClient(
                                BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(
                                        Barcode.FORMAT_QR_CODE,
                                        Barcode.FORMAT_EAN_13,
                                        Barcode.FORMAT_EAN_8,
                                        Barcode.FORMAT_CODE_128,
                                        Barcode.FORMAT_CODE_39,
                                        Barcode.FORMAT_UPC_A,
                                        Barcode.FORMAT_UPC_E
                                    )
                                    .build()
                            )

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalyzer.setAnalyzer(executor) { imageProxy ->
                                if (isScanning) {
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )

                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                if (barcodes.isNotEmpty()) {
                                                    val firstBarcode = barcodes.first()
                                                    firstBarcode.rawValue?.let { value ->
                                                        isScanning = false
                                                        scannedCode = value
                                                    }
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, executor)

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 4.dp.toPx()
                    val lineLength = 30.dp.toPx()
                    val cornerSize = size.minDimension * 0.6f
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val left = centerX - cornerSize / 2
                    val top = centerY - cornerSize / 2

                    drawRect(
                        color = Color(0xFF43A047),
                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(cornerSize, cornerSize),
                        style = Stroke(width = strokeWidth)
                    )

                    val lineColor = Color(0xFF43A047)
                    val lineWidth = 6.dp.toPx()

                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left - lineLength, top),
                        end = androidx.compose.ui.geometry.Offset(left + lineWidth, top),
                        strokeWidth = lineWidth
                    )
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left, top - lineLength),
                        end = androidx.compose.ui.geometry.Offset(left, top + lineWidth),
                        strokeWidth = lineWidth
                    )

                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left + cornerSize + lineLength, top),
                        end = androidx.compose.ui.geometry.Offset(left + cornerSize - lineWidth, top),
                        strokeWidth = lineWidth
                    )
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left + cornerSize, top - lineLength),
                        end = androidx.compose.ui.geometry.Offset(left + cornerSize, top + lineWidth),
                        strokeWidth = lineWidth
                    )

                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left - lineLength, top + cornerSize),
                        end = androidx.compose.ui.geometry.Offset(left + lineWidth, top + cornerSize),
                        strokeWidth = lineWidth
                    )
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left, top + cornerSize + lineLength),
                        end = androidx.compose.ui.geometry.Offset(left, top + cornerSize - lineWidth),
                        strokeWidth = lineWidth
                    )

                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left + cornerSize + lineLength, top + cornerSize),
                        end = androidx.compose.ui.geometry.Offset(left + cornerSize - lineWidth, top + cornerSize),
                        strokeWidth = lineWidth
                    )
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(left + cornerSize, top + cornerSize + lineLength),
                        end = androidx.compose.ui.geometry.Offset(left + cornerSize, top + cornerSize - lineWidth),
                        strokeWidth = lineWidth
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.6f)
                        .border(2.dp, Color(0xFF43A047).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "需要相机权限才能扫码",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        if (scannedCode != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "✅ 扫码成功",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "条码内容:",
                        fontSize = 14.sp,
                        color = Color(0xFF78909C)
                    )
                    Text(
                        text = scannedCode!!,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isScanning = true
                                scannedCode = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90A4AE)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("继续扫码", fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                scannedCode?.let { onScanResult(it) }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("查看商品", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
