package com.kasirpoint.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirpoint.app.data.model.Category
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.ProductVariant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // --- CATEGORIES ---

    fun getCategoriesFlow(uid: String): Flow<List<Category>> = callbackFlow {
        val collection = firestore.collection("users").document(uid).collection("categories")
        val subscription = collection.orderBy("urutan").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val nama = doc.getString("nama") ?: ""
                    val urutan = doc.getLong("urutan")?.toInt() ?: 0
                    Category(id = doc.id, nama = nama, urutan = urutan)
                }
                trySend(list)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun saveCategory(uid: String, category: Category): Result<Unit> {
        return try {
            val docRef = if (category.id.isBlank()) {
                firestore.collection("users").document(uid).collection("categories").document()
            } else {
                firestore.collection("users").document(uid).collection("categories").document(category.id)
            }

            val data = mapOf(
                "nama" to category.nama,
                "urutan" to category.urutan
            )

            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(uid: String, categoryId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("categories")
                .document(categoryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- PRODUCTS ---

    fun getProductsFlow(uid: String): Flow<List<Product>> = callbackFlow {
        val collection = firestore.collection("users").document(uid).collection("products")
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val nama = doc.getString("nama") ?: ""
                    val hargaDoc = doc.getDouble("harga") ?: 0.0
                    val kategoriId = doc.getString("kategoriId") ?: ""
                    val fotoUrl = doc.getString("fotoUrl")
                    val deskripsi = doc.getString("deskripsi") ?: ""
                    val pakaiStok = doc.getBoolean("pakaiStok") ?: false
                    val stok = doc.getLong("stok")?.toInt() ?: 0
                    val aktif = doc.getBoolean("aktif") ?: true

                    val varianList = (doc.get("varian") as? List<Map<String, Any>>)?.map { v ->
                        val vNama = v["nama"] as? String ?: ""
                        val vHarga = (v["harga"] as? Number)?.toDouble()
                            ?: ((v["tambahanHarga"] as? Number)?.toDouble() ?: 0.0)
                        ProductVariant(nama = vNama, harga = vHarga)
                    } ?: emptyList()

                    val finalHarga = if (varianList.isNotEmpty()) varianList.first().harga else hargaDoc

                    Product(
                        id = doc.id,
                        nama = nama,
                        harga = finalHarga,
                        kategoriId = kategoriId,
                        fotoUrl = fotoUrl,
                        deskripsi = deskripsi,
                        varian = varianList,
                        pakaiStok = pakaiStok,
                        stok = stok,
                        aktif = aktif
                    )
                }
                trySend(list)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun saveProduct(uid: String, product: Product): Result<Unit> {
        return try {
            val docRef = if (product.id.isBlank()) {
                firestore.collection("users").document(uid).collection("products").document()
            } else {
                firestore.collection("users").document(uid).collection("products").document(product.id)
            }

            val varianMapList = product.varian.map { v ->
                mapOf("nama" to v.nama, "harga" to v.harga)
            }

            val baseHarga = product.varian.firstOrNull()?.harga ?: product.harga

            val data = mapOf(
                "nama" to product.nama,
                "harga" to baseHarga,
                "kategoriId" to product.kategoriId,
                "fotoUrl" to product.fotoUrl,
                "deskripsi" to product.deskripsi,
                "varian" to varianMapList,
                "pakaiStok" to product.pakaiStok,
                "stok" to product.stok,
                "aktif" to product.aktif
            )

            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(uid: String, productId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("products")
                .document(productId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
