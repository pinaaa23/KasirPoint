package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.CartItem
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.ui.components.CartPanel
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.TextDark

@Composable
fun KeranjangScreen(
    cartItems: List<CartItem> = emptyList(),
    businessProfile: BusinessProfile = BusinessProfile(),
    orderType: OrderType = OrderType.MAKAN_DI_TEMPAT,
    catatan: String = "",
    onOrderTypeChange: (OrderType) -> Unit,
    onCatatanChange: (String) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveCartItem: (CartItem) -> Unit,
    onCheckout: () -> Unit,
    onBackToKasir: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBackToKasir) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextDark
                    )
                }
                Text(
                    text = "Keranjang Belanja",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (cartItems.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ShoppingBag,
                    title = "Keranjang Kosong",
                    message = "Keranjang belanja masih kosong. Pilih produk dari menu Kasir.",
                    actionLabel = "Kembali ke Kasir",
                    onActionClick = onBackToKasir,
                    modifier = Modifier.weight(1f)
                )
            } else {
                CartPanel(
                    cartItems = cartItems,
                    orderType = orderType,
                    catatan = catatan,
                    pajakPersen = businessProfile.pajakPersen,
                    isPajakAktif = businessProfile.pajakAktif,
                    isTipePesananAktif = businessProfile.tipePesananAktif,
                    onOrderTypeChange = onOrderTypeChange,
                    onCatatanChange = onCatatanChange,
                    onQuantityChange = onQuantityChange,
                    onRemoveItem = onRemoveCartItem,
                    onCheckoutClick = onCheckout,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
