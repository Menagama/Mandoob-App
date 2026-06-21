package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.Order
import com.example.ui.theme.BackgroundGray
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CancelledLight
import com.example.ui.theme.CancelledRed
import com.example.ui.theme.GreenLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.ProgressIndicator
import com.example.ui.theme.ProgressLight
import com.example.ui.theme.PurpleStar
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.OrderViewModel
import java.net.URLEncoder

@Composable
fun MainScreen(viewModel: OrderViewModel) {
    var currentTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // State dialogs
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
            containerColor = BackgroundGray
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Screen contents based on selected tab
                when (currentTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onOpenAddOrder = { showAddDialog = true },
                        onOpenImportExcel = { showImportDialog = true },
                        onOpenSettings = { currentTab = 4 }
                    )
                    1 -> ActiveRouteScreen(
                        viewModel = viewModel,
                        onOpenSettings = { currentTab = 4 }
                    )
                    2 -> SuccessfulOrdersScreen(
                        viewModel = viewModel,
                        onOpenSettings = { currentTab = 4 }
                    )
                    3 -> CancelledOrdersScreen(
                        viewModel = viewModel,
                        onOpenSettings = { currentTab = 4 }
                    )
                    4 -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentTab = 0 }
                    )
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

// Bottom Bar with 4 primary courier options
@Composable
fun BottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = SurfaceWhite,
        modifier = Modifier
            .shadow(16.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(SurfaceWhite)
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
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = BlueLight
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
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "خط السير"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = BlueLight
            ),
            modifier = Modifier.testTag("nav_route")
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            label = {
                Text(
                    "الناجحة",
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "الناجحة"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenSuccess,
                selectedTextColor = GreenSuccess,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = GreenLight
            ),
            modifier = Modifier.testTag("nav_success")
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            label = {
                Text(
                    "الملغاة",
                    fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 3) Icons.Filled.Cancel else Icons.Outlined.Cancel,
                    contentDescription = "الملغاة"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CancelledRed,
                selectedTextColor = CancelledRed,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = CancelledLight
            ),
            modifier = Modifier.testTag("nav_cancelled")
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            label = {
                Text(
                    "الإعدادات",
                    fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 4) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "الإعدادات"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = BlueLight
            ),
            modifier = Modifier.testTag("nav_settings")
        )
    }
}

