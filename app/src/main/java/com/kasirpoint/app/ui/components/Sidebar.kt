package com.kasirpoint.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirpoint.app.navigation.Screen
import com.kasirpoint.app.ui.theme.ChipPeach
import com.kasirpoint.app.ui.theme.PrimaryOrange
import com.kasirpoint.app.ui.theme.RedDanger
import com.kasirpoint.app.ui.theme.SurfaceWhite
import com.kasirpoint.app.ui.theme.TextDark

data class SidebarNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val sidebarNavItems = listOf(
    SidebarNavItem(Screen.Kasir.route, "Kasir", Icons.Default.PointOfSale),
    SidebarNavItem(Screen.Riwayat.route, "Riwayat", Icons.Default.History),
    SidebarNavItem(Screen.Produk.route, "Produk", Icons.Default.Inventory),
    SidebarNavItem(Screen.Laporan.route, "Laporan", Icons.Default.Assessment),
    SidebarNavItem(Screen.Pengaturan.route, "Pengaturan", Icons.Default.Settings)
)

@Composable
fun Sidebar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(240.dp)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceWhite,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header: Pin Logo & App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ChipPeach),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Kasir Point",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                }

                // Nav Items
                sidebarNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    SidebarItemRow(
                        title = item.title,
                        icon = item.icon,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Bottom Item: Keluar
            SidebarItemRow(
                title = "Keluar",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                isSelected = false,
                isDanger = true,
                onClick = { showLogoutDialog = true }
            )
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            }
        )
    }
}

@Composable
private fun SidebarItemRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) ChipPeach else SurfaceWhite
    val contentColor = when {
        isDanger -> RedDanger
        isSelected -> PrimaryOrange
        else -> TextDark
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        )
    }
}
