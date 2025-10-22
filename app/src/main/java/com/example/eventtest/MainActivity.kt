package com.example.eventtest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtest.ui.theme.EventTestTheme

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

fun onButtonPointerEvent(
    event: PointerEvent,
    setButtonBackgroundColor: (Color) -> Unit,
    setButtonFontColor: (Color) -> Unit,
    getButtonPressPressure: () -> Float,
    setButtonPressPressure: (Float) -> Unit,
    getButtonPressMillis: () -> Long,
    setButtonPressMillis: (Long) -> Unit
) {
    val firstChange = event.changes.first()
    Log.d(
        "onButtonPointerEvent",
        "event: type = ${event.type} "
                + "position = ${firstChange.position.x},${firstChange.position.y}) "
                + "pressure = ${firstChange.pressure} "
                + "uptime = ${firstChange.uptimeMillis} "
                + "pressed = ${firstChange.pressed} "
    )
    // https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/PointerEventType
    when (event.type) {
        PointerEventType.Press -> {
            setButtonPressPressure(firstChange.pressure)
            setButtonPressMillis(firstChange.uptimeMillis)
        }

        PointerEventType.Release -> {
            setButtonBackgroundColor(pressureColor(firstChange.pressure, getButtonPressPressure()))
            setButtonFontColor(durationColor(firstChange.uptimeMillis, getButtonPressMillis()))
        }

        else -> {}
    }
}

@Composable
fun EventBox(name: String, modifier: Modifier = Modifier) {
    var count by remember { mutableStateOf(0) }
    val getCounter: () -> Int = { count++ }
    val setCounter: (Int) -> Unit = { newCount -> count = newCount }

    var buttonBackgroundColor by remember { mutableStateOf(CustomRed) }
    val getButtonBackgroundColor: () -> Color = { buttonBackgroundColor }
    val setButtonBackgroundColor: (Color) -> Unit =
        { newButtonBackgroundColor -> buttonBackgroundColor = newButtonBackgroundColor }

    var buttonFontColor by remember { mutableStateOf(CustomBlue) }
    val getButtonFontColor: () -> Color = { buttonFontColor }
    val setButtonFontColor: (Color) -> Unit =
        { newButtonFontColor -> buttonFontColor = newButtonFontColor }

    var buttonPressPressure by remember { mutableFloatStateOf(0.0f) }
    val getButtonPressPressure: () -> Float = { buttonPressPressure }
    val setButtonPressPressure: (Float) -> Unit =
        { newButtonPressPressure -> buttonPressPressure = newButtonPressPressure }

    var buttonPressMillis by remember { mutableLongStateOf(0L) }
    val getButtonPressMillis: () -> Long = { buttonPressMillis }
    val setButtonPressMillis: (Long) -> Unit =
        { newButtonPressMillis -> buttonPressMillis = newButtonPressMillis }

    Column(
        modifier = Modifier.fillMaxSize(), // Makes the Column take the full width
        horizontalAlignment = Alignment.CenterHorizontally // Centers children horizontally
    ) {
        Spacer(modifier = Modifier.height(300.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(
                    300.dp,
                    300.dp
                )
                .clip(RoundedCornerShape(31.dp))//28.dp)) // Apply rounded corners
                .background(buttonBackgroundColor)
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
                                getButtonPressPressure,
                                setButtonPressPressure,
                                getButtonPressMillis,
                                setButtonPressMillis,
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
    }
}


