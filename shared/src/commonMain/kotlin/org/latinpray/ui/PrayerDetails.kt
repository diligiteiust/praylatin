package org.latinpray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.latinpray.data.Config
import org.latinpray.data.Prayer

fun preparePrayer(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    indent: String = ""
): String {

    val lang1 = prayer.langs[config.prayerLang]
    var lang2 = prayer.langs[config.secondLang]
    if (config.preferTranslation && prayer.langs[config.secondLang + "_tr"] != null) {
        lang2 = prayer.langs[config.secondLang + "_tr"]
    }

    var result = ""

    lang1?.lines?.forEachIndexed { i, it ->
        if (it != null && it.trim().startsWith("@")) {
            val subprayer = prayers.find { p -> p.name == it.trim().substring(1) }
            result += if (subprayer != null) {
                preparePrayer(subprayer, config, prayers, indent + ">")
            } else {
                "$indent *$it* not found\n\n"
            }
            return@forEachIndexed
        }
        result += indent + "" + (it ?: "") + "\n"
        if (lang2?.lines != null
            && lang2.lines.size > i
            && lang2.lines[i]?.isNotEmpty() == true
        ) {
            result += indent + " *" + lang2.lines[i] + "*" + "\n"
            if (indent.isEmpty()) result += "\n"
        }
    }

    return result
}

//val customItalicComponent: MarkdownComponent = {
//    Markdown
//}

@Composable
fun PrayerDetails(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
) {
    val scrollState = rememberScrollState()

    val content = preparePrayer(prayer, config, prayers)

    Markdown(
        content = content,
        colors = markdownColor(
            text = MaterialTheme.colorScheme.onBackground,
        ),
        typography = markdownTypography(
            text = MaterialTheme.typography.bodySmall,
            paragraph = MaterialTheme.typography.bodyMedium,
            quote = MaterialTheme.typography.bodySmall
        ),

        modifier = Modifier.fillMaxSize().padding(4.dp)
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    )

//    if (prayer.langs[config.secondLang]?.notes != null) {
//        Markdown(
//            content = prayer.langs[config.secondLang]?.notes ?: "",
//            colors = markdownColor(text = Color.Red),
//            typography = markdownTypography(text = MaterialTheme.typography.bodySmall),
//        )
//    }

}