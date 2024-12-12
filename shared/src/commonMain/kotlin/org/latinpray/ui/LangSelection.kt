package org.latinpray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelection(
    title: String,
    langs: Map<String, String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf(selectedItem) }

    Column {
        Spacer(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
        )
        OutlinedTextField(
            value = langs[selectedLang] ?: "none",
            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.secondary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
//                disabledBorderColor = MaterialTheme.colorScheme.secondary,
//                focusedTextColor = MaterialTheme.colorScheme.secondary,
//                unfocusedTextColor = MaterialTheme.colorScheme.secondary,
//                disabledTextColor = MaterialTheme.colorScheme.secondary,
//                cursorColor = MaterialTheme.colorScheme.secondary,
            ),
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.secondary
                        )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            //modifier = Modifier.background(MaterialTheme.colorScheme.onBackground),
        ) {
            langs.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item.key)
                        expanded = false
                        selectedLang = item.key
                    },
                    text = {
                        Text(
                            text = item.value,
                            color = if (item.key == selectedLang)
                                MaterialTheme.colorScheme.tertiary else
                                    MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }
    }
}
