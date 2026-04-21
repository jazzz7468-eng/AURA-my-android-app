package com.aura.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.util.XPManager

@Composable
fun XPProgressBar(
    currentXP: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    val extendedColors = AuraTheme.extendedColors
    val progress = XPManager.getXPProgressToNextStage(currentXP)
    val nextStageXP = XPManager.getXPForNextStage(currentXP)
    val currentStage = XPManager.getStageForXP(currentXP)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    )

    // Glow shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "xp_shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${XPManager.getStageEmoji(currentStage)} $currentXP XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (currentStage < 5) {
                    Text(
                        text = "${nextStageXP - currentXP} XP to ${XPManager.getStageName(currentStage + 1)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                } else {
                    Text(
                        text = "MAX STAGE ✨",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                extendedColors.xpGradientStart,
                                extendedColors.xpGradientEnd,
                                extendedColors.xpGradientStart,
                            ),
                            start = Offset(shimmerOffset * 500f - 100f, 0f),
                            end = Offset(shimmerOffset * 500f + 100f, 0f),
                        )
                    ),
            )
        }
    }
}
