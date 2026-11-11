package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.PaymentMethod
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

@Composable
fun PembayaranScreen(
    total: Double = 0.0,
    onCompletePayment: (PaymentMethod, Double, Double) -> Unit,
    onBackToKasir: () -> Unit
) {
    if (total <= 0.0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPeach)
                .padding(16.dp)
        ) {
            EmptyState(
                icon = Icons.Default.Payments,
                title = "Tidak Ada Pesanan",
                message = "Belum ada pesanan yang perlu dibayar.",
                actionLabel = "Kembali ke Kasir",
                onActionClick = onBackToKasir,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    var selectedMethod by remember { mutableStateOf(PaymentMethod.TUNAI) }
    var uangDiterimaText by remember { mutableStateOf("") }

    val uangDiterima = uangDiterimaText.toDoubleOrNull() ?: 0.0
    val kembalian = if (uangDiterima >= total) uangDiterima - total else 0.0
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
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBackToKasir) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextDark)
                }
                Text(
                    text = "Pembayaran",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Total Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Tagihan", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyFormatter.formatRupiah(total),
                        style = MaterialTheme.typography.headlineLarge.copy(color = PrimaryOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods Selection
            Text("Pilih Metode Pembayaran", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PaymentMethodCard(
                    title = "Tunai",
                    icon = Icons.Default.Money,
                    isSelected = selectedMethod == PaymentMethod.TUNAI,
                    onClick = { selectedMethod = PaymentMethod.TUNAI },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodCard(
                    title = "QRIS",
                    icon = Icons.Default.QrCode,
                    isSelected = selectedMethod == PaymentMethod.QRIS,
                    onClick = { selectedMethod = PaymentMethod.QRIS },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodCard(
                    title = "Transfer",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedMethod == PaymentMethod.TRANSFER,
                    onClick = { selectedMethod = PaymentMethod.TRANSFER },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tunai Payment Form
            if (selectedMethod == PaymentMethod.TUNAI) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Nominal Uang Diterima", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uangDiterimaText,
                            onValueChange = { uangDiterimaText = it },
                            placeholder = { Text("Contoh: 50000") },
                            leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = BorderLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Cash Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { uangDiterimaText = total.toInt().toString() },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Uang Pas")
                            }
                            OutlinedButton(
                                onClick = { uangDiterimaText = "50000" },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("50.000")
                            }
                            OutlinedButton(
                                onClick = { uangDiterimaText = "100000" },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("100.000")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kembalian", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = CurrencyFormatter.formatRupiah(kembalian),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            )
                        }
                    }
                }
            } else {
                // QRIS / Transfer info note
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (selectedMethod == PaymentMethod.QRIS) Icons.Default.QrCode else Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pembayaran via ${selectedMethod.displayName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pastikan pelanggan sudah menyelesaikan pembayaran via ${selectedMethod.displayName} sebelum menyelesaikan transaksi.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Finish Transaction Button
            val isButtonEnabled = selectedMethod != PaymentMethod.TUNAI || uangDiterima >= total
            Button(
                onClick = { onCompletePayment(selectedMethod, uangDiterima, kembalian) },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Selesaikan Transaksi",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) ChipPeach else SurfaceWhite
    val borderColor = if (isSelected) PrimaryOrange else BorderLight

    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PrimaryOrange else TextMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = TextDark
                )
            )
        }
    }
}
