package com.aura.app.ui.components.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.ui.theme.AuraColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A procedural, age-adaptive avatar component that evolves over stages.
 * Kids: Organic/Blossom
 * Teens: Kinetic/Neon
 * Adults: Fluid/Flow
 */
@Composable
fun LivingAura(
    stage: Int,
    ageGroup: String,
    modifier: Modifier = Modifier,
    isCelebration: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    
    // Core rotation for all styles
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCelebration) 5000 else 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsate scale logic
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Background Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCelebration) 0.dp else 24.dp)
                .blur(if (isCelebration) 40.dp else 20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 * pulseScale
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = getAuraColors(ageGroup, stage),
                        center = center,
                        radius = radius
                    ),
                    center = center,
                    radius = radius,
                    alpha = 0.4f
                )
            }
        }

        // Procedural Artwork
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (ageGroup) {
                "kids" -> drawKidsAura(stage, rotation, pulseScale, isCelebration)
                "teens" -> drawTeensAura(stage, rotation, pulseScale, isCelebration)
                else -> drawAdultsAura(stage, rotation, pulseScale, isCelebration)
            }
        }

        // Central Character Representation (Emoji)
        Text(
            text = getStageEmoji(stage, ageGroup),
            fontSize = if (isCelebration) 80.sp else 48.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

private fun DrawScope.drawKidsAura(stage: Int, rotation: Float, pulse: Float, celebration: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val baseRadius = size.minDimension / 4
    val numPetals = 3 + stage
    
    rotate(rotation, center) {
        for (i in 0 until numPetals) {
            val angle = (2 * PI * i / numPetals).toFloat()
            val petalRadius = baseRadius * (1f + (stage * 0.15f)) * pulse
            
            val path = Path().apply {
                val x = center.x + (petalRadius * cos(angle))
                val y = center.y + (petalRadius * sin(angle))
                moveTo(center.x, center.y)
                quadraticTo(
                    center.x + (petalRadius * 0.8f * cos(angle - 0.2f)),
                    center.y + (petalRadius * 0.8f * sin(angle - 0.2f)),
                    x, y
                )
                quadraticTo(
                    center.x + (petalRadius * 0.8f * cos(angle + 0.2f)),
                    center.y + (petalRadius * 0.8f * sin(angle + 0.2f)),
                    center.x, center.y
                )
            }
            
            drawPath(
                path = path,
                color = KidsAuraColors[i % KidsAuraColors.size].copy(alpha = 0.6f)
            )
        }
    }
}

private fun DrawScope.drawTeensAura(stage: Int, rotation: Float, pulse: Float, celebration: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val size = size.minDimension / 2.5f * pulse
    val numLayers = stage
    
    for (layer in 1..numLayers) {
        rotate(rotation * (layer * 0.5f), center) {
            val layerSize = size * (layer.toFloat() / numLayers)
            val sides = 3 + layer
            
            val path = Path().apply {
                for (i in 0 until sides) {
                    val angle = (2 * PI * i / sides).toFloat()
                    val x = center.x + (layerSize * cos(angle))
                    val y = center.y + (layerSize * sin(angle))
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            
            drawPath(
                path = path,
                color = TeensAuraColors[layer % TeensAuraColors.size].copy(alpha = 0.3f)
            )
            
            // Draw vertices as glowing points
            for (i in 0 until sides) {
                val angle = (2 * PI * i / sides).toFloat()
                drawCircle(
                    color = TeensAuraColors[layer % TeensAuraColors.size],
                    radius = 4f,
                    center = Offset(
                        center.x + (layerSize * cos(angle)),
                        center.y + (layerSize * sin(angle))
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawAdultsAura(stage: Int, rotation: Float, pulse: Float, celebration: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2.2f * pulse
    
    // Adult aura is focus on sweeping, elegant circular flows
    for (i in 1..stage) {
        rotate(rotation * 0.2f * i, center) {
            val sweepRadius = radius * (i.toFloat() / stage)
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = AdultsAuraColors,
                    center = center
                ),
                center = center,
                radius = sweepRadius,
                alpha = 0.15f / i
            )
            
            // Subtle accent arcs
            drawArc(
                color = AdultsAuraColors[i % AdultsAuraColors.size].copy(alpha = 0.3f),
                startAngle = (rotation + i * 40f) % 360f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - sweepRadius, center.y - sweepRadius),
                size = androidx.compose.ui.geometry.Size(sweepRadius * 2, sweepRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }
    }
}

private fun getAuraColors(ageGroup: String, stage: Int): List<Color> {
    return when (ageGroup) {
        "kids" -> listOf(Color(0xFF81C784), Color(0xFFAED581), Color.Transparent)
        "teens" -> listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF), Color.Transparent)
        else -> listOf(Color(0xFF1A237E), Color(0xFF0D47A1), Color.Transparent)
    }
}

private val KidsAuraColors = listOf(Color(0xFF81C784), Color(0xFFFFF176), Color(0xFFFFB74D), Color(0xFFF06292))
private val TeensAuraColors = listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF), Color(0xFFFF4081), Color(0xFF1DE9B6))
private val AdultsAuraColors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0), Color(0xFF00ACC1), Color(0xFFB0BEC5))

private fun getStageEmoji(stage: Int, ageGroup: String): String {
    return when (ageGroup) {
        "kids" -> when (stage) {
            1 -> "🌱" // Seed
            2 -> "🌿" // Sprout
            3 -> "🌸" // Bloom
            4 -> "✨" // Radiant
            5 -> "🦸" // Hero
            else -> "🧒"
        }
        "teens" -> when (stage) {
            1 -> "🌑" // Seed
            2 -> "🌙" // Sprout
            3 -> "🌕" // Bloom
            4 -> "☄️" // Radiant
            5 -> "🔥" // Master
            else -> "😶"
        }
        else -> when (stage) {
            1 -> "💎" // Seed
            2 -> "💠" // Sprout
            3 -> "🔱" // Bloom
            4 -> "👑" // Radiant
            5 -> "🌌" // Visionary
            else -> "🙂"
        }
    }
}
