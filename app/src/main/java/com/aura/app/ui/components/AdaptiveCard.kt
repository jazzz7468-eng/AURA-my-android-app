package com.aura.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aura.app.ui.theme.AuraTheme

@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    glowColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ageGroup = AuraTheme.ageGroup
    val extendedColors = AuraTheme.extendedColors

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    )

    val cardShape = MaterialTheme.shapes.medium
    val elevation: Dp
    val backgroundColor: Color
    val borderBrush: Brush?

    when (ageGroup) {
        "kids" -> {
            elevation = 6.dp
            backgroundColor = MaterialTheme.colorScheme.surface
            borderBrush = null
        }
        "teens" -> {
            elevation = 0.dp
            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            borderBrush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                ),
                start = Offset.Zero,
                end = Offset.Infinite,
            )
        }
        else -> {
            elevation = 0.dp
            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            borderBrush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                ),
                start = Offset.Zero,
                end = Offset.Infinite,
            )
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (elevation > 0.dp) {
                    Modifier.shadow(elevation, cardShape, ambientColor = glowColor ?: extendedColors.cardGlow)
                } else Modifier
            )
            .clip(cardShape)
            .background(backgroundColor)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onClick()
                    }
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content,
        )
    }
}
