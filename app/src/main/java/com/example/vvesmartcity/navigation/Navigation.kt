package com.example.vvesmartcity.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vvesmartcity.R
import com.example.vvesmartcity.auth.AuthViewModel
import com.example.vvesmartcity.auth.LoginScreen
import com.example.vvesmartcity.auth.SessionManager
import com.example.vvesmartcity.farm.AddEditRecordScreen
import com.example.vvesmartcity.farm.AllFarmRecordsScreen
import com.example.vvesmartcity.farm.FarmMainScreen
import com.example.vvesmartcity.farm.FarmSettingsScreen
import com.example.vvesmartcity.home.BottomNavigationBar
import com.example.vvesmartcity.home.SmartCityHomeScreen
import com.example.vvesmartcity.profile.ProfileScreen
import com.example.vvesmartcity.supermarket.AdminManageScreen
import com.example.vvesmartcity.supermarket.CartDataSource
import com.example.vvesmartcity.supermarket.CartScreen
import com.example.vvesmartcity.supermarket.CartViewModel
import com.example.vvesmartcity.supermarket.CustomerScanScreen
import com.example.vvesmartcity.supermarket.OrderManagementScreen
import com.example.vvesmartcity.supermarket.ProductViewModel
import com.example.vvesmartcity.supermarket.SupermarketMainScreen
import com.example.vvesmartcity.warning.AddEditWarningScreen
import com.example.vvesmartcity.warning.AllWarningsScreen
import com.example.vvesmartcity.warning.WarningMainScreen
import com.example.vvesmartcity.warning.WarningViewModel
import com.example.vvesmartcity.weather.WeatherScreen
import com.example.vvesmartcity.camera.CameraCaptureScreen

sealed class Screen(val route: String, val title: String, val icon: Int, val activeIcon: Int) {
    object Home : Screen("home", "主页", R.drawable.ic_home, R.drawable.ic_home)
    object Profile : Screen("profile", "我的", R.drawable.ic_account_box, R.drawable.ic_account_box)
}

sealed class AppPage {
    object Home : AppPage()
    object Profile : AppPage()
    object Weather : AppPage()
    object SupermarketMain : AppPage()
    object Cart : AppPage()
    object CustomerScan : AppPage()
    object AdminManage : AppPage()
    object OrderManagement : AppPage()
    object WarningMain : AppPage()
    object AllWarnings : AppPage()
    data class AddEditWarning(val warningId: String?) : AppPage()
    object FarmMain : AppPage()
    object AllFarmRecords : AppPage()
    data class AddEditRecord(val recordId: String?) : AppPage()
    object FarmSettings : AppPage()
    object ShoppingDatabase : AppPage()
    object CameraCapture : AppPage()
}

@Composable
fun SmartCityApp(
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.state.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var currentPage by remember { mutableStateOf<AppPage>(AppPage.Home) }
    var pageHistory by remember { mutableStateOf(listOf<AppPage>(AppPage.Home)) }

    LaunchedEffect(Unit) {
        authViewModel.checkSavedSession(context)
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

    if (!authState.isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { user ->
                SessionManager.saveLogin(context, user)
                CartDataSource.clearCart()
                authViewModel.checkSavedSession(context)
                selectedTab = 0
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
                userRole = authState.currentUser?.role ?: "用户",
                onBack = { goBack() },
                onCartClick = { navigateTo(AppPage.Cart) },
                onCustomerScan = { navigateTo(AppPage.CustomerScan) },
                onAdminManage = { navigateTo(AppPage.AdminManage) }
            )
        }
        is AppPage.Cart -> {
            CartScreen(
                onBack = { goBack() },
                onCheckoutSuccess = { goBack() }
            )
        }
        is AppPage.CustomerScan -> {
            CustomerScanScreen(
                onBack = { goBack() },
                onCartClick = { navigateTo(AppPage.Cart) }
            )
        }
        is AppPage.AdminManage -> {
            AdminManageScreen(
                onBack = { goBack() }
            )
        }
        is AppPage.OrderManagement -> {
            OrderManagementScreen(
                onBack = { goBack() }
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
        is AppPage.FarmSettings -> {
            FarmSettingsScreen(
                onBack = { goBack() }
            )
        }
        is AppPage.ShoppingDatabase -> {
            OrderManagementScreen(
                onBack = { goBack() }
            )
        }
        is AppPage.CameraCapture -> {
            CameraCaptureScreen(
                onBack = { goBack() }
            )
        }
        else -> {
            Scaffold(
                bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (selectedTab) {
                        0 -> SmartCityHomeScreen(onModuleClick = { page -> navigateTo(page) })
                        1 -> ProfileScreen(
                            user = authState.currentUser,
                            onLogout = {
                                authViewModel.logout(context)
                                CartDataSource.clearCart()
                                selectedTab = 0
                                currentPage = AppPage.Home
                                pageHistory = listOf(AppPage.Home)
                            },
                            onAvatarChange = { uri ->
                                authViewModel.updateAvatar(context, uri?.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}
