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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.bible_settings_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.settings_screen_title
import org.latinpray.shared.text_decrease_24dp_e8eaed_fill0_wght400_grad0_opsz24
import org.latinpray.shared.text_increase_24dp_e8eaed_fill0_wght400_grad0_opsz24

@Composable
fun MainMenu(
    navController: NavController,
    isExpanded: MutableState<Boolean>,
    fontChange: (scale: Float) -> Unit
) {

    DropdownMenu(
        expanded = isExpanded.value,
        onDismissRequest = { isExpanded.value = false },
        modifier = Modifier.padding(end = 2.dp)
    ) {
        DropdownMenuItem(
            onClick = {
                isExpanded.value = false
                navController.navigate(MainScreens.SettingsScreen.name)
                            },
            text = { Text(text = stringResource(Res.string.settings_screen_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Application Setting",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )
        DropdownMenuItem(
            onClick = {
                isExpanded.value = false
                navController.navigate(MainScreens.SettingsScreen.name)
            },
            text = { Text(text = stringResource(Res.string.bible_settings_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Bible Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )
        DropdownMenuItem(
            onClick = {
                isExpanded.value = false
                navController.navigate(MainScreens.HelpScreen.name)
            },
            text = { Text(text = stringResource(Res.string.help_screen_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = "Help and Support Screen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )
        DropdownMenuItem(
            onClick = {
                isExpanded.value = false
                navController.navigate(MainScreens.AboutScreen.name)
            },
            text = { Text(text = stringResource(Res.string.about_screen_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Application About Screen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )
        HorizontalDivider()
        Row() {
            IconButton(
                onClick = {
                    isExpanded.value = false
                    fontChange(-0.1f)
                },
                modifier = Modifier.size(48.dp).padding(horizontal = 12.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.text_decrease_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Decrease font size",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    isExpanded.value = false
                    fontChange(0.1f)
                },
                modifier = Modifier.size(48.dp).padding(horizontal = 12.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.text_increase_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                    contentDescription = "Increase font size",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

}