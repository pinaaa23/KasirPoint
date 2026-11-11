package com.kasirpoint.app.data.model

enum class OrderType(val displayName: String) {
    MAKAN_DI_TEMPAT("Makan di tempat"),
    BAWA_PULANG("Bawa pulang")
}

enum class PaymentMethod(val displayName: String) {
    TUNAI("Tunai"),
    QRIS("QRIS"),
    TRANSFER("Transfer")
}

enum class TransactionStatus(val displayName: String) {
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan")
}

data class TransactionItem(
    val produkId: String = "",
    val nama: String = "",
    val harga: Double = 0.0,
    val varian: String? = null,
    val jumlah: Int = 1
)

data class Transaction(
    val id: String = "",
    val nomor: String = "",
    val waktu: Long = System.currentTimeMillis(),
    val tipePesanan: OrderType = OrderType.MAKAN_DI_TEMPAT,
    val items: List<TransactionItem> = emptyList(),
    val subtotal: Double = 0.0,
    val pajak: Double = 0.0,
    val total: Double = 0.0,
    val metodeBayar: PaymentMethod = PaymentMethod.TUNAI,
    val uangDiterima: Double = 0.0,
    val kembalian: Double = 0.0,
    val catatan: String = "",
    val status: TransactionStatus = TransactionStatus.SELESAI
)
