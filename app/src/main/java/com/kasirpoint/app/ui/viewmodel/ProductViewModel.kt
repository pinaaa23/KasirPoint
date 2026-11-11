package com.kasirpoint.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kasirpoint.app.data.model.Category
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var categoriesJob: Job? = null
    private var productsJob: Job? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startListeningData(currentUser.uid)
        } else {
            clearData()
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        auth.currentUser?.uid?.let { uid ->
            startListeningData(uid)
        }
    }

    fun startListeningData(uid: String) {
        categoriesJob?.cancel()
        productsJob?.cancel()

        categoriesJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getCategoriesFlow(uid)
                .catch { e ->
                    _errorMessage.value = "Gagal memuat kategori: ${e.localizedMessage}"
                    _isLoading.value = false
                }
                .collect { list ->
                    _categories.value = list
                    _isLoading.value = false
                }
        }

        productsJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getProductsFlow(uid)
                .catch { e ->
                    _errorMessage.value = "Gagal memuat produk: ${e.localizedMessage}"
                    _isLoading.value = false
                }
                .collect { list ->
                    _products.value = list
                    _isLoading.value = false
                }
        }
    }

    fun saveProduct(product: Product) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.saveProduct(uid, product)
                .onFailure { e -> _errorMessage.value = "Gagal menyimpan produk: ${e.localizedMessage}" }
        }
    }

    fun deleteProduct(productId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.deleteProduct(uid, productId)
                .onFailure { e -> _errorMessage.value = "Gagal menghapus produk: ${e.localizedMessage}" }
        }
    }

    fun saveCategory(category: Category) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.saveCategory(uid, category)
                .onFailure { e -> _errorMessage.value = "Gagal menyimpan kategori: ${e.localizedMessage}" }
        }
    }

    fun deleteCategory(categoryId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.deleteCategory(uid, categoryId)
                .onFailure { e -> _errorMessage.value = "Gagal menghapus kategori: ${e.localizedMessage}" }
        }
    }

    fun clearData() {
        categoriesJob?.cancel()
        productsJob?.cancel()
        _products.value = emptyList()
        _categories.value = emptyList()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
