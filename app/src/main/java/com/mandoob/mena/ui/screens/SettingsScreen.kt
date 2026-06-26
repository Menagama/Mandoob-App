package com.mandoob.mena.ui.screens

import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.R
import com.mandoob.mena.ui.theme.BlueDark
import com.mandoob.mena.ui.theme.BluePrimary
import com.mandoob.mena.ui.theme.CancelledRed
import com.mandoob.mena.ui.theme.GreenSuccess
import com.mandoob.mena.viewmodel.OrderViewModel

@Composable
fun SettingsMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
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
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            } else if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
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
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Right
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Left: Arrow pointing left "<-"
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
    val appThemeSettings by viewModel.appThemeSettings.collectAsState()

    var activeSection by remember { mutableStateOf<String?>(null) }

    var inputName by remember { mutableStateOf(captainName) }
    var selectedAvatar by remember { mutableStateOf(captainAvatar) }
    var inputCat1 by remember { mutableStateOf(commissionCat1.toString()) }
    var inputCat2 by remember { mutableStateOf(commissionCat2.toString()) }
    var inputCat3 by remember { mutableStateOf(commissionCat3.toString()) }

    LaunchedEffect(captainName) { inputName = captainName }
    LaunchedEffect(captainAvatar) { selectedAvatar = captainAvatar }
    LaunchedEffect(commissionCat1) { inputCat1 = commissionCat1.toString() }
    LaunchedEffect(commissionCat2) { inputCat2 = commissionCat2.toString() }
    LaunchedEffect(commissionCat3) { inputCat3 = commissionCat3.toString() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                // Copy selected file stream to we persist only the name (e.g., custom_captain_avatar.png)
                val fileName = "custom_captain_avatar.png"
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val targetFile = java.io.File(context.filesDir, fileName)
                val outputStream = java.io.FileOutputStream(targetFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                selectedAvatar = fileName // Storing ONLY the file name! Relaxes path breaking bugs
                Toast.makeText(context, "تم حفظ الصورة بنجاح! اضغط حفظ لاعتمادها 👤", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "فشل نسخ وحفظ صورة الكابتن!", Toast.LENGTH_SHORT).show()
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
                        text = when (activeSection) {
                            "profile" -> "بيانات الكابتن"
                            "commissions" -> "فئات العمولات"
                            "appearance" -> "مظهر التطبيق"
                            else -> "إعدادات التطبيق"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
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
                                tint = MaterialTheme.colorScheme.onSurface,
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            // Section 1: Captain Info
                            SettingsMenuItem(
                                title = "بيانات الكابتن",
                                subtitle = "تعديل اسم وصورة كابتن التوصيل",
                                icon = Icons.Default.Person,
                                iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                iconColor = MaterialTheme.colorScheme.primary,
                                onClick = { activeSection = "profile" }
                            )

                            // Separator
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))

                            // Section 2: Commissions
                            SettingsMenuItem(
                                title = "فئات العمولات",
                                subtitle = "تعديل أسعار عمولات حالات التوصيل",
                                icon = Icons.Default.Payments,
                                iconBackgroundColor = Color(0xFFE6F4EA),
                                iconColor = Color(0xFF34A853),
                                onClick = { activeSection = "commissions" }
                            )

                            // Separator
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))

                            // Section 3: Appearance mode (Light / Dark)
                            SettingsMenuItem(
                                title = "مظهر التطبيق (الوضع الليلي)",
                                subtitle = "تبديل المظهر بين المضيء، الداكن أو التلقائي وبألوان متناسقة",
                                icon = Icons.Default.Brightness4,
                                iconBackgroundColor = Color(0xFFFEF7E0),
                                iconColor = Color(0xFFF9AB00),
                                onClick = { activeSection = "appearance" }
                            )
                        }
                    }
                }

                // Section: Rights & Copyright ("حقوق الملكية")
                item {
                    Text(
                        text = "حقوق الملكية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                        textAlign = TextAlign.Right
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            // Element 1: App Version Number (Read dynamically from BuildConfig.VERSION_NAME)
                            SettingsMenuItem(
                                title = "إصدار التطبيق",
                                subtitle = "الإصدار ${com.mandoob.mena.BuildConfig.VERSION_NAME}",
                                icon = Icons.Default.Info,
                                iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                iconColor = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    Toast.makeText(context, "إصدار التطبيق الحالي: ${com.mandoob.mena.BuildConfig.VERSION_NAME}", Toast.LENGTH_SHORT).show()
                                }
                            )

                            // Separator
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))

                            // Element 2: Contact Developer (Direct WhatsApp Chat)
                            SettingsMenuItem(
                                title = "تواصل مع المطور",
                                subtitle = "راسل المطور مباشرة عبر تطبيق واتساب",
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                iconColor = Color(0xFF25D366),
                                onClick = {
                                    val pm = context.packageManager
                                    val isWhatsappInstalled = try {
                                        pm.getPackageInfo("com.whatsapp", 0)
                                        true
                                    } catch (e: Exception) {
                                        try {
                                            pm.getPackageInfo("com.whatsapp.w4b", 0)
                                            true
                                        } catch (e2: Exception) {
                                            false
                                        }
                                    }

                                    if (isWhatsappInstalled) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201064139779"))
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "واتساب غير مثبت على جهازك", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }
                }
            } else if (activeSection == "profile") {
                // EDIT PROFILE SECTION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            )

                            CaptainAvatarView(
                                avatar = selectedAvatar,
                                modifier = Modifier.size(110.dp)
                            )

                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رفع صورة جديدة من ملفات الجهاز 📁", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }

                            OutlinedTextField(
                                value = inputName,
                                onValueChange = { inputName = it },
                                label = { Text("اسم الكابتن الجديد", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )

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
                                    Text("إلغاء التعديل", color = MaterialTheme.colorScheme.onSurface)
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right
                            )

                            Text(
                                text = "هذه الفئات تُسـتخدم في الحسـاب التـلقائي لصافي أربـاح الكـابتن والورديـة عـند تعديل حـالة أي أوردر.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right
                            )

                            OutlinedTextField(
                                value = inputCat1,
                                onValueChange = { inputCat1 = it },
                                label = { Text("الفئة الأولى: التسليم الفعلي والتسليم الجزئي (ج.م)", fontSize = 11.sp) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )

                            OutlinedTextField(
                                value = inputCat2,
                                onValueChange = { inputCat2 = it },
                                label = { Text("الفئة الثانية: الرفض مع تحصيل مصاريف الشحن (ج.م)", fontSize = 11.sp) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )

                            OutlinedTextField(
                                value = inputCat3,
                                onValueChange = { inputCat3 = it },
                                label = { Text("الفئة الثالثة: الرفض بدون دفع مصاريف الشحن (ج.م)", fontSize = 11.sp) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )

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
                                    Text("إلغاء", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            } else if (activeSection == "appearance") {
                // APPEARANCE SECTION (Issue 9 & 10)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "مظهر التطبيق ☀️🌙",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right
                            )

                            Text(
                                text = "اختر نمط الألوان المفضل لك. تم دعم الوضع المضيء والداكن بألوان مريحة ومتناسقة للعين مع ألوان شيت بوسطة للعمل الميداني وساعات الليل الطويلة.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right
                            )

                            val modes = listOf(
                                "system" to "مزامنة تلقائية مع مظهر النظام 💻",
                                "light" to "الوضع المضيء دائمًا ☀️",
                                "dark" to "الوضع الداكن دائمًا 🌙"
                            )

                            modes.forEach { (key, titleKey) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (appThemeSettings == key) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.updateAppTheme(key)
                                            Toast.makeText(context, "تم حفظ نمط المظهر المختار!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                 ) {
                                    Text(
                                        text = titleKey,
                                        fontSize = 14.sp,
                                        fontWeight = if (appThemeSettings == key) FontWeight.Bold else FontWeight.Medium,
                                        color = if (appThemeSettings == key) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    RadioButton(
                                        selected = appThemeSettings == key,
                                        onClick = {
                                            viewModel.updateAppTheme(key)
                                            Toast.makeText(context, "تم حفظ نمط المظهر المختار!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }

                            Button(
                                onClick = { activeSection = null },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("رجوع للقائمة الرئيسية", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
