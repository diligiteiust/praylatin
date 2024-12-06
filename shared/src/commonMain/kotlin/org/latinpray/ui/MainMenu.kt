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