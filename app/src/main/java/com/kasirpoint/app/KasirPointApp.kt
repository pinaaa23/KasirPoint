package com.kasirpoint.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kasirpoint.app.data.model.CartItem
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionItem
import com.kasirpoint.app.navigation.KasirPointNavGraph
import com.kasirpoint.app.navigation.Screen
import com.kasirpoint.app.ui.components.Sidebar
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.KasirPointTheme
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.viewmodel.AuthViewModel
import com.kasirpoint.app.ui.viewmodel.ProductViewModel
import com.kasirpoint.app.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirPointApp(
    windowSizeClass: WindowSizeClass,
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    KasirPointTheme {
        val navController = rememberNavController()
        val widthSizeClass = windowSizeClass.widthSizeClass
        val isMobile = widthSizeClass == WindowWidthSizeClass.Compact

        // Sidebar state: Tablet defaults to open (true), Mobile defaults to closed (false)
        var isSidebarOpen by remember { mutableStateOf(!isMobile) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Kasir.route

        // Realtime Firestore Products, Categories & Transactions State
        val products by productViewModel.products.collectAsState()
        val categories by productViewModel.categories.collectAsState()
        val transactions by transactionViewModel.transactions.collectAsState()
        val lastTransaction by transactionViewModel.lastTransaction.collectAsState()

        val cartItems = remember { mutableStateListOf<CartItem>() }
        var orderType by remember { mutableStateOf(OrderType.MAKAN_DI_TEMPAT) }

        val showTopAndSidebar = currentRoute != Screen.Splash.route && currentRoute != Screen.Login.route

        Scaffold(
            topBar = {
                if (showTopAndSidebar) {
                    TopAppBar(
                        title = {
                            val screenTitle = when (currentRoute) {
                                Screen.Kasir.route -> "Kasir"
                                Screen.Riwayat.route -> "Riwayat Transaksi"
                                Screen.Produk.route -> "Kelola Produk"
                                Screen.Laporan.route -> "Laporan Penjualan"
                                Screen.Pengaturan.route -> "Pengaturan"
                                Screen.Keranjang.route -> "Keranjang Belanja"
                                Screen.Pembayaran.route -> "Pembayaran"
                                Screen.Struk.route -> "Struk"
                                else -> "Kasir Point"
                            }
                            Text(
                                text = screenTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { isSidebarOpen = !isSidebarOpen }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Buka/Tutup Sidebar Menu",
                                    tint = TextDark
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundPeach)
                    )
                }
            },
            containerColor = BackgroundPeach
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Permanent Sidebar for Tablet / Wide screen when open
                    if (showTopAndSidebar && !isMobile && isSidebarOpen) {
                        Sidebar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    popUpTo(Screen.Kasir.route) { saveState = true }
                                    restoreState = true
                                }
                            },
                            onLogoutClick = {
                                authViewModel.logout()
                                cartItems.clear()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // Main Content Area
                    KasirPointNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        widthSizeClass = widthSizeClass,
                        products = products,
                        categories = categories,
                        cartItems = cartItems,
                        orderType = orderType,
                        transactions = transactions,
                        lastTransaction = lastTransaction,
                        onAddToCart = { prod, variant, qty ->
                            val existingIndex = cartItems.indexOfFirst {
                                it.product.id == prod.id && it.selectedVariant == variant
                            }
                            if (existingIndex >= 0) {
                                val oldItem = cartItems[existingIndex]
                                cartItems[existingIndex] = oldItem.copy(jumlah = oldItem.jumlah + qty)
                            } else {
                                cartItems.add(CartItem(prod, variant, qty))
                            }
                        },
                        onOrderTypeChange = { orderType = it },
                        onQuantityChange = { item, newQty ->
                            val index = cartItems.indexOf(item)
                            if (index >= 0) {
                                if (newQty > 0) {
                                    cartItems[index] = item.copy(jumlah = newQty)
                                } else {
                                    cartItems.removeAt(index)
                                }
                            }
                        },
                        onRemoveCartItem = { item -> cartItems.remove(item) },
                        onCompletePayment = { method, uang, kembali ->
                            val profile = authViewModel.businessProfile.value
                            val subtotal = cartItems.sumOf { it.totalHarga }
                            val pajak = if (profile.pajakAktif) subtotal * (profile.pajakPersen / 100.0) else 0.0
                            val total = subtotal + pajak

                            val trxItems = cartItems.map { ci ->
                                TransactionItem(
                                    produkId = ci.product.id,
                                    nama = ci.product.nama,
                                    harga = ci.hargaSatuan,
                                    varian = ci.selectedVariant?.nama,
                                    jumlah = ci.jumlah
                                )
                            }

                            val newTrx = Transaction(
                                id = "",
                                nomor = "",
                                waktu = System.currentTimeMillis(),
                                tipePesanan = orderType,
                                items = trxItems,
                                subtotal = subtotal,
                                pajak = pajak,
                                total = total,
                                metodeBayar = method,
                                uangDiterima = uang,
                                kembalian = kembali
                            )

                            transactionViewModel.processTransaction(newTrx) {
                                cartItems.clear()
                            }
                        },
                        onCancelTransaction = { trx ->
                            transactionViewModel.cancelTransaction(trx)
                        },
                        onSaveProduct = { prod -> productViewModel.saveProduct(prod) },
                        onDeleteProduct = { prod -> productViewModel.deleteProduct(prod.id) },
                        onSaveCategory = { cat -> productViewModel.saveCategory(cat) },
                        onDeleteCategory = { cat -> productViewModel.deleteCategory(cat.id) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Mobile Overlay Sidebar
                if (showTopAndSidebar && isMobile) {
                    AnimatedVisibility(
                        visible = isSidebarOpen,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.zIndex(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { isSidebarOpen = false }
                        )
                    }

                    AnimatedVisibility(
                        visible = isSidebarOpen,
                        enter = slideInHorizontally(initialOffsetX = { -it }),
                        exit = slideOutHorizontally(targetOffsetX = { -it }),
                        modifier = Modifier.zIndex(2f)
                    ) {
                        Sidebar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                isSidebarOpen = false
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    popUpTo(Screen.Kasir.route) { saveState = true }
                                    restoreState = true
                                }
                            },
                            onLogoutClick = {
                                isSidebarOpen = false
                                authViewModel.logout()
                                cartItems.clear()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
