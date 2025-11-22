@Composable
fun MyRadioGroup(
    options: List<String>,
    initialSelection: String,
    onOptionSelected: (String) -> Unit
) {
    // 1. Internal state to hold the currently selected option.
    // We initialize it with the value passed from the parent.
    val (selectedOption, onOptionChange) = remember {
        mutableStateOf(initialSelection)
    }

    // Pass the state change back to the parent component immediately on change
    LaunchedEffect(selectedOption) {
        if (selectedOption != initialSelection) {
            onOptionSelected(selectedOption)
        }
    }

    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionChange(option) },
                        role = Role.RadioButton
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionChange(option) } // The row's onClick handles the main selection
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun RadioGroupScreen() {
    // 1. Define the available options
    val choices = listOf("Option A", "Option B", "Option C")

    // 2. State to hold the selected value received from MyRadioGroup
    var selectedValue by remember { mutableStateOf(choices.first()) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "Selected Choice: **$selectedValue**",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3. Instantiate the Radio Group
        MyRadioGroup(
            options = choices,
            initialSelection = selectedValue,
            // 4. The callback function to receive the selected option
            onOptionSelected = { newOption ->
                selectedValue = newOption // Update the parent's state
            }
        )
    }
}