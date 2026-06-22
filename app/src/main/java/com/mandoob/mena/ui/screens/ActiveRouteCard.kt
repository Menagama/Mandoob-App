package com.mandoob.mena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    isSortingEnabled: Boolean = false,
    isFastMoveEnabled: Boolean = false,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var showDropdown by remember(order.id) { mutableStateOf(false) }
    var showPartialInput by remember(order.id) { mutableStateOf(false) }
    var showFeeInput by remember(order.id) { mutableStateOf(false) }
    
    // Hold temp inputs for amounts
    var partialAmountText by remember(order.id) { mutableStateOf(order.collectedAmount?.toString() ?: order.amount.toString()) }
    var feeAmountText by remember(order.id) { mutableStateOf(order.deliveryFeeAmount?.toString() ?: "25") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
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
                                tint = GreenSuccess,
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

                    // Up and Down Arrow buttons (ONLY when isSortingEnabled is true)
                    if (isSortingEnabled) {
                        IconButton(
                            onClick = { onMoveUp?.invoke() },
                            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "نقل لأعلى",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { onMoveDown?.invoke() },
                            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "نقل لأسفل",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Fourth Row: Three quick communication keys, and Status selection menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Dialer Button
                    IconButton(
                        onClick = { launchDialer(context, order.phoneNumber) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFE8F8F5), RoundedCornerShape(14.dp))
                            .border(1.5.dp, Color(0xFFA7E8D4), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال سريع للعميل",
                            tint = Color(0xFF0EA371),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Quick Message (SMS)
                    IconButton(
                        onClick = { sendQuickSMSMessage(context, order) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFEBF5FF), RoundedCornerShape(14.dp))
                            .border(1.5.dp, Color(0xFFC0E0FF), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = Color(0xFF0084FF),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 3. WhatsApp Quick Message
                    IconButton(
                        onClick = { sendQuickWhatsAppMessage(context, order) },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFEBFDF5), RoundedCornerShape(14.dp))
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

                // Status selector
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
                                    Order.STATUS_PENDING -> MaterialTheme.colorScheme.primaryContainer
                                    Order.STATUS_DELIVERED -> GreenLight
                                    Order.STATUS_CANCELLED -> CancelledLight
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                1.5.dp,
                                when (order.status) {
                                    Order.STATUS_PENDING -> MaterialTheme.colorScheme.primary
                                    Order.STATUS_DELIVERED -> GreenSuccess
                                    Order.STATUS_CANCELLED -> CancelledRed
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
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
                            color = when (order.status) {
                                Order.STATUS_PENDING -> MaterialTheme.colorScheme.primary
                                Order.STATUS_DELIVERED -> Color(0xFF2E7D32)
                                Order.STATUS_CANCELLED -> CancelledRed
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
                                text = { Text(mode, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp) },
                                onClick = {
                                    showDropdown = false
                                    if (mode == Order.STATUS_PARTIAL) {
                                        showPartialInput = true
                                        showFeeInput = false
                                    } else if (mode == Order.STATUS_REJECTED_WITH_FEE) {
                                        showFeeInput = true
                                        showPartialInput = false
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

            // Inline inputs
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
                        },
                        label = { Text("قيمة التحصيل", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val valAmt = partialAmountText.toDoubleOrNull() ?: 0.0
                            onStatusChanged(Order.STATUS_PARTIAL, valAmt, null, false)
                            showPartialInput = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("موافق", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

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
                        },
                        label = { Text("مصاريف الشحن", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = CancelledRed,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val valAmt = feeAmountText.toDoubleOrNull() ?: 25.0
                            onStatusChanged(Order.STATUS_REJECTED_WITH_FEE, null, valAmt, false)
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
