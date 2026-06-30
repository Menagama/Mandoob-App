package com.mandoob.mena.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.R
import com.mandoob.mena.data.Order
import com.mandoob.mena.data.OrderStatus
import com.mandoob.mena.ui.theme.CancelledRed

@Composable
fun OrderDetailsTopBar(order: Order, isDark: Boolean, onDismiss: () -> Unit) {
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
                    contentDescription = stringResource(R.string.string_ar_102),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.string_ar_83),
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
            OrderStatus.PENDING.value -> {
                if (isDark) Pair(Color(0xFF78350F).copy(alpha = 0.4f), Color(0xFFFBBF24))
                else Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
            }
            OrderStatus.DELIVERED.value -> {
                if (isDark) Pair(Color(0xFF064E3B).copy(alpha = 0.4f), Color(0xFF34D399))
                else Pair(Color(0xFFECFDF5), Color(0xFF047857))
            }
            OrderStatus.PARTIAL.value -> {
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
}

@Composable
fun OrderDetailsBottomBar(
    order: Order,
    isDark: Boolean,
    onSuccessClick: () -> Unit,
    onAdditionalOptionsClick: () -> Unit,
    onResetStatusClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 64.dp)
    ) {
        if (order.status == OrderStatus.PENDING.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSuccessClick,
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
                            text = stringResource(R.string.string_ar_84),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onAdditionalOptionsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, if (isDark) Color(0xFFEF4444).copy(alpha = 0.5f) else Color(0xFFFCA5A5)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDark) Color(0xFF7F1D1D).copy(alpha = 0.3f) else Color(0xFFFEF2F2),
                        contentColor = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.string_ar_85),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                    )
                }
            }
        } else {
            Button(
                onClick = onResetStatusClick,
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
                        text = stringResource(R.string.string_ar_86),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDetailsActionCards(
    order: Order,
    isDark: Boolean,
    context: Context,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clickable(onClick = onCallClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFE8F5E9)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFA5D6A7)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = stringResource(R.string.string_ar_21),
                    tint = if (isDark) Color(0xFF34D399) else Color(0xFF2E7D32),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.string_ar_4),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF34D399) else Color(0xFF2E7D32)
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clickable(onClick = onSmsClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFEBF5FF)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFC0E0FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
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
                    text = stringResource(R.string.string_ar_5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0084FF)
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clickable(onClick = onWhatsAppClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFEBFDF5)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF374151) else Color(0xFFA5D6A7)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    text = stringResource(R.string.string_ar_6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF10B981) else Color(0xFF0EA371)
                )
            }
        }
    }
}

