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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import org.latinpray.shared.on_option
import org.latinpray.shared.settings_grouping
import org.latinpray.shared.settings_prayer_lang
import org.latinpray.shared.settings_prefer_translation
import org.latinpray.shared.settings_second_lang
import org.latinpray.shared.settings_ui_lang

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SettingsScreen(
    title: String,
    goBack: () -> Unit,
    uiLandChange: (config: Config) -> Unit,
    config: Config,
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val (fraction, setFraction) = remember { mutableStateOf(0.25f) }
    val scope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        if (sharedTransitionScope.isTransitionActive.not()) {
            setFraction(0f)
        }
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
                        contentDescription = null,
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
                modifier = Modifier.fillMaxSize().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LangSelection(
                    title = stringResource(Res.string.settings_ui_lang),
                    langs = config.allUIlangs,
                    selectedItem = config.uiLang,
                    onItemSelected = { lang ->
                        if (lang != config.uiLang) {
                            scope.launch {
                                config.saveUILang(lang)
                                uiLandChange(config)
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
                        }
                    }
                )
                val secondLang = config.allPrayerLangs
                secondLang["off"] = stringResource(Res.string.off_option)
                LangSelection(
                    title = stringResource(Res.string.settings_second_lang),
                    langs = secondLang,
                    selectedItem = config.secondLang,
                    onItemSelected = { lang ->
                        scope.launch {
                            config.saveSecondLang(lang)
                        }

                    }
                )
                LangSelection(
                    title = stringResource(Res.string.settings_prefer_translation),
                    langs = mapOf("on" to stringResource(Res.string.on_option), "off" to stringResource(Res.string.off_option)),
                    selectedItem = if (config.preferTranslation) "on" else "off",
                    onItemSelected = { lang ->
                        scope.launch {
                            config.savePreferTranslation(lang == "on")
                        }
                    }
                )
                LangSelection(
                    title = stringResource(Res.string.settings_grouping),
                    langs = mapOf("on" to stringResource(Res.string.on_option), "off" to stringResource(Res.string.off_option)),
                    selectedItem = if (config.grouping) "on" else "off",
                    onItemSelected = { lang ->
                        scope.launch {
                            config.saveGrouping(lang == "on")
                        }
                    }
                )
            }
        }
    }
}

