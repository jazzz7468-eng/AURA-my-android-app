package com.aura.app.ui.screens.mirror

import android.Manifest
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.aura.app.data.mirror.PoseAnalyzer
import com.aura.app.ui.components.AdaptiveCard
import com.aura.app.ui.components.avatar.StageUpCelebration
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.viewmodel.MirrorViewModel
import java.util.concurrent.Executors

@Composable
@kotlin.OptIn(ExperimentalPermissionsApi::class)
fun MirrorScreen(
    viewModel: MirrorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val ageGroup = AuraTheme.ageGroup
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var showStageUp by remember { mutableStateOf(false) }
    var newStage by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.stageUpEvent.collect { stage ->
            newStage = stage
            showStageUp = true
        }
    }

    if (uiState.isSessionActive) {
        // ── ACTIVE SESSION VIEW (Camera + Live Feedback) ──
        ActiveSessionView(
            uiState = uiState,
            viewModel = viewModel,
            hasCameraPermission = cameraPermission.status.isGranted,
            onRequestPermission = { cameraPermission.launchPermissionRequest() },
        )
    } else {
        // ── MAIN HUB VIEW ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = if (ageGroup == "kids") "🪞 Mirror-Tech" else "Mirror-Tech",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Practice your body language with real-time AI feedback. All analysis runs on your device — no video is ever uploaded.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            // ── Stats Overview ──
            if (uiState.totalSessions > 0) {
                AdaptiveCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StatColumn("🪞", "${uiState.totalSessions}", "Sessions")
                        StatColumn("💪", "${uiState.averageConfidence?.toInt() ?: 0}%", "Avg Confidence")
                    }
                }
            }

            // ── Start Session Card ──
            AdaptiveCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("🎯", fontSize = 48.sp)
                    Text(
                        text = "Practice Session",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "The AI will analyze your posture, eye contact, and gestures in real-time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DurationChip("30s", 30, viewModel)
                        DurationChip("60s", 60, viewModel)
                        DurationChip("90s", 90, viewModel)
                    }

                    Button(
                        onClick = { viewModel.startSession() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Session (+30 XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── Past Sessions ──
            if (uiState.pastSessions.isNotEmpty()) {
                Text(
                    text = "Recent Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                uiState.pastSessions.take(5).forEach { session ->
                    PastSessionCard(session)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // ── Session Results Dialog ──
    if (uiState.showResults && uiState.sessionSummary != null) {
        ResultsDialog(
            summary = uiState.sessionSummary!!,
            xpEarned = uiState.xpEarned,
            onDismiss = { viewModel.dismissResults() },
        )
    }

    // ── Stage-up celebration ──
    if (showStageUp) {
        StageUpCelebration(
            stage = newStage,
            ageGroup = ageGroup,
            onDismiss = { showStageUp = false },
        )
    }
}

// ──────────────────────────────────────────────────────
// ACTIVE SESSION (Camera + Real-time overlay)
// ──────────────────────────────────────────────────────

@Composable
private fun ActiveSessionView(
    uiState: com.aura.app.viewmodel.MirrorUiState,
    viewModel: MirrorViewModel,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
) {
    if (!hasCameraPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("📷", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Camera Permission Needed",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "We need access to your camera to provide real-time body language feedback.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRequestPermission, shape = RoundedCornerShape(12.dp)) {
                Text("Grant Permission", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        CameraPreview(viewModel = viewModel)

        // Top bar overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Timer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.6f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val pulsatingAlpha by rememberInfiniteTransition(label = "rec").animateFloat(
                        initialValue = 0.3f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                        label = "rec_alpha",
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = pulsatingAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTime(uiState.elapsedSeconds),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = " / ${formatTime(uiState.sessionDurationTarget)}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                    )
                }
            }

            // End button
            FilledTonalButton(
                onClick = { viewModel.endSession() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f),
                    contentColor = Color.White,
                ),
            ) {
                Text("End", fontWeight = FontWeight.Bold)
            }
        }

        // Live Metrics at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            // Feedback message
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = uiState.feedbackMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Live score bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LiveMetric("Posture", uiState.currentPosture, Color(0xFF4CAF50))
                LiveMetric("Eyes", if (uiState.currentEyeContact) 100 else 20, Color(0xFF2196F3))
                LiveMetric("Gestures", if (uiState.currentGestures) 80 else 10, Color(0xFFFF9800))
                LiveMetric("Overall", uiState.liveConfidence, MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun CameraPreview(viewModel: MirrorViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    val poseDetector = remember {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(640, 480))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees,
                        )
                        poseDetector.process(inputImage)
                            .addOnSuccessListener { pose ->
                                if (pose.allPoseLandmarks.isNotEmpty()) {
                                    val analysis = PoseAnalyzer.analyzeFrame(pose)
                                    viewModel.onFrameAnalyzed(analysis)
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )
}

// ──────────────────────────────────────────────────────
// UI COMPONENTS
// ──────────────────────────────────────────────────────

@Composable
private fun LiveMetric(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun DurationChip(label: String, seconds: Int, viewModel: MirrorViewModel) {
    FilledTonalButton(
        onClick = { viewModel.startSession(seconds) },
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun PastSessionCard(session: com.aura.app.data.local.entities.MirrorSession) {
    val dateFormat = remember { java.text.SimpleDateFormat("MMM dd • hh:mm a", java.util.Locale.getDefault()) }

    AdaptiveCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getConfidenceColor(session.overallConfidence).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("💪", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Confidence: ${session.overallConfidence}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${session.durationSeconds}s • ${dateFormat.format(java.util.Date(session.completedAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
            Text(
                text = "+${session.xpEarned} XP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ResultsDialog(
    summary: PoseAnalyzer.SessionSummary,
    xpEarned: Int,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("🪞", fontSize = 40.sp)
                Text(
                    text = "Session Complete!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Overall confidence
                Surface(
                    shape = CircleShape,
                    color = getConfidenceColor(summary.overallConfidence).copy(alpha = 0.15f),
                    modifier = Modifier.size(80.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${summary.overallConfidence}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = getConfidenceColor(summary.overallConfidence),
                        )
                    }
                }
                Text("Overall Confidence", style = MaterialTheme.typography.labelMedium)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Breakdown
                FeedbackSection("Posture", summary.postureScore, summary.postureFeedback)
                FeedbackSection("Eye Contact", summary.eyeContactScore, summary.eyeContactFeedback)
                FeedbackSection("Gestures", summary.gestureScore, summary.gestureFeedback)

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "You earned +$xpEarned XP!",
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FeedbackSection(label: String, score: Int, feedback: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, fontWeight = FontWeight.Bold)
            Text("$score%", color = getConfidenceColor(score))
        }
        Text(
            text = feedback,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StatColumn(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

// ──────────────────────────────────────────────────────
// HELPERS
// ──────────────────────────────────────────────────────

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

private fun getConfidenceColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50)
        score >= 50 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}
