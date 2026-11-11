package com.kasirpoint.app.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kasirpoint.app.data.model.CartItem
import com.kasirpoint.app.data.model.Category
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.PaymentMethod
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.ProductVariant
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.ui.screens.KasirScreen
import com.kasirpoint.app.ui.screens.KeranjangScreen
import com.kasirpoint.app.ui.screens.LaporanScreen
import com.kasirpoint.app.ui.screens.LoginScreen
import com.kasirpoint.app.ui.screens.PembayaranScreen
import com.kasirpoint.app.ui.screens.PengaturanScreen
import com.kasirpoint.app.ui.screens.ProdukScreen
import com.kasirpoint.app.ui.screens.RiwayatScreen
import com.kasirpoint.app.ui.screens.SplashScreen
import com.kasirpoint.app.ui.screens.StrukScreen
import com.kasirpoint.app.ui.viewmodel.AuthViewModel

@Composable
fun KasirPointNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    widthSizeClass: WindowWidthSizeClass,
    products: List<Product>,
    categories: List<Category>,
    cartItems: List<CartItem>,
    orderType: OrderType,
    transactions: List<Transaction>,
    lastTransaction: Transaction?,
    onAddToCart: (Product, ProductVariant?, Int) -> Unit,
    onOrderTypeChange: (OrderType) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveCartItem: (CartItem) -> Unit,
    onCompletePayment: (PaymentMethod, Double, Double) -> Unit,
    onCancelTransaction: (Transaction) -> Unit,
    onSaveProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onSaveCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val businessProfile by authViewModel.businessProfile.collectAsState()
    val receiptTemplate by authViewModel.receiptTemplate.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            val currentUser = authViewModel.getCurrentUser()

            SplashScreen(
                businessName = if (currentUser != null) businessProfile.namaUsaha else null,
                onSplashFinished = {
                    if (currentUser != null) {
                        authViewModel.loadUserData(currentUser.uid)
                        navController.navigate(Screen.Kasir.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authUiState,
                onLoginSubmit = { email, pass ->
                    authViewModel.login(email, pass)
                },
                onRegisterSubmit = { email, pass, namaUsaha ->
                    authViewModel.register(email, pass, namaUsaha)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Kasir.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onResetUiState = {
                    authViewModel.resetUiState()
                }
            )
        }

        composable(Screen.Kasir.route) {
            KasirScreen(
                products = products,
                categories = categories,
                cartItems = cartItems,
                businessProfile = businessProfile,
                orderType = orderType,
                widthSizeClass = widthSizeClass,
                onAddToCart = onAddToCart,
                onOrderTypeChange = onOrderTypeChange,
                onCatatanChange = {},
                onQuantityChange = onQuantityChange,
                onRemoveCartItem = onRemoveCartItem,
                onOpenMobileCart = { navController.navigate(Screen.Keranjang.route) },
                onCheckout = { navController.navigate(Screen.Pembayaran.route) },
                onNavigateToProduk = { navController.navigate(Screen.Produk.route) }
            )
        }

        composable(Screen.Keranjang.route) {
            KeranjangScreen(
                cartItems = cartItems,
                businessProfile = businessProfile,
                orderType = orderType,
                onOrderTypeChange = onOrderTypeChange,
                onCatatanChange = {},
                onQuantityChange = onQuantityChange,
                onRemoveCartItem = onRemoveCartItem,
                onCheckout = { navController.navigate(Screen.Pembayaran.route) },
                onBackToKasir = { navController.popBackStack() }
            )
        }

        composable(Screen.Pembayaran.route) {
            val subtotal = cartItems.sumOf { it.totalHarga }
            val pajak = if (businessProfile.pajakAktif) subtotal * (businessProfile.pajakPersen / 100.0) else 0.0
            val total = subtotal + pajak

            PembayaranScreen(
                total = total,
                onCompletePayment = { method, uang, kembali ->
                    onCompletePayment(method, uang, kembali)
                    navController.navigate(Screen.Struk.route) {
                        popUpTo(Screen.Kasir.route)
                    }
                },
                onBackToKasir = { navController.popBackStack() }
            )
        }

        composable(Screen.Struk.route) {
            StrukScreen(
                lastTransaction = lastTransaction,
                businessProfile = businessProfile,
                receiptTemplate = receiptTemplate,
                onNewTransactionClick = {
                    navController.navigate(Screen.Kasir.route) {
                        popUpTo(Screen.Kasir.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Riwayat.route) {
            RiwayatScreen(
                transactions = transactions,
                onCancelTransaction = onCancelTransaction
            )
        }

        composable(Screen.Produk.route) {
            ProdukScreen(
                products = products,
                categories = categories,
                onSaveProduct = onSaveProduct,
                onDeleteProduct = onDeleteProduct,
                onSaveCategory = onSaveCategory,
                onDeleteCategory = onDeleteCategory
            )
        }

        composable(Screen.Laporan.route) {
            LaporanScreen(
                transactions = transactions
            )
        }

        composable(Screen.Pengaturan.route) {
            val user = authViewModel.getCurrentUser()
            LaunchedEffect(user) {
                if (user != null) {
                    authViewModel.loadUserData(user.uid)
                }
            }

            PengaturanScreen(
                businessProfile = businessProfile,
                receiptTemplate = receiptTemplate,
                onSaveProfile = { profile ->
                    authViewModel.updateBusinessProfile(profile)
                },
                onSaveTemplate = { template ->
                    authViewModel.updateReceiptTemplate(template)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
