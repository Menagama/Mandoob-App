package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.data.Order
import com.mandoob.mena.ui.theme.BluePrimary
import com.mandoob.mena.viewmodel.OrderViewModel

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

        // 1. Header Card with profile context info and settings cog
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..."
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
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
                        .clickable { if (hasText) searchQuery = "" }
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

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأوردرات الناجحة والمكتملة (${filteredOrders.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Scrollable list of orders
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
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "عندما تقوم بتوصيل الأوردر وتحديث حالته إلى (تم التسليم)، سيظهر إنجازك هنا.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}

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

        // 1. Header Card with profile context info and settings cog
        HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Beautiful Search Field "بحث باسم العميل أو رقم الهاتف..."
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
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
                        .clickable { if (hasText) searchQuery = "" }
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

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأوردرات الملغاة والمؤجلة (${filteredOrders.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "لا توجد أوردرات ملغاة أو مؤجلة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "كل شيء يسير على ما يرام! لا توجد طلبات معلقة بانتظار الرد، مؤجلة أو ملغاة حالياً.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}
