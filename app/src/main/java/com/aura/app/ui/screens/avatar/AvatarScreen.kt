package com.aura.app.ui.screens.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.app.data.local.entities.UserProfile
import com.aura.app.ui.components.AdaptiveCard
import com.aura.app.ui.components.XPProgressBar
import com.aura.app.ui.components.avatar.EvolutionTimeline
import com.aura.app.ui.components.avatar.RadarChart
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.util.XPManager
import com.aura.app.viewmodel.AvatarUiState
import com.aura.app.viewmodel.AvatarViewModel

@Composable
fun AvatarScreen(
    userProfile: UserProfile?,
    viewModel: AvatarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState(initial = AvatarUiState())
    val ageGroup = AuraTheme.ageGroup

    // Floating animation for avatar
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float_y",
    )

    // Glow pulse for avatar
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ── Title ──
        Text(
            text = if (ageGroup == "kids") "🧬 My Avatar" else "My Avatar",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )

        // ── Avatar Display ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Glow circle behind avatar
            Box(
                modifier = Modifier
                    .size((120 + uiState.avatarStage * 15).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha * (uiState.avatarStage / 5f)),
                                Color.Transparent,
                            ),
                        )
                    ),
            )

            // Living Aura (Dynamic Procedural Avatar)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = floatOffset.dp),
            ) {
                LivingAura(
                    stage = uiState.avatarStage,
                    ageGroup = ageGroup,
                    modifier = Modifier.size((140 + uiState.avatarStage * 15).dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stage badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = uiState.stageEmoji,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = "Stage ${uiState.avatarStage}: ${uiState.stageName}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.stageDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // ── XP Progress ──
        AdaptiveCard {
            XPProgressBar(currentXP = uiState.xp)
        }

        // ── Evolution Timeline ──
        AdaptiveCard {
            Text(
                text = "Evolution Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            EvolutionTimeline(currentStage = uiState.avatarStage)
        }

        // ── Skills Radar ──
        AdaptiveCard {
            Text(
                text = "Skills Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.skills.values.any { it > 0 }) {
                RadarChart(
                    skills = uiState.skills,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🌱", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete Social Lab sessions\nto build your skills!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // ── Stats ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatItem("📊", "${uiState.totalSessions}", "Sessions", Modifier.weight(1f))
            StatItem("✅", "${uiState.totalMissions}", "Missions", Modifier.weight(1f))
            StatItem("🔥", "${uiState.streakDays}", "Streak", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StatItem(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    AdaptiveCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

/**
 * Returns a representative emoji for the avatar based on stage and age group.
 * In production, these would be replaced with generated avatar images.
 */
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
