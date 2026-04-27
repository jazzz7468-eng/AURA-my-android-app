package com.aura.app.data.mirror

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Analyzes ML Kit Pose landmarks to produce body language scores.
 * All processing runs entirely on-device — no video is ever uploaded.
 */
object PoseAnalyzer {

    data class FrameAnalysis(
        val postureScore: Int = 0,       // 0-100
        val shoulderAlignment: Float = 0f,
        val headTilt: Float = 0f,
        val isLookingStraight: Boolean = false,
        val handsVisible: Boolean = false,
        val isStandingTall: Boolean = false,
    )

    data class SessionSummary(
        val postureScore: Int = 0,
        val postureFeedback: String = "",
        val eyeContactScore: Int = 0,
        val eyeContactFeedback: String = "",
        val gestureScore: Int = 0,
        val gestureFeedback: String = "",
        val overallConfidence: Int = 0,
    )

    /**
     * Analyze a single frame's pose landmarks.
     */
    fun analyzeFrame(pose: Pose): FrameAnalysis {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
        val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (leftShoulder == null || rightShoulder == null || nose == null) {
            return FrameAnalysis()
        }

        // 1. Shoulder Alignment (are shoulders level?)
        val shoulderDiff = abs(leftShoulder.position.y - rightShoulder.position.y)
        val shoulderAlignment = (1f - (shoulderDiff / 80f).coerceIn(0f, 1f)) * 100f

        // 2. Head Tilt (is head centered between shoulders?)
        val shoulderCenterX = (leftShoulder.position.x + rightShoulder.position.x) / 2f
        val headOffset = abs(nose.position.x - shoulderCenterX)
        val headTilt = (1f - (headOffset / 100f).coerceIn(0f, 1f)) * 100f

        // 3. Looking Straight (based on ear symmetry)
        val isLookingStraight = if (leftEar != null && rightEar != null) {
            val earDiff = abs(leftEar.position.x - rightEar.position.x)
            earDiff > 30f // If ears are roughly equidistant, user is facing camera
        } else false

        // 4. Hands visible (gesture engagement)
        val handsVisible = leftWrist != null && rightWrist != null

        // 5. Standing tall (shoulders above hips by a good margin)
        val isStandingTall = if (leftHip != null && rightHip != null) {
            val hipCenter = (leftHip.position.y + rightHip.position.y) / 2f
            val shoulderCenter = (leftShoulder.position.y + rightShoulder.position.y) / 2f
            (hipCenter - shoulderCenter) > 100f // Good vertical distance
        } else false

        val postureScore = (
            shoulderAlignment * 0.4f +
            headTilt * 0.3f +
            (if (isStandingTall) 30f else 0f)
        ).toInt().coerceIn(0, 100)

        return FrameAnalysis(
            postureScore = postureScore,
            shoulderAlignment = shoulderAlignment,
            headTilt = headTilt,
            isLookingStraight = isLookingStraight,
            handsVisible = handsVisible,
            isStandingTall = isStandingTall,
        )
    }

    /**
     * Summarize an entire session from multiple frame analyses.
     */
    fun summarizeSession(frames: List<FrameAnalysis>): SessionSummary {
        if (frames.isEmpty()) return SessionSummary()

        // Average posture
        val avgPosture = frames.map { it.postureScore }.average().toInt()
        val postureFeedback = when {
            avgPosture >= 80 -> "Excellent posture! Your shoulders were well-aligned and you stood tall throughout."
            avgPosture >= 60 -> "Good posture overall. Try keeping your shoulders more level for an even stronger presence."
            avgPosture >= 40 -> "Your posture needs work. Focus on straightening your back and leveling your shoulders."
            else -> "Significant posture improvement needed. Practice standing tall with shoulders back."
        }

        // Eye contact (looking straight)
        val lookingStraightPercent = (frames.count { it.isLookingStraight }.toFloat() / frames.size * 100).toInt()
        val eyeContactFeedback = when {
            lookingStraightPercent >= 80 -> "Outstanding eye contact! You maintained focus consistently."
            lookingStraightPercent >= 60 -> "Good eye contact. You looked away occasionally — try to stay engaged longer."
            lookingStraightPercent >= 40 -> "Moderate eye contact. Practice maintaining your gaze more steadily."
            else -> "Eye contact needs improvement. Try focusing on the camera as if it were a person's eyes."
        }

        // Gesture engagement (hands visible = actively gesturing)
        val gesturePercent = (frames.count { it.handsVisible }.toFloat() / frames.size * 100).toInt()
        val gestureFeedback = when {
            gesturePercent >= 70 -> "Great use of hand gestures! Your expressiveness adds energy to your communication."
            gesturePercent >= 40 -> "Decent gesture usage. Try incorporating more open-hand movements to emphasize points."
            else -> "Very limited gestures detected. Using your hands while speaking makes you appear more confident."
        }

        val overallConfidence = (avgPosture * 0.4 + lookingStraightPercent * 0.35 + gesturePercent * 0.25).toInt().coerceIn(0, 100)

        return SessionSummary(
            postureScore = avgPosture,
            postureFeedback = postureFeedback,
            eyeContactScore = lookingStraightPercent,
            eyeContactFeedback = eyeContactFeedback,
            gestureScore = gesturePercent,
            gestureFeedback = gestureFeedback,
            overallConfidence = overallConfidence,
        )
    }
}
