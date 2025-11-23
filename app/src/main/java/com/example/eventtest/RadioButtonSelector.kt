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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable that displays a group of radio buttons for selecting an option.
 *
 * @param label The label to display above the radio button group
 * @param selections List of selection options
 * @param initialSelection The initially selected option
 * @param onOptionSelected Callback when an option is selected
 */
@Composable
fun RadioButtonSelector(
    label: String,
    selections: List<String>,
    initialSelection: String,
    onOptionSelected: (String) -> Unit
) {
    var selectedOption by remember(initialSelection) { mutableStateOf(initialSelection) }

    LaunchedEffect(selectedOption) {
        if (selectedOption != initialSelection) {
            onOptionSelected(selectedOption)
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium
        )
        selections.forEach { selection ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = (selection == selectedOption),
                        onClick = { selectedOption = selection },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val radioButtonColor = MaterialTheme.colorScheme.tertiary
                RadioButton(
                    selected = (selection == selectedOption),
                    onClick = { selectedOption = selection },
                    modifier = Modifier.padding(4.dp),
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = radioButtonColor,
                        unselectedColor = radioButtonColor
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
