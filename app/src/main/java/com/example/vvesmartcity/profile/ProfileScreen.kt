package com.example.vvesmartcity.profile

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.vvesmartcity.R
import com.example.vvesmartcity.auth.User
import com.example.vvesmartcity.ui.theme.SmartCityBlue
import com.example.vvesmartcity.ui.theme.SmartCityDarkBlue
import com.example.vvesmartcity.ui.theme.SmartCityLightBlue
import com.yalantis.ucrop.UCrop

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit, onAvatarChange: (Uri?) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFF5F7FA),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(user = user, onAvatarChange = onAvatarChange)
            Spacer(modifier = Modifier.height(32.dp))
            ProfileInfoCard(user = user, onLogout = onLogout)
        }
    }
}

@Composable
fun ProfileHeader(user: User?, onAvatarChange: (Uri?) -> Unit) {
    val context = LocalContext.current

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val resultUri = UCrop.getOutput(data)
                    resultUri?.let { onAvatarChange(it) }
                }
            }
            UCrop.RESULT_ERROR -> {
                result.data?.let { data ->
                    val error = UCrop.getError(data)
                    error?.printStackTrace()
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val destinationUri = Uri.fromFile(
                java.io.File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
            )
            val options = UCrop.Options().apply {
                setCircleDimmedLayer(true)
                setShowCropGrid(true)
                setShowCropFrame(true)
                setToolbarColor(android.graphics.Color.WHITE)
                setStatusBarColor(android.graphics.Color.WHITE)
                setToolbarWidgetColor(android.graphics.Color.parseColor("#1A237E"))
                setDimmedLayerColor(android.graphics.Color.parseColor("#99000000"))
                setCropGridColor(android.graphics.Color.WHITE)
                setCropFrameColor(android.graphics.Color.WHITE)
            }
            val uCropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(800, 800)
                .withOptions(options)
                .getIntent(context)
            uCropLauncher.launch(uCropIntent)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = SmartCityBlue.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SmartCityLightBlue, SmartCityDarkBlue)
                    ),
                    shape = CircleShape
                )
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (user?.avatarUri != null) {
                AsyncImage(
                    model = user.avatarUri,
                    contentDescription = "头像",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_account_box),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = user?.displayName ?: "管理员",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "点击头像更换",
                fontSize = 13.sp,
                color = Color(0xFF90A4AE)
            )
        }
    }
}

@Composable
fun ProfileInfoCard(user: User?, onLogout: () -> Unit) {
    val context = LocalContext.current
    val versionName = try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "v${packageInfo.versionName}"
    } catch (e: Exception) {
        "v1.0.0"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = "个人信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoRow("用户名", user?.username ?: "Admin")
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoRow("角色", user?.role ?: "系统管理员")
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoRow("城市", "智慧城市")
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoRow("版本", versionName)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF90A4AE)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF263238)
        )
    }
}
