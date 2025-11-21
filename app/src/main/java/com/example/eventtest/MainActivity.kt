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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

// https://www.computerhope.com/htmcolor.htm
val CustomRed = Color(0xFFFF0000)
val CustomOrange = Color(0xFFFFA500)
val CustomYellow = Color(0xFFFFFF00)
val CustomBlue = Color(0xFF0000FF)
val CustomCyan = Color(0xFF00FFFF)
val CustomGreen = Color(0xFF008000)

fun pressureColor(end: Float, begin: Float): Color {
    val avg = (end + begin) / 2
    return when {
        avg < 0.33 -> CustomRed
        avg < 0.66 -> CustomOrange
        else -> CustomYellow
    }
}

fun durationColor(end: Long, begin: Long): Color {
    val duration = (end - begin)
    return when {
        duration < 333 -> CustomBlue
        duration < 666 -> CustomCyan
        else -> CustomGreen
    }
}

fun calculateDistance(offset1: Offset, offset2: Offset): Float {
    val deltaX = offset2.x - offset1.x
    val deltaY = offset2.y - offset1.y
    return sqrt(deltaX.pow(2) + deltaY.pow(2))
}

fun onButtonPointerEvent(
    event: PointerEvent,
    setButtonBackgroundColor: (Color) -> Unit,
    setButtonFontColor: (Color) -> Unit,
    getButtonPressPosition: () -> Offset,
    setButtonPressPosition: (Offset) -> Unit,
    getButtonPressPressure: () -> Float,
    setButtonPressPressure: (Float) -> Unit,
    getButtonPressUptime: () -> Long,
    setButtonPressUptime: (Long) -> Unit,
    getButtonIsMoved: () -> Boolean,
    setButtonIsMoved: (Boolean) -> Unit,
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
            /*
                        Log.d(
                            "onButtonPointerEvent",
                            "\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved"
                        )
            */
        }

        PointerEventType.Release -> {
            setButtonBackgroundColor(pressureColor(firstChange.pressure, getButtonPressPressure()))
            setButtonFontColor(durationColor(firstChange.uptimeMillis, getButtonPressUptime()))
            val duration = uptime - getButtonPressUptime()
            val pressPosition = getButtonPressPosition()
            val distance = calculateDistance(pressPosition, position)
            val isContained = isContained(position)
            Log.d(
                "onButtonPointerEvent",
                //"\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved\t$duration\t$distance"
                "\t$pressure\t$duration\t$distance\t$isContained"
            )
            setButtonIsMoved(false)
        }

        PointerEventType.Move -> {
            /*
                        Log.d(
                            "onButtonPointerEvent",
                            "\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved"
                        )
            */
            setButtonIsMoved(true)
        }

        else -> {
            Log.d(
                "onButtonPointerEvent",
                "\t$type\t${position.x}\t${position.y}\t$uptime\t$pressure\t$isMoved"
            )
        }
    }
}

enum class PressureLevel {
    LOW,
    MED,
    HIGH
}

@Composable
fun PressureSelector() {
    val pressureOptions = listOf(PressureLevel.LOW, PressureLevel.MED, PressureLevel.HIGH)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(pressureOptions[2]) }
    Column(
        // modifier = Modifier
        //  .fillMaxWidth()
        //   .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Pressure",
                fontSize = 20.sp,
            )
            pressureOptions.forEach { pressure ->
                Row(
                    Modifier
                        //  .fillMaxWidth()
                        .selectable(
                            selected = (pressure == selectedOption),
                            onClick = { onOptionSelected(pressure) }
                        )
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (pressure == selectedOption),
                        onClick = {
                            onOptionSelected(pressure)
                        },
                        modifier = Modifier.padding(4.dp),
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            Color.Green,
                            Color.DarkGray
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    Text(
                        text = pressure.name,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 15.sp,
                        //fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

enum class DurationLevel {
    LOW,
    MED,
    HIGH
}

@Composable
fun DurationSelector() {
    val durationOptions = listOf(DurationLevel.LOW, DurationLevel.MED, DurationLevel.HIGH)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(durationOptions[2]) }
    Column(
        // modifier = Modifier
        //  .fillMaxWidth()
        //   .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Duration",
                fontSize = 20.sp,
            )
            durationOptions.forEach { duration ->
                Row(
                    Modifier
                        //  .fillMaxWidth()
                        .selectable(
                            selected = (duration == selectedOption),
                            onClick = { onOptionSelected(duration) }
                        )
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (duration == selectedOption),
                        onClick = {
                            onOptionSelected(duration)
                        },
                        modifier = Modifier.padding(4.dp),
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            Color.Green,
                            Color.DarkGray
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    Text(
                        text = duration.name,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 15.sp,
                        //fontWeight = FontWeight.Bold
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
    //val setCounter: (Int) -> Unit = { newCount -> count = newCount }

    var buttonBackgroundColor by remember { mutableStateOf(CustomRed) }
    //val getButtonBackgroundColor: () -> Color = { buttonBackgroundColor }
    val setButtonBackgroundColor: (Color) -> Unit =
        { newButtonBackgroundColor -> buttonBackgroundColor = newButtonBackgroundColor }

    var buttonFontColor by remember { mutableStateOf(CustomBlue) }
    //val getButtonFontColor: () -> Color = { buttonFontColor }
    val setButtonFontColor: (Color) -> Unit =
        { newButtonFontColor -> buttonFontColor = newButtonFontColor }

    var buttonPressPosition by remember { mutableStateOf(Offset.Zero) }
    val getButtonPressPosition: () -> Offset = { buttonPressPosition }
    val setButtonPressPosition: (Offset) -> Unit =
        { newButtonPressPosition -> buttonPressPosition = newButtonPressPosition }

    var buttonPressPressure by remember { mutableFloatStateOf(0.0f) }
    val getButtonPressPressure: () -> Float = { buttonPressPressure }
    val setButtonPressPressure: (Float) -> Unit =
        { newButtonPressPressure -> buttonPressPressure = newButtonPressPressure }

    var buttonPressUptime by remember { mutableLongStateOf(0L) }
    val getButtonPressUptime: () -> Long = { buttonPressUptime }
    val setButtonPressUptime: (Long) -> Unit =
        { newButtonPressUptime -> buttonPressUptime = newButtonPressUptime }

    var buttonIsMoved by remember { mutableStateOf(false) }
    val getButtonIsMoved: () -> Boolean = { buttonIsMoved }
    val setButtonIsMoved: (Boolean) -> Unit =
        { newButtonIsMoved -> buttonIsMoved = newButtonIsMoved }
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
        //  modifier = Modifier.fillMaxSize(), // Makes the Column take the full width
        horizontalAlignment = Alignment.CenterHorizontally // Centers children horizontally
    ) {
        Spacer(modifier = Modifier.height(300.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
            //   .fillMaxHeight(),
        ) {
            Column() {
                PressureSelector()
                DurationSelector()
            }
            Spacer(modifier = Modifier.width(10.dp))
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
                                    setButtonBackgroundColor,
                                    setButtonFontColor,
                                    getButtonPressPosition,
                                    setButtonPressPosition,
                                    getButtonPressPressure,
                                    setButtonPressPressure,
                                    getButtonPressUptime,
                                    setButtonPressUptime,
                                    getButtonIsMoved,
                                    setButtonIsMoved,
                                    isContained
                                )
                            }
                        }
                    }
            ) {
                Text(
                    text = "test",
                    color = buttonFontColor,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}


