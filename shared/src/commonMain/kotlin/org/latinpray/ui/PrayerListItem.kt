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
import org.latinpray.data.Config
import org.latinpray.data.Prayer

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
    Card(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .clickable { onClick(prayer) },
        elevation = CardDefaults.cardElevation( defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                modifier = Modifier.padding(vertical = pad),
                text = title ?: "No title", style = MaterialTheme.typography.headlineMedium
            )
            //println("Rendering prayer ${prayer.name}")
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}
