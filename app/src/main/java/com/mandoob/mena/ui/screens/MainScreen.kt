package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.mandoob.mena.R
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.viewmodel.OrderViewModel

@Composable
fun MainScreen(viewModel: OrderViewModel) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()

    // Enforce Arabic RTL layout direction for the entire interface
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (isFirstLaunch) {
            OnboardingScreen(viewModel)
        } else {
            var currentTab by rememberSaveable { mutableStateOf(0) }
            val context = LocalContext.current
            val snackbarHostState = remember { SnackbarHostState() }
            val uiError by viewModel.uiError.collectAsState()

            androidx.compose.runtime.LaunchedEffect(uiError) {
                uiError?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearError()
                }
            }

            var showAddDialog by remember { mutableStateOf(false) }
            var showImportDialog by remember { mutableStateOf(false) }
            val commissionCat1 by viewModel.commissionCat1.collectAsState()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (currentTab) {
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
                                    address, amount, commissionCat1, notes?.ifBlank { null }
                                )
                                showAddDialog = false
                                Toast.makeText(context, context.getString(R.string.string_ar_57), Toast.LENGTH_SHORT).show()
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
                Text(stringResource(R.string.string_ar_55),
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = stringResource(R.string.string_ar_55)
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
                Text(stringResource(R.string.string_ar_56),
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.Navigation else Icons.Outlined.Navigation,
                    contentDescription = stringResource(R.string.string_ar_56)
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
                Text(stringResource(R.string.string_ar_20),
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.string_ar_20)
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
