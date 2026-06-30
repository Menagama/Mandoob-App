package com.mandoob.mena.ui.screens

import com.mandoob.mena.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mandoob.mena.ui.theme.GreenSuccess
import com.mandoob.mena.ui.theme.PurpleStar
import com.mandoob.mena.viewmodel.OrderViewModel

@Composable
fun HomeScreen(
    viewModel: OrderViewModel,
    onOpenAddOrder: () -> Unit,
    onOpenImportExcel: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Collect from ViewModel
    val netRemittance by viewModel.netRemittanceToOffice.collectAsState()
    val walletCash by viewModel.totalCashInWallet.collectAsState()
    val commissions by viewModel.totalCommissions.collectAsState()
    val completedCount by viewModel.completedOrdersCount.collectAsState()
    val totalCount by viewModel.totalOrdersCount.collectAsState()

    var showDeleteAllConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Spacing for status bar area
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 1. Header Zone (Captain info & settings cog)
            item {
                HeaderCard(viewModel = viewModel, onOpenSettings = onOpenSettings, showSettingsCog = false)
            }

            // 2. Net Remittance to Office ("صافي التوريد للمكتب")
            item {
                NetRemittanceCard(netRemittance = netRemittance)
            }

            // 3. Double small cards: In Wallet Cash ("في الحقيبة") and Total Commissions ("إجمالي عمولاتك")
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cash in Wallet (placed first so it shows on the Right under RTL)
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = stringResource(R.string.string_ar_12),
                            value = "${walletCash.toInt()} ج.م",
                            subtext = stringResource(R.string.string_ar_47),
                            icon = Icons.Default.Payments,
                            iconColor = GreenSuccess,
                            testTagPrefix = "wallet"
                        )
                    }

                    // Total Commissions (placed second so it shows on the Left under RTL)
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = stringResource(R.string.string_ar_13),
                            value = "${commissions.toInt()} ج.م",
                            subtext = stringResource(R.string.string_ar_48),
                            icon = Icons.Default.Star,
                            iconColor = PurpleStar,
                            testTagPrefix = "commissions"
                        )
                    }
                }
            }

            // 4. Interactive routing track card
            item {
                InteractiveRouteProgressCard(
                    completed = completedCount,
                    total = totalCount
                )
            }

            // Margin bottom to clear overlapping floating action bar
            item { Spacer(modifier = Modifier.height(160.dp)) }
        }

        // Beautiful glassmorphic modern floating action bar containing all three actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // New "حذف الكل" Button (Red gradient, full width, above the other two buttons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFEF4444),
                                Color(0xFFF87171)
                            )
                        )
                    )
                    .clickable { showDeleteAllConfirm = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.string_ar_49),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Existing Row of Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First Button: Add New Order (Vibrant blue background with custom gradient)
                Row(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0EA5E9),
                                    Color(0xFF38BDF8)
                                )
                            )
                        )
                        .clickable { onOpenAddOrder() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.string_ar_50),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Second Button: Import from Excel (Sleek professional green gradient)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF34D399)
                                )
                            )
                        )
                        .clickable { onOpenImportExcel() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.string_ar_51),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Confirmation dialog for deleting all orders
        if (showDeleteAllConfirm) {
            val context = LocalContext.current
            AlertDialog(
                onDismissRequest = { showDeleteAllConfirm = false },
                title = {
                    Text(
                        text = stringResource(R.string.string_ar_52),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.string_ar_53),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearItinerary()
                            showDeleteAllConfirm = false
                            Toast.makeText(context, context.getString(R.string.string_ar_54), Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text(stringResource(R.string.string_ar_46), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllConfirm = false }) {
                        Text(stringResource(R.string.string_ar_2), color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    }
}
