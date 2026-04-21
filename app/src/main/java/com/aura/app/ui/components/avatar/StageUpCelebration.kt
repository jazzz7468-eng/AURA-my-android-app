package com.aura.app.ui.components.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.util.XPManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun StageUpCelebration(
    stage: Int,
    ageGroup: String,
    onDismiss: () -> Unit,
) {
    val stageName = XPManager.getStageName(stage)
    val stageEmoji = getAvatarEmoji(stage, ageGroup)
    val stageDesc = XPManager.getStageDescription(stage, ageGroup)

    // Entry animation
    val scaleAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            )
        )
    }

    // Particle colors based on age group
    val particleColors = when (ageGroup) {
        "kids" -> listOf(Color(0xFFFF7043), Color(0xFF66BB6A), Color(0xFF42A5F5), Color(0xFFFFCA28), Color(0xFFE91E63))
        "teens" -> listOf(Color(0xFF7C4DFF), Color(0xFFFF4081), Color(0xFF00E5FF), Color(0xFFFF9800))
        else -> listOf(Color(0xFFFFD700), Color(0xFF00E5FF), Color(0xFF7C4DFF), Color(0xFFFF6B9D))
    }

    // Generate particles
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                dx = (Random.nextFloat() - 0.5f) * 3f,
                dy = (Random.nextFloat() - 0.5f) * 3f,
                size = Random.nextFloat() * 8f + 3f,
                color = particleColors.random(),
                alpha = Random.nextFloat() * 0.7f + 0.3f,
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
        ),
        label = "progress",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        // Particle effects
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val t = particleProgress
                val x = (p.x + p.dx * t) % 1f * size.width
                val y = (p.y + p.dy * t + t * 0.5f) % 1f * size.height
                drawCircle(
                    color = p.color.copy(alpha = p.alpha * (1f - t * 0.3f)),
                    radius = p.size * scaleAnim.value,
                    center = Offset(x, y),
                )
            }
        }

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            // Living Aura with bounce
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(scaleAnim.value),
                contentAlignment = Alignment.Center
            ) {
                LivingAura(
                    stage = stage,
                    ageGroup = ageGroup,
                    isCelebration = true,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Title
            Text(
                text = "You've Evolved!",
                fontSize = (32 * scaleAnim.value).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            // Stage name with glow
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            ) {
                Text(
                    text = "Stage $stage: $stageName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }

            // Description
            Text(
                text = stageDesc,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tap to dismiss hint
            Text(
                text = "Tap anywhere to continue",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.4f),
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val dx: Float,
    val dy: Float,
    val size: Float,
    val color: Color,
    val alpha: Float,
)

private fun getAvatarEmoji(stage: Int, ageGroup: String): String {
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
