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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.utils.buildMarkdownAnnotatedString
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.getPlatform

const val INDENT = "%"
const val TRANSLATION = "!"
const val EMBEDDED = "@"
//const val EMBEDDED_TTILE = "@@"
const val TRANSLATION_TRAIL = "_tr"
const val TRANSLATION_INDENT = "\t\t"
const val EMBEDDED_INDENT = "\t\t\t\t\t\t"
const val EMPTY_LINE = "^^^"
const val QUOTE = ">"
val MULTI_REGEX = Regex("[0-9]+x")

fun preparePrayer(
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    indent: String = "",
    extras: Boolean = true,
    listMode : Boolean = false,
    title: Boolean = false
): String {
    var list = listMode
    var lang2: BasicPrayer? = null
    var lang1 = prayer.langs[config.prayerLang]
    // If content for the primary language is not found,
    // we use content for secondary language as primary
    if (lang1 == null) {
        lang1 = prayer.langs[config.secondLang]
    } else {
        lang2 = prayer.langs[config.secondLang]
    }
    // If prefered translation is enabled, we try to find content with translation
    // instead of the standard content
    if (config.preferTranslation && prayer.langs[config.secondLang + TRANSLATION_TRAIL] != null) {
        lang2 = prayer.langs[config.secondLang + TRANSLATION_TRAIL]
    }

    var result = ""
    var prayerStart = true

    lang1?.lines?.forEachIndexed { i, it ->
        if (it != null && it.trim().startsWith(EMBEDDED)) {
            // If the first line starts from embedded content
            // `list` mode is turned on. No indentation for embedded content.
            if (i == 0) {
                list = true
            }
            var file = it.trim().substring(1)
            // If the line contains only embedded content mark, skip the line
            // It is used to enable `list` mode for lists which do not start
            // with embedded content
            if (file.isEmpty()) {
                return@forEachIndexed
            }
            var t = false
            // Double embedded mark means title = true
            if (file.startsWith(EMBEDDED)) {
                t = true
                file = file.substring(1)
            }
            val subprayer = prayers.find { p -> p.name == file }
            result += if (subprayer != null) {
                preparePrayer(subprayer, config, prayers,
                    indent + (if (list)  "" else INDENT), false, list, t)
            } else {
                "$indent *$it* not found\n\n"
            }
            return@forEachIndexed
        }
        // Display prayer title at the very beginning
        if (prayerStart && title) {
            result += "## " + lang1.title + "\n\n^^^\n\n"
            prayerStart = false
        }
        result += indent + "" + (it ?: "") + "\n\n"
        if (lang2?.lines != null
            && lang2.lines.size > i
            && lang2.lines[i]?.isNotEmpty() == true
            && lang2.lines[i]?.trim()?.startsWith(EMBEDDED) == false
            && lang2.lines[i]?.trim() != EMPTY_LINE
        ) {
            var line = lang2.lines[i]
            if (line?.startsWith(QUOTE) == true) {
                line = line.substring(1).trim()
            }
            val nline = MULTI_REGEX.replace(line ?: "", "")
            if (nline.trim().isEmpty()) return@forEachIndexed
            result += TRANSLATION + indent + line + "\n\n"
        }
    }

    if (extras) {
        if (lang1?.links != null && lang1.links!!.isNotEmpty()) {
            lang1.links!!.forEach { link ->
                if (link is org.latinpray.data.Link.Youtube) {
                    val url_title = link.title ?: "Listen on YouTube"
                    val yt_link = "[$url_title](${link.url})"
                    result += "^^^\n\n$yt_link\n\n"
                }
            }
        }
        if (lang2?.notes != null && lang2.notes!!.isNotEmpty()) {
            result += "^^^\n\n__Notes:__\n\n" + lang2.notes
        } else if (lang2 == null && lang1?.notes != null && lang1.notes!!.isNotEmpty()) {
            result += "^^^\n\n__Notes:__\n\n" + lang1.notes
        }
    }

    return result
}

val customParagraphComponent: MarkdownComponent = {
    // build a styled paragraph. (util function provided by the library)
    val mainStyle = LocalMarkdownTypography.current.paragraph.toSpanStyle()
    var styledText = buildAnnotatedString {
        pushStyle(mainStyle)
        //println("Children size: ${it.node.children.size}")
        buildMarkdownAnnotatedString(it.content, it.node)
        pop()
    }

    if (styledText.text.trim() == EMPTY_LINE) {
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

    MarkdownText(
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
    val content: String = remember(prayer, config) { preparePrayer(prayer, config, prayers) }
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
            quote = MaterialTheme.typography.bodySmall,
            h2 = MaterialTheme.typography.titleMedium,
            h3 = MaterialTheme.typography.titleSmall,
            link = MaterialTheme.typography.labelMedium
        ),
        modifier = Modifier.fillMaxSize().padding(4.dp)
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        components = markdownComponents(
            paragraph = customParagraphComponent,
        ),
    )

//    if (prayer.langs[config.secondLang]?.notes != null) {
//        Markdown(
//            content = prayer.langs[config.secondLang]?.notes ?: "",
//            colors = markdownColor(text = Color.Red),
//            typography = markdownTypography(text = MaterialTheme.typography.bodySmall),
//        )
//    }

}