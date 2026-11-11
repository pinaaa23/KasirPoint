package com.kasirpoint.app.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.ReceiptTemplate
import com.kasirpoint.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _businessProfile = MutableStateFlow(BusinessProfile())
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    private val _receiptTemplate = MutableStateFlow(ReceiptTemplate())
    val receiptTemplate: StateFlow<ReceiptTemplate> = _receiptTemplate.asStateFlow()

    fun getCurrentUser(): FirebaseUser? = repository.getCurrentUser()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan kata sandi tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                    loadUserData(user.uid)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(mapFirebaseError(exception))
                }
        }
    }

    fun register(email: String, password: String, namaUsaha: String) {
        if (namaUsaha.isBlank()) {
            _uiState.value = AuthUiState.Error("Nama usaha tidak boleh kosong.")
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Email tidak valid.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Kata sandi minimal 6 karakter.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(email, password, namaUsaha)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                    loadUserData(user.uid)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(mapFirebaseError(exception))
                }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
        _businessProfile.value = BusinessProfile()
        _receiptTemplate.value = ReceiptTemplate()
    }

    fun loadUserData(uid: String) {
        viewModelScope.launch {
            repository.getBusinessProfile(uid).onSuccess { profile ->
                _businessProfile.value = profile
            }
            repository.getReceiptTemplate(uid).onSuccess { template ->
                _receiptTemplate.value = template
            }
        }
    }

    fun updateBusinessProfile(profile: BusinessProfile) {
        val currentUser = repository.getCurrentUser() ?: return
        viewModelScope.launch {
            _businessProfile.value = profile
            repository.saveBusinessProfile(currentUser.uid, profile)
        }
    }

    fun updateReceiptTemplate(template: ReceiptTemplate) {
        val currentUser = repository.getCurrentUser() ?: return
        viewModelScope.launch {
            _receiptTemplate.value = template
            repository.saveReceiptTemplate(currentUser.uid, template)
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun mapFirebaseError(e: Throwable): String {
        val msg = e.message ?: ""
        return when {
            msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                "Layanan Email/Password belum diaktifkan di Firebase Console (Menu Authentication -> Sign-in method)."
            e is FirebaseAuthUserCollisionException ->
                "Email ini sudah terdaftar."
            e is FirebaseAuthWeakPasswordException ->
                "Kata sandi terlalu pendek (minimal 6 karakter)."
            e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException ->
                "Email atau kata sandi salah. Coba lagi ya."
            e is FirebaseNetworkException ->
                "Tidak ada koneksi internet. Periksa koneksimu lalu coba lagi."
            else -> e.localizedMessage ?: "Terjadi kesalahan. Coba lagi ya."
        }
    }
}
