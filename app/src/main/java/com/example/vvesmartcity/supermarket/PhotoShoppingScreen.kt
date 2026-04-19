package com.example.vvesmartcity.supermarket

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PhotoRecord(
    val id: String,
    val photoBytes: ByteArray,
    val timestamp: Long,
    val note: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PhotoRecord
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

@Composable
fun PhotoShoppingScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    val photoRecords = remember { mutableStateListOf<PhotoRecord>() }
    var showNoteDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        capturedPhoto = bitmap
        if (bitmap != null) {
            showNoteDialog = true
        }
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
                    painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFF2E7D32)
                )
            }
            Text(
                text = "拍照购物",
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
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📷",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "拍照识别商品",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "拍摄商品照片进行识别",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { cameraLauncher.launch(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "📷 打开相机拍照",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (capturedPhoto != null) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "最近拍摄",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Image(
                            bitmap = capturedPhoto!!.asImageBitmap(),
                            contentDescription = "拍摄的照片",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        Text(
                            text = "拍摄时间: ${timeFormat.format(Date(System.currentTimeMillis()))}",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE)
                        )
                    }
                }
            }

            if (photoRecords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "历史记录 (${photoRecords.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )

                Spacer(modifier = Modifier.height(8.dp))

                photoRecords.reversed().forEach { record ->
                    Spacer(modifier = Modifier.height(8.dp))
                    PhotoRecordCard(record = record)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showNoteDialog && capturedPhoto != null) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showNoteDialog = false
                capturedPhoto = null
            },
            title = {
                Text(
                    text = "添加备注",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("商品备注") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val outputStream = ByteArrayOutputStream()
                        capturedPhoto!!.compress(
                            android.graphics.Bitmap.CompressFormat.PNG,
                            100,
                            outputStream
                        )
                        val record = PhotoRecord(
                            id = "P${System.currentTimeMillis()}",
                            photoBytes = outputStream.toByteArray(),
                            timestamp = System.currentTimeMillis(),
                            note = noteText
                        )
                        photoRecords.add(record)
                        capturedPhoto = null
                        showNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showNoteDialog = false
                        capturedPhoto = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90A4AE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun PhotoRecordCard(record: PhotoRecord) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val bitmap = BitmapFactory.decodeByteArray(record.photoBytes, 0, record.photoBytes.size)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "商品照片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (record.note.isNotEmpty()) {
                        Text(
                            text = "备注: ${record.note}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = "🕐 ${timeFormat.format(Date(record.timestamp))}",
                        fontSize = 12.sp,
                        color = Color(0xFF90A4AE)
                    )
                }
            }
        }
    }
}
