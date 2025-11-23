package com.example.eventtest

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculates the Euclidean distance between two points.
 */
fun calculateDistance(offset1: Offset, offset2: Offset): Float {
    val deltaX = offset2.x - offset1.x
    val deltaY = offset2.y - offset1.y
    return sqrt(deltaX.pow(2) + deltaY.pow(2))
}

/**
 * Handles pointer events for button interactions.
 * Tracks pressure, duration, distance, and movement of pointer events.
 *
 * @param event The pointer event to process
 * @param buttonPressPosition Current press position
 * @param onButtonPressPositionChange Callback to update press position
 * @param onButtonPressPressureChange Callback to update press pressure
 * @param onButtonPressureChange Callback to update current pressure
 * @param buttonPressUptime Current press uptime
 * @param onButtonPressUptimeChange Callback to update press uptime
 * @param onButtonDurationChange Callback to update button duration
 * @param onButtonDistanceChange Callback to update button distance
 * @param buttonIsMoved Current moved state
 * @param onButtonIsMovedChange Callback to update moved state
 * @param selectedDuration Currently selected duration option
 * @param selectedPressure Currently selected pressure option
 * @param isContained Function to check if a point is contained within bounds
 */
fun onButtonPointerEvent(
    event: PointerEvent,
    buttonPressPosition: Offset,
    onButtonPressPositionChange: (Offset) -> Unit,
    onButtonPressPressureChange: (Float) -> Unit,
    onButtonPressureChange: (Float) -> Unit,
    buttonPressUptime: Long,
    onButtonPressUptimeChange: (Long) -> Unit,
    onButtonDurationChange: (Long) -> Unit,
    onButtonDistanceChange: (Float) -> Unit,
    buttonIsMoved: Boolean,
    onButtonIsMovedChange: (Boolean) -> Unit,
    selectedDuration: String,
    selectedPressure: String,
    isContained: (Offset) -> Boolean
) {
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerInputChange
    val firstChange = event.changes.firstOrNull() ?: return
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerEventType
    val type = event.type
    val position = firstChange.position
    val pressure = firstChange.pressure
    val uptime = firstChange.uptimeMillis

    when (type) {
        PointerEventType.Press -> {
            onButtonPressPositionChange(position)
            onButtonPressPressureChange(pressure)
            onButtonPressUptimeChange(uptime)
        }

        PointerEventType.Release -> {
            onButtonPressureChange(pressure)
            val duration = uptime - buttonPressUptime
            onButtonDurationChange(duration)
            val distance = calculateDistance(buttonPressPosition, position)
            onButtonDistanceChange(distance)
            val contained = isContained(position)
            Log.d(
                "onButtonPointerEvent",
                "\t$selectedDuration\t$selectedPressure\t$pressure\t$duration\t$distance\t$contained"
            )
            onButtonIsMovedChange(false)
        }

        PointerEventType.Move -> {
            onButtonIsMovedChange(true)
        }

        else -> {
            Log.d(
                "onButtonPointerEvent",
                "\t$selectedDuration\t$selectedPressure\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$buttonIsMoved"
            )
        }
    }
}
