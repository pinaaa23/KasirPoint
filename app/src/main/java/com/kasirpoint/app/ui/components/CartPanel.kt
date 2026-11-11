package com.kasirpoint.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.CartItem
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BorderLight
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.KasirPointTheme
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter

@Composable
fun CartPanel(
    cartItems: List<CartItem>,
    orderType: OrderType,
    catatan: String = "",
    pajakPersen: Double = 10.0,
    isPajakAktif: Boolean = false,
    isTipePesananAktif: Boolean = true,
    onOrderTypeChange: (OrderType) -> Unit = {},
    onCatatanChange: (String) -> Unit = {},
    onQuantityChange: (CartItem, Int) -> Unit = { _, _ -> },
    onRemoveItem: (CartItem) -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { it.totalHarga }
    val pajak = if (isPajakAktif) subtotal * (pajakPersen / 100.0) else 0.0
    val total = subtotal + pajak

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceWhite,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ringkasan Keranjang",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Order Type Selector Row (if active in settings)
                if (isTipePesananAktif) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(ChipPeach)
                            .padding(4.dp)
                    ) {
                        OrderTypeChip(
                            title = "Makan di tempat",
                            icon = Icons.Default.Restaurant,
                            isSelected = orderType == OrderType.MAKAN_DI_TEMPAT,
                            onClick = { onOrderTypeChange(OrderType.MAKAN_DI_TEMPAT) },
                            modifier = Modifier.weight(1f)
                        )
                        OrderTypeChip(
                            title = "Bawa pulang",
                            icon = Icons.Default.ShoppingBag,
                            isSelected = orderType == OrderType.BAWA_PULANG,
                            onClick = { onOrderTypeChange(OrderType.BAWA_PULANG) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (cartItems.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.ShoppingBag,
                        title = "Keranjang Kosong",
                        message = "Pilih produk dari daftar untuk mulai mencatat pesanan.",
                        modifier = Modifier.padding(top = 32.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { q -> onQuantityChange(item, q) },
                                onRemove = { onRemoveItem(item) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Catatan Pesanan Field (PRD 10.4)
                            OutlinedTextField(
                                value = catatan,
                                onValueChange = onCatatanChange,
                                placeholder = { Text("Catatan pesanan (opsional)...") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = TextMuted)
                                },
                                singleLine = true,
                                shape = CircleShape,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Calculation & Checkout Section
            if (cartItems.isNotEmpty()) {
                Column {
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatRupiah(subtotal), style = MaterialTheme.typography.bodyMedium)
                    }

                    if (isPajakAktif) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pajak (${pajakPersen.toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.formatRupiah(pajak), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            CurrencyFormatter.formatRupiah(total),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onCheckoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Bayar (${CurrencyFormatter.formatRupiah(total)})",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderTypeChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) SurfaceWhite else ChipPeach)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) PrimaryOrange else TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TextDark else TextMuted
            )
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ChipPeach),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalCafe,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.nama,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val variantLabel = item.selectedVariant?.nama
            if (!variantLabel.isNullOrEmpty()) {
                Text(
                    text = variantLabel,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = CurrencyFormatter.formatRupiah(item.totalHarga),
                style = MaterialTheme.typography.labelMedium.copy(color = TextDark)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (item.jumlah > 1) onQuantityChange(item.jumlah - 1) else onRemove() },
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = if (item.jumlah == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (item.jumlah == 1) RedDanger else TextDark,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "${item.jumlah}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
                onClick = { onQuantityChange(item.jumlah + 1) },
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPanelPreview() {
    KasirPointTheme {
        CartPanel(
            cartItems = listOf(
                CartItem(Product(nama = "Cappuccino", harga = 18000.0), jumlah = 2),
                CartItem(Product(nama = "Croissant", harga = 15000.0), jumlah = 1)
            ),
            orderType = OrderType.MAKAN_DI_TEMPAT,
            catatan = "Kurangi gula",
            pajakPersen = 10.0,
            isPajakAktif = true,
            isTipePesananAktif = true
        )
    }
}
