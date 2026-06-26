package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.data.Order
import com.mandoob.mena.ui.theme.BlueLight
import com.mandoob.mena.ui.theme.BluePrimary
import com.mandoob.mena.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRouteScreen(viewModel: OrderViewModel, onOpenSettings: () -> Unit) {
    val allOrders by viewModel.allOrders.collectAsState()
    val isSortingEnabled by viewModel.isSortingEnabled.collectAsState()
    val isFastMoveEnabled by viewModel.isFastMoveEnabled.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setSearchQuery("")
        }
    }

    var editingOrder by remember { mutableStateOf<Order?>(null) }
    var showReorderScreen by remember { mutableStateOf(false) }
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = جاري العمل, 1 = الناجحة, 2 = الملغاة

    // Count pending, success and cancelled categories
    val countPending = allOrders.count { it.status == Order.STATUS_PENDING }
    val countSuccess = allOrders.count { it.status == Order.STATUS_DELIVERED || it.status == Order.STATUS_PARTIAL }
    val countCancelled = allOrders.count { it.isCancelledOrPostponed() }

    // Filter by the selected sub-tab
    val activeOrders = remember(allOrders, selectedSubTab) {
        when (selectedSubTab) {
            0 -> allOrders.filter { it.status == Order.STATUS_PENDING }
            1 -> allOrders.filter { it.status == Order.STATUS_DELIVERED || it.status == Order.STATUS_PARTIAL }
            else -> allOrders.filter { it.isCancelledOrPostponed() }
        }
    }

    // Apply search query and automatic routing sequence
    val filteredAndSortedOrders = remember(activeOrders, searchQuery, isSortingEnabled) {
        val filtered = activeOrders.filter { order ->
            if (searchQuery.isBlank()) true
            else {
                order.clientName.contains(searchQuery, ignoreCase = true) ||
                order.phoneNumber.contains(searchQuery)
            }
        }
        if (filtered.any { it.isSequenceArranged }) {
            filtered.sortedWith(compareBy({ it.sequenceNumber }, { it.id }))
        } else {
            if (isSortingEnabled) {
                filtered.sortedWith(compareBy({ it.address }, { it.id }))
            } else {
                filtered.sortedBy { it.id }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Header Card with profile context info and settings cog
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..." with Dynamic changing search/close icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("بحث باسم العميل أو رقم الهاتف...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            singleLine = true,
            leadingIcon = {
                val hasText = searchQuery.isNotEmpty()
                Icon(
                    imageVector = if (hasText) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (hasText) "مسح البحث" else "بحث",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (hasText) {
                                viewModel.setSearchQuery("")
                            }
                        }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Sub-Tabs under search box (as shown in the user's design)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("جاري العمل", countPending, 0),
                Triple("الناجحة", countSuccess, 1),
                Triple("الملغاة", countCancelled, 2)
            )

            tabs.forEach { (title, count, index) ->
                val isSelected = selectedSubTab == index
                
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedSubTab = index }
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count and Route Sorting Sequence Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headerTitle = when (selectedSubTab) {
                0 -> "أوردرات خط السير الحالية"
                1 -> "الأوردرات الناجحة"
                else -> "الأوردرات الملغاة والمؤجلة"
            }

            Text(
                text = "$headerTitle (${filteredAndSortedOrders.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sorting Reorder Screen Button as custom double-arrow icon (down and up)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { showReorderScreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "ترتيب خط السير",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "ترتيب خط السير",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Scrollable list of orders
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredAndSortedOrders.isEmpty()) {
                item {
                    val emptyMessage = when (selectedSubTab) {
                        0 -> "لا توجد أوردرات جاري العمل حالياً!"
                        1 -> "لا توجد أوردرات ناجحة حالياً!"
                        else -> "لا توجد أوردرات ملغاة أو مؤجلة حالياً!"
                    }
                    EmptyStateView(
                        if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة لبحثك!"
                        else emptyMessage
                    )
                }
            } else {
                items(
                    count = filteredAndSortedOrders.size,
                    key = { index -> filteredAndSortedOrders[index].id }
                ) { i ->
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
                        },
                        onUpdateNotes = { updateNotes, courierNotes ->
                            viewModel.updateOrderNotes(order.id, updateNotes, courierNotes)
                        },
                        isSortingEnabled = isSortingEnabled,
                        isFastMoveEnabled = isFastMoveEnabled,
                        onMoveUp = {
                            viewModel.moveOrder(order.id, true, isFastMoveEnabled, filteredAndSortedOrders)
                        },
                        onMoveDown = {
                            viewModel.moveOrder(order.id, false, isFastMoveEnabled, filteredAndSortedOrders)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
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

        if (showReorderScreen) {
            RouteReorderScreen(
                viewModel = viewModel,
                onDismiss = { showReorderScreen = false }
            )
        }
    }
}
