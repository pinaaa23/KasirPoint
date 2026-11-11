package com.kasirpoint.app.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val localeID = Locale("in", "ID")

    fun formatRupiah(amount: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(localeID)
        return "Rp${numberFormat.format(amount.toLong())}"
    }

    fun formatRupiah(amount: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(localeID)
        return "Rp${numberFormat.format(amount)}"
    }
}
