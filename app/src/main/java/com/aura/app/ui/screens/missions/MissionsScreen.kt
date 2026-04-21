package com.aura.app.ui.screens.missions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aura.app.data.missions.Mission
import com.aura.app.data.missions.MissionData
import com.aura.app.ui.components.AdaptiveCard
import com.aura.app.ui.components.avatar.StageUpCelebration
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.viewmodel.MissionsViewModel
import com.aura.app.viewmodel.UserViewModel

@Composable
fun MissionsScreen(
    userViewModel: UserViewModel,
    viewModel: MissionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val ageGroup = AuraTheme.ageGroup

    var showStageUp by remember { mutableStateOf(false) }
    var newStage by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.stageUpEvent.collect { stage ->
            newStage = stage
            showStageUp = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ── Header ──
        Text(
            text = if (ageGroup == "kids") "🎯 Missions" else "Missions",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // ── Streak Banner ──
        StreakBanner(streakDays = profile?.streakDays ?: 0)

        // ── Daily Missions ──
        Text(
            text = "📋 Today's Missions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        MissionData.dailyMissions.forEach { mission ->
            MissionCard(
                mission = mission,
                isCompleted = uiState.completedMissionIds.contains(mission.id),
                onClick = {
                    if (!uiState.completedMissionIds.contains(mission.id)) {
                        viewModel.completeMission(mission.id, mission.xpReward)
                    }
                },
            )
        }

        // ── Quick Tasks ──
        Text(
            text = "⚡ Quick Tasks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Spin the Wheel button
        SpinWheelButton(
            isSpinning = uiState.isSpinning,
            isCompleted = uiState.completedMissionIds.contains("quick_spin"),
            onClick = { viewModel.spinWheel() },
        )

        // Reflection & Kindness
        MissionCard(
            mission = MissionData.quickTasks[1], // Daily Reflection
            isCompleted = uiState.completedMissionIds.contains("quick_reflect"),
            onClick = { viewModel.showReflection("reflect") },
        )

        MissionCard(
            mission = MissionData.quickTasks[2], // Kindness Log
            isCompleted = uiState.completedMissionIds.contains("quick_kindness"),
            onClick = { viewModel.showReflection("kindness") },
        )

        // ── Challenges ──
        Text(
            text = "🏆 Challenges",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        MissionData.challenges.forEach { mission ->
            MissionCard(
                mission = mission,
                isCompleted = uiState.completedMissionIds.contains(mission.id),
                onClick = { },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Quiz Dialog
    uiState.currentQuiz?.let { quiz ->
        Dialog(onDismissRequest = { viewModel.dismissQuiz() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "🎡 Spin the Wheel Quiz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    ) {
                        Text(
                            text = quiz.skill.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    Text(
                        text = quiz.question,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )

                    quiz.options.forEachIndexed { index, option ->
                        val isSelected = uiState.selectedAnswer == index
                        val isCorrect = index == quiz.correctIndex
                        val hasAnswered = uiState.quizResult != null

                        val borderColor = when {
                            hasAnswered && isCorrect -> Color(0xFF4CAF50)
                            hasAnswered && isSelected && !isCorrect -> Color(0xFFE53935)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        }

                        val bgColor = when {
                            hasAnswered && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            hasAnswered && isSelected && !isCorrect -> Color(0xFFE53935).copy(alpha = 0.1f)
                            else -> Color.Transparent
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !hasAnswered) {
                                    viewModel.answerQuiz(index)
                                }
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp),
                            )
                        }
                    }

                    // Show explanation after answering
                    if (uiState.quizResult != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (uiState.quizResult == true) {
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            } else {
                                Color(0xFFFFCA28).copy(alpha = 0.1f)
                            },
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (uiState.quizResult == true) "✅ Correct! +25 XP" else "❌ Not quite...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = quiz.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.dismissQuiz() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Continue", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Reflection Dialog
    if (uiState.showReflectionDialog) {
        Dialog(onDismissRequest = { viewModel.dismissReflection() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = if (uiState.reflectionType == "reflect") "📝 Daily Reflection" else "💝 Kindness Log",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = if (uiState.reflectionType == "reflect")
                            "What's one thing you're proud of today?"
                        else
                            "Describe a kind act you did today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    OutlinedTextField(
                        value = uiState.reflectionText,
                        onValueChange = { viewModel.updateReflectionText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Write here...") },
                        shape = RoundedCornerShape(12.dp),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.dismissReflection() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = { viewModel.submitReflection() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = uiState.reflectionText.isNotBlank(),
                        ) {
                            Text("Submit ✨", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Stage-up celebration
    if (showStageUp) {
        StageUpCelebration(
            stage = newStage,
            ageGroup = profile?.ageGroup ?: "adults",
            onDismiss = { showStageUp = false },
        )
    }
}

@Composable
private fun StreakBanner(streakDays: Int) {
    AdaptiveCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "🔥", fontSize = 36.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$streakDays Day Streak!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Keep it going — don't break the chain!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
            // Weekly dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(7) { day ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (day < (streakDays % 7).coerceAtLeast(1)) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: Mission,
    isCompleted: Boolean,
    onClick: () -> Unit,
) {
    AdaptiveCard(onClick = if (!isCompleted) onClick else null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = mission.icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isCompleted) 0.3f else 0.6f),
                )
            }
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            } else {
                Text(
                    text = "+${mission.xpReward} XP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SpinWheelButton(
    isSpinning: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
        ),
        label = "rotation",
    )

    AdaptiveCard(onClick = if (!isSpinning && !isCompleted) onClick else null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "🎡",
                fontSize = 28.sp,
                modifier = if (isSpinning) Modifier.rotate(rotation) else Modifier,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Spin the Wheel!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    text = if (isSpinning) "Spinning..." else "Answer a random skill quiz",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isCompleted) 0.3f else 0.6f),
                )
            }
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            } else {
                Text(
                    text = "+25 XP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
