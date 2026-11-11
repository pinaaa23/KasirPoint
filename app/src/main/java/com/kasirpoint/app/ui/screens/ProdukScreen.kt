package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kasirpoint.app.data.model.Category
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.ProductVariant
import com.kasirpoint.app.ui.components.CategoryTabs
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.components.SearchFilterBar
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.BackgroundPeach
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter

@Composable
fun ProdukScreen(
    products: List<Product> = emptyList(),
    categories: List<Category> = emptyList(),
    onSaveProduct: (Product) -> Unit = {},
    onDeleteProduct: (Product) -> Unit = {},
    onSaveCategory: (Category) -> Unit = {},
    onDeleteCategory: (Category) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var showAddEditProductDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategoryId == null || product.kategoriId == selectedCategoryId
        val matchesSearch = searchQuery.isEmpty() || product.nama.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Title & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kelola Produk",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showCategoryDialog = true },
                        shape = CircleShape,
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kategori")
                    }

                    Button(
                        onClick = {
                            editingProduct = null
                            showAddEditProductDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                        shape = CircleShape,
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah")
                    }
                }
            }

            SearchFilterBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CategoryTabs(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Store,
                    title = "Belum Ada Produk",
                    message = if (products.isEmpty()) {
                        "Belum ada produk tersimpan. Tambahkan produk pertamamu sekarang."
                    } else {
                        "Tidak ada produk yang cocok dengan pencarian."
                    },
                    actionLabel = if (products.isEmpty()) "Tambah Produk Pertama" else null,
                    onActionClick = if (products.isEmpty()) {
                        {
                            editingProduct = null
                            showAddEditProductDialog = true
                        }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredProducts) { product ->
                        val categoryName = categories.find { it.id == product.kategoriId }?.nama ?: ""
                        ProductManagementRow(
                            product = product,
                            categoryName = categoryName,
                            onEdit = {
                                editingProduct = product
                                showAddEditProductDialog = true
                            },
                            onDelete = { productToDelete = product }
                        )
                    }
                }
            }
        }

        // Add/Edit Product Dialog
        if (showAddEditProductDialog) {
            ProductFormDialog(
                product = editingProduct,
                categories = categories,
                onDismiss = { showAddEditProductDialog = false },
                onSave = { prod ->
                    onSaveProduct(prod)
                    showAddEditProductDialog = false
                }
            )
        }

        // Category Manager Dialog
        if (showCategoryDialog) {
            CategoryFormDialog(
                categories = categories,
                onDismiss = { showCategoryDialog = false },
                onSaveCategory = onSaveCategory,
                onDeleteCategory = onDeleteCategory
            )
        }

        // Delete Product Confirmation Dialog
        productToDelete?.let { prod ->
            AlertDialog(
                onDismissRequest = { productToDelete = null },
                title = { Text("Hapus Produk") },
                text = { Text("Apakah Anda yakin ingin menghapus produk '${prod.nama}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteProduct(prod)
                            productToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductManagementRow(
    product: Product,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChipPeach),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalCafe,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nama,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.formatRupiah(product.harga),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDark)
                    )
                    if (categoryName.isNotEmpty()) {
                        Text(
                            text = " • $categoryName",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted)
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = RedDanger)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormDialog(
    product: Product?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var nama by remember { mutableStateOf(product?.nama ?: "") }
    var hargaText by remember { mutableStateOf(if ((product?.harga ?: 0.0) > 0) product?.harga?.toInt()?.toString() ?: "" else "") }
    var selectedCategoryId by remember { mutableStateOf(product?.kategoriId ?: "") }
    var deskripsi by remember { mutableStateOf(product?.deskripsi ?: "") }
    var pakaiStok by remember { mutableStateOf(product?.pakaiStok ?: false) }
    var stokText by remember { mutableStateOf(product?.stok?.toString() ?: "0") }

    val variants = remember {
        mutableStateListOf<ProductVariant>().apply {
            if (product != null) addAll(product.varian)
        }
    }

    var newVariantNama by remember { mutableStateOf("") }
    var newVariantHargaText by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = if (product == null) "Tambah Produk Baru" else "Ubah Produk",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Produk") },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = hargaText,
                    onValueChange = { hargaText = it },
                    label = { Text("Harga (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.nama ?: "Pilih Kategori"
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.nama) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Singkat (Opsional)") },
                    maxLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Variants Section
                Text("Varian Produk (Opsional)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                variants.forEachIndexed { index, v ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• ${v.nama}" + if (v.tambahanHarga > 0) " (+${CurrencyFormatter.formatRupiah(v.tambahanHarga)})" else "")
                        IconButton(onClick = { variants.removeAt(index) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus", tint = RedDanger)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newVariantNama,
                        onValueChange = { newVariantNama = it },
                        placeholder = { Text("Ukuran/Nama") },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = newVariantHargaText,
                        onValueChange = { newVariantHargaText = it },
                        placeholder = { Text("+Rp") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = CircleShape,
                        modifier = Modifier.weight(0.8f)
                    )
                    IconButton(onClick = {
                        if (newVariantNama.isNotBlank()) {
                            variants.add(
                                ProductVariant(
                                    nama = newVariantNama,
                                    tambahanHarga = newVariantHargaText.toDoubleOrNull() ?: 0.0
                                )
                            )
                            newVariantNama = ""
                            newVariantHargaText = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Varian", tint = PrimaryOrange)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gunakan Stok", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = pakaiStok,
                        onCheckedChange = { pakaiStok = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange)
                    )
                }

                if (pakaiStok) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stokText,
                        onValueChange = { stokText = it },
                        label = { Text("Jumlah Stok") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal", color = TextMuted) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newProd = Product(
                                id = product?.id ?: "",
                                nama = nama,
                                harga = hargaText.toDoubleOrNull() ?: 0.0,
                                kategoriId = selectedCategoryId,
                                deskripsi = deskripsi,
                                varian = variants.toList(),
                                pakaiStok = pakaiStok,
                                stok = stokText.toIntOrNull() ?: 0
                            )
                            onSave(newProd)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                        shape = CircleShape
                    ) {
                        Text("Simpan", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFormDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSaveCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var namaKategori by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Kelola Kategori", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = namaKategori,
                    onValueChange = { namaKategori = it },
                    label = { Text("Nama Kategori Baru") },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (namaKategori.isNotBlank()) {
                            onSaveCategory(Category(id = "", nama = namaKategori, urutan = categories.size + 1))
                            namaKategori = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionBrown),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Tambah Kategori")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Daftar Kategori Saat Ini:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (categories.isEmpty()) {
                    Text("Belum ada kategori tersimpan.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    categories.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ${cat.nama}", style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { categoryToDelete = cat }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Kategori", tint = RedDanger)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Tutup") }
                }
            }
        }
    }

    // Category Deletion Confirmation
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Hapus Kategori") },
            text = { Text("Apakah Anda yakin ingin menghapus kategori '${cat.nama}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
