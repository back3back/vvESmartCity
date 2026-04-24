package com.example.vvesmartcity.farm

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FarmMainScreen(
    onBack: () -> Unit,
    onViewAll: () -> Unit
) {
    var sensorData by remember { mutableStateOf(FarmDataSource.getCurrentSensorData()) }
    var devices by remember { mutableStateOf(FarmDataSource.getDevices()) }
    var thresholds by remember { mutableStateOf(FarmDataSource.getThresholds()) }
    var showThresholdDialog by remember { mutableStateOf<SensorType?>(null) }
    var abnormalCount by remember { mutableStateOf(FarmDataSource.getAbnormalRecords().size) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFFFF9800)
                )
            }
            Text(
                text = "智能农场",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800),
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
                                colors = listOf(Color(0xFFFF9800), Color(0xFFFFA726))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌾",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "农场监控中心",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${abnormalCount} 条异常数据",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "传感器数据",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            sensorData.forEach { record ->
                Spacer(modifier = Modifier.height(8.dp))
                SensorCard(
                    record = record,
                    onThresholdClick = { showThresholdDialog = record.type }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "设备控制",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            devices.forEach { device ->
                Spacer(modifier = Modifier.height(8.dp))
                DeviceCard(
                    device = device,
                    onToggle = {
                        FarmDataSource.toggleDevice(device.id)
                        devices = FarmDataSource.getDevices()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewAll,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("全部信息", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    showThresholdDialog?.let { sensorType ->
        ThresholdDialog(
            sensorType = sensorType,
            currentThreshold = thresholds.find { it.sensorType == sensorType },
            onDismiss = { showThresholdDialog = null },
            onSave = { min, max ->
                FarmDataSource.updateThreshold(WarningThreshold(sensorType, min, max))
                thresholds = FarmDataSource.getThresholds()
                sensorData = FarmDataSource.getCurrentSensorData()
                abnormalCount = FarmDataSource.getAbnormalRecords().size
                showThresholdDialog = null
            }
        )
    }
}

@Composable
fun SensorCard(
    record: SensorRecord,
    onThresholdClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val statusColor = if (record.isNormal) Color(0xFF4CAF50) else Color(0xFFE53935)
    val statusText = if (record.isNormal) "正常" else "异常"

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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.type.icon,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.type.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${record.value} ${record.type.unit}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "范围: ${record.thresholdMin}-${record.thresholdMax} ${record.type.unit}",
                        fontSize = 11.sp,
                        color = Color(0xFF90A4AE)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "📍 ${record.location}",
                        fontSize = 12.sp,
                        color = Color(0xFF90A4AE)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "🕐 ${timeFormat.format(Date(record.timestamp))}",
                        fontSize = 12.sp,
                        color = Color(0xFF90A4AE)
                    )
                }
            }

            IconButton(onClick = onThresholdClick) {
                Text(
                    text = "⚙",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: FarmDevice,
    onToggle: () -> Unit
) {
    val statusColor = if (device.isOn) Color(0xFF4CAF50) else Color(0xFF90A4AE)

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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = device.type.icon,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "📍 ${device.location}",
                        fontSize = 12.sp,
                        color = Color(0xFF90A4AE)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (device.isOn) "运行中" else "已关闭",
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (device.isOn) Color(0xFFE53935) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (device.isOn) "关闭" else "开启",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ThresholdDialog(
    sensorType: SensorType,
    currentThreshold: WarningThreshold?,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var minValue by remember { mutableStateOf(currentThreshold?.min?.toString() ?: "") }
    var maxValue by remember { mutableStateOf(currentThreshold?.max?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "设置${sensorType.label}预警阈值",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = minValue,
                    onValueChange = { minValue = it },
                    label = { Text("最小值 (${sensorType.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = maxValue,
                    onValueChange = { maxValue = it },
                    label = { Text("最大值 (${sensorType.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val min = minValue.toDoubleOrNull() ?: 0.0
                    val max = maxValue.toDoubleOrNull() ?: 100.0
                    onSave(min, max)
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AllFarmRecordsScreen(
    onBack: () -> Unit,
    onAddRecord: () -> Unit,
    onEditRecord: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var records by remember { mutableStateOf(FarmDataSource.getAllRecords()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFFFF9800)
                )
            }
            Text(
                text = "全部信息",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddRecord) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_add),
                    contentDescription = "添加",
                    tint = Color(0xFFFF9800)
                )
            }
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
                        Text("搜索地点/类型...", fontSize = 14.sp, color = Color(0xFFB0BEC5))
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            records = FarmDataSource.searchRecords(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF263238))
                    )
                }
                Button(
                    onClick = {
                        records = FarmDataSource.searchRecords(searchQuery)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("查询", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "共 ${records.size} 条记录",
                fontSize = 14.sp,
                color = Color(0xFF78909C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(records) { record ->
                    RecordListItem(
                        record = record,
                        onEdit = { onEditRecord(record.id) },
                        onDelete = {
                            FarmDataSource.deleteRecord(record.id)
                            records = FarmDataSource.getAllRecords()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordListItem(
    record: SensorRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val statusColor = if (record.isNormal) Color(0xFF4CAF50) else Color(0xFFE53935)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = record.type.icon,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = record.type.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (record.isNormal) "正常" else "异常",
                                fontSize = 11.sp,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${record.value} ${record.type.unit}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "📍 ${record.location}",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "🕐 ${timeFormat.format(Date(record.timestamp))}",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("编辑", fontSize = 12.sp)
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("删除", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddEditRecordScreen(
    recordId: String?,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val editingRecord = recordId?.let { FarmDataSource.getAllRecords().find { r -> r.id == it } }
    var location by remember { mutableStateOf(editingRecord?.location ?: "") }
    var value by remember { mutableStateOf(editingRecord?.value?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(editingRecord?.type ?: SensorType.TEMPERATURE) }
    var thresholdMin by remember { mutableStateOf(editingRecord?.thresholdMin?.toString() ?: "") }
    var thresholdMax by remember { mutableStateOf(editingRecord?.thresholdMax?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFF5F7FA), Color(0xFFFFFFFF))
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
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "返回",
                    tint = Color(0xFFFF9800)
                )
            }
            Text(
                text = if (editingRecord == null) "添加记录" else "编辑记录",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800),
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "传感器类型",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SensorType.values().toList()) { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(0xFFFF9800) else Color(0xFFF5F5F5)
                                )
                                .clickable { selectedType = type }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${type.icon} ${type.label}",
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF263238),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("位置") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("数值 (${selectedType.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = thresholdMin,
                    onValueChange = { thresholdMin = it },
                    label = { Text("阈值最小值 (${selectedType.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = thresholdMax,
                    onValueChange = { thresholdMax = it },
                    label = { Text("阈值最大值 (${selectedType.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val v = value.toDoubleOrNull() ?: 0.0
                        val min = thresholdMin.toDoubleOrNull() ?: 0.0
                        val max = thresholdMax.toDoubleOrNull() ?: 100.0
                        if (editingRecord != null) {
                            FarmDataSource.updateRecord(
                                editingRecord.copy(
                                    type = selectedType,
                                    value = v,
                                    thresholdMin = min,
                                    thresholdMax = max,
                                    location = location
                                )
                            )
                        } else {
                            FarmDataSource.addRecord(
                                SensorRecord(
                                    id = "S${System.currentTimeMillis()}",
                                    type = selectedType,
                                    value = v,
                                    thresholdMin = min,
                                    thresholdMax = max,
                                    timestamp = System.currentTimeMillis(),
                                    location = location
                                )
                            )
                        }
                        onSaveSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("保存", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}}
