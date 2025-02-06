/*
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. Look for COPYING file in the top folder.
 *  If not, see http://www.gnu.org/licenses/.
 */

package org.latinpray.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LangSelection(
    title: String,
    langs: MutableMap<String, String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    withInput: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf(selectedItem) }
    var text by remember { mutableStateOf(langs[selectedLang] ?: "") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
        )
        OutlinedTextField(
            value = (if (withInput) selectedLang else langs[selectedLang]) ?: "none",
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
                        if (!withInput) {
                            onItemSelected(item.key)
                        }
                        expanded = false
                        selectedLang = item.key
                        text = item.value
                    },
                    text = {
                        Text(
                            text = (if (withInput) item.key else item.value),
                            color = if (item.key == selectedLang)
                                MaterialTheme.colorScheme.tertiary else
                                    MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }
        if (withInput) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    langs[selectedLang] = it
                    onItemSelected(selectedLang)
                                },
                readOnly = false,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
        }
    }
}
