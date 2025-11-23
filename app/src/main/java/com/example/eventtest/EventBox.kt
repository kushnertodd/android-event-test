package com.example.eventtest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Constants for pressure thresholds
private const val PRESSURE_LOW_THRESHOLD = 0.25f
private const val PRESSURE_MEDIUM_THRESHOLD = 0.45f

// Constants for distance thresholds (in pixels)
private const val DISTANCE_SHORT_THRESHOLD = 50f
private const val DISTANCE_MEDIUM_THRESHOLD = 100f

// Constants for UI dimensions
private val BOX_SIZE = 300.dp
private val BOX_CORNER_RADIUS = 31.dp
private val TOP_SPACER_HEIGHT = 300.dp

/**
 * Composable that displays an interactive event box for testing pointer events.
 * Tracks pressure, duration, and distance of pointer interactions.
 */
@Composable
fun EventBox(modifier: Modifier = Modifier) {
    // https://developer.android.com/reference/android/view/ViewConfiguration
    // https://gemini.google.com/share/e3a0e858ec87
    val longPressTimeout = LocalViewConfiguration.current.longPressTimeoutMillis
    val tapTimeout = LocalViewConfiguration.current.doubleTapTimeoutMillis

    // State variables
    var buttonPressPosition by remember { mutableStateOf(Offset.Zero) }
    var buttonPressPressure by remember { mutableFloatStateOf(0.0f) }
    var buttonPressure by remember { mutableFloatStateOf(0.0f) }
    var buttonPressUptime by remember { mutableLongStateOf(0L) }
    var buttonDuration by remember { mutableLongStateOf(0L) }
    var buttonDistance by remember { mutableFloatStateOf(0.0f) }
    var buttonIsMoved by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf("TAP") }
    var selectedPressure by remember { mutableStateOf("AVG") }
    var boxCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Color scheme
    val colorScheme = MaterialTheme.colorScheme
    val buttonBackColor = colorScheme.background
    val buttonMinColor = colorScheme.surface
    val buttonMedColor = colorScheme.primary
    val buttonMaxColor = colorScheme.tertiary
    val buttonFontMinColor = colorScheme.onSurface
    val buttonFontMedColor = colorScheme.onPrimary
    val buttonFontMaxColor = colorScheme.onTertiary

    // Helper function to get color based on pressure
    fun getPressureButtonColor(): Color = when {
        buttonPressure < PRESSURE_LOW_THRESHOLD -> buttonMinColor
        buttonPressure < PRESSURE_MEDIUM_THRESHOLD -> buttonMedColor
        else -> buttonMaxColor
    }

    fun getPressureButtonFontColor(): Color = when {
        buttonPressure < PRESSURE_LOW_THRESHOLD -> buttonFontMinColor
        buttonPressure < PRESSURE_MEDIUM_THRESHOLD -> buttonFontMedColor
        else -> buttonFontMaxColor
    }

    // Helper function to get color based on duration
    fun getDurationButtonColor(): Color = when {
        buttonDuration < tapTimeout -> buttonMinColor
        buttonDuration < longPressTimeout -> buttonMedColor
        else -> buttonMaxColor
    }

    fun getDurationButtonFontColor(): Color = when {
        buttonDuration < tapTimeout -> buttonFontMinColor
        buttonDuration < longPressTimeout -> buttonFontMedColor
        else -> buttonFontMaxColor
    }

    // Helper function to get color based on distance
    fun getDistanceButtonColor(): Color = when {
        buttonDistance < DISTANCE_SHORT_THRESHOLD -> buttonMinColor
        buttonDistance < DISTANCE_MEDIUM_THRESHOLD -> buttonMedColor
        else -> buttonMaxColor
    }

    fun getDistanceButtonFontColor(): Color = when {
        buttonDistance < DISTANCE_SHORT_THRESHOLD -> buttonFontMinColor
        buttonDistance < DISTANCE_MEDIUM_THRESHOLD -> buttonFontMedColor
        else -> buttonFontMaxColor
    }

    // Check if point is contained within box bounds
    fun isContained(pointToCheck: Offset): Boolean =
        boxCoordinates?.let { coordinates ->
            val boxSize = coordinates.size
            pointToCheck.x >= 0 &&
                    pointToCheck.x < boxSize.width &&
                    pointToCheck.y >= 0 &&
                    pointToCheck.y < boxSize.height
        } ?: false

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(TOP_SPACER_HEIGHT))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                RadioButtonSelector(
                    label = "Pressure",
                    selections = listOf("LIGHT", "AVG", "FIRM"),
                    initialSelection = selectedPressure,
                    onOptionSelected = { selectedPressure = it }
                )
                RadioButtonSelector(
                    label = "Duration",
                    selections = listOf("SHORT", "TAP", "LONG"),
                    initialSelection = selectedDuration,
                    onOptionSelected = { selectedDuration = it }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(BOX_SIZE, BOX_SIZE)
                    .clip(RoundedCornerShape(BOX_CORNER_RADIUS))
                    .background(buttonBackColor)
                    .onGloballyPositioned { boxCoordinates = it }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                onButtonPointerEvent(
                                    event = event,
                                    buttonPressPosition = buttonPressPosition,
                                    onButtonPressPositionChange = { buttonPressPosition = it },
                                    onButtonPressPressureChange = { buttonPressPressure = it },
                                    onButtonPressureChange = { buttonPressure = it },
                                    buttonPressUptime = buttonPressUptime,
                                    onButtonPressUptimeChange = { buttonPressUptime = it },
                                    onButtonDurationChange = { buttonDuration = it },
                                    onButtonDistanceChange = { buttonDistance = it },
                                    buttonIsMoved = buttonIsMoved,
                                    onButtonIsMovedChange = { buttonIsMoved = it },
                                    selectedDuration = selectedDuration,
                                    selectedPressure = selectedPressure,
                                    isContained = ::isContained
                                )
                            }
                        }
                    }
            ) {
                Column {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getPressureButtonColor(),
                            contentColor = getPressureButtonFontColor()
                        )
                    ) {
                        Text(
                            text = "pressure: $selectedPressure",
                            fontSize = 24.sp
                        )
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getDurationButtonColor(),
                            contentColor = getDurationButtonFontColor()
                        )
                    ) {
                        Text(
                            text = "duration: $selectedDuration",
                            fontSize = 24.sp
                        )
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getDistanceButtonColor(),
                            contentColor = getDistanceButtonFontColor()
                        )
                    ) {
                        Text(
                            text = "distance: ${String.format("%5d", buttonDistance.toInt())}",
                            fontSize = 24.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}
