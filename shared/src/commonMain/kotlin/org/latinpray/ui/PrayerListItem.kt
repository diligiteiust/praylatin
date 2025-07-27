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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.shared.Res
import org.latinpray.shared.daily_prayers
import org.latinpray.shared.today_and_now
import org.latinpray.theme.Gray600
import org.latinpray.theme.darken

data class PrayerItem(val prayer: Prayer, var darker: Boolean, val tag: String?)

@Composable
fun PrayerListItem(
    prayerItem: PrayerItem,
    onClick: (prayer: Prayer) -> Unit,
    config: Config,
) {
    val normalSurface =  MaterialTheme.colorScheme.surfaceVariant
    val darkerSurface = normalSurface.darken()
    val onBackground = MaterialTheme.colorScheme.onBackground
    val dailyPrayersStr = stringResource(Res.string.daily_prayers)
    val todayAndNowStr = stringResource(Res.string.today_and_now)

    prayerItem.darker = prayerItem.prayer.prayedToday()
            && (prayerItem.tag == dailyPrayersStr || prayerItem.tag == todayAndNowStr)

    var backgroundColor by remember { mutableStateOf( if (prayerItem.darker) { darkerSurface } else { normalSurface} ) }
    var textColor by remember { mutableStateOf( if (prayerItem.darker) { Gray600 } else { onBackground } ) }
    var currentHour by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour ) }

    val updateColors: () -> Unit = {
        prayerItem.darker = prayerItem.prayer.prayedToday()
        backgroundColor = if (prayerItem.darker) { darkerSurface } else { normalSurface }
        textColor = if (prayerItem.darker) { Gray600 } else { onBackground }
    }

    if (prayerItem.tag == dailyPrayersStr || prayerItem.tag == todayAndNowStr) {
        prayerItem.prayer.addExternalChangeListener {
            updateColors()
//            prayerItem.darker = prayerItem.prayer.prayedToday()
//            backgroundColor = if (prayerItem.darker) { darkerSurface } else { normalSurface }
//            textColor = if (prayerItem.darker) { Gray600 } else { onBackground }
        }

        val scope = rememberCoroutineScope()
        scope.launch {
            while (true) {
                delay(untilNextFullHour(prayerItem.prayer.name))
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                //println("Checking time: ${now.hour}, previous: ${currentHour}")
                if (now.hour != currentHour) {
                    currentHour = now.hour
                    updateColors()
                    //println("New time: ${currentHour}")
                }
            }
        }

        OnResume {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            //println("OnResume, Checking time: ${now.hour}, previous: ${currentHour}")
            if (now.hour != currentHour) {
                currentHour = now.hour
                updateColors()
                //println("OnResume, New time: ${currentHour}")
            }
        }
    }

    var subtitle: String? = null
    var title = prayerItem.prayer.langs[config.prayerLang]?.title
    var pad = 2.dp
    if (title == null) {
        title = prayerItem.prayer.langs[config.secondLang]?.title
    } else {
        subtitle = prayerItem.prayer.langs[config.secondLang]?.title
    }
    if (subtitle == null || subtitle.isEmpty()) {
        pad = 6.dp
    }
    Card(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .clickable { onClick(prayerItem.prayer) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = pad, bottom = pad),
                text = title ?: "No title", style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )
            //println("Rendering prayer ${prayer.name}")
            if (subtitle != null) {
                Text(
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = pad, bottom = pad),
                    text = subtitle, style = MaterialTheme.typography.headlineSmall,
                    color = textColor
                )
            }
        }
    }
}
