package com.example.eventtest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtest.ui.theme.EventTestTheme
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EventBox(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun calculateDistance(offset1: Offset, offset2: Offset): Float {
    val deltaX = offset2.x - offset1.x
    val deltaY = offset2.y - offset1.y
    return sqrt(deltaX.pow(2) + deltaY.pow(2))
}

fun onButtonPointerEvent(
    event: PointerEvent,

    getButtonPressPosition: () -> Offset,
    setButtonPressPosition: (Offset) -> Unit,

    getButtonPressPressure: () -> Float,
    setButtonPressPressure: (Float) -> Unit,

    getButtonPressure: () -> Float,
    setButtonPressure: (Float) -> Unit,
    getPressureButtonColor: () -> Color,
    getPressureButtonFontColor: () -> Color,

    getButtonPressUptime: () -> Long,
    setButtonPressUptime: (Long) -> Unit,

    getButtonDuration: () -> Long,
    setButtonDuration: (Long) -> Unit,
    getDurationButtonColor: () -> Color,
    getDurationButtonFontColor: () -> Color,

    getButtonDistance: () -> Float,
    setButtonDistance: (Float) -> Unit,
    getDistanceButtonColor: () -> Color,
    getDistanceButtonFontColor: () -> Color,

    getButtonIsMoved: () -> Boolean,
    setButtonIsMoved: (Boolean) -> Unit,
    getSelectedDuration: () -> DurationLevel,
    getSelectedDPressure: () -> PressureLevel,
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
                "\t${getSelectedDuration()}\t${getSelectedDPressure()}\t$pressure\t$duration\t$distance\t$isContained"
            )
            setButtonIsMoved(false)
        }

        PointerEventType.Move -> {
            setButtonIsMoved(true)
        }

        else -> {
            Log.d(
                "onButtonPointerEvent",
                "\t${getSelectedDuration()}\t${getSelectedDPressure()}\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved"
            )
        }
    }
}

enum class PressureLevel {
    LIGHT,
    AVG,
    FIRM
}

