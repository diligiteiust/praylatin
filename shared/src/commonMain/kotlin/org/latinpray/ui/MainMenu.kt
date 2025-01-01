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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.settings_screen_title

@Composable
fun MainMenu(
    navController: NavController,
    isExpanded: MutableState<Boolean>,
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
                    contentDescription = null
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
                    contentDescription = null
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
                    contentDescription = null
                )
            }
        )
    }

}