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
import androidx.compose.runtime.mutableIntStateOf
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


@Composable
fun EventBox(modifier: Modifier = Modifier) {
    // https://developer.android.com/reference/android/view/ViewConfiguration
    // https://gemini.google.com/share/e3a0e858ec87
    val longPressTimeout = LocalViewConfiguration.current.longPressTimeoutMillis
    val tapTimeout = LocalViewConfiguration.current.doubleTapTimeoutMillis

    var count by remember { mutableIntStateOf(0) }
    val getCounter: () -> Int = { count++ }

    var buttonPressPosition by remember { mutableStateOf(Offset.Zero) }
    val getButtonPressPosition: () -> Offset = { buttonPressPosition }
    val setButtonPressPosition: (Offset) -> Unit =
        { newButtonPressPosition -> buttonPressPosition = newButtonPressPosition }

    var buttonPressPressure by remember { mutableFloatStateOf(0.0f) }
    val setButtonPressPressure: (Float) -> Unit =
        { newButtonPressPressure ->
            buttonPressPressure = newButtonPressPressure
        }
    var buttonPressure by remember { mutableFloatStateOf(0.0f) }
    val setButtonPressure: (Float) -> Unit =
        { newButtonPressure ->
            buttonPressure = newButtonPressure
        }

    val buttonBackColor = MaterialTheme.colorScheme.background
    val buttonMinColor = MaterialTheme.colorScheme.surface
    val buttonMedColor = MaterialTheme.colorScheme.primary
    val buttonMaxColor = MaterialTheme.colorScheme.tertiary
    val buttonFontMinColor = MaterialTheme.colorScheme.onSurface
    val buttonFontMedColor = MaterialTheme.colorScheme.onPrimary
    val buttonFontMaxColor = MaterialTheme.colorScheme.onTertiary

    val getPressureButtonColor: () -> Color = {
        val pressure = buttonPressure
        when {
            buttonPressure < 0.25f -> buttonMinColor
            buttonPressure < 0.45f -> buttonMedColor
            else -> buttonMaxColor
        }
    }
    val getPressureButtonFontColor: () -> Color = {
        when {
            buttonPressure < 0.25f -> buttonFontMinColor
            buttonPressure < 0.45f -> buttonFontMedColor
            else -> buttonFontMaxColor
        }
    }

    var buttonPressUptime by remember { mutableLongStateOf(0L) }
    val getButtonPressUptime: () -> Long = { buttonPressUptime }
    val setButtonPressUptime: (Long) -> Unit =
        { newButtonPressUptime -> buttonPressUptime = newButtonPressUptime }
    var buttonDuration by remember { mutableLongStateOf(0L) }
    val setButtonDuration: (Long) -> Unit =
        { newButtonDuration -> buttonDuration = newButtonDuration }

    val durationButtonShortColor = buttonMinColor
    val durationButtonTapColor = buttonMedColor
    val durationButtonLongColor = buttonMaxColor
    val durationButtonFontShortColor = buttonFontMinColor
    val durationButtonFontTapColor = buttonFontMedColor
    val durationButtonFontLongColor = buttonFontMaxColor
    val getDurationButtonColor: () -> Color = {
        when {
            buttonDuration < tapTimeout -> durationButtonShortColor
            buttonDuration < longPressTimeout -> durationButtonTapColor
            else -> durationButtonLongColor
        }
    }
    val getDurationButtonFontColor: () -> Color = {
        when {
            buttonDuration < tapTimeout -> durationButtonFontShortColor
            buttonDuration < longPressTimeout -> durationButtonFontTapColor
            else -> durationButtonFontLongColor
        }
    }

    var buttonDistance by remember { mutableFloatStateOf(0.0f) }
    val getButtonDistance: () -> Float = { buttonDistance }
    val setButtonDistance: (Float) -> Unit =
        { newButtonDistance -> buttonDistance = newButtonDistance }

    val distanceButtonShortColor = buttonMinColor
    val distanceButtonTapColor = buttonMedColor
    val distanceButtonLongColor = buttonMaxColor
    val distanceButtonFontShortColor = buttonFontMinColor
    val distanceButtonFontTapColor = buttonFontMedColor
    val distanceButtonFontLongColor = buttonFontMaxColor
    val getDistanceButtonColor: () -> Color = {
        when {
            buttonDistance < 50L -> distanceButtonShortColor
            buttonDistance < 100L -> distanceButtonTapColor
            else -> distanceButtonLongColor
        }
    }
    val getDistanceButtonFontColor: () -> Color = {
        when {
            buttonDistance < 50L -> distanceButtonFontShortColor
            buttonDistance < 100L -> distanceButtonFontTapColor
            else -> distanceButtonFontLongColor
        }
    }

    var buttonIsMoved by remember { mutableStateOf(false) }
    val getButtonIsMoved: () -> Boolean = { buttonIsMoved }
    val setButtonIsMoved: (Boolean) -> Unit =
        { newButtonIsMoved -> buttonIsMoved = newButtonIsMoved }
    var selectedDuration by remember { mutableStateOf("TAP") }
    val getSelectedDuration: () -> String = { selectedDuration }
    var selectedPressure by remember { mutableStateOf("AVG") }
    val getSelectedPressure: () -> String = { selectedPressure }
    var boxCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val isContained: (Offset) -> Boolean =
        { pointToCheck ->
            boxCoordinates?.let { coordinates ->
                val boxSize = coordinates.size
                val boxWidth = boxSize.width
                val boxHeight = boxSize.height

                pointToCheck.x >= 0 &&
                        pointToCheck.x < boxWidth &&
                        pointToCheck.y >= 0 &&
                        pointToCheck.y < boxHeight
            } ?: false
        }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(300.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                RadioButtonSelector(
                    selections = listOf("LIGHT", "AVG", "FIRM"),
                    initialSelection = selectedPressure,
                    onOptionSelected = { newOption ->
                        selectedPressure = newOption
                        getCounter()
                    })
                RadioButtonSelector(
                    selections = listOf("SHORT", "TAP", "LONG"),
                    initialSelection = selectedDuration,
                    onOptionSelected = { newOption ->
                        selectedDuration = newOption
                        getCounter()
                    })
            }
            Spacer(modifier = Modifier.width(10.dp))
            val buttonBackgroundColor = buttonBackColor

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(
                        300.dp,
                        300.dp
                    )
                    .clip(RoundedCornerShape(31.dp))
                    .background(buttonBackgroundColor)
                    .onGloballyPositioned { coordinates ->
                        boxCoordinates = coordinates
                    }
                    .pointerInput(
                        getCounter()
                    ) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                onButtonPointerEvent(
                                    event,
                                    getButtonPressPosition,
                                    setButtonPressPosition,
                                    setButtonPressPressure,
                                    setButtonPressure,
                                    getButtonPressUptime,
                                    setButtonPressUptime,
                                    setButtonDuration,
                                    setButtonDistance,
                                    getButtonIsMoved,
                                    setButtonIsMoved,
                                    getSelectedDuration,
                                    getSelectedPressure,
                                    isContained
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
                        ),
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
                        ),
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
                        ),
                    ) {
                        val distance = getButtonDistance().toInt()
                        val distanceString = String.format("%5d", distance)
                        Text(
                            text = "distance: $distanceString",
                            fontSize = 24.sp
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}


