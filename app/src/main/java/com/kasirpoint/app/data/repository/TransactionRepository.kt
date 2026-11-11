package com.kasirpoint.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kasirpoint.app.data.model.OrderType
import com.kasirpoint.app.data.model.PaymentMethod
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.model.TransactionItem
import com.kasirpoint.app.data.model.TransactionStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun generateTransactionNumber(uid: String): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("in", "ID"))
        val dateStr = dateFormat.format(Date())
        val prefix = "TRX-$dateStr-"

        return try {
            val collection = firestore.collection("users").document(uid).collection("transactions")
            val snapshot = collection
                .whereGreaterThanOrEqualTo("nomor", prefix)
                .whereLessThan("nomor", prefix + "\uF8FF")
                .get()
                .await()

            val count = snapshot.documents.size + 1
            val numberFormatted = String.format(Locale.US, "%03d", count)
            prefix + numberFormatted
        } catch (e: Exception) {
            val randomSuffix = (100..999).random()
            "$prefix$randomSuffix"
        }
    }

    suspend fun saveTransaction(uid: String, transaction: Transaction): Result<Transaction> {
        return try {
            val number = if (transaction.nomor.isBlank()) {
                generateTransactionNumber(uid)
            } else {
                transaction.nomor
            }

            val docRef = if (transaction.id.isBlank()) {
                firestore.collection("users").document(uid).collection("transactions").document()
            } else {
                firestore.collection("users").document(uid).collection("transactions").document(transaction.id)
            }

            val finalTransaction = transaction.copy(id = docRef.id, nomor = number)

            val itemsMapList = finalTransaction.items.map { item ->
                mapOf(
                    "produkId" to item.produkId,
                    "nama" to item.nama,
                    "harga" to item.harga,
                    "varian" to item.varian,
                    "jumlah" to item.jumlah
                )
            }

            val data = mapOf(
                "nomor" to finalTransaction.nomor,
                "waktu" to finalTransaction.waktu,
                "tipePesanan" to finalTransaction.tipePesanan.name,
                "items" to itemsMapList,
                "subtotal" to finalTransaction.subtotal,
                "pajak" to finalTransaction.pajak,
                "total" to finalTransaction.total,
                "metodeBayar" to finalTransaction.metodeBayar.name,
                "uangDiterima" to finalTransaction.uangDiterima,
                "kembalian" to finalTransaction.kembalian,
                "catatan" to finalTransaction.catatan,
                "status" to finalTransaction.status.name
            )

            docRef.set(data).await()

            // Deduct stock for products with stock management enabled
            finalTransaction.items.forEach { item ->
                if (item.produkId.isNotBlank()) {
                    val productRef = firestore.collection("users")
                        .document(uid)
                        .collection("products")
                        .document(item.produkId)

                    val prodSnapshot = productRef.get().await()
                    if (prodSnapshot.exists() && prodSnapshot.getBoolean("pakaiStok") == true) {
                        productRef.update("stok", FieldValue.increment(-item.jumlah.toLong())).await()
                    }
                }
            }

            Result.success(finalTransaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTransactionsFlow(uid: String): Flow<List<Transaction>> = callbackFlow {
        val collection = firestore.collection("users").document(uid).collection("transactions")
        val subscription = collection.orderBy("waktu", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val nomor = doc.getString("nomor") ?: ""
                        val waktu = doc.getLong("waktu") ?: System.currentTimeMillis()
                        val tipePesananStr = doc.getString("tipePesanan") ?: "MAKAN_DI_TEMPAT"
                        val subtotal = doc.getDouble("subtotal") ?: 0.0
                        val pajak = doc.getDouble("pajak") ?: 0.0
                        val total = doc.getDouble("total") ?: 0.0
                        val metodeBayarStr = doc.getString("metodeBayar") ?: "TUNAI"
                        val uangDiterima = doc.getDouble("uangDiterima") ?: 0.0
                        val kembalian = doc.getDouble("kembalian") ?: 0.0
                        val catatan = doc.getString("catatan") ?: ""
                        val statusStr = doc.getString("status") ?: "SELESAI"

                        val itemsList = (doc.get("items") as? List<Map<String, Any>>)?.map { i ->
                            TransactionItem(
                                produkId = i["produkId"] as? String ?: "",
                                nama = i["nama"] as? String ?: "",
                                harga = (i["harga"] as? Number)?.toDouble() ?: 0.0,
                                varian = i["varian"] as? String,
                                jumlah = (i["jumlah"] as? Number)?.toInt() ?: 1
                            )
                        } ?: emptyList()

                        Transaction(
                            id = doc.id,
                            nomor = nomor,
                            waktu = waktu,
                            tipePesanan = try { OrderType.valueOf(tipePesananStr) } catch (_: Exception) { OrderType.MAKAN_DI_TEMPAT },
                            items = itemsList,
                            subtotal = subtotal,
                            pajak = pajak,
                            total = total,
                            metodeBayar = try { PaymentMethod.valueOf(metodeBayarStr) } catch (_: Exception) { PaymentMethod.TUNAI },
                            uangDiterima = uangDiterima,
                            kembalian = kembalian,
                            catatan = catatan,
                            status = try { TransactionStatus.valueOf(statusStr) } catch (_: Exception) { TransactionStatus.SELESAI }
                        )
                    }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun cancelTransaction(uid: String, transaction: Transaction): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("transactions")
                .document(transaction.id)
                .update("status", TransactionStatus.DIBATALKAN.name)
                .await()

            // Restore stock if transaction had stock management
            transaction.items.forEach { item ->
                if (item.produkId.isNotBlank()) {
                    val productRef = firestore.collection("users")
                        .document(uid)
                        .collection("products")
                        .document(item.produkId)

                    val prodSnapshot = productRef.get().await()
                    if (prodSnapshot.exists() && prodSnapshot.getBoolean("pakaiStok") == true) {
                        productRef.update("stok", FieldValue.increment(item.jumlah.toLong())).await()
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
