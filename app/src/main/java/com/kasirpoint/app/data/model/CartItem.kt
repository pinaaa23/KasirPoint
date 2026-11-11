package com.kasirpoint.app.data.model

data class CartItem(
    val product: Product,
    val selectedVariant: ProductVariant? = null,
    val jumlah: Int = 1
) {
    val hargaSatuan: Double
        get() = selectedVariant?.harga ?: product.harga

    val totalHarga: Double
        get() = hargaSatuan * jumlah
}
