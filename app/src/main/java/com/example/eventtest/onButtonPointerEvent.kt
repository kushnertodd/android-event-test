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
            logEvent(event)
        }

        PointerEventType.Release -> {
/*
            /only release pressure matters
            Press	0.14
            Release	0.14
            Press	0.15
            Release	0.33
            Press	0.17
            Release	0.52
            Press	0.23
            Release	0.74
*/
            onButtonPressureChange(pressure)
            val duration = uptime - buttonPressUptime
            onButtonDurationChange(duration)
            val distance = calculateDistance(buttonPressPosition, position)
            onButtonDistanceChange(distance)
            val contained = isContained(position)
            logEvent(event, duration, distance, contained, buttonIsMoved)
            /*
                        Log.d(
                            "onButtonPointerEvent",
                            "\t$selectedDuration\t$selectedPressure\t$pressure\t$duration\t$distance\t$contained"
                        )
            */
            onButtonIsMovedChange(false)
        }

        PointerEventType.Move -> {
            onButtonIsMovedChange(true)
        }

        else -> {
/*
            Log.d(
                "onButtonPointerEvent",
                "\t$selectedDuration\t$selectedPressure\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$buttonIsMoved"
            )
*/
            logEvent(event)
        }
    }
}
fun formatFloat(number:Float,digits:Int) = String.format("%.${digits}f", number)

fun logEvent(
    event: PointerEvent,
    duration: Long = 0L,
    distance: Float = 0.0f,
    contained: Boolean = false,
    buttonIsMoved: Boolean = false
) {
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerInputChange
    val firstChange = event.changes.firstOrNull() ?: return
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerEventType
    val type = event.type
    val position = firstChange.position
    val pressure = formatFloat(firstChange.pressure,2)
    val distanceString = formatFloat(distance,2)
    //val uptime = firstChange.uptimeMillis
    Log.d(
        "onButtonPointerEvent",
//        "\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$duration\t$distance\t$contained\t$buttonIsMoved"
        "\t$type\t$pressure\t$duration\t${position.x}\t${position.y}\t$contained\t$distance\t$buttonIsMoved"
    )
}
