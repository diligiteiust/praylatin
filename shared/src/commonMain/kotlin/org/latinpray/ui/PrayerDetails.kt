package org.latinpray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import org.latinpray.data.Config
import org.latinpray.data.Prayer

@Composable
fun PrayerDetails(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>
) {
    val scrollState = rememberScrollState()
    val lang1 = prayer.langs[config.prayerLang]
    var lang2 = prayer.langs[config.secondLang]
    if (config.preferTranslation && prayer.langs[config.secondLang + "_tr"] != null) {
        lang2 = prayer.langs[config.secondLang + "_tr"]
    }

    if (lang1 != null) {
        var content = ""
        lang1.lines.forEachIndexed { i, it ->
            content += (it ?: "") + "\n"
            if (lang2?.lines != null
                && lang2.lines.size > i
                && lang2.lines[i]?.isNotEmpty() == true
            ) {
                content += "##### *" + lang2.lines[i] + "*" + "\n\n"
            }
        }

        Markdown(
            content = content,
            colors = markdownColor(
                text = MaterialTheme.colorScheme.onBackground,
            ),
            typography = markdownTypography(
                text = MaterialTheme.typography.bodySmall,
                paragraph = MaterialTheme.typography.bodyMedium,
                quote = MaterialTheme.typography.bodySmall,
                h5 = MaterialTheme.typography.bodySmall,
            ),
            modifier = Modifier.fillMaxSize().padding(4.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        )
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            state = listState
//        ) {
//            itemsIndexed(lang1.lines) { index, line ->
//                Column(
//                    modifier = Modifier.fillMaxWidth().padding(4.dp)
//                        .background(color = MaterialTheme.colorScheme.background)
//                ) {
//                    Text(
//                        text = line ?: "",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Justify,
//                        color = MaterialTheme.colorScheme.onBackground
//                    )
//                    if (lang2?.lines != null
//                        && lang2.lines.size > index
//                        && lang2.lines[index]?.isNotEmpty() == true
//                    ) {
//                        val translation = lang2.lines[index]
//                        Text(
//                            text = translation ?: "",
//                            style = MaterialTheme.typography.bodyMedium,
//                            textAlign = TextAlign.Justify,
//                            color = MaterialTheme.colorScheme.onBackground
//                        )
//                    }
//                }
//            }
//        }
    }

}