package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueDark
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PurpleStar
import com.example.viewmodel.OrderViewModel

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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            title = "في الحقيبة (كاش)",
                            value = "${walletCash.toInt()} ج.م",
                            subtext = "كاش تم تحصيله فعلياً",
                            icon = Icons.Default.Payments,
                            iconColor = GreenSuccess,
                            testTagPrefix = "wallet"
                        )
                    }

                    // Total Commissions (placed second so it shows on the Left under RTL)
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "إجمالي عمولاتك",
                            value = "${commissions.toInt()} ج.م",
                            subtext = "إجمالي عمولاتك المستحقة",
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

            // Margin bottom to clear overlapping floating action buttons
            item { Spacer(modifier = Modifier.height(160.dp)) }
        }

        // Floating stacked action buttons matching the layout perfectly
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // First FAB: Add New Order
            Row(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(BlueDark)
                    .clickable { onOpenAddOrder() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "إضافة أوردر جديد",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Second FAB: Import from Excel
            Row(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B5E20)) // Dark green matching excel style
                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(16.dp))
                    .clickable { onOpenImportExcel() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "استيراد من إكسيل",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
