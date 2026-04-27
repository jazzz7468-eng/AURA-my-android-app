package com.aura.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (name: String, ageGroup: String, empathy: Int, confidence: Int, comms: Int) -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedAgeGroup by remember { mutableStateOf("") }

    // Quiz answers (0, 1, or 2 for three options)
    var q1Answer by remember { mutableIntStateOf(-1) }
    var q2Answer by remember { mutableIntStateOf(-1) }
    var q3Answer by remember { mutableIntStateOf(-1) }

    // Aurora gradient background animation
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradient",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0B1629),
                        Color(0xFF1A0A3E),
                        Color(0xFF0B2948),
                        Color(0xFF0B1629),
                    ),
                    start = Offset(0f, gradientOffset * 1000),
                    end = Offset(1000f, 1000f + gradientOffset * 500),
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 3 })
                    .togetherWith(
                        fadeOut(tween(400)) + slideOutHorizontally(tween(400)) { -it / 3 }
                    )
            },
            label = "step",
        ) { currentStep ->
            when (currentStep) {
                0 -> WelcomeStep(onNext = { step = 1 })
                1 -> NameStep(
                    name = name,
                    onNameChange = { name = it },
                    onNext = { if (name.isNotBlank()) step = 2 },
                )
                2 -> AgeGroupStep(
                    selectedAgeGroup = selectedAgeGroup,
                    onSelect = { selectedAgeGroup = it },
                    onNext = { if (selectedAgeGroup.isNotBlank()) step = 3 },
                )
                3 -> QuizStep(
                    q1 = q1Answer, onQ1 = { q1Answer = it },
                    q2 = q2Answer, onQ2 = { q2Answer = it },
                    q3 = q3Answer, onQ3 = { q3Answer = it },
                    onNext = { if (q1Answer != -1 && q2Answer != -1 && q3Answer != -1) step = 4 }
                )
                4 -> ReadyStep(
                    name = name,
                    ageGroup = selectedAgeGroup,
                    onStart = {
                        // Calculate base skills from quiz
                        // Q1 (Empathy): 0=Low, 1=Med, 2=High
                        // Q2 (Confidence): 0=Low, 1=Med, 2=High
                        // Q3 (Communication): 0=Low, 1=Med, 2=High
                        val baseEmpathy = listOf(10, 25, 40)[q1Answer.coerceIn(0, 2)]
                        val baseConfidence = listOf(10, 25, 40)[q2Answer.coerceIn(0, 2)]
                        val baseComms = listOf(10, 25, 40)[q3Answer.coerceIn(0, 2)]
                        
                        onComplete(name, selectedAgeGroup, baseEmpathy, baseConfidence, baseComms)
                    },
                )
            }
        }

        // Step indicators at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == step) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index == step) Color(0xFF00E5FF)
                            else Color.White.copy(alpha = 0.3f)
                        )
                        .animateContentSize(),
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // AURA logo text
        Text(
            text = "✨ AURA ✨",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "The AI-powered soft skills coach\nthat grows with you",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Animated tagline
        Text(
            text = "Master your soft skills.\nEvolve your personality.",
            fontSize = 16.sp,
            color = Color(0xFF00E5FF).copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF0B1629),
            ),
        ) {
            Text(
                text = "Begin Your Journey",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "👋",
            fontSize = 64.sp,
        )

        Text(
            text = "What should we call you?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Enter your name", color = Color.White.copy(alpha = 0.4f)) },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF00E5FF),
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (name.isNotBlank()) onNext()
            }),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF0B1629),
            ),
        ) {
            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun AgeGroupStep(
    selectedAgeGroup: String,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Pick your age group",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "This personalizes your experience",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Age group cards
        AgeGroupCard(
            emoji = "🧒",
            label = "Kids",
            ageRange = "6 – 12 years",
            colors = listOf(Color(0xFFFF7043), Color(0xFFFFCA28)),
            isSelected = selectedAgeGroup == "kids",
            onClick = { onSelect("kids") },
        )

        AgeGroupCard(
            emoji = "🧑‍🎓",
            label = "Teens",
            ageRange = "13 – 21 years",
            colors = listOf(Color(0xFF7C4DFF), Color(0xFFFF4081)),
            isSelected = selectedAgeGroup == "teens",
            onClick = { onSelect("teens") },
        )

        AgeGroupCard(
            emoji = "👩‍💼",
            label = "Adults",
            ageRange = "22+ years",
            colors = listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF)),
            isSelected = selectedAgeGroup == "adults",
            onClick = { onSelect("adults") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNext,
            enabled = selectedAgeGroup.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF0B1629),
            ),
        ) {
            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun AgeGroupCard(
    emoji: String,
    label: String,
    ageRange: String,
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = Brush.linearGradient(colors),
                shape = RoundedCornerShape(16.dp),
            )
            .background(
                if (isSelected) colors[0].copy(alpha = 0.15f)
                else Color.White.copy(alpha = 0.05f)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Column {
                Text(
                    text = label,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = ageRange,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(colors)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun ReadyStep(
    name: String,
    ageGroup: String,
    onStart: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
    )

    val stageEmoji = when (ageGroup) {
        "kids" -> "🌱"
        "teens" -> "🌿"
        else -> "🌱"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(text = stageEmoji, fontSize = 80.sp)

        Text(
            text = "Welcome, $name!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Your AURA journey begins at\nStage 1: Seed",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )

        Text(
            text = "Complete missions, practice in the Social Lab,\nand watch your avatar evolve!",
            fontSize = 14.sp,
            color = Color(0xFF00E5FF).copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF0B1629),
            ),
        ) {
            Text("Let's Go! 🚀", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun QuizStep(
    q1: Int, onQ1: (Int) -> Unit,
    q2: Int, onQ2: (Int) -> Unit,
    q3: Int, onQ3: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Let's discover your Aura",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        // Q1: Empathy
        QuizQuestion(
            question = "When a friend is upset, I usually...",
            options = listOf(
                "Try to fix their problem",
                "Give them space",
                "Listen and validate their feelings"
            ),
            selectedIndex = q1,
            onSelect = onQ1
        )

        // Q2: Confidence
        QuizQuestion(
            question = "In a group setting, I tend to...",
            options = listOf(
                "Stay quiet and observe",
                "Speak up if I have to",
                "Take charge of the conversation"
            ),
            selectedIndex = q2,
            onSelect = onQ2
        )

        // Q3: Communication
        QuizQuestion(
            question = "When giving feedback, I am...",
            options = listOf(
                "Direct and blunt",
                "Hesitant to hurt feelings",
                "Clear but considerate"
            ),
            selectedIndex = q3,
            onSelect = onQ3
        )

        Button(
            onClick = onNext,
            enabled = q1 != -1 && q2 != -1 && q3 != -1,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF0B1629),
            ),
        ) {
            Text("Reveal My Aura", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun QuizQuestion(
    question: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, text ->
                val isSelected = index == selectedIndex
                Surface(
                    onClick = { onSelect(index) },
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
