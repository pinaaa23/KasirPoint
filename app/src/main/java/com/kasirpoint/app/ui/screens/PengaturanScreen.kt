package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.ReceiptTemplate
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionItem
import com.kasirpoint.app.ui.components.LogoutConfirmDialog
import com.kasirpoint.app.ui.components.ReceiptView
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted

@Composable
fun PengaturanScreen(
    businessProfile: BusinessProfile = BusinessProfile(),
    receiptTemplate: ReceiptTemplate = ReceiptTemplate(),
    onSaveProfile: (BusinessProfile) -> Unit = {},
    onSaveTemplate: (ReceiptTemplate) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var namaUsaha by remember { mutableStateOf(businessProfile.namaUsaha) }
    var alamat by remember { mutableStateOf(businessProfile.alamat) }
    var telepon by remember { mutableStateOf(businessProfile.telepon) }
    var pajakAktif by remember { mutableStateOf(businessProfile.pajakAktif) }
    var pajakPersenText by remember { mutableStateOf(businessProfile.pajakPersen.toInt().toString()) }
    var tipePesananAktif by remember { mutableStateOf(businessProfile.tipePesananAktif) }

    var templateState by remember { mutableStateOf(receiptTemplate) }

    LaunchedEffect(businessProfile) {
        namaUsaha = businessProfile.namaUsaha
        alamat = businessProfile.alamat
        telepon = businessProfile.telepon
        pajakAktif = businessProfile.pajakAktif
        pajakPersenText = businessProfile.pajakPersen.toInt().toString()
        tipePesananAktif = businessProfile.tipePesananAktif
    }

    LaunchedEffect(receiptTemplate) {
        templateState = receiptTemplate
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
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
                text = "Pengaturan",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Business Profile Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Profil Usaha", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = namaUsaha,
                        onValueChange = { namaUsaha = it },
                        label = { Text("Nama Usaha") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted) },
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = alamat,
                        onValueChange = { alamat = it },
                        label = { Text("Alamat Usaha") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = TextMuted) },
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = telepon,
                        onValueChange = { telepon = it },
                        label = { Text("Nomor Telepon") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSaveProfile(
                                BusinessProfile(
                                    namaUsaha = namaUsaha,
                                    alamat = alamat,
                                    telepon = telepon,
                                    pajakAktif = pajakAktif,
                                    pajakPersen = pajakPersenText.toDoubleOrNull() ?: 10.0,
                                    tipePesananAktif = tipePesananAktif
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Simpan Profil")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tax & Order Type Settings Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Pajak & Tipe Pesanan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktifkan Tipe Pesanan (Makan/Bawa)", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = tipePesananAktif,
                            onCheckedChange = { tipePesananAktif = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktifkan Pajak Penjualan", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = pajakAktif,
                            onCheckedChange = { pajakAktif = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                        )
                    }

                    if (pajakAktif) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pajakPersenText,
                            onValueChange = { pajakPersenText = it },
                            label = { Text("Persentase Pajak (%)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Receipt Template Settings Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Template Struk", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))

                    TemplateToggleRow("Tampilkan Logo Pin", templateState.tampilLogo) {
                        templateState = templateState.copy(tampilLogo = it)
                        onSaveTemplate(templateState)
                    }
                    TemplateToggleRow("Tampilkan Nama Usaha", templateState.tampilNamaUsaha) {
                        templateState = templateState.copy(tampilNamaUsaha = it)
                        onSaveTemplate(templateState)
                    }
                    TemplateToggleRow("Tampilkan Alamat Usaha", templateState.tampilAlamat) {
                        templateState = templateState.copy(tampilAlamat = it)
                        onSaveTemplate(templateState)
                    }
                    TemplateToggleRow("Tampilkan Nomor Telepon", templateState.tampilTelepon) {
                        templateState = templateState.copy(tampilTelepon = it)
                        onSaveTemplate(templateState)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = templateState.pesanPenutup,
                        onValueChange = {
                            templateState = templateState.copy(pesanPenutup = it)
                            onSaveTemplate(templateState)
                        },
                        label = { Text("Pesan Penutup Struk") },
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pratinjau Struk (Live Preview):", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReceiptView(
                        transaction = Transaction(
                            nomor = "TRX-PREVIEW-001",
                            subtotal = 33000.0,
                            total = 33000.0,
                            items = listOf(TransactionItem(nama = "Contoh Produk Cafe", harga = 33000.0, jumlah = 1))
                        ),
                        businessProfile = BusinessProfile(
                            namaUsaha = namaUsaha.ifEmpty { "Nama Usaha Anda" },
                            alamat = alamat.ifEmpty { "Alamat Usaha" },
                            telepon = telepon.ifEmpty { "08123456789" }
                        ),
                        receiptTemplate = templateState,
                        onNewTransactionClick = {},
                        scrollable = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDanger),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari Akun", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (showLogoutDialog) {
            LogoutConfirmDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    onLogoutClick()
                }
            )
        }
    }
}

@Composable
private fun TemplateToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
        )
    }
}
