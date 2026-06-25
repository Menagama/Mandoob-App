package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

            var showAddDialog by remember { mutableStateOf(false) }
            var showImportDialog by remember { mutableStateOf(false) }

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
}

@Composable
fun OnboardingScreen(viewModel: OrderViewModel) {
    val context = LocalContext.current
    var currentOnboardingStep by remember { mutableStateOf(0) }

    // Step 0 variables
    var nameInput by remember { mutableStateOf("") }
    var avatarFile by remember { mutableStateOf("") }

    // Step 1 variables
    var cat1Input by remember { mutableStateOf("") }
    var cat2Input by remember { mutableStateOf("") }
    var cat3Input by remember { mutableStateOf("") }

    // Step 2 variables
    var selectedTheme by remember { mutableStateOf("system") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                val fileName = "custom_captain_avatar.png"
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val targetFile = java.io.File(context.filesDir, fileName)
                val outputStream = java.io.FileOutputStream(targetFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                avatarFile = fileName
                Toast.makeText(context, "تم حفظ الصورة بنجاح! 👤", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "فشل نسخ وحفظ صورة الكابتن!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Progress Indicator at the top
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                if (i <= currentOnboardingStep) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            // 2. Navigation Row with Back button (small, except on Step 0)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentOnboardingStep > 0) {
                    IconButton(
                        onClick = { currentOnboardingStep-- },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "السابق",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
                
                Text(
                    text = "الخطوة ${currentOnboardingStep + 1} من 4",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Step Content
            when (currentOnboardingStep) {
                0 -> {
                    // Step 1: Welcome and Captain Info
                    Text(
                        text = "👋",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "أهلاً بك في Mandoob",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "يرجى إدخال اسمك وصورتك للبدء في تنظيم رحلات التوصيل الخاصة بك.",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("اسم الكابتن") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captain_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CaptainAvatarView(
                            avatar = avatarFile,
                            modifier = Modifier.size(100.dp)
                        )

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("اختر صورة من الجهاز")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (nameInput.trim().isEmpty()) {
                                Toast.makeText(context, "الرجاء إدخال اسم الكابتن أولاً!", Toast.LENGTH_SHORT).show()
                            } else {
                                currentOnboardingStep++
                            }
                        },
                        enabled = nameInput.trim().isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("next_button_step_0"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "التالي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                1 -> {
                    // Step 2: Commission Rates
                    Text(
                        text = "💰",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "أدخل قيم عمولاتك",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "حدد قيم العمولات الافتراضية لكل حالة توصيل لحساب أرباحك تلقائياً.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cat1Input,
                        onValueChange = { cat1Input = it },
                        label = { Text("عمولة تم التسليم ج.م") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("commission_cat1_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = cat2Input,
                        onValueChange = { cat2Input = it },
                        label = { Text("عمولة رفض ودفع مصاريف الشحن ج.م") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("commission_cat2_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = cat3Input,
                        onValueChange = { cat3Input = it },
                        label = { Text("عمولة رفض ولم يدفع مصاريف الشحن ج.م") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("commission_cat3_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { currentOnboardingStep++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("next_button_step_1"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "التالي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                2 -> {
                    // Step 3: Theme Choice
                    Text(
                        text = "🎨",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "اختر مظهر التطبيق",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "اختر نمط الألوان المفضل والمريح لك للعمل الميداني وساعات الليل الطويلة.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val themes = listOf(
                            Triple("light", "مضيء ☀️", "light"),
                            Triple("dark", "داكن 🌙", "dark"),
                            Triple("system", "تلقائي 💻", "system")
                        )
                        
                        themes.forEach { (themeKey, label, _) ->
                            val isSelected = selectedTheme == themeKey
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTheme = themeKey }
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { currentOnboardingStep++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("next_button_step_2"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "التالي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                3 -> {
                    // Step 4: Completion
                    Text(
                        text = "🎉",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "كل شيء جاهز يا كابتن",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "تم إعداد ملفك الشخصي وعمولاتك ومظهر التطبيق بنجاح. نتمنى لك رحلات توصيل موفقة وآمنة!",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val cat1 = cat1Input.toDoubleOrNull() ?: 0.0
                            val cat2 = cat2Input.toDoubleOrNull() ?: 0.0
                            val cat3 = cat3Input.toDoubleOrNull() ?: 0.0
                            
                            viewModel.updateCaptainInfo(nameInput.trim(), avatarFile)
                            viewModel.updateCommissionRates(cat1, cat2, cat3)
                            viewModel.updateAppTheme(selectedTheme)
                            viewModel.completeOnboarding()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("start_button_onboarding"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "ابدأ العمل",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
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
