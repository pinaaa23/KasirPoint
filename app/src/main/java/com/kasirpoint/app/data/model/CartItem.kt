package com.kasirpoint.app.data.model

data class CartItem(
    val product: Product,
    val selectedVariant: ProductVariant? = null,
    val jumlah: Int = 1
) {
    val hargaSatuan: Double
        get() = product.harga + (selectedVariant?.tambahanHarga ?: 0.0)

    val totalHarga: Double
        get() = hargaSatuan * jumlah
}
