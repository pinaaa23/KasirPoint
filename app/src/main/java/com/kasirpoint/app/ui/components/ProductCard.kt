package com.kasirpoint.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.Product
import com.kasirpoint.app.data.model.ProductVariant
import com.kasirpoint.app.ui.theme.ActionBrown
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.KasirPointTheme
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark
import com.kasirpoint.app.ui.theme.TextMuted
import com.kasirpoint.app.util.CurrencyFormatter

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product, ProductVariant?, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableIntStateOf(1) }
    val isOutOfStock = product.pakaiStok && product.stok <= 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image / Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ChipPeach.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalCafe,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(48.dp)
                )

                if (isOutOfStock) {
                    Surface(
                        color = RedDanger,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "Habis",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.nama,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = CurrencyFormatter.formatRupiah(product.harga),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
            }

            // Description
            if (product.deskripsi.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Varian pills preview
            if (product.varian.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    product.varian.take(2).forEach { v ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ChipPeach)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = v.nama,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stepper & Add Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stepper (- 1 +)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ChipPeach)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1 && !isOutOfStock,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Kurang",
                            modifier = Modifier.size(16.dp),
                            tint = TextDark
                        )
                    }
                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { quantity++ },
                        enabled = !isOutOfStock,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            modifier = Modifier.size(16.dp),
                            tint = TextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Add to Cart Button
                Button(
                    onClick = {
                        // If product has variants, pass null as variant to prompt VariantDialog
                        val selectedVariant = if (product.varian.isNotEmpty()) null else null
                        onAddToCart(product, selectedVariant, quantity)
                    },
                    enabled = !isOutOfStock,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionBrown,
                        disabledContainerColor = TextMuted.copy(alpha = 0.3f)
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    KasirPointTheme {
        ProductCard(
            product = Product(
                id = "p1",
                nama = "Cappuccino",
                harga = 18000.0,
                deskripsi = "Espresso segar dengan susu hangat dan busa lembut.",
                varian = listOf(ProductVariant("Small"), ProductVariant("Large", 3000.0))
            ),
            onAddToCart = { _, _, _ -> },
            modifier = Modifier.width(260.dp)
        )
    }
}
