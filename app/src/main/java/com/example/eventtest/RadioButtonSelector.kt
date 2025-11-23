package com.example.eventtest


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RadioButtonSelector(
    selections: List<String>,
    initialSelection: String,
    onOptionSelected: (String) -> Unit
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
                text = "Pressure",
                fontSize = 20.sp,
            )
            selections.forEach { selection ->
                Row(
                    Modifier
                        .selectable(
                            selected = (selection == selectedOption),
                            onClick = { onOptionChange(selection) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initialRadioButtonColor = MaterialTheme.colorScheme.tertiary
                    RadioButton(
                        selected = (selection == selectedOption),
                        onClick = {
                            onOptionChange(selection)
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
                        text = selection,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
