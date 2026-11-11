package com.kasirpoint.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.data.repository.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _lastTransaction = MutableStateFlow<Transaction?>(null)
    val lastTransaction: StateFlow<Transaction?> = _lastTransaction.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var transactionsJob: Job? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startListeningTransactions(currentUser.uid)
        } else {
            clearData()
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        auth.currentUser?.uid?.let { uid ->
            startListeningTransactions(uid)
        }
    }

    fun startListeningTransactions(uid: String) {
        transactionsJob?.cancel()
        transactionsJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getTransactionsFlow(uid)
                .catch { e ->
                    _errorMessage.value = "Gagal memuat transaksi: ${e.localizedMessage}"
                    _isLoading.value = false
                }
                .collect { list ->
                    _transactions.value = list
                    _isLoading.value = false
                }
        }
    }

    fun processTransaction(transaction: Transaction, onSuccess: (Transaction) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.saveTransaction(uid, transaction)
                .onSuccess { savedTrx ->
                    _lastTransaction.value = savedTrx
                    _isLoading.value = false
                    onSuccess(savedTrx)
                }
                .onFailure { e ->
                    _errorMessage.value = "Gagal menyimpan transaksi: ${e.localizedMessage}"
                    _isLoading.value = false
                }
        }
    }

    fun cancelTransaction(transaction: Transaction) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.cancelTransaction(uid, transaction)
                .onFailure { e ->
                    _errorMessage.value = "Gagal membatalkan transaksi: ${e.localizedMessage}"
                }
        }
    }

    fun clearData() {
        transactionsJob?.cancel()
        _transactions.value = emptyList()
        _lastTransaction.value = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
