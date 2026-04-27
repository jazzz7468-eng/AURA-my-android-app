package com.aura.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aura.app.navigation.Screen
import com.aura.app.ui.theme.AuraTheme

data class BottomBarItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val kidsEmoji: String,
)

@Composable
fun AuraBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    val ageGroup = AuraTheme.ageGroup
    val items = listOf(
        BottomBarItem(Screen.Dashboard.route, "Home", Icons.Filled.Home, Icons.Outlined.Home, "🏠"),
        BottomBarItem(Screen.SocialLab.route, "Social Lab", Icons.Filled.Psychology, Icons.Outlined.Psychology, "🧪"),
        BottomBarItem(Screen.Missions.route, "Missions", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "🎯"),
        BottomBarItem(Screen.Journal.route, "Journal", Icons.Filled.Book, Icons.Outlined.Book, "📔"),
        BottomBarItem(Screen.Avatar.route, "Avatar", Icons.Filled.Person, Icons.Outlined.Person, "🧬"),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomBarTab(
                    item = item,
                    isSelected = isSelected,
                    ageGroup = ageGroup,
                    onClick = { onNavigate(item.route) },
                )
            }
        }
    }
}

@Composable
private fun BottomBarTab(
    item: BottomBarItem,
    isSelected: Boolean,
    ageGroup: String,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (ageGroup == "kids") {
            Text(
                text = item.kidsEmoji,
                style = MaterialTheme.typography.titleLarge,
            )
        } else {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
        )
    }
}
