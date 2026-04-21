package com.aura.app.ui.components.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.ui.theme.AuraColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    skills: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val animatedValues = skills.map { (_, value) ->
        animateFloatAsState(
            targetValue = value / 100f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing),
        ).value
    }

    val skillColors = listOf(
        AuraColors.SkillEmpathy,
        AuraColors.SkillConfidence,
        AuraColors.SkillCommunication,
        AuraColors.SkillLeadership,
        AuraColors.SkillResilience,
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                val labels = skills.keys.toList()
                val values = animatedValues
                val numAxes = labels.size

                // Draw grid rings
                for (ring in 1..4) {
                    val ringRadius = radius * ring / 4
                    drawPolygon(
                        center = center,
                        radius = ringRadius,
                        sides = numAxes,
                        color = surfaceVariantColor.copy(alpha = 0.4f),
                        style = Stroke(width = 1f),
                    )
                }

                // Draw axis lines
                for (i in 0 until numAxes) {
                    val angle = (2 * PI * i / numAxes) - PI / 2
                    val endX = center.x + (radius * cos(angle)).toFloat()
                    val endY = center.y + (radius * sin(angle)).toFloat()
                    drawLine(
                        color = surfaceVariantColor.copy(alpha = 0.3f),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 1f,
                    )
                }

                // Draw data polygon (filled)
                val dataPath = Path()
                values.forEachIndexed { i, value ->
                    val angle = (2 * PI * i / numAxes) - PI / 2
                    val r = radius * value.coerceIn(0f, 1f)
                    val x = center.x + (r * cos(angle)).toFloat()
                    val y = center.y + (r * sin(angle)).toFloat()
                    if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
                }
                dataPath.close()

                drawPath(
                    path = dataPath,
                    color = primaryColor.copy(alpha = 0.2f),
                )

                drawPath(
                    path = dataPath,
                    color = primaryColor,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round),
                )

                // Draw data points
                values.forEachIndexed { i, value ->
                    val angle = (2 * PI * i / numAxes) - PI / 2
                    val r = radius * value.coerceIn(0f, 1f)
                    val x = center.x + (r * cos(angle)).toFloat()
                    val y = center.y + (r * sin(angle)).toFloat()
                    drawCircle(
                        color = skillColors[i % skillColors.size],
                        radius = 5f,
                        center = Offset(x, y),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f,
                        center = Offset(x, y),
                    )
                }
            }

            // Skill labels around the chart
            val labels = skills.entries.toList()
            labels.forEachIndexed { i, (name, value) ->
                val angle = (2 * PI * i / labels.size) - PI / 2
                val labelRadius = 115.dp
                val offsetX = (labelRadius.value * cos(angle)).toFloat()
                val offsetY = (labelRadius.value * sin(angle)).toFloat()

                Column(
                    modifier = Modifier.offset(x = offsetX.dp, y = offsetY.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceColor.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                    )
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.labelSmall,
                        color = skillColors[i % skillColors.size],
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPolygon(
    center: Offset,
    radius: Float,
    sides: Int,
    color: Color,
    style: Stroke,
) {
    val path = Path()
    for (i in 0 until sides) {
        val angle = (2 * PI * i / sides) - PI / 2
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = style)
}
