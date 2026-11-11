package com.kasirpoint.app.data.model

data class BusinessProfile(
    val namaUsaha: String = "",
    val alamat: String = "",
    val telepon: String = "",
    val logoUrl: String? = null,
    val pajakAktif: Boolean = false,
    val pajakPersen: Double = 10.0,
    val tipePesananAktif: Boolean = true
)

data class ReceiptTemplate(
    val tampilLogo: Boolean = true,
    val tampilNamaUsaha: Boolean = true,
    val tampilAlamat: Boolean = true,
    val tampilTelepon: Boolean = true,
    val tampilCatatan: Boolean = true,
    val pesanPenutup: String = "Terima kasih atas kunjungan Anda!"
)
