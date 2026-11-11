package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.PaymentMethod
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionStatus
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.BorderLight
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter
import com.kasirpoint.app.util.DateFormatter
import java.util.Calendar

enum class ReportPeriod(val displayName: String) {
    HARI_INI("Hari ini"),
    MINGGU_INI("Minggu ini"),
    BULAN_INI("Bulan ini"),
    PILIH_TANGGAL("Pilih tanggal")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    transactions: List<Transaction> = emptyList()
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.HARI_INI) }
    var customTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Filter valid completed transactions
    val validTransactions = transactions.filter { it.status == TransactionStatus.SELESAI }

    val filteredByPeriod = validTransactions.filter { trx ->
        val calTrx = Calendar.getInstance().apply { timeInMillis = trx.waktu }
        val calNow = Calendar.getInstance()

        when (selectedPeriod) {
            ReportPeriod.HARI_INI -> {
                calTrx.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                        calTrx.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)
            }
            ReportPeriod.MINGGU_INI -> {
                calTrx.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                        calTrx.get(Calendar.WEEK_OF_YEAR) == calNow.get(Calendar.WEEK_OF_YEAR)
            }
            ReportPeriod.BULAN_INI -> {
                calTrx.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                        calTrx.get(Calendar.MONTH) == calNow.get(Calendar.MONTH)
            }
            ReportPeriod.PILIH_TANGGAL -> {
                val calCustom = Calendar.getInstance().apply { timeInMillis = customTimestamp }
                calTrx.get(Calendar.YEAR) == calCustom.get(Calendar.YEAR) &&
                        calTrx.get(Calendar.DAY_OF_YEAR) == calCustom.get(Calendar.DAY_OF_YEAR)
            }
        }
    }

    val totalOmzet = filteredByPeriod.sumOf { it.total }
    val totalTrx = filteredByPeriod.size
    val rataRata = if (totalTrx > 0) totalOmzet / totalTrx else 0.0

    // Payment Methods Breakdown
    val omzetTunai = filteredByPeriod.filter { it.metodeBayar == PaymentMethod.TUNAI }.sumOf { it.total }
    val omzetQRIS = filteredByPeriod.filter { it.metodeBayar == PaymentMethod.QRIS }.sumOf { it.total }
    val omzetTransfer = filteredByPeriod.filter { it.metodeBayar == PaymentMethod.TRANSFER }.sumOf { it.total }

    // Top Selling Products Calculation
    val productSalesMap = mutableMapOf<String, Int>()
    filteredByPeriod.forEach { trx ->
        trx.items.forEach { item ->
            val currentCount = productSalesMap[item.nama] ?: 0
            productSalesMap[item.nama] = currentCount + item.jumlah
        }
    }
    val topProducts = productSalesMap.entries.sortedByDescending { it.value }.take(5)

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Laporan Penjualan",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Period Selection Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReportPeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryOrange else SurfaceWhite)
                            .clickable {
                                selectedPeriod = period
                                if (period == ReportPeriod.PILIH_TANGGAL) {
                                    showDatePickerDialog = true
                                }
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (period == ReportPeriod.PILIH_TANGGAL && selectedPeriod == ReportPeriod.PILIH_TANGGAL) {
                                DateFormatter.formatDate(customTimestamp)
                            } else {
                                period.displayName
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SurfaceWhite else TextDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredByPeriod.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Assessment,
                    title = "Belum Ada Data Penjualan",
                    message = "Belum ada transaksi penjualan pada periode ini.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                )
            } else {
                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportStatCard(
                        title = "Total Omzet",
                        value = CurrencyFormatter.formatRupiah(totalOmzet),
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    ReportStatCard(
                        title = "Total Transaksi",
                        value = "$totalTrx TRX",
                        icon = Icons.Default.Receipt,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ReportStatCard(
                    title = "Rata-rata per Transaksi",
                    value = CurrencyFormatter.formatRupiah(rataRata),
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Daily Omzet Chart Representation (PRD 10.9)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Grafik Omzet Penjualan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(16.dp))

                        val maxOmzet = filteredByPeriod.maxOfOrNull { it.total } ?: 1.0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            filteredByPeriod.takeLast(7).forEach { trx ->
                                val heightFraction = (trx.total / maxOmzet).toFloat().coerceIn(0.1f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .background(PrimaryOrange)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = DateFormatter.formatTime(trx.waktu),
                                        style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Breakdown per Payment Method (PRD 10.9)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Rincian Metode Pembayaran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentMethodBreakdownRow("Tunai", omzetTunai, Icons.Default.Money)
                        HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))
                        PaymentMethodBreakdownRow("QRIS", omzetQRIS, Icons.Default.QrCode)
                        HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))
                        PaymentMethodBreakdownRow("Transfer", omzetTransfer, Icons.Default.CreditCard)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Top Selling Products (PRD 10.9)
                if (topProducts.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Produk Terlaris", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            topProducts.forEachIndexed { index, entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(ChipPeach),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${index + 1}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(entry.key, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                                    }
                                    Text("${entry.value} Terjual", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ActionBrown))
                                }
                                if (index < topProducts.size - 1) {
                                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom Date Picker Dialog
        if (showDatePickerDialog) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = customTimestamp)
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { customTimestamp = it }
                            showDatePickerDialog = false
                        }
                    ) {
                        Text("Pilih")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun PaymentMethodBreakdownRow(
    title: String,
    amount: Double,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ChipPeach),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Text(CurrencyFormatter.formatRupiah(amount), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ReportStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ChipPeach),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
            }
        }
    }
}
