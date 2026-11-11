package com.kasirpoint.app.data.model

data class Product(
    val id: String = "",
    val nama: String = "",
    val harga: Double = 0.0,
    val kategoriId: String = "",
    val fotoUrl: String? = null,
    val deskripsi: String = "",
    val varian: List<ProductVariant> = emptyList(),
    val pakaiStok: Boolean = false,
    val stok: Int = 0,
    val aktif: Boolean = true
) {
    val hargaDisplay: Double
        get() = if (varian.isNotEmpty()) varian.first().harga else harga
}
