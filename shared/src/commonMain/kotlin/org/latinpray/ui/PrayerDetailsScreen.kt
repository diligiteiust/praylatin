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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.shared.Res
import org.latinpray.shared.bookmark_add
import org.latinpray.shared.bookmark_check
import org.latinpray.shared.calendar_add_on
import org.latinpray.shared.calendar_month

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerDetailsScreen(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    goBack: () -> Unit,
) {
    val (fraction) = remember { mutableStateOf(0.25f) }
    var firstLang by remember { mutableStateOf(true) }
    var daily by remember { mutableStateOf(false) }
    var favorite by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(bottom = 20.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).alpha(fraction),
            contentAlignment = Alignment.TopCenter
        ) {
            Row() {
                IconButton(
                    onClick = {
                        goBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = "Go back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                IconToggleButton(
                    checked = firstLang,
                    onCheckedChange = {
                        firstLang = it
                    }
                ) {
                    if (firstLang) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                            contentDescription = "1st lang On",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Notes,
                            contentDescription = "1st lang Off",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconToggleButton(
                    checked = daily,
                    onCheckedChange = {
                        daily = it
                    }
                ) {
                    if (daily) {
                        Icon(
                            painter = painterResource(Res.drawable.calendar_month),
                            contentDescription = "In daily prayers",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.calendar_add_on),
                            contentDescription = "Add to daily prayers",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconToggleButton(
                    checked = favorite,
                    onCheckedChange = {
                        favorite = it
                    }
                ) {
                    if (favorite) {
                        Icon(
                            painter = painterResource(Res.drawable.bookmark_check),
                            contentDescription = "In favorites",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.bookmark_add),
                            contentDescription = "Add to favorites",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
                //.windowInsetsPadding(WindowInsets.systemBars),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = prayer.langs[config.prayerLang]?.title ?:
                    (prayer.langs[config.secondLang]?.title ?: "No title"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        PrayerDetails(
            firstLang = firstLang,
            prayer = prayer,
            config = config,
            prayers = prayers,
        )
    }
}