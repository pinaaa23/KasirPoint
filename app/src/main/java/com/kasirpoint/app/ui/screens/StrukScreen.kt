package com.kasirpoint.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.data.model.BusinessProfile
import com.kasirpoint.app.data.model.ReceiptTemplate
import com.kasirpoint.app.data.model.Transaction
import com.kasirpoint.app.ui.components.EmptyState
import com.kasirpoint.app.ui.components.ReceiptView
import com.kasirpoint.app.ui.theme.BackgroundPeach

@Composable
fun StrukScreen(
    lastTransaction: Transaction? = null,
    businessProfile: BusinessProfile = BusinessProfile(),
    receiptTemplate: ReceiptTemplate = ReceiptTemplate(),
    onNewTransactionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPeach)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Struk Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (lastTransaction == null) {
                EmptyState(
                    icon = Icons.Default.Receipt,
                    title = "Belum Ada Struk",
                    message = "Belum ada transaksi yang dilakukan.",
                    actionLabel = "Mulai Kasir",
                    onActionClick = onNewTransactionClick,
                    modifier = Modifier.weight(1f)
                )
            } else {
                ReceiptView(
                    transaction = lastTransaction,
                    businessProfile = businessProfile,
                    receiptTemplate = receiptTemplate,
                    onNewTransactionClick = onNewTransactionClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
