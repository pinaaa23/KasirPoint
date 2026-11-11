package com.kasirpoint.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Kasir : Screen("kasir")
    object Keranjang : Screen("keranjang")
    object Pembayaran : Screen("pembayaran")
    object Struk : Screen("struk")
    object Riwayat : Screen("riwayat")
    object Produk : Screen("produk")
    object Laporan : Screen("laporan")
    object Pengaturan : Screen("pengaturan")
}
