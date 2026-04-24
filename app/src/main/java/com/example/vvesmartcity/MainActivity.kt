package com.example.vvesmartcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vvesmartcity.ui.theme.SmartCityBlue
import com.example.vvesmartcity.ui.theme.SmartCityDarkBlue
import com.example.vvesmartcity.ui.theme.SmartCityLightBlue
import com.example.vvesmartcity.ui.theme.VvESmartCityTheme
import com.example.vvesmartcity.supermarket.AddEditProductScreen
import com.example.vvesmartcity.supermarket.ProductPurchaseScreen
import com.example.vvesmartcity.supermarket.SupermarketMainScreen
import com.example.vvesmartcity.supermarket.UnifiedScanShopScreen
import com.example.vvesmartcity.supermarket.VideoMonitorScreen
import com.example.vvesmartcity.auth.LoginScreen
import com.example.vvesmartcity.auth.SessionManager
import com.example.vvesmartcity.auth.User
import com.example.vvesmartcity.farm.AddEditRecordScreen
import com.example.vvesmartcity.farm.AllFarmRecordsScreen
import com.example.vvesmartcity.farm.FarmMainScreen
import com.example.vvesmartcity.warning.AddEditWarningScreen
import com.example.vvesmartcity.warning.AllWarningsScreen
import com.example.vvesmartcity.warning.WarningMainScreen
import com.example.vvesmartcity.weather.WeatherAdvice
import com.example.vvesmartcity.weather.WeatherDataSource
import com.example.vvesmartcity.weather.WeatherRecord
import kotlinx.coroutines.delay

sealed class Screen(val route: String, val title: String, val icon: Int, val activeIcon: Int) {
    object Home : Screen("home", "主页", R.drawable.ic_home, R.drawable.ic_home)
    object Profile : Screen("profile", "我的", R.drawable.ic_account_box, R.drawable.ic_account_box)
}

sealed class AppPage {
    object Home : AppPage()
    object Profile : AppPage()
    object Weather : AppPage()
    object SupermarketMain : AppPage()
    data class ProductPurchase(val productId: String) : AppPage()
    data class AddEditProduct(val productId: String?) : AppPage()
    object VideoMonitor : AppPage()
    object UnifiedScanShop : AppPage()
    object WarningMain : AppPage()
    object AllWarnings : AppPage()
    data class AddEditWarning(val warningId: String?) : AppPage()
    object FarmMain : AppPage()
    object AllFarmRecords : AppPage()
    data class AddEditRecord(val recordId: String?) : AppPage()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VvESmartCityTheme {
                SmartCityApp()
            }
        }
    }
}

