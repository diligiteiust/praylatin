package org.latinpray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.utils.buildMarkdownAnnotatedString
import org.latinpray.data.Config
import org.latinpray.data.Prayer

val INDENT = "%"

fun preparePrayer(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    indent: String = "",
    extras: Boolean = true
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
                preparePrayer(subprayer, config, prayers, indent + INDENT, false)
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
            result += "\n" + "!" + indent + lang2.lines[i] + "\n\n"
            //if (indent.isEmpty()) result += "\n"
        }
    }

    if (extras) {
        if (lang2?.notes != null && lang2.notes!!.isNotEmpty()) {
            result += "\n\n__Notes:__\n\n" + lang2.notes
        }
        if (lang1?.links != null && lang1.links.isNotEmpty()) {
            lang1.links.forEach { link ->
                if (link is org.latinpray.data.Link.Youtube) {
                    result += "\n\n[Listen on YouTube](${link.url})\n\n"
                }
            }
        }

    }

    return result
}

val customParagraphComponent: MarkdownComponent = {
    // build a styled paragraph. (util function provided by the library)
    var style = LocalMarkdownTypography.current.paragraph.toSpanStyle()
    var styledText = buildAnnotatedString {
        pushStyle(style)
        //println("Children size: ${it.node.children.size}")
        buildMarkdownAnnotatedString(it.content, it.node)
        pop()
    }
    //println("Paragraph after: ${styledText.text}")
    if (styledText.text.startsWith("!")) {
        style = style.copy(
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
            baselineShift = BaselineShift.Superscript,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append(styledText.text.substring(1))
            }
        }
    }
    if (styledText.text.startsWith(INDENT)) {
        style = style.copy(
            fontSize = style.fontSize * 0.8f,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append("\t\t\t\t\t\t" + styledText.text.substring(1))
            }
        }
    }
    Text(
        styledText,
    )
}

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
        padding = markdownPadding(
            block = 1.dp,
            //list = 0.dp,
        ),
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
            .verticalScroll(scrollState),
        components = markdownComponents(
            paragraph = customParagraphComponent
        )
    )

//    if (prayer.langs[config.secondLang]?.notes != null) {
//        Markdown(
//            content = prayer.langs[config.secondLang]?.notes ?: "",
//            colors = markdownColor(text = Color.Red),
//            typography = markdownTypography(text = MaterialTheme.typography.bodySmall),
//        )
//    }

}