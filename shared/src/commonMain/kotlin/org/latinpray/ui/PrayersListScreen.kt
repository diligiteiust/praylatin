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
import androidx.compose.material3.Icon
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
import com.ucasoft.kcron.kotlinx.datetime.plusHours
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.ReadingPlan
import org.latinpray.util.truncateToHour
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun untilNextFullHour(name: String = ""): Duration {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val nextFullHour = now.plusHours(1).truncateToHour()
    val result = nextFullHour.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()
    //println("$name - It is now: $now and the next full hour is: $nextFullHour until next full hour: $result")
    return result
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalTime::class)
@Composable
fun PrayersListScreen(
    title: String,
    prayers: List<Prayer>,
    config: Config,
    onClick: (content: ContentItem) -> Unit,
    navController: NavController,
    groupedPrayers: List<Any> = emptyList(),
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
    fontChange: (scale: Float) -> Unit,
    readingPlan: ReadingPlan? = null
) {
    val (fraction) = remember { mutableStateOf(0.50f) }
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    //var bible: Content? by remember { mutableStateOf(null) }

    //val scope = rememberCoroutineScope()

//    if (config.biblePlan) {
//        scope.launch {
//            bible = readingPlan?.bibleForToday(config)
//        }
//    }

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

                    is ContentItem -> {
                        PrayerListItem(
                            contentItem = item,
                            onClick = onClick,
                            config = config,
                        )
                    }

                    is Prayer -> {
                        PrayerListItem(
                            contentItem = ContentItem(item, false, null),
                            onClick = onClick,
                            config = config,
                        )
                        //println("Displaying prayer ${item.name}")
                    }

                    else -> println("Unknown item $item")
                }
            }
        }
    }
}

