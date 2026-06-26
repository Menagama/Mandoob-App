package com.mandoob.mena.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.data.Order
import com.mandoob.mena.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteReorderScreen(
    viewModel: OrderViewModel,
    onDismiss: () -> Unit
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF121212)
    
    val pendingOrders = remember(allOrders) {
        allOrders.filter { it.status == Order.STATUS_PENDING }
            .sortedWith(compareBy({ it.sequenceNumber }, { it.id }))
    }

    var itemsList by remember { mutableStateOf<List<Order>>(emptyList()) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    // Sync from database list if not dragging
    LaunchedEffect(pendingOrders) {
        if (draggedIndex == null) {
            itemsList = pendingOrders
        }
    }

    // Dynamic color coordination
    val screenBg = if (isDark) Color(0xFF000000) else Color(0xFFF8FAFC)
    val appBarBg = if (isDark) Color(0xFF121212) else Color.White
    val bottomBg = if (isDark) Color(0xFF121212) else Color.White
    val cardBg = if (isDark) Color(0xFF121212) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF262626) else Color(0xFFE2E8F0)
    
    val accentColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF139CB5)
    val dividerColor = if (isDark) Color(0xFF262626) else Color(0xFFCBD5E1)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "ترتيب عناوين خط السير",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveRouteSequence(itemsList)
                            onDismiss()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = appBarBg
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bottomBg)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.saveRouteSequence(itemsList)
                            Toast.makeText(context, "تم حفظ وترتيب خط السير بنجاح", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "حفظ الترتيب",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "تم",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(screenBg)
                    .padding(innerPadding)
            ) {
                if (itemsList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أوردرات جاري العمل لترتيبها!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = subtextColor
                        )
                    }
                } else {
                    // Parent-Level Drag and Drop LazyColumn
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .pointerInput(itemsList) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { offset ->
                                        val visibleItems = listState.layoutInfo.visibleItemsInfo
                                        val touchedItem = visibleItems.find { itemInfo ->
                                            // itemInfo.offset is coordinates inside the viewable list
                                            // We add a 12px padding coverage to gap selections
                                            val top = itemInfo.offset
                                            val bottom = itemInfo.offset + itemInfo.size + 12
                                            offset.y.toInt() in top..bottom
                                        }
                                        if (touchedItem != null && touchedItem.index < itemsList.size) {
                                            draggedIndex = touchedItem.index
                                            dragOffset = 0f
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    },
                                    onDragEnd = {
                                        draggedIndex = null
                                        dragOffset = 0f
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragCancel = {
                                        draggedIndex = null
                                        dragOffset = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val currentDragIdx = draggedIndex ?: return@detectDragGesturesAfterLongPress
                                        dragOffset += dragAmount.y

                                        val visibleItems = listState.layoutInfo.visibleItemsInfo
                                        val draggedItem = visibleItems.find { it.index == currentDragIdx }

                                        if (draggedItem != null) {
                                            val currentList = itemsList
                                            val currentItemY = draggedItem.offset + dragOffset

                                            // Dragging down: Check crossing center of the next item
                                            if (dragOffset > 0 && currentDragIdx < currentList.size - 1) {
                                                val nextItem = visibleItems.find { it.index == currentDragIdx + 1 }
                                                if (nextItem != null) {
                                                    val nextCenter = nextItem.offset + nextItem.size / 2
                                                    val currentCenter = currentItemY + draggedItem.size / 2
                                                    if (currentCenter > nextCenter) {
                                                        val mutable = currentList.toMutableList()
                                                        val temp = mutable[currentDragIdx]
                                                        mutable[currentDragIdx] = mutable[currentDragIdx + 1]
                                                        mutable[currentDragIdx + 1] = temp
                                                        itemsList = mutable
                                                        draggedIndex = currentDragIdx + 1
                                                        dragOffset -= nextItem.size + 12 // shift drag offset relative to new position index
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    }
                                                }
                                            }
                                            // Dragging up: Check crossing center of the previous item
                                            else if (dragOffset < 0 && currentDragIdx > 0) {
                                                val prevItem = visibleItems.find { it.index == currentDragIdx - 1 }
                                                if (prevItem != null) {
                                                    val prevCenter = prevItem.offset + prevItem.size / 2
                                                    val currentCenter = currentItemY + draggedItem.size / 2
                                                    if (currentCenter < prevCenter) {
                                                        val mutable = currentList.toMutableList()
                                                        val temp = mutable[currentDragIdx]
                                                        mutable[currentDragIdx] = mutable[currentDragIdx - 1]
                                                        mutable[currentDragIdx - 1] = temp
                                                        itemsList = mutable
                                                        draggedIndex = currentDragIdx - 1
                                                        dragOffset += prevItem.size + 12 // shift drag offset relative to new position index
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            },
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = itemsList,
                            key = { _, order -> order.id }
                        ) { index, order ->
                            val isDragged = index == draggedIndex
                            val itemElevation by animateDpAsState(targetValue = if (isDragged) 8.dp else 0.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        if (isDragged) {
                                            translationY = dragOffset
                                            scaleX = 1.04f
                                            scaleY = 1.04f
                                            shadowElevation = itemElevation.toPx()
                                        }
                                    }
                                    .background(
                                        color = if (isDragged) {
                                            if (isDark) Color(0xFF1E293B) else Color(0xFFE0F2FE)
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Column 1 (Right): Index box & up arrow button (Placed 1st for RTL layout)
                                Column(
                                    modifier = Modifier.wrapContentWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Index box (Order number at top)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(1.2.dp, if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                                            .background(if (isDark) Color(0xFF0C4A6E).copy(alpha = 0.2f) else Color(0xFFE0F2FE), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (index + 1).toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                                        )
                                    }

                                    // Up arrow button (at bottom)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(
                                                1.2.dp,
                                                if (index > 0) {
                                                    if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFBAE6FD)
                                                } else {
                                                    if (isDark) Color(0xFF262626) else Color(0xFFE2E8F0)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                if (index > 0) {
                                                    if (isDark) Color(0xFF0C4A6E).copy(alpha = 0.2f) else Color(0xFFE0F2FE)
                                                } else {
                                                    if (isDark) Color(0xFF1A1A1E) else Color(0xFFF1F5F9)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable(enabled = index > 0) {
                                                val mutable = itemsList.toMutableList()
                                                val item = mutable.removeAt(index)
                                                mutable.add(0, item)
                                                itemsList = mutable
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "للأعلى",
                                            tint = if (index > 0) {
                                                if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                                            } else {
                                                if (isDark) Color(0xFF404040) else Color(0xFFCBD5E1)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Column 2 (Middle): Order details with adaptive tags & colors
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = BorderStroke(1.dp, cardBorderColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start, // Start is right side in RTL
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // "تسليم" tag is shown first dynamically inside RTL row
                                            val deliveryBg = if (isDark) Color(0xFF0B2545) else Color(0xFFE0F2FE)
                                            val deliveryBorder = if (isDark) Color(0xFF139CB5).copy(alpha = 0.5f) else Color(0xFFBAE6FD)
                                            val deliveryText = if (isDark) Color(0xFF38BDF8) else Color(0xFF139CB5)

                                            Row(
                                                modifier = Modifier
                                                    .border(1.dp, deliveryBorder, RoundedCornerShape(100.dp))
                                                    .background(deliveryBg, RoundedCornerShape(100.dp))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "تسليم",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = deliveryText
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Address (Naturally aligned RTL Start, which is Right)
                                        Text(
                                            text = order.address,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Column 3 (Left): Drag handles & manual down arrow buttons (Placed 3rd for RTL layout)
                                Column(
                                    modifier = Modifier.wrapContentWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Drag visual indicator (at top)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(1.2.dp, if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                                            .background(if (isDark) Color(0xFF0C4A6E).copy(alpha = 0.2f) else Color(0xFFE0F2FE), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "اسحب للترتيب",
                                            tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Down arrow button (at bottom)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(
                                                1.2.dp,
                                                if (index < itemsList.size - 1) {
                                                    if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFBAE6FD)
                                                } else {
                                                    if (isDark) Color(0xFF262626) else Color(0xFFE2E8F0)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                if (index < itemsList.size - 1) {
                                                    if (isDark) Color(0xFF0C4A6E).copy(alpha = 0.2f) else Color(0xFFE0F2FE)
                                                } else {
                                                    if (isDark) Color(0xFF1A1A1E) else Color(0xFFF1F5F9)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable(enabled = index < itemsList.size - 1) {
                                                val mutable = itemsList.toMutableList()
                                                val item = mutable.removeAt(index)
                                                mutable.add(item)
                                                itemsList = mutable
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = "للأسفل",
                                            tint = if (index < itemsList.size - 1) {
                                                if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                                            } else {
                                                if (isDark) Color(0xFF404040) else Color(0xFFCBD5E1)
                                            },
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
    }
}