@Composable
fun OrderDetailsAmountCard(order: Order, isDark: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFEFF6FF)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, if (isDark) Color(0xFF0369A1).copy(alpha = 0.4f) else Color(0xFFBFDBFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    text = stringResource(R.string.string_ar_87),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                )
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF1D4ED8),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${order.amount.toInt()} ج.م",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color(0xFF38BDF8) else Color(0xFF1D4ED8)
                )
                if (order.status == OrderStatus.PARTIAL.value) {
                    Text(
                        text = "التسليم الجزئى (${order.collectedAmount?.toInt() ?: 0} ج.م)",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (order.status == OrderStatus.REJECTED_WITH_FEE.value) {
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
    onUpdateNotes: (String?) -> Unit
) {
    val context = LocalContext.current
    var showPartialAmountSelector by remember { mutableStateOf(false) }
    var showFeeAmountSelector by remember { mutableStateOf(false) }
    var showAdditionalStatusOptions by remember { mutableStateOf(false) }
    var showSuccessConfirmation by remember { mutableStateOf(false) }
    
    var tempPartialAmount by remember { mutableStateOf(order.collectedAmount?.toString() ?: "0") }
    var tempFeeAmount by remember { mutableStateOf(order.deliveryFeeAmount?.toString() ?: "0") }

    var showEditNotesDialog by remember { mutableStateOf(false) }
    var tempNotesValue by remember(order.notes) { mutableStateOf(order.notes ?: "") }

    var showPhoneSelectorDialog by remember { mutableStateOf(false) }
    var pendingActionType by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            OrderDetailsTopBar(order = order, isDark = isDark, onDismiss = onDismiss)
        },
        bottomBar = {
            OrderDetailsBottomBar(
                order = order,
                isDark = isDark,
                onSuccessClick = { showSuccessConfirmation = true },
                onAdditionalOptionsClick = { showAdditionalStatusOptions = true },
                onResetStatusClick = {
                    onStatusChanged(OrderStatus.PENDING.value, null, null, false)
                    onDismiss()
                }
            )
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
            OrderDetailsActionCards(
                order = order,
                isDark = isDark,
                context = context,
                onCallClick = {
                    val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                    if (hasSecondPhone) {
                        pendingActionType = "CALL"
                        showPhoneSelectorDialog = true
                    } else {
                        launchDialerWithNumber(context, order.phoneNumber)
                    }
                },
                onSmsClick = {
                    val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                    if (hasSecondPhone) {
                        pendingActionType = "SMS"
                        showPhoneSelectorDialog = true
                    } else {
                        sendQuickSMSMessageWithNumber(context, order, order.phoneNumber)
                    }
                },
                onWhatsAppClick = {
                    val hasSecondPhone = !order.phoneNumber2.isNullOrEmpty()
                    if (hasSecondPhone) {
                        pendingActionType = "WHATSAPP"
                        showPhoneSelectorDialog = true
                    } else {
                        sendQuickWhatsAppMessageWithNumber(context, order, order.phoneNumber)
                    }
                }
            )

            OrderDetailsAmountCard(order = order, isDark = isDark)

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.string_ar_88),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                
                HorizontalDivider(color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9), thickness = 1.dp)

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
                                text = stringResource(R.string.string_ar_89),
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
                                text = stringResource(R.string.string_ar_90),
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
                    text = stringResource(R.string.string_ar_91),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                HorizontalDivider(color = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9), thickness = 1.dp)

                // 2. Order Notes Block (ملاحظات الأوردر)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDark) Color(0xFF064E3B).copy(alpha = 0.1f) else Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isDark) Color(0xFF065F46).copy(alpha = 0.3f) else Color(0xFFBBF7D0),
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
                                tint = if (isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.string_ar_75),
                                fontSize = 14.sp,
                                color = if (isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showEditNotesDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(if (isDark) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFFD1FAE5), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.string_ar_103),
                                tint = if (isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = order.notes ?: "لا توجد ملاحظات مسجلة للأوردر حالياً.",
                        fontSize = 14.sp,
                        color = if (order.notes != null) (if (isDark) Color.White else Color(0xFF1E293B)) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                        fontWeight = if (order.notes != null) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showEditNotesDialog) {
        AlertDialog(
            onDismissRequest = { showEditNotesDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.string_ar_92),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                OutlinedTextField(
                    value = tempNotesValue,
                    onValueChange = { tempNotesValue = it },
                    label = { Text(stringResource(R.string.string_ar_75), fontSize = 12.sp) },
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
                            onUpdateNotes(tempNotesValue)
                            showEditNotesDialog = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.string_ar_76), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { showEditNotesDialog = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.string_ar_2), fontWeight = FontWeight.Bold)
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
                    text = stringResource(R.string.string_ar_93),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.string_ar_94),
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
                            onStatusChanged(OrderStatus.DELIVERED.value, null, null, false)
                            showSuccessConfirmation = false
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.string_ar_77), color = Color.White, fontWeight = FontWeight.Bold)
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
                        Text(stringResource(R.string.string_ar_78), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    
                    TextButton(
                        onClick = { showSuccessConfirmation = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(R.string.string_ar_2), fontWeight = FontWeight.Bold)
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
                    text = stringResource(R.string.string_ar_95),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.string_ar_96),
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
                        Pair("رفض بدون مصاريف شحن", OrderStatus.REJECTED_NO_FEE.value),
                        Pair("رفض ودفع مصاريف شحن", OrderStatus.REJECTED_WITH_FEE.value),
                        Pair("لا يرد", OrderStatus.NO_ANSWER.value),
                        Pair("مؤجل", OrderStatus.POSTPONED.value),
                        Pair("لاغى", OrderStatus.CANCELLED.value),
                        Pair("إرجاع لـ جاري العمل", OrderStatus.PENDING.value)
                    ).filter { !(order.status == OrderStatus.PENDING.value && it.second == OrderStatus.PENDING.value) }
                    
                    otherStatuses.forEach { (label, statusStr) ->
                        Button(
                            onClick = {
                                showAdditionalStatusOptions = false
                                if (statusStr == OrderStatus.REJECTED_WITH_FEE.value) {
                                    showFeeAmountSelector = true
                                } else {
                                    onStatusChanged(statusStr, null, null, false)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (statusStr == OrderStatus.CANCELLED.value) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (statusStr == OrderStatus.CANCELLED.value) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
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
                        Text(stringResource(R.string.string_ar_79), fontWeight = FontWeight.Bold)
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
                    text = stringResource(R.string.string_ar_97),
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
                        label = { Text(stringResource(R.string.string_ar_80), fontSize = 12.sp) },
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
                                onStatusChanged(OrderStatus.PARTIAL.value, valAmt, null, false)
                                showPartialAmountSelector = false
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.string_ar_81), color = Color.White)
                    }

                    TextButton(
                        onClick = { showPartialAmountSelector = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.string_ar_2))
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
                    text = stringResource(R.string.string_ar_98),
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
                        text = stringResource(R.string.string_ar_99),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempFeeAmount,
                        onValueChange = { tempFeeAmount = it },
                        label = { Text(stringResource(R.string.string_ar_82), fontSize = 12.sp) },
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
                            onStatusChanged(OrderStatus.REJECTED_WITH_FEE.value, null, valAmt, false)
                            showFeeAmountSelector = false
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CancelledRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.string_ar_81), color = Color.White)
                    }

                    TextButton(
                        onClick = { showFeeAmountSelector = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.string_ar_2))
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
                text = stringResource(R.string.string_ar_100),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
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
                    text = stringResource(R.string.string_ar_101),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                // Button for phone number 1
                Button(
                    onClick = { onNumberSelected(phoneNumber1) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Text("الرقم الأساسي: $phoneNumber1", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Button for phone number 2
                Button(
                    onClick = { onNumberSelected(phoneNumber2) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("الرقم الثاني: $phoneNumber2", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.string_ar_2), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

fun launchDialerWithNumber(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.string_ar_104), Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickWhatsAppMessageWithNumber(context: Context, order: Order, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val formattedNum = if (cleanNumber.startsWith("0")) "2$cleanNumber" else cleanNumber
        
        val quickText = "مرحبا ${order.clientName} مندوب شركة الشحن يتواصل معك لايصال شحنتك ${order.amount.toInt()} ج.م"
        
        val url = "https://api.whatsapp.com/send?phone=$formattedNum&text=" + java.net.URLEncoder.encode(quickText, "UTF-8")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.string_ar_105), Toast.LENGTH_SHORT).show()
    }
}

fun sendQuickSMSMessageWithNumber(context: Context, order: Order, phoneNumber: String) {
    try {
        val quickText = "مرحبا ${order.clientName} مندوب شركة الشحن يتواصل معك لايصال شحنتك ${order.amount.toInt()} ج.م"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", quickText)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.string_ar_106), Toast.LENGTH_SHORT).show()
    }
}

fun executeCommunication(
    context: Context,
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

fun shareOrderToWhatsApp(context: Context, order: Order) {
    try {
        val statusText = when (order.status) {
            OrderStatus.PARTIAL.value -> "تسليم جزئي — تم تحصيل ${order.collectedAmount?.toInt() ?: 0} ج.م فقط"
            OrderStatus.REJECTED_WITH_FEE.value -> "رفض ودفع مصاريف شحن — تم دفع ${order.deliveryFeeAmount?.toInt() ?: 0} ج.م"
            else -> order.status
        }

        val templateText = """
            اسم العميل: ${order.clientName}
            رقم الموبايل: ${order.phoneNumber}
            العنوان: ${order.address}
            مبلغ التحصيل: ${order.amount.toInt()} ج.م
            حالة الأوردر: $statusText
        """.trimIndent()
        
        val url = "https://api.whatsapp.com/send?text=" + java.net.URLEncoder.encode(templateText, "UTF-8")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.string_ar_107), Toast.LENGTH_SHORT).show()
    }
}
