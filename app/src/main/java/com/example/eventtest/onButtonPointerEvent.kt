package com.example.eventtest

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import kotlin.math.pow
import kotlin.math.sqrt

fun calculateDistance(offset1: Offset, offset2: Offset): Float {
    val deltaX = offset2.x - offset1.x
    val deltaY = offset2.y - offset1.y
    return sqrt(deltaX.pow(2) + deltaY.pow(2))
}

fun onButtonPointerEvent(
    event: PointerEvent,

    getButtonPressPosition: () -> Offset,
    setButtonPressPosition: (Offset) -> Unit,
    setButtonPressPressure: (Float) -> Unit,
    setButtonPressure: (Float) -> Unit,
    getButtonPressUptime: () -> Long,
    setButtonPressUptime: (Long) -> Unit,
    setButtonDuration: (Long) -> Unit,
    setButtonDistance: (Float) -> Unit,
    getButtonIsMoved: () -> Boolean,
    setButtonIsMoved: (Boolean) -> Unit,
    getSelectedDuration: () -> String,
    getSelectedPressure: () -> String,
    isContained: (Offset) -> Boolean
) {
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerInputChange
    val firstChange = event.changes.first()
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerEventType
    val type = event.type
    val position = firstChange.position
    val pressure = firstChange.pressure
    val uptime = firstChange.uptimeMillis
    val isMoved = getButtonIsMoved()
    when (type) {
        PointerEventType.Press -> {
            setButtonPressPosition(position)
            setButtonPressPressure(pressure)
            setButtonPressUptime(uptime)
        }

        PointerEventType.Release -> {
            setButtonPressure(pressure)
            val duration = uptime - getButtonPressUptime()
            setButtonDuration(duration)
            val pressPosition = getButtonPressPosition()
            val distance = calculateDistance(pressPosition, position)
            setButtonDistance(distance)
            val isContained = isContained(position)
            Log.d(
                "onButtonPointerEvent",
                "\t${getSelectedDuration()}\t${getSelectedPressure()}\t$pressure\t$duration\t$distance\t$isContained"
            )
            setButtonIsMoved(false)
        }

        PointerEventType.Move -> {
            setButtonIsMoved(true)
        }

        else -> {
            Log.d(
                "onButtonPointerEvent",
                "\t${getSelectedDuration()}\t${getSelectedPressure()}\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved"
            )
        }
    }
}
