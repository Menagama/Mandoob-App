package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.viewmodel.OrderViewModel

@Composable
fun MainScreen(viewModel: OrderViewModel) {
    var currentTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    // Enforce Arabic RTL layout direction for the entire interface
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Animated container for switching between screens smoothly and with high performance
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        (fadeIn(animationSpec = androidx.compose.animation.core.tween(120)) +
                                scaleIn(initialScale = 0.98f, animationSpec = androidx.compose.animation.core.tween(120)))
                            .togetherWith(fadeOut(animationSpec = androidx.compose.animation.core.tween(120)))
                    },
                    label = "TabTransition"
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onOpenAddOrder = { showAddDialog = true },
                            onOpenImportExcel = { showImportDialog = true },
                            onOpenSettings = { currentTab = 2 }
                        )
                        1 -> ActiveRouteScreen(
                            viewModel = viewModel,
                            onOpenSettings = { currentTab = 2 }
                        )
                        2 -> SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentTab = 0 }
                        )
                    }
                }

                // Add Dialog Implementation
                if (showAddDialog) {
                    AddOrderDialog(
                        onDismiss = { showAddDialog = false },
                        onSave = { name, phone, phone2, address, amount, notes ->
                            viewModel.addNewOrder(
                                name, phone, phone2?.ifBlank { null },
                                address, amount, 0.0, notes?.ifBlank { null }
                            )
                            showAddDialog = false
                            Toast.makeText(context, "تم إضافة الأوردر بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Import Dialog Implementation
                if (showImportDialog) {
                    ImportExcelDialog(
                        viewModel = viewModel,
                        onDismiss = { showImportDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .shadow(16.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding(),
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            label = {
                Text(
                    "الرئيسية",
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "الرئيسية"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            label = {
                Text(
                    "خط السير",
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.Navigation else Icons.Outlined.Navigation,
                    contentDescription = "خط السير"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_route")
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            label = {
                Text(
                    "الإعدادات",
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "الإعدادات"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_settings")
        )
    }
}