@Composable
fun SmartCityApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var currentPage by remember { mutableStateOf<AppPage>(AppPage.Home) }
    var pageHistory by remember { mutableStateOf(listOf<AppPage>(AppPage.Home)) }

    LaunchedEffect(Unit) {
        val savedUser = SessionManager.getSavedUser(context)
        if (savedUser != null) {
            currentUser = savedUser
            isLoggedIn = true
        }
    }

    fun navigateTo(page: AppPage) {
        currentPage = page
        pageHistory = pageHistory + page
    }

    fun goBack() {
        if (pageHistory.size > 1) {
            pageHistory = pageHistory.dropLast(1)
            currentPage = pageHistory.last()
        }
    }

    BackHandler(enabled = currentPage !is AppPage.Home && currentPage !is AppPage.Profile) {
        goBack()
    }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { user ->
                SessionManager.saveLogin(context, user)
                currentUser = user
                isLoggedIn = true
                currentPage = AppPage.Home
                pageHistory = listOf(AppPage.Home)
            }
        )
        return
    }

    when (currentPage) {
        is AppPage.Weather -> {
            WeatherScreen(onBack = { goBack() })
        }
        is AppPage.SupermarketMain -> {
            SupermarketMainScreen(
                onBack = { goBack() },
                onProductClick = { productId -> navigateTo(AppPage.ProductPurchase(productId)) },
                onAddProduct = { navigateTo(AppPage.AddEditProduct(null)) },
                onUnifiedScanShop = { navigateTo(AppPage.UnifiedScanShop) },
                onVideoMonitor = { navigateTo(AppPage.VideoMonitor) }
            )
        }
        is AppPage.UnifiedScanShop -> {
            UnifiedScanShopScreen(
                onBack = { goBack() },
                onProductScanned = { productId -> navigateTo(AppPage.ProductPurchase(productId)) }
            )
        }
        is AppPage.WarningMain -> {
            WarningMainScreen(
                onBack = { goBack() },
                onViewAll = { navigateTo(AppPage.AllWarnings) }
            )
        }
        is AppPage.AllWarnings -> {
            AllWarningsScreen(
                onBack = { goBack() },
                onAddWarning = { navigateTo(AppPage.AddEditWarning(null)) },
                onEditWarning = { id -> navigateTo(AppPage.AddEditWarning(id)) }
            )
        }
        is AppPage.AddEditWarning -> {
            val warningPage = currentPage as AppPage.AddEditWarning
            AddEditWarningScreen(
                warningId = warningPage.warningId,
                onBack = { goBack() },
                onSaveSuccess = { goBack() }
            )
        }
        is AppPage.ProductPurchase -> {
            val purchasePage = currentPage as AppPage.ProductPurchase
            ProductPurchaseScreen(
                productId = purchasePage.productId,
                onBack = { goBack() },
                onPurchaseSuccess = { goBack() }
            )
        }
        is AppPage.AddEditProduct -> {
            val editPage = currentPage as AppPage.AddEditProduct
            AddEditProductScreen(
                productId = editPage.productId,
                onBack = { goBack() },
                onSaveSuccess = { goBack() }
            )
        }
        is AppPage.VideoMonitor -> {
            VideoMonitorScreen(onBack = { goBack() })
        }
        is AppPage.FarmMain -> {
            FarmMainScreen(
                onBack = { goBack() },
                onViewAll = { navigateTo(AppPage.AllFarmRecords) }
            )
        }
        is AppPage.AllFarmRecords -> {
            AllFarmRecordsScreen(
                onBack = { goBack() },
                onAddRecord = { navigateTo(AppPage.AddEditRecord(null)) },
                onEditRecord = { id -> navigateTo(AppPage.AddEditRecord(id)) }
            )
        }
        is AppPage.AddEditRecord -> {
            val recordPage = currentPage as AppPage.AddEditRecord
            AddEditRecordScreen(
                recordId = recordPage.recordId,
                onBack = { goBack() },
                onSaveSuccess = { goBack() }
            )
        }
        else -> {
            Scaffold(
                bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (selectedTab) {
                        0 -> SmartCityHomeScreen(onModuleClick = { page -> navigateTo(page) })
                        1 -> ProfileScreen(user = currentUser) {
                            SessionManager.clearSession(context)
                            currentUser = null
                            isLoggedIn = false
                            currentPage = AppPage.Home
                            pageHistory = listOf(AppPage.Home)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val screens = listOf(Screen.Home, Screen.Profile)
        screens.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            if (selectedIndex == index) screen.activeIcon else screen.icon
                        ),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title, fontSize = 12.sp) },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SmartCityBlue,
                    selectedTextColor = SmartCityBlue,
                    unselectedIconColor = Color(0xFF90A4AE),
                    unselectedTextColor = Color(0xFF90A4AE),
                    indicatorColor = Color(0xFFE3F2FD)
                )
            )
        }
    }
}

data class ModuleItem(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val iconColor: Color,
    val gradient: List<Color>,
    val pageType: AppPage? = null
)

