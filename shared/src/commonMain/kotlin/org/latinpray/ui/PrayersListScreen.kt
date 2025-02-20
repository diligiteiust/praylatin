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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.HIDE_TAG
import org.latinpray.data.Prayer
import org.latinpray.shared.Res
import org.latinpray.shared.daily_prayers
import org.latinpray.shared.favorite_prayers

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PrayersListScreen(
    title: String,
    prayers: List<Prayer>,
    config: Config,
    onClick: (prayer: Prayer) -> Unit,
    navController: NavController,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
    fontChange: (scale: Float) -> Unit
) {
    val (fraction) = remember { mutableStateOf(0.50f) }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    val dailyPrayersStr = stringResource(Res.string.daily_prayers)
    val favoritePrayersStr = stringResource(Res.string.favorite_prayers)
    val groupedPrayers: MutableList<Any> = remember(prayers, config) {
        val gp = mutableListOf<Any>()
        if (config.grouping) {
            val tags = mutableSetOf<String>()
            prayers.forEach { prayer ->
                if ((prayer.langs[config.prayerLang] != null && prayer.langs[config.prayerLang]?.tags != null)
                    || (prayer.langs[config.secondLang] != null && prayer.langs[config.secondLang]?.tags != null)
                ) {
                    if (prayer.langs[config.prayerLang]?.tags != null) {
                        tags.addAll(prayer.langs[config.prayerLang]?.tags!!)
                    } else if (prayer.langs[config.secondLang]?.tags != null) {
                        tags.addAll(prayer.langs[config.secondLang]?.tags!!)
                    }

                    //tags.addAll(prayer.langs[config.prayerLang]?.tags!!)
                }
            }
            tags.remove(HIDE_TAG)
            if (config.dailyPrayers.isNotEmpty()) {
                gp.add(dailyPrayersStr)
                config.dailyPrayers.forEach { prayer ->
                    prayers.firstOrNull { it.name == prayer }?.let { gp.add(it) }
                }
            }
            if (config.favorites.isNotEmpty()) {
                gp.add(favoritePrayersStr)
                config.favorites.forEach { prayer ->
                    prayers.firstOrNull { it.name == prayer }?.let { gp.add(it) }
                }
            }
            tags.sorted().forEach { tag ->
                gp.add(tag)
                prayers.forEach { prayer ->
                    //println("Checking prayer ${prayer.name} for tag $tag with tags ${prayer.langs[config.prayerLang]?.tags} or ${prayer.langs[config.secondLang]?.tags}")
                    if ((prayer.langs[config.prayerLang] != null)
                        && (prayer.langs[config.prayerLang]?.tags?.contains(tag) == true)
                        && (prayer.langs[config.prayerLang]?.tags?.contains(HIDE_TAG) == false)
                    ) {
                        gp.add(prayer)
                        //println("Added 1st prayer ${prayer.name} to group: $tag")
                    } else if ((prayer.langs[config.secondLang] != null)
                        && (prayer.langs[config.secondLang]?.tags?.contains(tag) == true)
                        && prayer.langs[config.secondLang]?.tags?.contains(HIDE_TAG) == false
                    ) {
                        gp.add(prayer)
                        //println("Added 2nd prayer ${prayer.name} to group: $tag")
                    }
                }
            }
            gp
        } else {
            prayers.toMutableList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(bottom = 30.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Box(
                modifier = Modifier.size(50.dp)
                    .align(Alignment.CenterStart)
                    .padding(10.dp)
                    .alpha(fraction)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(50)
                    ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                        expanded.value = true
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Main menu",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(30.dp)
                )
            }
            MainMenu(
                navController = navController,
                isExpanded = expanded,
                fontChange = fontChange
            )

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            items(items = groupedPrayers) { item ->
                when (item) {
                    is String -> {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            text = item,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    is Prayer -> {
                        PrayerListItem(
                            prayer = item,
                            onClick = onClick,
                            config = config
                        )
                        //println("Displaying prayer ${item.name}")
                    }

                    else -> println("Unknown item $item")
                }
            }
        }
    }
}