// ---------------- HOME TAB SCREEN ----------------
@Composable
fun HomeScreen(
    viewModel: OrderViewModel,
    onOpenAddOrder: () -> Unit,
    onOpenImportExcel: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Collect from ViewModel
    val netRemittance by viewModel.netRemittanceToOffice.collectAsState()
    val walletCash by viewModel.totalCashInWallet.collectAsState()
    val commissions by viewModel.totalCommissions.collectAsState()
    val completedCount by viewModel.completedOrdersCount.collectAsState()
    val totalCount by viewModel.totalOrdersCount.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Spacing for status bar area
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 1. Header Zone (Captain info & settings cog)
            item {
                HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)
            }

            // 2. Net Remittance to Office ("صافي التوريد للمكتب")
            item {
                NetRemittanceCard(netRemittance = netRemittance)
            }

            // 3. Double small cards: In Wallet Cash ("في الحقيبة") and Total Commissions ("إجمالي عمولاتك")
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cash in Wallet (placed first so it shows on the Right under RTL)
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "في الحقيبة (كاش)",
                            value = "${walletCash.toInt()} ج.م",
                            subtext = "كاش تم تحصيله فعلياً",
                            icon = Icons.Default.Payments,
                            iconColor = GreenSuccess,
                            testTagPrefix = "wallet"
                        )
                    }

                    // Total Commissions (placed second so it shows on the Left under RTL)
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "إجمالي عمولاتك",
                            value = "${commissions.toInt()} ج.م",
                            subtext = "إجمالي عمولاتك المستحقة",
                            icon = Icons.Default.Star,
                            iconColor = PurpleStar,
                            testTagPrefix = "commissions"
                        )
                    }
                }
            }

            // 4. Interactive routing track card
            item {
                InteractiveRouteProgressCard(
                    completed = completedCount,
                    total = totalCount
                )
            }

            // Margin bottom to clear overlapping floating action buttons
            item { Spacer(modifier = Modifier.height(160.dp)) }
        }

        // Floating stacked action buttons matching the layout perfectly
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // First FAB: Add New Order
            Row(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(BlueDark)
                    .clickable { onOpenAddOrder() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "إضافة أوردر جديد",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Second FAB: Import from Excel
            Row(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B5E20)) // Dark green matching excel style
                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(16.dp))
                    .clickable { onOpenImportExcel() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "استيراد من إكسيل",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ---------------- CAPTAIN AVATAR COMPONENT ----------------
@Composable
fun CaptainAvatarView(avatar: String, modifier: Modifier = Modifier) {
    if (avatar != "default" && avatar != "delivery" && avatar.isNotEmpty() && avatar.startsWith("/")) {
        val bitmap = remember(avatar) {
            try {
                val file = java.io.File(avatar)
                if (file.exists()) {
                    android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                } else null
            } catch (e: Exception) {
                null
            }
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "صورة الكابتن",
                modifier = modifier
                    .clip(CircleShape)
                    .border(2.dp, BlueLight, CircleShape),
                contentScale = ContentScale.Crop
            )
            return
        }
    }

    when (avatar) {
        "delivery" -> {
            Image(
                painter = painterResource(id = R.drawable.img_delivery_icon),
                contentDescription = "صورة الكابتن",
                modifier = modifier
                    .clip(CircleShape)
                    .border(2.dp, BlueLight, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            Image(
                painter = painterResource(id = R.drawable.img_profile_avatar),
                contentDescription = "صورة الكابتن",
                modifier = modifier
                    .clip(CircleShape)
                    .border(2.dp, BlueLight, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ---------------- HEADER COMPONENT ----------------
@Composable
fun HeaderCard(viewModel: OrderViewModel, onOpenSettings: () -> Unit) {
    val captainName by viewModel.captainName.collectAsState()
    val captainAvatar by viewModel.captainAvatar.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Captain image representation (right end)
            CaptainAvatarView(
                avatar = captainAvatar,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text column (middle)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "أهلاً، $captainName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "الأحد، 21 يونيو 2026",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            // Settings gear icon (left end)
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(48.dp)
                    .background(BlueLight, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = BluePrimary
                )
            }
        }
    }
}

// ---------------- NET REMITTANCE CARD ----------------
@Composable
fun NetRemittanceCard(netRemittance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(BlueDark, BluePrimary)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bank/Building icon (left in RTL, right in UI)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0x20FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Numerical facts (right in RTL)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "صافي التوريد للمكتب",
                        color = Color(0xCEFFFFFF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${netRemittance.toInt()} ج.م",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "صافي المبالغ المستحقة للتسليم وتصفية الوردية",
                        color = Color(0x9EFFFFFF),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ---------------- SMALL STAT CARDS ----------------
@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    testTagPrefix: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .testTag("${testTagPrefix}_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = value,
                color = TextDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = subtext,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

// ---------------- INTERACTIVE ROUTING CARD ----------------
@Composable
fun InteractiveRouteProgressCard(completed: Int, total: Int) {
    // Determine dynamic cheering message
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f
    
    val (cheerTitle, emoji) = when {
        total == 0 -> "لا يوجد أوردرات اليوم" to "📦"
        progress == 0f -> "بالتوفيق يا بطل!" to "📦"
        progress > 0f && progress < 0.5f -> "بداية ممتازة يا بطل! استمر" to "💪"
        progress >= 0.5f && progress < 1.0f -> "عديت النص، عاش يا وحش!" to "🚀"
        else -> "الله ينور عليك يا كابتن! كله تمام" to "🎉"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFC0F0E9), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Mint light turquoise
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$cheerTitle $emoji",
                        color = TextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "خلصت ($completed من $total) من مهماتك النهاردة",
                    fontSize = 14.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (total > 0) progress else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ProgressIndicator,
                    trackColor = Color(0x3E00ACC1)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Delivery boy/box circle indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(ProgressIndicator, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ---------------- ACTIVE ROUTE SCREEN (TAB 1) ----------------
@Composable
fun ActiveRouteScreen(viewModel: OrderViewModel, onOpenSettings: () -> Unit) {
    val allOrders by viewModel.allOrders.collectAsState()
    val isSortingEnabled by viewModel.isSortingEnabled.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    var editingOrder by remember { mutableStateOf<Order?>(null) }

    // Filter by pending/working orders first for active route worksheet, matching "جاري العمل"
    val activeOrders = allOrders.filter { it.status == Order.STATUS_PENDING }

    // Apply search query and automatic routing sequence
    val filteredAndSortedOrders = remember(activeOrders, searchQuery, isSortingEnabled) {
        val filtered = activeOrders.filter { order ->
            if (searchQuery.isBlank()) true
            else {
                order.clientName.contains(searchQuery, ignoreCase = true) ||
                order.phoneNumber.contains(searchQuery)
            }
        }
        if (isSortingEnabled) {
            // Sort to optimize geographic sequence (e.g. by address segment)
            filtered.sortedWith(compareBy({ it.address }, { it.id }))
        } else {
            filtered.sortedBy { it.id }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Header Card with profile context info and settings cog (STAYS STATIC/FIXED)
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..." (STAYS STATIC/FIXED)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("بحث باسم العميل أو رقم الهاتف...", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f)) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Cancel, // Close button when search has text, otherwise nothing or magnifying
                    contentDescription = "مسح البحث",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(enabled = searchQuery.isNotEmpty()) {
                            viewModel.setSearchQuery("")
                        }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                focusedLabelColor = BluePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count and Route Sorting Sequence Toggle Row (STAYS STATIC/FIXED)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Route count label
            Text(
                text = "أوردرات خط السير الحالية (${filteredAndSortedOrders.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            // Sequence lock/unlock toggle button "ترتيب خط السير"
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSortingEnabled) BlueLight else Color(0xFFF1F5F9))
                    .border(
                        1.dp,
                        if (isSortingEnabled) BluePrimary else Color(0xFFCBD5E1),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { viewModel.toggleSorting() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ترتيب خط السير",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSortingEnabled) BluePrimary else TextDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSortingEnabled) "🔒" else "🔓",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Scrollable list of orders (ONLY this part scrolls!)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredAndSortedOrders.isEmpty()) {
                item {
                    EmptyStateView(
                        if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة لبحثك!"
                        else "خط السير اليومي فارغ حالياً! اضغط الرئيسية لإضافة مهام."
                    )
                }
            } else {
                items(filteredAndSortedOrders.size) { i ->
                    val order = filteredAndSortedOrders[i]
                    ActiveRouteCard(
                        order = order,
                        index = i + 1,
                        onEditClick = { editingOrder = order },
                        onDelete = {
                            viewModel.deleteOrder(order.id)
                            Toast.makeText(context, "تم مسح الأوردر", Toast.LENGTH_SHORT).show()
                        },
                        onStatusChanged = { status, collected, fee, isQuiet ->
                            viewModel.updateOrderStatusWithValues(
                                orderId = order.id,
                                status = status,
                                collectedAmount = collected,
                                deliveryFeeAmount = fee
                            )
                            if (!isQuiet) {
                                Toast.makeText(context, "تم حفظ حالة الأوردر: $status", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(130.dp)) }
        }
    }

    // Modal Edit Dialog popup
    editingOrder?.let { order ->
        EditOrderDialog(
            order = order,
            onDismiss = { editingOrder = null },
            onSave = { name, phone, phone2, address, amount, notes ->
                viewModel.updateOrderDetails(
                    orderId = order.id,
                    clientName = name,
                    phoneNumber = phone,
                    phoneNumber2 = phone2,
                    address = address,
                    amount = amount,
                    commission = order.commission,
                    notes = notes
                )
                editingOrder = null
                Toast.makeText(context, "تم تعديل بيانات الأوردر بنجاح!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// Helper sharing functions for WhatsApp
fun shareOrderToWhatsApp(context: Context, order: Order) {
    val statusLabel = when (order.status) {
        Order.STATUS_PENDING -> "جاري العمل"
        Order.STATUS_DELIVERED -> "تم التسليم"
        Order.STATUS_PARTIAL -> "التسليم الجزئى"
        Order.STATUS_REJECTED_NO_FEE -> "رفض بدون مصاريف شحن"
        Order.STATUS_REJECTED_WITH_FEE -> "رفض ودفع مصاريف شحن"
        Order.STATUS_NO_ANSWER -> "لا يرد"
        Order.STATUS_POSTPONED -> "مؤجل"
        Order.STATUS_CANCELLED -> "لاغى"
        else -> order.status
    }

    val extraInfo = when (order.status) {
        Order.STATUS_PARTIAL -> " (قيمة التحصيل: ${order.collectedAmount?.toInt() ?: 0} ج.م)"
        Order.STATUS_REJECTED_WITH_FEE -> " (مصاريف الشحن: ${order.deliveryFeeAmount?.toInt() ?: 0} ج.م)"
        else -> ""
    }

    val text = """
        العميل: ${order.clientName}
        الهاتف: ${order.phoneNumber}
        العنوان: ${order.address}
        القيمة: ${order.amount.toInt()} ج.م
        الحالة: $statusLabel$extraInfo
    """.trimIndent()
    
    // Copy to clipboard
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("تفاصيل أوردر شحن بوسطة", text)
        clipboard.setPrimaryClip(clip)
    } catch (e: Exception) {}

    // Send WhatsApp explicit share chooser
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            setPackage("com.whatsapp")
        }
        context.startActivity(sendIntent)
    } catch (ex: Exception) {
        try {
            val shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }, "مشاركة تفاصيل الأوردر")
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "لم نتمكن من مشاركة التفاصيل", Toast.LENGTH_SHORT).show()
        }
    }
}

// Prefilled Quick templates
fun sendQuickWhatsAppMessage(context: Context, order: Order) {
    val message = "مرحبا ${order.clientName} مندوب شركه الشحن يتواصل معك لايصال شحنتك بقيمه ${order.amount} ج.م"
    try {
        var formatted = order.phoneNumber.filter { it.isDigit() }
        if (formatted.startsWith("0")) {
            formatted = "2" + formatted
        }
        val url = "https://wa.me/$formatted?text=${URLEncoder.encode(message, "UTF-8")}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "لم نتمكن من تشغيل تطبيق الواتس آب", Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickSMSMessage(context: Context, order: Order) {
    val message = "مرحبا ${order.clientName} مندوب شركه الشحن يتواصل معك لايصال شحنتك بقيمه ${order.amount} ج.م"
    try {
        val uri = Uri.parse("smsto:${order.phoneNumber}")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "لم نتمكن من فتح تطبيق رسائل الـ SMS", Toast.LENGTH_SHORT).show()
    }
}

// ---------------- CUSTOM ACTIVE ROUTE CARD COMPONENT (FAITHFUL TO ATTACHED DESIGN) ----------------
@Composable
fun ActiveRouteCard(
    order: Order,
    index: Int,
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onStatusChanged: (String, Double?, Double?, Boolean) -> Unit
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    var showPartialInput by remember { mutableStateOf(false) }
    var showFeeInput by remember { mutableStateOf(false) }
    
    // Hold temp inputs for amounts
    var partialAmountText by remember { mutableStateOf(order.amount.toString()) }
    var feeAmountText by remember { mutableStateOf("25") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First Row: Name/Phone, Avatar, Index, Delete, Share (proper RTL alignment matching design)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right: Name, phone, Avatarbubble (appears on the Right in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.clientName.firstOrNull()?.toString() ?: "",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = order.clientName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Right,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = order.phoneNumber,
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Phone,
                                contentDescription = null,
                                tint = GreenSuccess,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Left: Share, Trash, badge (appears on the Left in RTL layout)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Share button (appears on the Right of this row)
                    IconButton(
                        onClick = { shareOrderToWhatsApp(context, order) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Share,
                            contentDescription = "مشاركة الأوردر",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. Trash (Delete) button (appears in the Middle)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "تمسح",
                            tint = CancelledRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 3. Order number sequence badge (appears on the Left)
                    Box(
                        modifier = Modifier
                            .background(BlueLight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#$index",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Second Row: Detailed Address with Pin Icon (Icon is placed first/Right in RTL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.address,
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Third Row: Collected cash display, and Edit pencil button (Column/Cash on Right in RTL, Edit Button on Left)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cash details column (appears on the Right in RTL)
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "المطلوب تحصيله كاش",
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "${order.amount.toInt()} ج.م",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (order.status == Order.STATUS_PARTIAL) {
                        Text(
                            text = "التسليم الجزئى (${order.collectedAmount?.toInt() ?: 0} ج.م)",
                            color = BluePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    } else if (order.status == Order.STATUS_REJECTED_WITH_FEE) {
                        Text(
                            text = "رفض ودفع مصاريف شحن (${order.deliveryFeeAmount?.toInt() ?: 0} ج.م)",
                            color = CancelledRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Edit pencil button (appears on the Left in RTL)
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "تعديل بيانات الأوردر",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fourth Row: Three quick communication keys, and Status selection menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right: SMS, WhatsApp quick text, and Dialer phone keys (appears on the Right in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Dialer Button (on the Right) - Light green squircle
                    IconButton(
                        onClick = { launchDialer(context, order.phoneNumber) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFE8F8F5), RoundedCornerShape(14.dp)) // Pale green
                            .border(1.5.dp, Color(0xFFA7E8D4), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Phone,
                            contentDescription = "اتصال سريع للعميل",
                            tint = Color(0xFF0EA371),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Quick Message (SMS) (in the Middle) - Light blue squircle
                    IconButton(
                        onClick = { sendQuickSMSMessage(context, order) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFEBF5FF), RoundedCornerShape(14.dp)) // Pale blue
                            .border(1.5.dp, Color(0xFFC0E0FF), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = Color(0xFF0084FF),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 3. WhatsApp Quick Message (on the Left) - Light green squircle (WhatsApp)
                    IconButton(
                        onClick = { sendQuickWhatsAppMessage(context, order) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFEBFDF5), RoundedCornerShape(14.dp)) // Pale green WhatsApp
                            .border(1.5.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Left: Dropdown state selector (appears on the Left in RTL)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (order.status) {
                                    Order.STATUS_PENDING -> BlueLight
                                    Order.STATUS_DELIVERED -> GreenLight
                                    Order.STATUS_CANCELLED -> CancelledLight
                                    else -> Color(0xFFF1F5F9)
                                }
                            )
                            .border(
                                1.5.dp,
                                when (order.status) {
                                    Order.STATUS_PENDING -> BluePrimary
                                    Order.STATUS_DELIVERED -> GreenSuccess
                                    Order.STATUS_CANCELLED -> CancelledRed
                                    else -> Color(0xFFCBD5E1)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { showDropdown = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "● ${order.status}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        val statuses = listOf(
                            Order.STATUS_PENDING,
                            Order.STATUS_DELIVERED,
                            Order.STATUS_PARTIAL,
                            Order.STATUS_REJECTED_NO_FEE,
                            Order.STATUS_REJECTED_WITH_FEE,
                            Order.STATUS_NO_ANSWER,
                            Order.STATUS_POSTPONED,
                            Order.STATUS_CANCELLED
                        )
                        statuses.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp) },
                                onClick = {
                                    showDropdown = false
                                    if (mode == Order.STATUS_PARTIAL) {
                                        showPartialInput = true
                                        showFeeInput = false
                                        // Update instantly with existing partial amount value
                                        val valAmt = partialAmountText.toDoubleOrNull() ?: 0.0
                                        onStatusChanged(Order.STATUS_PARTIAL, valAmt, null, false)
                                    } else if (mode == Order.STATUS_REJECTED_WITH_FEE) {
                                        showFeeInput = true
                                        showPartialInput = false
                                        // Update instantly with existing fee amount value
                                        val valAmt = feeAmountText.toDoubleOrNull() ?: 25.0
                                        onStatusChanged(Order.STATUS_REJECTED_WITH_FEE, null, valAmt, false)
                                    } else {
                                        showPartialInput = false
                                        showFeeInput = false
                                        onStatusChanged(mode, null, null, false)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Expandable inline input field for "التسليم الجزئى"
            if (showPartialInput) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = partialAmountText,
                        onValueChange = {
                            partialAmountText = it
                            val valAmt = it.toDoubleOrNull() ?: 0.0
                            // Instant background save without Toast spams as they type
                            onStatusChanged(Order.STATUS_PARTIAL, valAmt, null, true)
                        },
                        label = { Text("قيمة التحصيل", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val valAmt = partialAmountText.toDoubleOrNull() ?: 0.0
                            onStatusChanged(Order.STATUS_PARTIAL, valAmt, null, false) // Save officially with Toast
                            showPartialInput = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("موافق", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Expandable inline input field for "رفض ودفع مصاريف شحن"
            if (showFeeInput) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = feeAmountText,
                        onValueChange = {
                            feeAmountText = it
                            val valAmt = it.toDoubleOrNull() ?: 25.0
                            // Instant background save without Toast spams as they type
                            onStatusChanged(Order.STATUS_REJECTED_WITH_FEE, null, valAmt, true)
                        },
                        label = { Text("مصاريف الشحن", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = CancelledRed,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val valAmt = feeAmountText.toDoubleOrNull() ?: 25.0
                            onStatusChanged(Order.STATUS_REJECTED_WITH_FEE, null, valAmt, false) // Save officially with Toast
                            showFeeInput = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CancelledRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("موافق", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ---------------- SUCCESSFUL ORDERS SCREEN (TAB 2) ----------------
@Composable
fun SuccessfulOrdersScreen(viewModel: OrderViewModel, onOpenSettings: () -> Unit) {
    val allOrders by viewModel.allOrders.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var editingOrder by remember { mutableStateOf<Order?>(null) }

    val successfulOrders = allOrders.filter { it.status == Order.STATUS_DELIVERED || it.status == Order.STATUS_PARTIAL }

    val filteredOrders = remember(successfulOrders, searchQuery) {
        successfulOrders.filter { order ->
            if (searchQuery.isBlank()) true
            else {
                order.clientName.contains(searchQuery, ignoreCase = true) ||
                order.phoneNumber.contains(searchQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Header Card with profile context info and settings cog (STAYS STATIC/FIXED)
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..." (STAYS STATIC/FIXED)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث باسم العميل أو رقم الهاتف...", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f)) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Cancel,
                    contentDescription = "مسح البحث",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(enabled = searchQuery.isNotEmpty()) {
                            searchQuery = ""
                        }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                focusedLabelColor = BluePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count (STAYS STATIC/FIXED)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأوردرات الناجحة والمكتملة (${filteredOrders.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Scrollable list of orders (ONLY this part scrolls!)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredOrders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Custom Check icon in green circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(Color(0xFFE8F8F5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF0EA371),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "لا توجد أوردرات ناجحة مسجلة حالياً",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "عندما تقوم بتوصيل الأوردر وتحديث حالته إلى (تم التسليم)، سيظهر إنجازك هنا.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(filteredOrders.size) { i ->
                    val order = filteredOrders[i]
                    ActiveRouteCard(
                        order = order,
                        index = i + 1,
                        onEditClick = { editingOrder = order },
                        onDelete = {
                            viewModel.deleteOrder(order.id)
                            Toast.makeText(context, "تم مسح الأوردر", Toast.LENGTH_SHORT).show()
                        },
                        onStatusChanged = { status, collected, fee, isQuiet ->
                            viewModel.updateOrderStatusWithValues(
                                orderId = order.id,
                                status = status,
                                collectedAmount = collected,
                                deliveryFeeAmount = fee
                            )
                            if (!isQuiet) {
                                Toast.makeText(context, "تم حفظ حالة الأوردر: $status", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(130.dp)) }
        }
    }

    // Modal Edit Dialog popup
    editingOrder?.let { order ->
        EditOrderDialog(
            order = order,
            onDismiss = { editingOrder = null },
            onSave = { name, phone, phone2, address, amount, notes ->
                viewModel.updateOrderDetails(
                    orderId = order.id,
                    clientName = name,
                    phoneNumber = phone,
                    phoneNumber2 = phone2,
                    address = address,
                    amount = amount,
                    commission = order.commission,
                    notes = notes
                )
                editingOrder = null
                Toast.makeText(context, "تم تعديل بيانات الأوردر بنجاح!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ---------------- CANCELLED ORDERS SCREEN (TAB 3) ----------------
@Composable
fun CancelledOrdersScreen(viewModel: OrderViewModel, onOpenSettings: () -> Unit) {
    val allOrders by viewModel.allOrders.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var editingOrder by remember { mutableStateOf<Order?>(null) }

    val cancelledOrders = allOrders.filter {
        it.status == Order.STATUS_CANCELLED || 
        it.status == Order.STATUS_REJECTED_NO_FEE || 
        it.status == Order.STATUS_REJECTED_WITH_FEE || 
        it.status == Order.STATUS_NO_ANSWER || 
        it.status == Order.STATUS_POSTPONED
    }

    val filteredOrders = remember(cancelledOrders, searchQuery) {
        cancelledOrders.filter { order ->
            if (searchQuery.isBlank()) true
            else {
                order.clientName.contains(searchQuery, ignoreCase = true) ||
                order.phoneNumber.contains(searchQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Header Card with profile context info and settings cog (STAYS STATIC/FIXED)
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..." (STAYS STATIC/FIXED)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث باسم العميل أو رقم الهاتف...", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f)) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Cancel,
                    contentDescription = "مسح البحث",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(enabled = searchQuery.isNotEmpty()) {
                            searchQuery = ""
                        }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.6f),
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                focusedLabelColor = BluePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count (STAYS STATIC/FIXED)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأوردرات الملغاة والمؤجلة (${filteredOrders.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Scrollable list of orders (ONLY this part scrolls!)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredOrders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Custom Close icon in dark gray circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "لا توجد أوردرات ملغاة أو مؤجلة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "كل شيء يسير على ما يرام! لا توجد طلبات معلقة بانتظار الرد، مؤجلة أو ملغاة حالياً.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(filteredOrders.size) { i ->
                    val order = filteredOrders[i]
                    ActiveRouteCard(
                        order = order,
                        index = i + 1,
                        onEditClick = { editingOrder = order },
                        onDelete = {
                            viewModel.deleteOrder(order.id)
                            Toast.makeText(context, "تم مسح الأوردر", Toast.LENGTH_SHORT).show()
                        },
                        onStatusChanged = { status, collected, fee, isQuiet ->
                            viewModel.updateOrderStatusWithValues(
                                orderId = order.id,
                                status = status,
                                collectedAmount = collected,
                                deliveryFeeAmount = fee
                            )
                            if (!isQuiet) {
                                Toast.makeText(context, "تم حفظ حالة الأوردر: $status", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(130.dp)) }
        }
    }

    // Modal Edit Dialog popup
    editingOrder?.let { order ->
        EditOrderDialog(
            order = order,
            onDismiss = { editingOrder = null },
            onSave = { name, phone, phone2, address, amount, notes ->
                viewModel.updateOrderDetails(
                    orderId = order.id,
                    clientName = name,
                    phoneNumber = phone,
                    phoneNumber2 = phone2,
                    address = address,
                    amount = amount,
                    commission = order.commission,
                    notes = notes
                )
                editingOrder = null
                Toast.makeText(context, "تم تعديل بيانات الأوردر بنجاح!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ---------------- CUSTOM ORDER ITEM CARD (STANDARD GENERIC REUSABLE) ----------------
@Composable
fun OrderCard(
    order: Order,
    onMarkDelivered: (() -> Unit)? = null,
    onMarkCancelled: (() -> Unit)? = null,
    onRevertToPending: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Raw Header Name + Action delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                when (order.status) {
                                    Order.STATUS_PENDING -> BluePrimary
                                    Order.STATUS_DELIVERED -> GreenSuccess
                                    else -> CancelledRed
                                }, CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.clientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "تمسح",
                        tint = TextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.address,
                    fontSize = 13.sp,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dual phone tags with quick click action handlers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Phone
                PhoneTag(
                    phone = order.phoneNumber,
                    label = "اتصال",
                    onClick = { launchDialer(context, order.phoneNumber) },
                    onWhatsApp = { launchWhatsApp(context, order.phoneNumber) }
                )

                // Optional Alt Phone
                if (!order.phoneNumber2.isNullOrBlank()) {
                    PhoneTag(
                        phone = order.phoneNumber2,
                        label = "بديل",
                        onClick = { launchDialer(context, order.phoneNumber2) },
                        onWhatsApp = { launchWhatsApp(context, order.phoneNumber2) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cash and Commissions Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("المبلغ المطلوب تحصيله", fontSize = 11.sp, color = TextMuted)
                    val amountText = when {
                        order.amount > 0 -> "+${order.amount.toInt()} ج.م"
                        order.amount < 0 -> "${order.amount.toInt()} ج.م مرتجع"
                        else -> "شحن مجاني / بدون تحصيل"
                    }
                    Text(
                        text = amountText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("عمولتك", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${order.commission.toInt()} ج.م",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            if (!order.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ملاحظات: ${order.notes}",
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick State toggles below item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (order.status == Order.STATUS_PENDING) {
                    OutlinedButton(
                        onClick = onMarkCancelled ?: {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CancelledRed),
                        border = BorderStroke(1.dp, CancelledRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("إلغاء", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onMarkDelivered ?: {},
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("تم التسليم", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    OutlinedButton(
                        onClick = onRevertToPending ?: {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary),
                        border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إرجاع لخط السير", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Phone selector tag to dial directly or load Whatsapp
@Composable
fun PhoneTag(
    phone: String,
    label: String,
    onClick: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(BlueLight, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: $phone",
            fontSize = 11.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClick() }
        )
        Spacer(modifier = Modifier.width(6.dp))
        // Small dial button
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier
                .size(14.dp)
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.width(6.dp))
        // Custom interactive Whatsapp element
        Text(
            text = "واتساب 💬",
            fontSize = 9.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onWhatsApp() }
                .border(0.5.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// ---------------- DIAL AND WHATSAPP UTIL LAUNCHERS ----------------
fun launchDialer(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "لم نتمكن من فتح طلب الاتصال", Toast.LENGTH_SHORT).show()
    }
}

fun launchWhatsApp(context: Context, phoneNumber: String) {
    try {
        // Clean local phone number
        var formatted = phoneNumber.filter { it.isDigit() }
        if (formatted.startsWith("0")) {
            formatted = "2" + formatted
        }
        val url = "https://wa.me/$formatted"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "لم نتمكن من تشغيل تطبيق الواتس آب", Toast.LENGTH_SHORT).show()
    }
}

// Re-usable BorderStroke for material buttons since we avoid imports clutter
@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = 
    androidx.compose.foundation.BorderStroke(width, color)

// ---------------- ADD NEW ORDER DIALOG ----------------
@Composable
fun AddOrderDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, phone2: String?, address: String, amount: Double, notes: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "إضافة أوردر جديد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Client Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل") },
                        modifier = Modifier.fillMaxWidth().testTag("add_name_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Phone numbers
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم تليفون العميل (أساسي)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_phone_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        label = { Text("رقم تليفون ثانى (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Address Input
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان بالتفصيل") },
                        modifier = Modifier.fillMaxWidth().testTag("add_address_field"),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Collection Amount input (always visible, accepts any amount, whether positive, zero, or negative)
                item {
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("مبلغ التحصيل (ج.م)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_amount_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Notes Input
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("الملاحظات") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Actions Button Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("إلغاء", color = CancelledRed)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                                    return@Button
                                }
                                val amt = amountValue.toDoubleOrNull() ?: 0.0

                                onSave(name, phone, phone2, address, amt, notes)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("حفظ الأوردر", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- EDIT EXISTING ORDER DIALOG ----------------
@Composable
fun EditOrderDialog(
    order: Order,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, phone2: String?, address: String, amount: Double, notes: String?) -> Unit
) {
    var name by remember { mutableStateOf(order.clientName) }
    var phone by remember { mutableStateOf(order.phoneNumber) }
    var phone2 by remember { mutableStateOf(order.phoneNumber2 ?: "") }
    var address by remember { mutableStateOf(order.address) }
    var amountValue by remember { mutableStateOf(order.amount.toString()) }
    var notes by remember { mutableStateOf(order.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "تعديل بيانات الأوردر",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Client Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Phone numbers
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم تليفون العميل (أساسي)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        label = { Text("رقم تليفون ثانى (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Address Input
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان بالتفصيل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Collection Amount input (always visible, accepts any amount, whether positive, zero, or negative)
                item {
                    OutlinedTextField(
                        value = amountValue,
                        onValueChange = { amountValue = it },
                        label = { Text("مبلغ التحصيل (ج.م)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Notes Input
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("الملاحظات") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                }

                // Actions Button Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("إلغاء", color = CancelledRed)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                                    return@Button
                                }
                                val amt = amountValue.toDoubleOrNull() ?: 0.0

                                onSave(name, phone, phone2.ifBlank { null }, address, amt, notes.ifBlank { null })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تعديل وحفظ الأوردر", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- EXCEL IMPORT SHEET DIALOG ----------------
@Composable
fun ImportExcelDialog(
    viewModel: OrderViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pastedText by remember { mutableStateOf("") }
    var tabIndex by remember { mutableStateOf(0) } // 0 -> pasted text, 1 -> pick file

    // Launcher file pick
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val text = inputStream.bufferedReader().use { it.readText() }
                    val count = viewModel.importOrdersFromCsv(text)
                    Toast.makeText(context, "تم استيراد $count أوردر بنجاح! 🎉", Toast.LENGTH_LONG).show()
                    onDismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في قراءة ملف الإكسيل العشوائي", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Default excel-paste template
    val demoTemplate = """اسم العميل,رقم الموبيل,عنوان العميل,قيمة التحصيل
مصطفى على جابر,0129887766,شارع النزهة مصر الجديدة,400
عبد الرحمن العوضي,113456789,ميدان الجيزة بجانب المحكمة,0
سليمان رزق مطر,0159012454,شارع جمال عبد الناصر الإسكندرية,-150"""

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "استيراد أوردرات من إكسيل",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Subtitle Instruction
                Text(
                    text = "يجب أن يحتوي شيت الإكسل (CSV) بالترتيب على:\nالعمود الأول: الاسم، ثم المحمول (١١ رقم)، ثم العنوان، ثم قيمة التحصيل.",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dialog Tabs View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundGray, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabIndex == 0) Color(0xFF1B5E20) else Color.Transparent)
                            .clickable { tabIndex = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "نسخ ولصق ورقة",
                            color = if (tabIndex == 0) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabIndex == 1) Color(0xFF1B5E20) else Color.Transparent)
                            .clickable { tabIndex = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "اختيار ملف CSV",
                            color = if (tabIndex == 1) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (tabIndex == 0) {
                    // Paste Area
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("انسخ جدول بياناتك أو الصقه هنا مباشرة:", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = pastedText,
                            onValueChange = { pastedText = it },
                            placeholder = { Text("مثال:\n$demoTemplate", color = Color.Black.copy(alpha = 0.5f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            singleLine = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF1B5E20),
                                unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                                focusedPlaceholderColor = Color.Black.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.5f)
                            )
                        )

                        // 1-tap Demo Loader
                        Text(
                            text = "💡 اضغط هنا لملء نموذج تجريبي جاهز وتجربة الاستيراد فوراً!",
                            fontSize = 11.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { pastedText = demoTemplate }
                                .padding(vertical = 4.dp)
                        )
                    }
                } else {
                    // Choose File Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("text/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصفح ملفات الهاتف (CSV)", color = Color.White)
                        }

                        Text(
                            "يدعم ملفات excel المصدرة بصيغ CSV النصية الشهيرة.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Confirm Actions bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = CancelledRed)
                    }
                    if (tabIndex == 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (pastedText.isBlank()) return@Button
                                val count = viewModel.importOrdersFromCsv(pastedText)
                                Toast.makeText(
                                    context,
                                    "تم استيراد $count أوردر من النص المنسوخ بنجاح! 🚀",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            enabled = pastedText.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ابدأ الاستيراد", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- SETTINGS PAGE / SCREEN (TAB 4) ----------------
@Composable
fun SettingsMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right side: Icon circle container (RTL order - first is right)
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBackgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Center: Title & Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Right
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Left: Arrow pointing left "<-"
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SettingsScreen(viewModel: OrderViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val captainName by viewModel.captainName.collectAsState()
    val captainAvatar by viewModel.captainAvatar.collectAsState()
    val commissionCat1 by viewModel.commissionCat1.collectAsState()
    val commissionCat2 by viewModel.commissionCat2.collectAsState()
    val commissionCat3 by viewModel.commissionCat3.collectAsState()

    var activeSection by remember { mutableStateOf<String?>(null) }

    var inputName by remember { mutableStateOf(captainName) }
    var selectedAvatar by remember { mutableStateOf(captainAvatar) }
    var inputCat1 by remember { mutableStateOf(commissionCat1.toString()) }
    var inputCat2 by remember { mutableStateOf(commissionCat2.toString()) }
    var inputCat3 by remember { mutableStateOf(commissionCat3.toString()) }

    var showClearConfirm by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(captainName) { inputName = captainName }
    androidx.compose.runtime.LaunchedEffect(captainAvatar) { selectedAvatar = captainAvatar }
    androidx.compose.runtime.LaunchedEffect(commissionCat1) { inputCat1 = commissionCat1.toString() }
    androidx.compose.runtime.LaunchedEffect(commissionCat2) { inputCat2 = commissionCat2.toString() }
    androidx.compose.runtime.LaunchedEffect(commissionCat3) { inputCat3 = commissionCat3.toString() }

    val GalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                // Copy selected stream to our safe persistent internal storage path
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val targetFile = java.io.File(context.filesDir, "custom_captain_avatar.png")
                val outputStream = java.io.FileOutputStream(targetFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                selectedAvatar = targetFile.absolutePath
                Toast.makeText(context, "تم تحميل الصورة بنجاح! اضغط حفظ البيانات لاعتمادها 👤", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "فشل قراءة الملف المختار!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Custom Title & Back Topbar exactly like the sent design
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (activeSection == "profile") "بيانات الكابتن" 
                               else if (activeSection == "commissions") "فئات العمولات" 
                               else "إعدادات التطبيق",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = BlueDark
                    )
                    IconButton(
                        onClick = {
                            if (activeSection != null) {
                                activeSection = null
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "رجوع",
                                tint = BlueDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            if (activeSection == null) {
                // Main list style identical to requested photo design
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            // Section 1: Captain Info
                            SettingsMenuItem(
                                title = "بيانات الكابتن",
                                subtitle = "تعديل اسم وصورة كابتن التوصيل",
                                icon = Icons.Default.Person,
                                iconBackgroundColor = Color(0xFFEBF5FA),
                                iconColor = BluePrimary,
                                onClick = { activeSection = "profile" }
                            )

                            // Separator line
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))

                            // Section 2: Commissions
                            SettingsMenuItem(
                                title = "فئات العمولات",
                                subtitle = "تعديل أسعار عمولات حالات التوصيل",
                                icon = Icons.Default.Payments,
                                iconBackgroundColor = Color(0xFFEBF5FA),
                                iconColor = BluePrimary,
                                onClick = { activeSection = "commissions" }
                            )

                            // Separator line
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))

                            // Section 3: Clear Itinerary
                            SettingsMenuItem(
                                title = "إفراغ خط السير",
                                subtitle = "حذف جميع الطلبات الحالية والبدء من جديد",
                                icon = Icons.Default.Delete,
                                iconBackgroundColor = Color(0xFFFFECEE),
                                iconColor = CancelledRed,
                                onClick = { showClearConfirm = true }
                            )
                        }
                    }
                }
            } else if (activeSection == "profile") {
                // EDIT PROFILE SECTION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "تعديل بيانات الكابتن الشخصية 👤",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            )

                            // Big circular avatar view using custom loaded bitmap
                            CaptainAvatarView(
                                avatar = selectedAvatar,
                                modifier = Modifier.size(110.dp)
                            )

                            // Upload picture button (ROBUST DEVICE INTERNAL FILES PICKER AS MANDATED)
                            Button(
                                onClick = { GalleryLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رفع صورة جديدة من ملفات الجهاز 📁", color = BluePrimary, fontWeight = FontWeight.Bold)
                            }

                            // Name input
                            OutlinedTextField(
                                value = inputName,
                                onValueChange = { inputName = it },
                                label = { Text("اسم الكابتن الجديد", fontSize = 12.sp, color = TextDark) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                                    focusedLabelColor = BluePrimary,
                                    unfocusedLabelColor = TextMuted
                                )
                            )

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (inputName.isBlank()) {
                                            Toast.makeText(context, "الرجاء كـتابة اسم جـديد!", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        viewModel.updateCaptainInfo(inputName.trim(), selectedAvatar)
                                        Toast.makeText(context, "تم حفظ بيانات الكابتن بنجاح! ✅", Toast.LENGTH_SHORT).show()
                                        activeSection = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("حفظ التعديلات ✅", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { activeSection = null },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("إلغاء التعديل", color = TextDark)
                                }
                            }
                        }
                    }
                }
            } else if (activeSection == "commissions") {
                // EDIT COMMISSIONS SECTION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "تعديل فئات العمولات 💰",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                textAlign = TextAlign.Right
                            )

                            Text(
                                text = "هذه الفئات تُسـتخدم في الحسـاب التـلقائي لصافي أربـاح الكـابتن والورديـة عـند تعديل حـالة أي أوردر.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Right
                            )

                            // Cat 1 (تم التسليم والتسليم الجزئي)
                            OutlinedTextField(
                                value = inputCat1,
                                onValueChange = { inputCat1 = it },
                                label = { Text("الفئة الأولى: التسليم الفعلي والتسليم الجزئي (ج.م)", fontSize = 11.sp, color = TextDark) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f)
                                )
                            )

                            // Cat 2 (رفض ودفع مصاريف الشحن)
                            OutlinedTextField(
                                value = inputCat2,
                                onValueChange = { inputCat2 = it },
                                label = { Text("الفئة الثانية: الرفض مع تحصيل مصاريف الشحن (ج.م)", fontSize = 11.sp, color = TextDark) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f)
                                )
                            )

                            // Cat 3 (رفض ولم يدفع مصاريف الشحن)
                            OutlinedTextField(
                                value = inputCat3,
                                onValueChange = { inputCat3 = it },
                                label = { Text("الفئة الثالثة: الرفض بدون دفع مصاريف الشحن (ج.م)", fontSize = 11.sp, color = TextDark) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f)
                                )
                            )

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val c1 = inputCat1.toDoubleOrNull() ?: 0.0
                                        val c2 = inputCat2.toDoubleOrNull() ?: 0.0
                                        val c3 = inputCat3.toDoubleOrNull() ?: 0.0
                                        viewModel.updateCommissionRates(c1, c2, c3)
                                        Toast.makeText(context, "تم حفظ قيم العمولات الجديدة وتحديث الأرقام! ✅", Toast.LENGTH_SHORT).show()
                                        activeSection = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("حفظ العمولات 💾", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { activeSection = null },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("إلغاء", color = TextDark)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // Confirmation dialog for clearing
        if (showClearConfirm) {
            AlertDialog(
                onDismissRequest = { showClearConfirm = false },
                title = {
                    Text(
                        text = "تأكيد مسح خط السير ؟ ⚠️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                },
                text = {
                    Text(
                        text = "هل أنت متأكد من تفريغ خط السير بالكامل؟ سيتم حذف جميع الطلبات الحالية والناجحة والملغاة نهائياً. لا يمكن التراجع عن هذا الإجراء.",
                        fontSize = 14.sp,
                        color = TextDark,
                        textAlign = TextAlign.Right
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearItinerary()
                            showClearConfirm = false
                            Toast.makeText(context, "تم مسح وتفريغ خط السير بنجاح! ويبدأ الآن من جديد 🚀", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CancelledRed)
                    ) {
                        Text("نعم، امسح خط السير", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirm = false }) {
                        Text("إلغاء والتراجع", color = BluePrimary)
                    }
                }
            )
        }
    }
}

// ---------------- EMPTY STATE PLACEHOLDER VIEW ----------------
@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsBus,
            contentDescription = null,
            tint = TextMuted.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = TextMuted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
