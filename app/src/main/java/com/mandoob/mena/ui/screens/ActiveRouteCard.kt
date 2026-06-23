package com.mandoob.mena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.R
import com.mandoob.mena.data.Order
import com.mandoob.mena.ui.theme.*

@Composable
fun ActiveRouteCard(
    order: Order,
    index: Int,
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onStatusChanged: (String, Double?, Double?, Boolean) -> Unit,
    onUpdateNotes: ((String?, String?) -> Unit)? = null,
    isSortingEnabled: Boolean = false,
    isFastMoveEnabled: Boolean = false,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF000000)
    val statusGreen = if (isDark) Color(0xFF34D399) else Color(0xFF127C41)
    val statusRed = if (isDark) Color(0xFFEF4444) else CancelledRed
    var showDropdown by remember(order.id) { mutableStateOf(false) }
    var showPartialInput by remember(order.id) { mutableStateOf(false) }
    var showFeeInput by remember(order.id) { mutableStateOf(false) }
    var showDeleteWarning by remember(order.id) { mutableStateOf(false) }
    var showDetailsPage by remember(order.id) { mutableStateOf(false) }
    var showPhoneSelectorDialog by remember(order.id) { mutableStateOf(false) }
    var pendingActionType by remember(order.id) { mutableStateOf<String?>(null) }
    
    // Hold temp inputs for amounts
    var partialAmountText by remember(order.id) { mutableStateOf(order.collectedAmount?.toString() ?: "0") }
    var feeAmountText by remember(order.id) { mutableStateOf(order.deliveryFeeAmount?.toString() ?: "0") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { showDetailsPage = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First Row: Name/Phone, Avatar, Index, Delete, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.clientName.firstOrNull()?.toString() ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurface,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = statusGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Share button
                    IconButton(
                        onClick = { shareOrderToWhatsApp(context, order) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة الأوردر",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. Trash (Delete) button
                    IconButton(
                        onClick = { showDeleteWarning = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "تمسح",
                            tint = CancelledRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 3. Order number sequence badge
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#$index",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Second Row: Detailed Address with Pin Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.address,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Third Row: Collected cash display, and Edit pencil button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "المطلوب تحصيله كاش",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${order.amount.toInt()} ج.م",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (order.status == Order.STATUS_PARTIAL) {
                        Text(
                            text = "التسليم الجزئى (${order.collectedAmount?.toInt() ?: 0} ج.م)",
                            color = MaterialTheme.colorScheme.primary,
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل بيانات الأوردر",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Dialer Button (renders on Right in RTL)
                IconButton(
                    onClick = {
                        val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                        if (hasSecondPhone) {
                            pendingActionType = "CALL"
                            showPhoneSelectorDialog = true
                        } else {
                            launchDialer(context, order.phoneNumber)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(if (isDark) Color(0xFF064E3B) else Color(0xFFE8F8F5), RoundedCornerShape(12.dp))
                        .border(1.5.dp, if (isDark) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFA7E8D4), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال سريع للعميل",
                            tint = if (isDark) Color(0xFF34D399) else Color(0xFF0EA371),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إتصال",
                            color = if (isDark) Color(0xFF34D399) else Color(0xFF0EA371),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // 2. Quick Message (SMS)
                IconButton(
                    onClick = {
                        val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                        if (hasSecondPhone) {
                            pendingActionType = "SMS"
                            showPhoneSelectorDialog = true
                        } else {
                            sendQuickSMSMessage(context, order)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(if (isDark) Color(0xFF0C4A6E) else Color(0xFFEBF5FF), RoundedCornerShape(12.dp))
                        .border(1.5.dp, if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFC0E0FF), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0084FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "رسالة",
                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0084FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // 3. WhatsApp Quick Message (renders on Left in RTL)
                IconButton(
                    onClick = {
                        val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                        if (hasSecondPhone) {
                            pendingActionType = "WHATSAPP"
                            showPhoneSelectorDialog = true
                        } else {
                            sendQuickWhatsAppMessage(context, order)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(if (isDark) Color(0xFF065F46).copy(alpha = 0.3f) else Color(0xFFEBFDF5), RoundedCornerShape(12.dp))
                        .border(1.5.dp, if (isDark) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "واتساب",
                            color = if (isDark) Color(0xFF10B981) else Color(0xFF0EA371),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    if (showDetailsPage) {
        Dialog(
            onDismissRequest = { showDetailsPage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                OrderDetailsFullScreenPage(
                    order = order,
                    isDark = isDark,
                    statusGreen = statusGreen,
                    statusRed = statusRed,
                    onDismiss = { showDetailsPage = false },
                    onStatusChanged = { newStatus, collAmt, feeAmt, isFull ->
                        onStatusChanged(newStatus, collAmt, feeAmt, isFull)
                    },
                    onEditClick = {
                        showDetailsPage = false
                        onEditClick()
                    },
                    onUpdateNotes = { updateNotes, courierNotes ->
                        onUpdateNotes?.invoke(updateNotes, courierNotes)
                    }
                )
            }
        }
    }

    if (showPhoneSelectorDialog && pendingActionType != null) {
        PhoneSelectorDialog(
            phoneNumber1 = order.phoneNumber,
            phoneNumber2 = order.phoneNumber2 ?: "",
            onNumberSelected = { selectedNumber ->
                executeCommunication(context, pendingActionType!!, selectedNumber, order)
                showPhoneSelectorDialog = false
                pendingActionType = null
            },
            onDismiss = {
                showPhoneSelectorDialog = false
                pendingActionType = null
            }
        )
    }

    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            title = {
                Text(
                    text = "تأكيد الحذف ⚠️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف هذا الأوردر للعميل (${order.clientName})؟ هذا الإجراء غير قابل للتراجع.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteWarning = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CancelledRed)
                ) {
                    Text("حذف الأوردر", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteWarning = false }
                ) {
                    Text("إلغاء", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun OrderDetailsFullScreenPage(
    order: Order,
    isDark: Boolean,
    statusGreen: Color,
    statusRed: Color,
    onDismiss: () -> Unit,
    onStatusChanged: (String, Double?, Double?, Boolean) -> Unit,
    onEditClick: () -> Unit,
    onUpdateNotes: (String?, String?) -> Unit
) {
    val context = LocalContext.current
    var showPartialAmountSelector by remember { mutableStateOf(false) }
    var showFeeAmountSelector by remember { mutableStateOf(false) }
    var showAdditionalStatusOptions by remember { mutableStateOf(false) }
    var showSuccessConfirmation by remember { mutableStateOf(false) }
    
    var tempPartialAmount by remember { mutableStateOf(order.collectedAmount?.toString() ?: "0") }
    var tempFeeAmount by remember { mutableStateOf(order.deliveryFeeAmount?.toString() ?: "0") }

    var showEditCourierNotesDialog by remember { mutableStateOf(false) }
    var tempCourierNotesValue by remember(order.courierNotes) { mutableStateOf(order.courierNotes ?: "") }

    var showPhoneSelectorDialog by remember { mutableStateOf(false) }
    var pendingActionType by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isDark) Color(0xFF2D2D2D) else Color(0xFFF1F5F9),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "رجوع",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "تسليم شحنة",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "#${5924692390L + order.id}",
                            fontSize = 14.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val statusText = order.status
                val (badgeBg, badgeTextColor) = when (statusText) {
                    Order.STATUS_PENDING -> {
                        if (isDark) Pair(Color(0xFF78350F).copy(alpha = 0.4f), Color(0xFFFBBF24))
                        else Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
                    }
                    Order.STATUS_DELIVERED -> {
                        if (isDark) Pair(Color(0xFF064E3B).copy(alpha = 0.4f), Color(0xFF34D399))
                        else Pair(Color(0xFFECFDF5), Color(0xFF047857))
                    }
                    Order.STATUS_PARTIAL -> {
                        if (isDark) Pair(Color(0xFF0C4A6E).copy(alpha = 0.4f), Color(0xFF38BDF8))
                        else Pair(Color(0xFFF0F9FF), Color(0xFF0284C7))
                    }
                    else -> {
                        if (isDark) Pair(Color(0xFF450A0A).copy(alpha = 0.4f), Color(0xFFF87171))
                        else Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(badgeBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 34.dp)
            ) {
                if (order.status == Order.STATUS_PENDING) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showSuccessConfirmation = true },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0097A7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تسليم الشحنة",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showAdditionalStatusOptions = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF4B5563) else Color(0xFFCBD5E1)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isDark) Color.White else Color(0xFF334155))
                        ) {
                            Text(
                                text = "إلغاء",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onStatusChanged(Order.STATUS_PENDING, null, null, false)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إعادة بدء التوصيل",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clickable {
                            val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                            if (hasSecondPhone) {
                                pendingActionType = "CALL"
                                showPhoneSelectorDialog = true
                            } else {
                                launchDialer(context, order.phoneNumber)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال",
                            tint = if (isDark) Color(0xFF34D399) else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إتصال",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF3F4F6) else Color(0xFF334155)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clickable {
                            val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                            if (hasSecondPhone) {
                                pendingActionType = "SMS"
                                showPhoneSelectorDialog = true
                            } else {
                                sendQuickSMSMessage(context, order)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "رسالة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF3F4F6) else Color(0xFF334155)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clickable {
                            val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                            if (hasSecondPhone) {
                                pendingActionType = "WHATSAPP"
                                showPhoneSelectorDialog = true
                            } else {
                                sendQuickWhatsAppMessage(context, order)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "واتساب",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF3F4F6) else Color(0xFF334155)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFE0F2FE)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF0369A1).copy(alpha = 0.4f) else Color(0xFFBAE6FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "المبلغ المطلوب",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                        )
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${order.amount.toInt()} ج.م",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0369A1)
                        )
                        if (order.status == Order.STATUS_PARTIAL) {
                            Text(
                                text = "التسليم الجزئى (${order.collectedAmount?.toInt() ?: 0} ج.م)",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (order.status == Order.STATUS_REJECTED_WITH_FEE) {
                            Text(
                                text = "مصاريف شحن (${order.deliveryFeeAmount?.toInt() ?: 0} ج.م)",
                                color = CancelledRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "بيانات العميل",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                
                Divider(color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "العميل",
                                fontSize = 14.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.clientName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "العنوان",
                                fontSize = 14.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val cleanAddr = order.address.trim()
                        val points = if (cleanAddr.contains(",")) cleanAddr.split(",") else cleanAddr.split("\n")
                        points.forEach { pt ->
                            if (pt.trim().isNotEmpty()) {
                                Text(
                                    text = "▪ ${pt.trim()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تفاصيل الشحنة والملاحظات",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Divider(color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9), thickness = 1.dp)

                // 2. Rider Notes Block (ملاحظات المندوب)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF34D399) else Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ملاحظات المندوب",
                                fontSize = 14.sp,
                                color = if (isDark) Color(0xFF34D399) else Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showEditCourierNotesDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(if (isDark) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFFD1FAE5), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل ملاحظات المندوب",
                                tint = if (isDark) Color(0xFF34D399) else Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = order.courierNotes ?: "لا توجد ملاحظات مسجلة للمندوب حالياً.",
                        fontSize = 14.sp,
                        color = if (order.courierNotes != null) MaterialTheme.colorScheme.onSurface else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontWeight = if (order.courierNotes != null) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }



    if (showEditCourierNotesDialog) {
        AlertDialog(
            onDismissRequest = { showEditCourierNotesDialog = false },
            title = {
                Text(
                    text = "ملاحظات المندوب ✍️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                OutlinedTextField(
                    value = tempCourierNotesValue,
                    onValueChange = { tempCourierNotesValue = it },
                    label = { Text("ملاحظات المندوب", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onUpdateNotes(null, tempCourierNotesValue)
                            showEditCourierNotesDialog = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { showEditCourierNotesDialog = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSuccessConfirmation) {
        AlertDialog(
            onDismissRequest = { showSuccessConfirmation = false },
            title = {
                Text(
                    text = "تسليم الشحنة 📦",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "يرجى تحديد نمط التسليم المطلوب للشحنة للعميل:",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onStatusChanged(Order.STATUS_DELIVERED, null, null, false)
                            showSuccessConfirmation = false
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تم التسليم بالكامل", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showSuccessConfirmation = false
                            showPartialAmountSelector = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("التسليم الجزئى", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    
                    TextButton(
                        onClick = { showSuccessConfirmation = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("إلغاء", fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAdditionalStatusOptions) {
        AlertDialog(
            onDismissRequest = { showAdditionalStatusOptions = false },
            title = {
                Text(
                    text = "الغاء / باقي الحالات ⚙️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "اختر الحالة الجديدة للشحنة للفئات الأخرى:",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val otherStatuses = listOf(
                        Pair("رفض بدون مصاريف شحن", Order.STATUS_REJECTED_NO_FEE),
                        Pair("رفض ودفع مصاريف شحن", Order.STATUS_REJECTED_WITH_FEE),
                        Pair("لا يرد", Order.STATUS_NO_ANSWER),
                        Pair("مؤجل", Order.STATUS_POSTPONED),
                        Pair("لاغى", Order.STATUS_CANCELLED),
                        Pair("إرجاع لـ جاري العمل", Order.STATUS_PENDING)
                    ).filter { !(order.status == Order.STATUS_PENDING && it.second == Order.STATUS_PENDING) }
                    
                    otherStatuses.forEach { (label, statusStr) ->
                        Button(
                            onClick = {
                                showAdditionalStatusOptions = false
                                if (statusStr == Order.STATUS_REJECTED_WITH_FEE) {
                                    showFeeAmountSelector = true
                                } else {
                                    onStatusChanged(statusStr, null, null, false)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (statusStr == Order.STATUS_CANCELLED) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (statusStr == Order.STATUS_CANCELLED) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { showAdditionalStatusOptions = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("تراجع", fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPartialAmountSelector) {
        AlertDialog(
            onDismissRequest = { showPartialAmountSelector = false },
            title = {
                Text(
                    text = "التسليم الجزئى 💳",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ادخل المبلغ الفعلي الذي تم تحصيله من العميل (قيمة الأوردر تحصيل كلي: ${order.amount.toInt()} ج.م):",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPartialAmount,
                        onValueChange = { tempPartialAmount = it },
                        label = { Text("المبلغ المحصل كاش", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val valAmt = tempPartialAmount.toDoubleOrNull() ?: 0.0
                            if (valAmt >= order.amount) {
                                android.widget.Toast.makeText(context, "مبلغ التحصيل الجزئي يجب أن يكون أقل من قيمة الأوردر (${order.amount})", android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                onStatusChanged(Order.STATUS_PARTIAL, valAmt, null, false)
                                showPartialAmountSelector = false
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد", color = Color.White)
                    }

                    TextButton(
                        onClick = { showPartialAmountSelector = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showFeeAmountSelector) {
        AlertDialog(
            onDismissRequest = { showFeeAmountSelector = false },
            title = {
                Text(
                    text = "مصاريف شحن الرفض 🚚",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ادخل قيمة مصاريف الشحن التي تم تحصيله من العميل عند الرفض:",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempFeeAmount,
                        onValueChange = { tempFeeAmount = it },
                        label = { Text("مصاريف الشحن المحصلة", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = CancelledRed,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val valAmt = tempFeeAmount.toDoubleOrNull() ?: 0.0
                            onStatusChanged(Order.STATUS_REJECTED_WITH_FEE, null, valAmt, false)
                            showFeeAmountSelector = false
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CancelledRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد", color = Color.White)
                    }

                    TextButton(
                        onClick = { showFeeAmountSelector = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPhoneSelectorDialog && pendingActionType != null) {
        PhoneSelectorDialog(
            phoneNumber1 = order.phoneNumber,
            phoneNumber2 = order.phoneNumber2 ?: "",
            onNumberSelected = { selectedNumber ->
                executeCommunication(context, pendingActionType!!, selectedNumber, order)
                showPhoneSelectorDialog = false
                pendingActionType = null
            },
            onDismiss = {
                showPhoneSelectorDialog = false
                pendingActionType = null
            }
        )
    }
}

@Composable
fun PhoneSelectorDialog(
    phoneNumber1: String,
    phoneNumber2: String,
    onNumberSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "اختر رقم الهاتف للتواصل 📞",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "يرجى تحديد الرقم الذي ترغب في التواصل من خلاله:",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = { onNumberSelected(phoneNumber1) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "الرقم الأساسي: $phoneNumber1",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
                
                Button(
                    onClick = { onNumberSelected(phoneNumber2) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "الرقم الثاني: $phoneNumber2",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

fun launchDialerWithNumber(context: android.content.Context, phoneNumber: String) {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:$phoneNumber"))
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "لا يمكن فتح لوحة الاتصال", android.widget.Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickWhatsAppMessageWithNumber(context: android.content.Context, order: Order, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val formattedNum = if (cleanNumber.startsWith("0")) "2$cleanNumber" else cleanNumber
        
        val quickText = "يا فندم مع حضرتك مندوب شحن بوسطة بخصوص الأوردر الخاص بك بمبلغ ${order.amount} ج.م. هل حضرتك متواجد حالياً للاستلام؟"
        
        val url = "https://api.whatsapp.com/send?phone=$formattedNum&text=" + java.net.URLEncoder.encode(quickText, "UTF-8")
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "تعذر إرسال رسالة سريعة للواتساب", android.widget.Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickSMSMessageWithNumber(context: android.content.Context, order: Order, phoneNumber: String) {
    try {
        val quickText = "مع حضرتك مندوب شحن بوسطة بخصوص الأوردر الخاص بك بمبلغ ${order.amount} ج.م. هل متواجد حالياً للاستلام؟"
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", quickText)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "تعذر إرسال رسالة SMS سريعة!", android.widget.Toast.LENGTH_SHORT).show()
    }
}

fun executeCommunication(
    context: android.content.Context,
    actionType: String,
    phoneNumber: String,
    order: Order
) {
    when (actionType) {
        "CALL" -> launchDialerWithNumber(context, phoneNumber)
        "SMS" -> sendQuickSMSMessageWithNumber(context, order, phoneNumber)
        "WHATSAPP" -> sendQuickWhatsAppMessageWithNumber(context, order, phoneNumber)
    }
}
