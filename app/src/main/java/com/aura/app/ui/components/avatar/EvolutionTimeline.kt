package com.aura.app.ui.components.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.util.XPManager

@Composable
fun EvolutionTimeline(
    currentStage: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_alpha",
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (stage in 1..5) {
            val isPast = stage < currentStage
            val isCurrent = stage == currentStage
            val isFuture = stage > currentStage

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                // Stage circle
                Box(
                    modifier = Modifier
                        .size(if (isCurrent) 48.dp else 40.dp)
                        .then(
                            if (isCurrent) {
                                Modifier.border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = glowAlpha),
                                        )
                                    ),
                                    shape = CircleShape,
                                )
                            } else Modifier
                        )
                        .clip(CircleShape)
                        .background(
                            when {
                                isPast -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isFuture) {
                        Text("🔒", fontSize = 16.sp)
                    } else {
                        Text(
                            text = XPManager.getStageEmoji(stage),
                            fontSize = if (isCurrent) 22.sp else 18.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stage label
                Text(
                    text = XPManager.getStageName(stage),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> MaterialTheme.colorScheme.primary
                        isPast -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    },
                    fontSize = 10.sp,
                )
            }

            // Connector line between stages (not after last)
            if (stage < 5) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            if (stage < currentStage) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }
                        ),
                )
            }
        }
    }
}
