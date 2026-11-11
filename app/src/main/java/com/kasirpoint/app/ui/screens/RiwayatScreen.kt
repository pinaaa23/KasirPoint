package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kasirpoint.app.data.model.PaymentMethod
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionStatus
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.components.SearchFilterBar
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.BorderLight
import com.kasirpoint.app.ui.theme.GreenSuccess
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter
import com.kasirpoint.app.util.DateFormatter

@Composable
fun RiwayatScreen(
    transactions: List<Transaction> = emptyList(),
    onCancelTransaction: (Transaction) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var transactionToCancel by remember { mutableStateOf<Transaction?>(null) }

    val filteredTransactions = transactions.filter {
        searchQuery.isEmpty() || it.nomor.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SearchFilterBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTransactions.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.History,
                    title = "Belum Ada Riwayat",
                    message = if (transactions.isEmpty()) {
                        "Belum ada riwayat transaksi yang tercatat."
                    } else {
                        "Tidak ada transaksi yang cocok dengan nomor pencarian."
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTransactions) { trx ->
                        TransactionRowCard(
                            transaction = trx,
                            onClick = { selectedTransaction = trx }
                        )
                    }
                }
            }
        }

        // Transaction Detail Dialog
        selectedTransaction?.let { trx ->
            TransactionDetailDialog(
                transaction = trx,
                onDismiss = { selectedTransaction = null },
                onCancelClick = {
                    transactionToCancel = trx
                    selectedTransaction = null
                }
            )
        }

        // Cancel Confirmation Dialog
        transactionToCancel?.let { trx ->
            AlertDialog(
                onDismissRequest = { transactionToCancel = null },
                title = { Text("Batalkan Transaksi") },
                text = { Text("Apakah Anda yakin ingin membatalkan transaksi '${trx.nomor}'? Tindakan ini akan mengembalikan stok produk.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onCancelTransaction(trx)
                            transactionToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                    ) {
                        Text("Batalkan Transaksi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { transactionToCancel = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionRowCard(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.nomor,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${DateFormatter.formatDateTime(transaction.waktu)} • ${transaction.tipePesanan.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatRupiah(transaction.total),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                val statusColor = if (transaction.status == TransactionStatus.SELESAI) GreenSuccess else RedDanger
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Text(
                        text = transaction.status.displayName,
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onCancelClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Detail Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("No. TRX", style = MaterialTheme.typography.bodyMedium)
                    Text(transaction.nomor, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Waktu", style = MaterialTheme.typography.bodyMedium)
                    Text(DateFormatter.formatDateTime(transaction.waktu), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tipe Pesanan", style = MaterialTheme.typography.bodyMedium)
                    Text(transaction.tipePesanan.displayName, style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status", style = MaterialTheme.typography.bodyMedium)
                    val color = if (transaction.status == TransactionStatus.SELESAI) GreenSuccess else RedDanger
                    Text(transaction.status.displayName, style = MaterialTheme.typography.bodyMedium.copy(color = color, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Daftar Item:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                transaction.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.jumlah}x ${item.nama}" + if (item.varian != null) " (${item.varian})" else "", modifier = Modifier.weight(1f))
                        Text(CurrencyFormatter.formatRupiah(item.harga * item.jumlah), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(CurrencyFormatter.formatRupiah(transaction.subtotal), style = MaterialTheme.typography.bodyMedium)
                }
                if (transaction.pajak > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pajak", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatRupiah(transaction.pajak), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(CurrencyFormatter.formatRupiah(transaction.total), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Metode Bayar", style = MaterialTheme.typography.bodyMedium)
                    Text(transaction.metodeBayar.displayName, style = MaterialTheme.typography.bodyMedium)
                }

                if (transaction.metodeBayar == PaymentMethod.TUNAI) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Uang Diterima", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatRupiah(transaction.uangDiterima), style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kembalian", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatRupiah(transaction.kembalian), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (transaction.status == TransactionStatus.SELESAI) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDanger),
                            shape = CircleShape
                        ) {
                            Text("Batalkan Transaksi")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}
