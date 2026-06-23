package com.mandoob.mena.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import com.mandoob.mena.R
import com.mandoob.mena.data.Order
import com.mandoob.mena.ui.theme.*
import com.mandoob.mena.viewmodel.OrderViewModel
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Get current date formatted dynamically in Arabic with current device configuration
fun getCurrentFormattedDate(): String {
    return try {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        sdf.format(Date())
    } catch (e: Exception) {
        "تاريخ اليوم"
    }
}

// ---------------- CAPTAIN AVATAR COMPONENT ----------------
@Composable
fun CaptainAvatarView(avatar: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    if (avatar != "default" && avatar != "delivery" && avatar.isNotEmpty()) {
        val bitmap = remember(avatar) {
            try {
                val file = if (avatar.startsWith("/")) {
                    java.io.File(avatar)
                } else {
                    java.io.File(context.filesDir, avatar)
                }
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
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
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
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            Image(
                painter = painterResource(id = R.drawable.img_profile_avatar),
                contentDescription = "صورة الكابتن",
                modifier = modifier
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ---------------- HEADER COMPONENT ----------------
@Composable
fun HeaderCard(
    viewModel: OrderViewModel,
    onOpenSettings: () -> Unit,
    showSettingsCog: Boolean = false
) {
    val captainName by viewModel.captainName.collectAsState()
    val captainAvatar by viewModel.captainAvatar.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Captain info (right side inside row)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CaptainAvatarView(
                    avatar = captainAvatar,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "أهلاً، $captainName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = getCurrentFormattedDate(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showSettingsCog) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "الإعدادات",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
            .height(145.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF006399)),
        shape = RoundedCornerShape(24.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Bank Icon inside a soft semi-transparent white circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Right side: Column of text styled exactly like the screenshot
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "صافي التوريد للمكتب",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.End
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${"%,.0f".format(netRemittance)} ج.م",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "صافي المبالغ المستحقة للتسليم وتصفية الوردية",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// ---------------- STAT CARD ----------------
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
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .testTag("${testTagPrefix}_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = subtext,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

private val Int.ddp: androidx.compose.ui.unit.Dp get() = this.dp

// ---------------- INTERACTIVE ROUTE PROGRESS CARD ----------------
@Composable
fun InteractiveRouteProgressCard(completed: Int, total: Int) {
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF000000)

    val cardBg = if (isDark) Color(0xFF121212) else Color(0xFFEAF8FE)
    val cardBorder = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFB1E6F8)
    val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val highlightColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF139CB5)
    val trackBg = if (isDark) Color(0xFF262626) else Color(0xFFE2F1F8)
    val trackCompleted = if (isDark) Color(0xFF0284C7) else Color(0xFF139CB5)
    val badgeBorder = if (isDark) Color(0xFF38BDF8) else Color.White

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.5.dp, cardBorder),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header line: "يلا يا بطل! 🛵"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "يلا يا بطل! 🛵",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Start
                    )
                }

                // Subtitle line: "خلصت (15 من 59) من مهماتك النهاردة" with coloured numbers
                val annotatedSubtitle = buildAnnotatedString {
                    append("خلصت ")
                    withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                        append("($completed من $total)")
                    }
                    append(" من مهماتك النهاردة")
                }

                Text(
                    text = annotatedSubtitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF94A3B8) else textColor,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

            // Custom Interactive Progress bar with Force Ltr support
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Underneath, the route track
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(trackBg)
                    ) {
                        // Uncompleted track segment (Left part)
                        Box(
                            modifier = Modifier
                                .weight(if (progress >= 1f) 0.001f else (1f - progress))
                                .fillMaxHeight()
                                .background(trackBg)
                        ) {
                            // Finish Flag at the far left
                            Text(
                                text = "🏁",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 2.dp)
                            )
                        }

                        // Completed track segment (Right part)
                        Box(
                            modifier = Modifier
                                .weight(if (progress <= 0f) 0.001f else progress)
                                .fillMaxHeight()
                                .background(trackCompleted)
                        ) {
                            // Dashed white line inside completed segment using Canvas
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 2.dp.toPx()
                                val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                    floatArrayOf(10f, 10f), 0f
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.85f),
                                    start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                                    end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                                    strokeWidth = strokeWidth,
                                    pathEffect = pathEffect
                                )
                            }
                        }
                    }

                    // Package moving circle indicator
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = BiasAlignment(horizontalBias = 1f - (progress * 2f), verticalBias = 0f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(3.dp, CircleShape)
                                .background(trackCompleted, CircleShape)
                                .border(2.dp, badgeBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
}

// ---------------- EMPTY STATE VIEW ----------------
@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---------------- PHONE TAG CHIPS ----------------
@Composable
fun PhoneTag(
    number: String,
    label: String,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFE3F2FD), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "اتصال",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onWhatsApp,
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFE8F5E9), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "واتساب",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ---------------- APP CALL & WHATSAPP HELPERS ----------------
fun launchDialer(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "لم نتمكن من تشغيل لوحة الاتصال!", Toast.LENGTH_SHORT).show()
    }
}

fun launchWhatsApp(context: Context, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val formattedNum = if (cleanNumber.startsWith("0")) "2$cleanNumber" else cleanNumber
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNum")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "الواتساب غير مثبت في هذا الجهاز!", Toast.LENGTH_SHORT).show()
    }
}

fun shareOrderToWhatsApp(context: Context, order: Order) {
    try {
        val statusText = when (order.status) {
            Order.STATUS_PARTIAL -> "${order.status} (تم تحصيل ${order.collectedAmount ?: 0.0} ج.م)"
            Order.STATUS_REJECTED_WITH_FEE -> "${order.status} (تم دفع مصاريف الشحن ${order.deliveryFeeAmount ?: 0.0} ج.م)"
            else -> order.status
        }

        val templateText = """
            👤 *العميل:* ${order.clientName}
            📞 *رقم الموبايل:* ${order.phoneNumber}
            📍 *العنوان:* ${order.address}
            💰 *المطلوب دفعه وتحصيله:* ${order.amount} ج.م
            📊 *حالة الأوردر:* $statusText
        """.trimIndent()
        
        val url = "https://api.whatsapp.com/send?text=" + java.net.URLEncoder.encode(templateText, "UTF-8")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر مشاركة بيانات الطلب عبر واتساب", Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickWhatsAppMessage(context: Context, order: Order) {
    try {
        val cleanNumber = order.phoneNumber.replace(Regex("[^0-9]"), "")
        val formattedNum = if (cleanNumber.startsWith("0")) "2$cleanNumber" else cleanNumber
        
        val quickText = "يا فندم مع حضرتك مندوب شحن بوسطة بخصوص الأوردر الخاص بك بمبلغ ${order.amount} ج.م. هل حضرتك متواجد حالياً للاستلام؟"
        
        val url = "https://api.whatsapp.com/send?phone=$formattedNum&text=" + URLEncoder.encode(quickText, "UTF-8")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر إرسال رسالة سريعة للواتساب", Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickSMSMessage(context: Context, order: Order) {
    try {
        val quickText = "مع حضرتك مندوب شحن بوسطة بخصوص الأوردر الخاص بك بمبلغ ${order.amount} ج.م. هل متواجد حالياً للاستلام؟"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${order.phoneNumber}"))
        intent.putExtra("sms_body", quickText)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر إرسال رسالة SMS سريعة!", Toast.LENGTH_SHORT).show()
    }
}