@Composable
fun SmartCityHomeScreen(onModuleClick: (AppPage) -> Unit) {
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            ModuleGrid(onModuleClick)
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = SmartCityBlue.copy(alpha = 0.3f)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SmartCityLightBlue, SmartCityDarkBlue)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_city_logo),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "智能城市",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Smart City Management System",
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF78909C),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ModuleGrid(onModuleClick: (AppPage) -> Unit) {
    val modules = listOf(
        ModuleItem(
            title = "环境气象",
            subtitle = "Environment",
            icon = R.drawable.ic_weather,
            iconColor = Color(0xFF1E88E5),
            gradient = listOf(Color(0xFF1E88E5), Color(0xFF42A5F5)),
            pageType = AppPage.Weather
        ),
        ModuleItem(
            title = "智能商超",
            subtitle = "Supermarket",
            icon = R.drawable.ic_supermarket,
            iconColor = Color(0xFF43A047),
            gradient = listOf(Color(0xFF43A047), Color(0xFF66BB6A)),
            pageType = AppPage.SupermarketMain
        ),
        ModuleItem(
            title = "预警信息",
            subtitle = "Warning",
            icon = R.drawable.ic_warning,
            iconColor = Color(0xFFE53935),
            gradient = listOf(Color(0xFFE53935), Color(0xFFEF5350)),
            pageType = AppPage.WarningMain
        ),
        ModuleItem(
            title = "智能农场",
            subtitle = "Smart Farm",
            icon = R.drawable.ic_farm,
            iconColor = Color(0xFFFF9800),
            gradient = listOf(Color(0xFFFF9800), Color(0xFFFFA726)),
            pageType = AppPage.FarmMain
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(2.dp),
        userScrollEnabled = false
    ) {
        items(modules) { module ->
            ModuleCard(module) {
                module.pageType?.let { onModuleClick(it) }
            }
        }
    }
}

@Composable
fun ModuleCard(module: ModuleItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(110.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF8FAFC))
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(colors = module.gradient),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(module.icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = module.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = module.subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF90A4AE),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit) {
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(user = user)
            Spacer(modifier = Modifier.height(32.dp))
            ProfileInfoCard(user = user, onLogout = onLogout)
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
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
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SmartCityLightBlue, SmartCityDarkBlue)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_account_box),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.White
            )
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
                text = user?.username ?: "admin@smartcity.com",
                fontSize = 13.sp,
                color = Color(0xFF78909C)
            )
        }
    }
}

@Composable
fun ProfileInfoCard(user: User?, onLogout: () -> Unit) {
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
            ProfileInfoRow("版本", "v1.0.0")
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

@Composable
fun WeatherScreen(onBack: () -> Unit) {
    var currentWeather by remember { mutableStateOf(WeatherDataSource.getCurrentData()) }
    var selectedDays by remember { mutableIntStateOf(1) }
    var historyData by remember { mutableStateOf<List<WeatherRecord>>(emptyList()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentWeather = WeatherDataSource.getCurrentData()
            delay(5000)
        }
    }

    LaunchedEffect(selectedDays) {
        historyData = WeatherDataSource.getHistoryData(selectedDays)
    }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = "返回",
                        tint = Color(0xFF1A237E)
                    )
                }
                Text(
                    text = "环境气象",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CurrentWeatherCard(currentWeather)

            Spacer(modifier = Modifier.height(16.dp))

            AdviceSection(currentWeather)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "历史数据趋势",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263238)
            )

            Spacer(modifier = Modifier.height(8.dp))

            DaySelector(selectedDays) { selectedDays = it }

            Spacer(modifier = Modifier.height(12.dp))

            TemperatureHumidityChart(historyData)

            Spacer(modifier = Modifier.height(12.dp))

            HistoryList(historyData)
        }
    }
}

@Composable
fun CurrentWeatherCard(weather: WeatherRecord) {
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
                        colors = listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "当前环境数据",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f°C", weather.temperature),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "温度",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f%%", weather.humidity),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "湿度",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdviceSection(weather: WeatherRecord) {
    val clothing = WeatherAdvice.getClothingAdvice(weather.temperature)
    val carWash = WeatherAdvice.getCarWashAdvice(weather.humidity, weather.temperature)
    val travel = WeatherAdvice.getTravelAdvice(weather.temperature, weather.humidity)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AdviceCard("穿衣建议", clothing.first, clothing.second, Color(0xFF43A047))
        AdviceCard("洗车建议", carWash.first, carWash.second, Color(0xFF1E88E5))
        AdviceCard("出行建议", travel.first, travel.second, Color(0xFFFF9800))
    }
}

