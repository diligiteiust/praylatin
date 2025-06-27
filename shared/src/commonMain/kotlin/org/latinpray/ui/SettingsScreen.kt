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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.shared.Res
import org.latinpray.shared.off_option
import org.latinpray.shared.settings_grouping
import org.latinpray.shared.settings_prayer_lang
import org.latinpray.shared.settings_prefer_translation
import org.latinpray.shared.settings_second_lang
import org.latinpray.shared.settings_substitutions
import org.latinpray.shared.settings_ui_lang
import org.latinpray.shared.shared_prayer_lists
import org.latinpray.shared.show_numbers
import org.latinpray.shared.reset_shared_data
import org.latinpray.sharedPrefsSupported

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SettingsScreen(
    title: String,
    goBack: () -> Unit,
    uiLangChange: (config: Config) -> Unit,
    reloadPrayers: (config: Config) -> Unit,
    config: Config,
) {
    val (fraction) = remember { mutableStateOf(0.25f) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var sharedPrefsChecked by remember { mutableStateOf(config.sharedPrefs) }
    var groupingChecked by remember { mutableStateOf(config.grouping) }
    var preferTranslationChecked by remember { mutableStateOf(config.preferTranslation) }
    var showNumbersChecked by remember { mutableStateOf(config.showNumbers) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(50.dp)
                .align(Alignment.CenterStart)
                .padding(10.dp)
                .alpha(fraction)
                //.alpha(alpha = if (fraction <= 0) 1f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(50)
                ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                    goBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(30.dp)
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        Column (
            modifier = Modifier.fillMaxSize().weight(1f).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            LangSelection(
                title = stringResource(Res.string.settings_ui_lang),
                langs = config.allUIlangs,
                selectedItem = config.uiLang,
                onItemSelected = { lang ->
                    if (lang != config.uiLang) {
                        scope.launch {
                            config.saveUILang(lang)
                            uiLangChange(config)
                        }
                    }
                }
            )
            LangSelection(
                title = stringResource(Res.string.settings_prayer_lang),
                langs = config.allPrayerLangs,
                selectedItem = config.prayerLang,
                onItemSelected = { lang ->
                    scope.launch {
                        config.savePrayerLang(lang)
                        reloadPrayers(config)
                    }
                }
            )
            val secondLang = config.allPrayerLangs.toMutableMap()
            secondLang["off"] = stringResource(Res.string.off_option)
            LangSelection(
                title = stringResource(Res.string.settings_second_lang),
                langs = secondLang,
                selectedItem = config.secondLang,
                onItemSelected = { lang ->
                    scope.launch {
                        config.saveSecondLang(lang)
                        reloadPrayers(config)
                    }
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( horizontal = 32.dp)
            )  {
                Text(
                    text = stringResource(Res.string.settings_prefer_translation),
                )
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = preferTranslationChecked,
                    onCheckedChange = {
                        scope.launch {
                            config.savePreferTranslation(it)
                            preferTranslationChecked = it
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( horizontal = 32.dp)
            )  {
                Text(
                    text = stringResource(Res.string.settings_grouping),
                )
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = groupingChecked,
                    onCheckedChange = {
                        scope.launch {
                            config.saveGrouping(it)
                            groupingChecked = it
                        }
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( horizontal = 32.dp)
            )  {
                Text(
                    text = stringResource(Res.string.show_numbers),
                )
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = showNumbersChecked,
                    onCheckedChange = {
                        scope.launch {
                            config.saveShowNumbers(it)
                            showNumbersChecked = it
                        }
                    }
                )
            }
            if (sharedPrefsSupported()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding( horizontal = 32.dp)
                )  {
                    Text(
                        text = stringResource(Res.string.shared_prayer_lists),
                    )
                    Spacer(Modifier.weight(1f))
                    Checkbox(
                        checked = sharedPrefsChecked,
                        onCheckedChange = {
                            scope.launch {
                                config.saveSharedPrefs(it)
                                sharedPrefsChecked = it
                                reloadPrayers(config)
                            }
                        }
                    )
                }
            }
            LangSelection(
                title = stringResource(Res.string.settings_substitutions),
                langs = config.substitutions,
                selectedItem = config.substitutions.keys.firstOrNull() ?: "",
                onItemSelected = { lang ->
                    scope.launch {
                        config.saveSubstitutions()
                        reloadPrayers(config)
                    }
                },
                true
            )
            if (sharedPrefsSupported()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.reset_shared_data),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            sharedPrefsChecked = false
                            scope.launch {
                                config.saveSharedPrefs(false)
                                config.resetSharedPrefs()
                                reloadPrayers(config)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.reset_shared_data),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

