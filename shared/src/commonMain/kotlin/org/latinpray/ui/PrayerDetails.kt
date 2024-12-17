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
import androidx.compose.ui.text.font.FontWeight
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
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.getPlatform

val INDENT = "%"
val TRANSLATION = "!"
val EMBEDDED = "@"
val TRANSLATION_TRAIL = "_tr"
val TRANSLATION_INDENT = "\t\t"
val EMBEDDED_INDENT = "\t\t\t\t\t\t"

fun preparePrayer(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    indent: String = "",
    extras: Boolean = true,
    listMode : Boolean = false
): String {
    var list = listMode
    var lang2: BasicPrayer? = null
    var lang1 = prayer.langs[config.prayerLang]
    if (lang1 == null) {
        lang1 = prayer.langs[config.secondLang]
    } else {
        lang2 = prayer.langs[config.secondLang]
    }
    if (config.preferTranslation && prayer.langs[config.secondLang + TRANSLATION_TRAIL] != null) {
        lang2 = prayer.langs[config.secondLang + TRANSLATION_TRAIL]
    }

    var result = ""

    lang1?.lines?.forEachIndexed { i, it ->
        if (it != null && it.trim().startsWith(EMBEDDED)) {
            if (i == 0) {
                list = true
            }
            if (it.trim().substring(1).isEmpty()) {
                return@forEachIndexed
            }
            val subprayer = prayers.find { p -> p.name == it.trim().substring(1) }
            result += if (subprayer != null) {
                preparePrayer(subprayer, config, prayers, indent + (if (list)  "" else INDENT), false, list)
            } else {
                "$indent *$it* not found\n\n"
            }
            return@forEachIndexed
        }
        result += indent + "" + (it ?: "") + "\n\n"
        if (lang2?.lines != null
            && lang2.lines.size > i
            && lang2.lines[i]?.isNotEmpty() == true
        ) {
            result += TRANSLATION + indent + lang2.lines[i] + "\n\n"
            //if (indent.isEmpty()) result += "\n"
        }
    }

    if (extras) {
        if (lang1?.links != null && lang1.links!!.isNotEmpty()) {
            lang1.links!!.forEach { link ->
                if (link is org.latinpray.data.Link.Youtube) {
                    result += "^^^\n\n[Listen on YouTube](${link.url})\n\n"
                }
            }
        }
        if (lang2?.notes != null && lang2.notes!!.isNotEmpty()) {
            result += "^^^\n\n__Notes:__\n\n" + lang2.notes
        }
    }

    return result
}

val customParagraphComponent: MarkdownComponent = {
    // build a styled paragraph. (util function provided by the library)
    var verPadding = 0.dp
    val mainStyle = LocalMarkdownTypography.current.paragraph.toSpanStyle()
    var styledText = buildAnnotatedString {
        pushStyle(mainStyle)
        //println("Children size: ${it.node.children.size}")
        buildMarkdownAnnotatedString(it.content, it.node)
        pop()
    }

    if (styledText.text.equals("^^^")) {
        //verPadding = 4.dp
        styledText = buildAnnotatedString {
            withStyle(mainStyle) {
                append("   ")
            }
        }
    }

    if (styledText.text.startsWith(TRANSLATION+INDENT)) {
        val style = mainStyle.copy(
            fontSize = mainStyle.fontSize * 0.7f,
            fontWeight = FontWeight.Light,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append(EMBEDDED_INDENT+TRANSLATION_INDENT+ getPlatform().extraIndent + styledText.text.substring(2))
            }
        }
    }

    if (styledText.text.startsWith(INDENT)) {
        val style = mainStyle.copy(
            fontSize = mainStyle.fontSize * 0.8f,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append(EMBEDDED_INDENT + styledText.text.substring(1))
            }
        }
    }

    if (styledText.text.startsWith(TRANSLATION)) {
        val style = mainStyle.copy(
            fontSize =  mainStyle.fontSize * 0.8f,
            fontWeight = FontWeight.Light,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append(TRANSLATION_INDENT + styledText.text.substring(1))
            }
        }
    }

    Text(
        styledText,
        modifier = Modifier.padding(vertical = verPadding)
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