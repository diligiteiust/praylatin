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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.theme.Gray200
import org.latinpray.theme.Gray600
import org.latinpray.theme.Gray700
import org.latinpray.theme.Gray900
import org.latinpray.theme.darken

@Composable
fun PrayerListItem(
    prayer: Prayer,
    onClick: (prayer: Prayer) -> Unit,
    config: Config
) {
    var subtitle: String? = null
    var title = prayer.langs[config.prayerLang]?.title
    var pad = 2.dp
    if (title == null) {
        title = prayer.langs[config.secondLang]?.title
    } else {
        subtitle = prayer.langs[config.secondLang]?.title
    }
    if (subtitle == null || subtitle.isEmpty()) {
        pad = 6.dp
    }
    val darker: Boolean = prayer.lastPrayed != null && prayer.lastPrayed == Clock.System.todayIn(TimeZone.currentSystemDefault())
    val backgroundColor =  if (darker) { MaterialTheme.colorScheme.surfaceVariant.darken() } else { MaterialTheme.colorScheme.surfaceVariant }
    val textColor = if (darker) { Gray600 } else { MaterialTheme.colorScheme.onBackground }
    Card(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .clickable { onClick(prayer) },
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
