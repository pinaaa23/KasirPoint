package com.kasirpoint.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestoreSettings
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.ReceiptTemplate
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    init {
        // Enable Firestore Offline Persistence
        try {
            val settings = firestoreSettings {
                setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            }
            firestore.firestoreSettings = settings
        } catch (_: Exception) {
            // Settings already set
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun register(
        email: String,
        password: String,
        namaUsaha: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Gagal membuat akun")

            val initialProfile = mapOf(
                "namaUsaha" to namaUsaha,
                "alamat" to "",
                "telepon" to "",
                "logoUrl" to null,
                "pajakAktif" to false,
                "pajakPersen" to 10.0,
                "tipePesananAktif" to true,
                "strukTemplate" to mapOf(
                    "tampilLogo" to true,
                    "tampilNamaUsaha" to true,
                    "tampilAlamat" to true,
                    "tampilTelepon" to true,
                    "tampilCatatan" to true,
                    "pesanPenutup" to "Terima kasih atas kunjungan Anda!"
                )
            )

            firestore.collection("users")
                .document(user.uid)
                .set(initialProfile)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Gagal masuk")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getBusinessProfile(uid: String): Result<BusinessProfile> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                val namaUsaha = snapshot.getString("namaUsaha") ?: ""
                val alamat = snapshot.getString("alamat") ?: ""
                val telepon = snapshot.getString("telepon") ?: ""
                val logoUrl = snapshot.getString("logoUrl")
                val pajakAktif = snapshot.getBoolean("pajakAktif") ?: false
                val pajakPersen = (snapshot.get("pajakPersen") as? Number)?.toDouble() ?: 10.0
                val tipePesananAktif = snapshot.getBoolean("tipePesananAktif") ?: true

                Result.success(
                    BusinessProfile(
                        namaUsaha = namaUsaha,
                        alamat = alamat,
                        telepon = telepon,
                        logoUrl = logoUrl,
                        pajakAktif = pajakAktif,
                        pajakPersen = pajakPersen,
                        tipePesananAktif = tipePesananAktif
                    )
                )
            } else {
                Result.success(BusinessProfile())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBusinessProfile(uid: String, profile: BusinessProfile): Result<Unit> {
        return try {
            val data = mapOf(
                "namaUsaha" to profile.namaUsaha,
                "alamat" to profile.alamat,
                "telepon" to profile.telepon,
                "pajakAktif" to profile.pajakAktif,
                "pajakPersen" to profile.pajakPersen,
                "tipePesananAktif" to profile.tipePesananAktif
            )
            firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReceiptTemplate(uid: String): Result<ReceiptTemplate> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                val map = snapshot.get("strukTemplate") as? Map<*, *>
                if (map != null) {
                    val template = ReceiptTemplate(
                        tampilLogo = map["tampilLogo"] as? Boolean ?: true,
                        tampilNamaUsaha = map["tampilNamaUsaha"] as? Boolean ?: true,
                        tampilAlamat = map["tampilAlamat"] as? Boolean ?: true,
                        tampilTelepon = map["tampilTelepon"] as? Boolean ?: true,
                        tampilCatatan = map["tampilCatatan"] as? Boolean ?: true,
                        pesanPenutup = map["pesanPenutup"] as? String ?: "Terima kasih atas kunjungan Anda!"
                    )
                    Result.success(template)
                } else {
                    Result.success(ReceiptTemplate())
                }
            } else {
                Result.success(ReceiptTemplate())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveReceiptTemplate(uid: String, template: ReceiptTemplate): Result<Unit> {
        return try {
            val data = mapOf(
                "strukTemplate" to mapOf(
                    "tampilLogo" to template.tampilLogo,
                    "tampilNamaUsaha" to template.tampilNamaUsaha,
                    "tampilAlamat" to template.tampilAlamat,
                    "tampilTelepon" to template.tampilTelepon,
                    "tampilCatatan" to template.tampilCatatan,
                    "pesanPenutup" to template.pesanPenutup
                )
            )
            firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
