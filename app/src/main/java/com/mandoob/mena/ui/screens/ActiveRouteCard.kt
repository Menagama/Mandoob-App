package com.mandoob.mena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.mandoob.mena.data.OrderStatus
import com.mandoob.mena.ui.theme.CancelledRed

@Composable
fun ActiveRouteCardHeader(
    order: Order,
    index: Int,
    statusGreen: Color,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
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
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "مشاركة الأوردر",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "تمسح",
                    tint = CancelledRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

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
}

@Composable
fun ActiveRouteCardAddressRow(order: Order) {
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
}

@Composable
fun ActiveRouteCardFinancialRow(order: Order, onEditClick: () -> Unit) {
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
            if (order.status == OrderStatus.PARTIAL.value) {
                Text(
                    text = "التسليم الجزئى (${order.collectedAmount?.toInt() ?: 0} ج.م)",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            } else if (order.status == OrderStatus.REJECTED_WITH_FEE.value) {
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
}

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
            ActiveRouteCardHeader(
                order = order,
                index = index,
                statusGreen = statusGreen,
                onShare = { shareOrderToWhatsApp(context, order) },
                onDelete = { showDeleteWarning = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActiveRouteCardAddressRow(order = order)

            Spacer(modifier = Modifier.height(12.dp))

            ActiveRouteCardFinancialRow(order = order, onEditClick = onEditClick)

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
                            launchDialerWithNumber(context, order.phoneNumber)
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
                            sendQuickSMSMessageWithNumber(context, order, order.phoneNumber)
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
                            sendQuickWhatsAppMessageWithNumber(context, order, order.phoneNumber)
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