@Composable
fun PressureSelector(
    initialSelection: PressureLevel,
    onOptionSelected: (PressureLevel) -> Unit
) {
    val (selectedOption, onOptionChange) = remember { mutableStateOf(initialSelection) }
    LaunchedEffect(selectedOption) {
        if (selectedOption != initialSelection) {
            onOptionSelected(selectedOption)
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Pressure",
                fontSize = 20.sp,
            )
            PressureLevel.entries.forEach { pressure ->
                Row(
                    Modifier
                        .selectable(
                            selected = (pressure == selectedOption),
                            onClick = { onOptionChange(pressure) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initialRadioButtonColor = MaterialTheme.colorScheme.tertiary
                    RadioButton(
                        selected = (pressure == selectedOption),
                        onClick = {
                            onOptionChange(pressure)
                        },
                        modifier = Modifier.padding(4.dp),
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            initialRadioButtonColor,
                            initialRadioButtonColor
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    Text(
                        text = pressure.name,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

enum class DurationLevel {
    SHORT,
    TAP,
    LONG
}

@Composable
fun DurationSelector(
    initialSelection: DurationLevel,
    onOptionSelected: (DurationLevel) -> Unit
) {
    val (selectedOption, onOptionChange) = remember { mutableStateOf(initialSelection) }
    LaunchedEffect(selectedOption) {
        if (selectedOption != initialSelection) {
            onOptionSelected(selectedOption)
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Duration",
                fontSize = 20.sp,
            )
            DurationLevel.entries.forEach { duration ->
                Row(
                    Modifier
                        .selectable(
                            selected = (duration == selectedOption),
                            onClick = { onOptionChange(duration) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initialRadioButtonColor = MaterialTheme.colorScheme.tertiary
                    RadioButton(
                        selected = (duration == selectedOption),
                        onClick = {
                            onOptionChange(duration)
                        },
                        modifier = Modifier.padding(4.dp),
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            initialRadioButtonColor,
                            initialRadioButtonColor
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    Text(
                        text = duration.name,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun EventBox(name: String, modifier: Modifier = Modifier) {
    var count by remember { mutableIntStateOf(0) }
    val getCounter: () -> Int = { count++ }

    var buttonPressPosition by remember { mutableStateOf(Offset.Zero) }
    val getButtonPressPosition: () -> Offset = { buttonPressPosition }
    val setButtonPressPosition: (Offset) -> Unit =
        { newButtonPressPosition -> buttonPressPosition = newButtonPressPosition }

    var buttonPressPressure by remember { mutableFloatStateOf(0.0f) }
    var buttonPressure by remember { mutableFloatStateOf(0.0f) }
    val getButtonPressPressure: () -> Float = { buttonPressPressure }
    val setButtonPressPressure: (Float) -> Unit =
        { newButtonPressPressure ->
            buttonPressPressure = newButtonPressPressure
        }
    val getButtonPressure: () -> Float = { buttonPressure }
    val setButtonPressure: (Float) -> Unit =
        { newButtonPressure ->
            buttonPressure = newButtonPressure
        }

    val pressureButtonLightColor = MaterialTheme.colorScheme.primary
    val pressureButtonAvgColor = MaterialTheme.colorScheme.secondary
    val pressureButtonFirmColor = MaterialTheme.colorScheme.tertiary
    val pressureButtonFontLightColor = MaterialTheme.colorScheme.onPrimary
    val pressureButtonFontAvgColor = MaterialTheme.colorScheme.onSecondary
    val pressureButtonFontFirmColor = MaterialTheme.colorScheme.onTertiary
    val getPressureButtonColor: () -> Color = {
        val pressure = buttonPressure
        when {
            buttonPressure < 0.30f -> pressureButtonLightColor
            buttonPressure < 0.50f -> pressureButtonAvgColor
            else -> pressureButtonFirmColor
        }
    }
    val getPressureButtonFontColor: () -> Color = {
        when {
            buttonPressure < 0.30f -> pressureButtonFontLightColor
            buttonPressure < 0.50f -> pressureButtonFontAvgColor
            else -> pressureButtonFontFirmColor
        }
    }

    var buttonPressUptime by remember { mutableLongStateOf(0L) }
    val getButtonPressUptime: () -> Long = { buttonPressUptime }
    val setButtonPressUptime: (Long) -> Unit =
        { newButtonPressUptime -> buttonPressUptime = newButtonPressUptime }
    var buttonPressDuration by remember { mutableFloatStateOf(0.0f) }
    var buttonDuration by remember { mutableLongStateOf(0L) }
    val getButtonDuration: () -> Long = { buttonDuration }
    val setButtonDuration: (Long) -> Unit =
        { newButtonDuration -> buttonDuration = newButtonDuration }

    val durationButtonShortColor = MaterialTheme.colorScheme.primary
    val durationButtonTapColor = MaterialTheme.colorScheme.secondary
    val durationButtonLongColor = MaterialTheme.colorScheme.tertiary
    val durationButtonFontShortColor = MaterialTheme.colorScheme.onPrimary
    val durationButtonFontTapColor = MaterialTheme.colorScheme.onSecondary
    val durationButtonFontLongColor = MaterialTheme.colorScheme.onTertiary
    val getDurationButtonColor: () -> Color = {
        when {
            buttonDuration < 150L -> durationButtonShortColor
            buttonDuration < 600L -> durationButtonTapColor
            else -> durationButtonLongColor
        }
    }
    val getDurationButtonFontColor: () -> Color = {
        when {
            buttonDuration < 150L -> durationButtonFontShortColor
            buttonDuration < 600L -> durationButtonFontTapColor
            else -> durationButtonFontLongColor
        }
    }

    var buttonDistance by remember { mutableStateOf(0.0f) }
    val getButtonDistance: () -> Float = { buttonDistance }
    val setButtonDistance: (Float) -> Unit =
        { newButtonDistance -> buttonDistance = newButtonDistance }

    val distanceButtonShortColor = MaterialTheme.colorScheme.primary
    val distanceButtonTapColor = MaterialTheme.colorScheme.secondary
    val distanceButtonLongColor = MaterialTheme.colorScheme.tertiary
    val distanceButtonFontShortColor = MaterialTheme.colorScheme.onPrimary
    val distanceButtonFontTapColor = MaterialTheme.colorScheme.onSecondary
    val distanceButtonFontLongColor = MaterialTheme.colorScheme.onTertiary
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
    var selectedDuration by remember { mutableStateOf(DurationLevel.TAP) }
    val getSelectedDuration: () -> DurationLevel = { selectedDuration }
    var selectedPressure by remember { mutableStateOf(PressureLevel.AVG) }
    val getSelectedDPressure: () -> PressureLevel = { selectedPressure }
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
            Column() {
                PressureSelector(
                    initialSelection = selectedPressure,
                    onOptionSelected = { newOption ->
                        selectedPressure = newOption
                        getCounter()
                    })
                DurationSelector(
                    initialSelection = selectedDuration,
                    onOptionSelected = { newOption ->
                        selectedDuration = newOption
                        getCounter()
                    })
            }
            Spacer(modifier = Modifier.width(10.dp))
            val buttonBackgroundColor = when (selectedPressure) {
                PressureLevel.LIGHT -> MaterialTheme.colorScheme.primary
                PressureLevel.AVG -> MaterialTheme.colorScheme.secondary
                PressureLevel.FIRM -> MaterialTheme.colorScheme.tertiary
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(
                        300.dp,
                        300.dp
                    )
                    .clip(RoundedCornerShape(31.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
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
                                    getButtonPressPressure,
                                    setButtonPressPressure,
                                    getButtonPressure,
                                    setButtonPressure,
                                    getPressureButtonColor,
                                    getPressureButtonFontColor,
                                    getButtonPressUptime,
                                    setButtonPressUptime,
                                    getButtonDuration,
                                    setButtonDuration,
                                    getDurationButtonColor,
                                    getDurationButtonFontColor,
                                    getButtonDistance,
                                    setButtonDistance,
                                    getDistanceButtonColor,
                                    getDistanceButtonFontColor,
                                    getButtonIsMoved,
                                    setButtonIsMoved,
                                    getSelectedDuration,
                                    getSelectedDPressure,
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
                            text = "distance: ${distanceString}",
                            fontSize = 24.sp
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}


