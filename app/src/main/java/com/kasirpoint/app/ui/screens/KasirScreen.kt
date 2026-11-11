package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.CartItem
import com.kasirpoint.app.data.model.Category
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.ProductVariant
import com.kasirpoint.app.ui.components.CartPanel
import com.kasirpoint.app.ui.components.CategoryTabs
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.components.ProductCard
import com.kasirpoint.app.ui.components.SearchFilterBar
import com.kasirpoint.app.ui.components.VariantDialog
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.util.CurrencyFormatter

@Composable
fun KasirScreen(
    products: List<Product> = emptyList(),
    categories: List<Category> = emptyList(),
    cartItems: List<CartItem> = emptyList(),
    businessProfile: BusinessProfile = BusinessProfile(),
    orderType: OrderType = OrderType.MAKAN_DI_TEMPAT,
    catatan: String = "",
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onAddToCart: (Product, ProductVariant?, Int) -> Unit,
    onOrderTypeChange: (OrderType) -> Unit,
    onCatatanChange: (String) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveCartItem: (CartItem) -> Unit,
    onOpenMobileCart: () -> Unit,
    onCheckout: () -> Unit,
    onNavigateToProduk: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var variantDialogProduct by remember { mutableStateOf<Product?>(null) }

    val isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded
    val showSideCartPanel = isExpanded

    val columns = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 2
        WindowWidthSizeClass.Medium -> 3
        WindowWidthSizeClass.Expanded -> 4
        else -> 2
    }

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategoryId == null || product.kategoriId == selectedCategoryId
        val matchesSearch = searchQuery.isEmpty() || product.nama.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Main Product Grid Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                SearchFilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CategoryTabs(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredProducts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Inventory,
                        title = "Belum Ada Produk",
                        message = if (products.isEmpty()) {
                            "Belum ada produk tersimpan. Tambahkan produk pertamamu di menu Produk."
                        } else {
                            "Tidak ada produk yang sesuai dengan pencarian."
                        },
                        actionLabel = if (products.isEmpty()) "Buka Menu Produk" else null,
                        onActionClick = if (products.isEmpty()) onNavigateToProduk else null,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = if (!showSideCartPanel && cartItems.isNotEmpty()) 80.dp else 16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredProducts) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { prod, variant, qty ->
                                    if (prod.varian.isNotEmpty() && variant == null) {
                                        variantDialogProduct = prod
                                    } else {
                                        onAddToCart(prod, variant, qty)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Tablet/Expanded Side Cart Panel
            if (showSideCartPanel) {
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
                    onCheckoutClick = onCheckout
                )
            }
        }

        // Mobile / Compact Bottom Cart Summary Bar
        if (!showSideCartPanel && cartItems.isNotEmpty()) {
            val totalItems = cartItems.sumOf { it.jumlah }
            val totalPrice = cartItems.sumOf { it.totalHarga }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .clickable { onOpenMobileCart() },
                shape = CircleShape,
                color = ActionBrown,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$totalItems item dipesan",
                            style = MaterialTheme.typography.titleMedium.copy(color = SurfaceWhite)
                        )
                    }

                    Text(
                        text = CurrencyFormatter.formatRupiah(totalPrice),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    )
                }
            }
        }

        // Variant Selection Dialog
        variantDialogProduct?.let { product ->
            VariantDialog(
                product = product,
                onDismiss = { variantDialogProduct = null },
                onVariantSelected = { variant ->
                    if (variant != null) {
                        onAddToCart(product, variant, 1)
                    }
                    variantDialogProduct = null
                }
            )
        }
    }
}
