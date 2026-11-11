package com.kasirpoint.app.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.PaymentMethod
import com.kasirpoint.app.data.model.ReceiptTemplate
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionItem
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BorderLight
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.KasirPointTheme
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter
import com.kasirpoint.app.util.DateFormatter
import java.lang.StringBuilder

@Composable
fun ReceiptView(
    transaction: Transaction,
    businessProfile: BusinessProfile,
    receiptTemplate: ReceiptTemplate,
    onNewTransactionClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val columnModifier = if (scrollable) {
        modifier.fillMaxWidth().verticalScroll(scrollState)
    } else {
        modifier.fillMaxWidth()
    }

    Column(
        modifier = columnModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Receipt Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo
                if (receiptTemplate.tampilLogo) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(ChipPeach, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Business Info
                if (receiptTemplate.tampilNamaUsaha) {
                    Text(
                        text = if (businessProfile.namaUsaha.isNotEmpty()) businessProfile.namaUsaha else "Kasir Point",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                }

                if (receiptTemplate.tampilAlamat && businessProfile.alamat.isNotEmpty()) {
                    Text(
                        text = businessProfile.alamat,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                if (receiptTemplate.tampilTelepon && businessProfile.telepon.isNotEmpty()) {
                    Text(
                        text = "Telp: ${businessProfile.telepon}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Transaction Meta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("No. TRX:", style = MaterialTheme.typography.bodyMedium)
                    Text(transaction.nomor, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Waktu:", style = MaterialTheme.typography.bodyMedium)
                    Text(DateFormatter.formatDateTime(transaction.waktu), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tipe Pesanan:", style = MaterialTheme.typography.bodyMedium)
                    Text(transaction.tipePesanan.displayName, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Items List
                transaction.items.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.nama + if (item.varian != null) " (${item.varian})" else "",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(item.harga * item.jumlah),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "${item.jumlah} x ${CurrencyFormatter.formatRupiah(item.harga)}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Subtotal, Pajak, Total
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

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        CurrencyFormatter.formatRupiah(transaction.total),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark)
                    )
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

                if (receiptTemplate.tampilCatatan && transaction.catatan.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Catatan: ${transaction.catatan}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                        textAlign = TextAlign.Center
                    )
                }

                if (receiptTemplate.pesanPenutup.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = receiptTemplate.pesanPenutup,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Share & New Transaction)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bagikan Struk (Share Sheet)
            OutlinedButton(
                onClick = {
                    val shareText = buildReceiptShareText(transaction, businessProfile, receiptTemplate)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Struk Transaksi ${transaction.nomor}")
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan Struk via"))
                },
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagikan Struk", style = MaterialTheme.typography.labelLarge.copy(color = ActionBrown))
            }

            // Transaksi Baru Button
            Button(
                onClick = onNewTransactionClick,
                colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Transaksi Baru", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun buildReceiptShareText(
    transaction: Transaction,
    profile: BusinessProfile,
    template: ReceiptTemplate
): String {
    val sb = StringBuilder()
    if (template.tampilNamaUsaha) sb.appendLine(profile.namaUsaha.ifEmpty { "Kasir Point" })
    if (template.tampilAlamat && profile.alamat.isNotEmpty()) sb.appendLine(profile.alamat)
    if (template.tampilTelepon && profile.telepon.isNotEmpty()) sb.appendLine("Telp: ${profile.telepon}")
    sb.appendLine("--------------------------------")
    sb.appendLine("No: ${transaction.nomor}")
    sb.appendLine("Waktu: ${DateFormatter.formatDateTime(transaction.waktu)}")
    sb.appendLine("Tipe: ${transaction.tipePesanan.displayName}")
    sb.appendLine("--------------------------------")
    transaction.items.forEach { item ->
        val vLabel = if (item.varian != null) " (${item.varian})" else ""
        sb.appendLine("${item.nama}$vLabel")
        sb.appendLine("  ${item.jumlah} x ${CurrencyFormatter.formatRupiah(item.harga)} = ${CurrencyFormatter.formatRupiah(item.harga * item.jumlah)}")
    }
    sb.appendLine("--------------------------------")
    sb.appendLine("Subtotal: ${CurrencyFormatter.formatRupiah(transaction.subtotal)}")
    if (transaction.pajak > 0) sb.appendLine("Pajak: ${CurrencyFormatter.formatRupiah(transaction.pajak)}")
    sb.appendLine("Total: ${CurrencyFormatter.formatRupiah(transaction.total)}")
    sb.appendLine("Metode: ${transaction.metodeBayar.displayName}")
    if (transaction.metodeBayar == PaymentMethod.TUNAI) {
        sb.appendLine("Bayar: ${CurrencyFormatter.formatRupiah(transaction.uangDiterima)}")
        sb.appendLine("Kembali: ${CurrencyFormatter.formatRupiah(transaction.kembalian)}")
    }
    if (template.pesanPenutup.isNotEmpty()) {
        sb.appendLine("--------------------------------")
        sb.appendLine(template.pesanPenutup)
    }
    return sb.toString()
}

@Preview(showBackground = true)
@Composable
fun ReceiptViewPreview() {
    KasirPointTheme {
        ReceiptView(
            transaction = Transaction(
                nomor = "TRX-20261111-001",
                subtotal = 33000.0,
                pajak = 3300.0,
                total = 36300.0,
                uangDiterima = 50000.0,
                kembalian = 13700.0,
                items = listOf(
                    TransactionItem(nama = "Cappuccino", harga = 18000.0, varian = "Large", jumlah = 1),
                    TransactionItem(nama = "Croissant", harga = 15000.0, jumlah = 1)
                )
            ),
            businessProfile = BusinessProfile(namaUsaha = "Kopi Senja Cafe", alamat = "Jl. Merdeka No. 123", telepon = "081234567890"),
            receiptTemplate = ReceiptTemplate(),
            onNewTransactionClick = {}
        )
    }
}
