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
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.shared.Res
import org.latinpray.shared.daily_prayers
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
    prayerItem.darker = prayerItem.prayer.prayedToday() && prayerItem.tag == stringResource(Res.string.daily_prayers)
    val backgroundColor = if (prayerItem.darker) { darkerSurface } else { normalSurface}
    val textColor = if (prayerItem.darker) { Gray600 } else { MaterialTheme.colorScheme.onBackground }
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
