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
import androidx.compose.material3.CheckboxDefaults
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
import org.latinpray.shared.bible_readingplan_setting
import org.latinpray.shared.first_bible_setting
import org.latinpray.shared.off_option
import org.latinpray.shared.second_bible_setting

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BibleSettings(
    title: String,
    goBack: () -> Unit,
    reloadPrayers: (config: Config) -> Unit,
    config: Config,
) {
    val (fraction) = remember { mutableStateOf(0.25f) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var biblePlanChecked by remember { mutableStateOf(config.biblePlan) }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( horizontal = 32.dp)
            )  {
                Text(
                    text = stringResource(Res.string.bible_readingplan_setting),
                )
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = biblePlanChecked,
                    onCheckedChange = {
                        scope.launch {
                            config.saveBiblePlan(it)
                            biblePlanChecked = it
                            reloadPrayers(config)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
            //val allBibles = biblesList()
            val allKeys = mutableMapOf<String, String>()
            config.allBibles.keys.forEach {
                allKeys[it] = it
            }
            LangSelection(
                title = stringResource(Res.string.first_bible_setting),
                langs = allKeys,
                selectedItem = config.firstBible ?: "English - Douay-Rheims Bible",
                onItemSelected = { bible ->
                    scope.launch {
                        //println("Selected bible: $lang")
                        config.saveFirstBible(bible)
                        //println("Selected bible: ${config.firstBible?.getName()}")
                        reloadPrayers(config)
                    }
                },
                width = 280.dp
            )
            val secondBibleKeys = allKeys.toMutableMap()
            secondBibleKeys["off"] = stringResource(Res.string.off_option)
            LangSelection(
                title = stringResource(Res.string.second_bible_setting),
                langs = secondBibleKeys,
                selectedItem = config.secondBible ?: "English - Douay-Rheims Bible",
                onItemSelected = { bible ->
                    scope.launch {
                        config.saveSecondBible(bible)
                        reloadPrayers(config)
                    }
                },
                width = 280.dp
            )
        }
    }
}

