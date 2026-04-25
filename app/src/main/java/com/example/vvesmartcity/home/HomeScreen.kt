package com.example.vvesmartcity.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vvesmartcity.R
import com.example.vvesmartcity.navigation.AppPage
import com.example.vvesmartcity.navigation.Screen
import com.example.vvesmartcity.ui.theme.SmartCityBlue
import com.example.vvesmartcity.ui.theme.SmartCityDarkBlue
import com.example.vvesmartcity.ui.theme.SmartCityLightBlue

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
            .statusBarsPadding()
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