@Composable
fun AdviceCard(title: String, status: String, detail: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.first().toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = Color(0xFF90A4AE)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = status,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = detail,
                        fontSize = 12.sp,
                        color = Color(0xFF78909C)
                    )
                }
            }
        }
    }
}

@Composable
fun DaySelector(selectedDays: Int, onDaySelected: (Int) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listOf(1, 3, 5, 7)) { days ->
            Button(
                onClick = { onDaySelected(days) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDays == days) SmartCityBlue else Color(0xFFE3F2FD),
                    contentColor = if (selectedDays == days) Color.White else SmartCityBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("${days}天", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun TemperatureHumidityChart(data: List<WeatherRecord>) {
    if (data.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    fontSize = 14.sp,
                    color = Color(0xFF90A4AE)
                )
            }
        }
        return
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFE53935), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("温度", fontSize = 12.sp, color = Color(0xFF78909C))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF1E88E5), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("湿度", fontSize = 12.sp, color = Color(0xFF78909C))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val chartWidth = size.width
                val chartHeight = size.height
                val padding = 30f
                val topPadding = 10f
                val bottomPadding = 30f
                val drawWidth = chartWidth - padding * 2
                val drawHeight = chartHeight - topPadding - bottomPadding

                val minTemp = data.minOf { it.temperature }
                val maxTemp = data.maxOf { it.temperature }
                val minHumidity = data.minOf { it.humidity }
                val maxHumidity = data.maxOf { it.humidity }

                val tempRange = maxTemp - minTemp
                val humidityRange = maxHumidity - minHumidity

                val tempY = { temp: Float ->
                    topPadding + drawHeight - if (tempRange > 0) ((temp - minTemp) / tempRange) * drawHeight else drawHeight / 2
                }
                val humidityY = { humidity: Float ->
                    topPadding + drawHeight - if (humidityRange > 0) ((humidity - minHumidity) / humidityRange) * drawHeight else drawHeight / 2
                }

                for (i in 0 until 5) {
                    val y = topPadding + (drawHeight / 4) * i
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = androidx.compose.ui.geometry.Offset(padding, y),
                        end = androidx.compose.ui.geometry.Offset(chartWidth - padding, y),
                        strokeWidth = 1f
                    )
                }

                for (i in 0 until data.size - 1) {
                    val x1 = padding + (drawWidth / (data.size - 1)) * i
                    val x2 = padding + (drawWidth / (data.size - 1)) * (i + 1)

                    drawLine(
                        color = Color(0xFFE53935),
                        start = androidx.compose.ui.geometry.Offset(x1, tempY(data[i].temperature)),
                        end = androidx.compose.ui.geometry.Offset(x2, tempY(data[i + 1].temperature)),
                        strokeWidth = 2.5f
                    )

                    drawLine(
                        color = Color(0xFF1E88E5),
                        start = androidx.compose.ui.geometry.Offset(x1, humidityY(data[i].humidity)),
                        end = androidx.compose.ui.geometry.Offset(x2, humidityY(data[i + 1].humidity)),
                        strokeWidth = 2.5f
                    )
                }

                for (i in data.indices) {
                    val x = padding + (drawWidth / (data.size - 1)) * i

                    drawCircle(
                        color = Color(0xFFE53935),
                        radius = 4f,
                        center = androidx.compose.ui.geometry.Offset(x, tempY(data[i].temperature))
                    )

                    drawCircle(
                        color = Color(0xFF1E88E5),
                        radius = 4f,
                        center = androidx.compose.ui.geometry.Offset(x, humidityY(data[i].humidity))
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryList(data: List<WeatherRecord>) {
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
            if (data.isEmpty()) {
                Text(
                    text = "暂无数据",
                    fontSize = 14.sp,
                    color = Color(0xFF90A4AE),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(data.reversed()) { record ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = WeatherDataSource.formatTime(record.timestamp),
                                fontSize = 12.sp,
                                color = Color(0xFF78909C)
                            )
                            Text(
                                text = String.format("%.1f°C / %.1f%%", record.temperature, record.humidity),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF263238)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SmartCityHomePreview() {
    VvESmartCityTheme {
        SmartCityApp()
    }
}
