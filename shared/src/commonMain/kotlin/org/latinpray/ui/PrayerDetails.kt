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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.markdownPadding
import kotlinx.coroutines.launch
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Config
import org.latinpray.data.Link
import org.latinpray.data.Prayer
import org.latinpray.data.PrayerIntention
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
var MAX_LEN = 15

@Composable
fun DpToPx(dp: Dp): Float {
    val density = LocalDensity.current.density
    return dp.value * density
}

fun getPrayerTitle(firstLang: Boolean, prayer: Prayer, config: Config, maxLen: Int = 100): String {
    var pr = prayer.langs[config.prayerLang]
    if (pr == null || !firstLang) {
        pr = prayer.langs[config.secondLang]
    }
    var result = pr?.title ?: ""
    if (result.length > maxLen) {
        result = result.substring(0, maxLen) + "..."
    }
    return result
}

fun preparePrayer(
    firstLang: Boolean,
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
    if (lang1 == null || !firstLang) {
        lang1 = prayer.langs[config.secondLang]
    } else {
        lang2 = prayer.langs[config.secondLang]
    }
    // If prefered translation is enabled, we try to find content with translation
    // instead of the standard content
    if (!firstLang && config.preferTranslation && prayer.langs[config.secondLang + TRANSLATION_TRAIL] != null) {
        lang2 = prayer.langs[config.secondLang + TRANSLATION_TRAIL]
    }

    var result = ""
    var prayerStart = true

    lang1?.lines?.forEachIndexed { i, it ->
        // Display prayer title at the very beginning
        if (prayerStart && title) {
            result += "## " + lang1.title + "\n\n"
            if (lang2?.title != null) {
                result += "### " + lang2.title + "\n\n"
            }
            result += "^^^\n\n"
            prayerStart = false
        }

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
                preparePrayer(firstLang, subprayer, config, prayers,
                    indent + (if (list)  "" else INDENT), false, list, t)
            } else {
                "$indent *$it* not found\n\n"
            }
            return@forEachIndexed
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
        if (lang1?.links != null && lang1.links.isNotEmpty()) {
            lang1.links.forEach { link ->
                if (link is Link.Youtube) {
                    val url_title = link.title ?: "Listen on YouTube"
                    val yt_link = "[$url_title](${link.url})"
                    result += "^^^\n\n$yt_link\n\n"
                }
            }
        }
        if (lang2?.notes != null && lang2.notes.isNotEmpty()) {
            result += "^^^\n\n__Notes:__\n\n" + lang2.notes
        } else if (lang2 == null && lang1?.notes != null && lang1.notes.isNotEmpty()) {
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
        buildMarkdownAnnotatedString(
            it.content, it.node,
            annotatorSettings = annotatorSettings()
        )
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
fun MyMarkdownSuccess(
    state: State.Success,
    components: MarkdownComponents,
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
) {
    LazyColumn(modifier = modifier, state = scrollState) {
        items(items = state.node.children) { node ->
            MarkdownElement(node, components, state.content, skipLinkDefinition = state.linksLookedUp)
        }
    }

}

@Composable
fun PrayerDetails(
    firstLang: Boolean,
    prayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    endReachedCallback: (pr: Prayer) -> Unit,
    prayerChangedCallback: (pr: Prayer) -> Unit,
    intention: PrayerIntention? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelLarge
    val text = "A"
    val textLayoutResult = textMeasurer.measure(text, textStyle)
    val textWidth = textLayoutResult.size.width
    val scrollState = rememberLazyListState()
    var endProcessed by remember { mutableStateOf(false) }
    val endReached by remember {
        derivedStateOf {
            !endProcessed && scrollState.layoutInfo.totalItemsCount > 0 && !scrollState.canScrollForward
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var changed by remember { mutableStateOf(false) }
    var currentPrayer by remember { mutableStateOf( prayer) }

    if (endReached) {
        endProcessed = true
        LaunchedEffect(scrollState) {
            endReachedCallback(currentPrayer)
        }
    }

    key(changed) {
        val content = preparePrayer(firstLang, currentPrayer, config, prayers)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
//            Box(
//                modifier = Modifier.fillMaxWidth()
//            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getPrayerTitle(firstLang, currentPrayer, config),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (intention != null) {
                            var nums = ""
                            if (config.showNumbers) {
                                nums = intention.totalNum.toString() + " / "
                            }
                            if (intention.days > 1) {
                                nums = nums + intention.inrowNum
                            }
                            if (nums.isNotEmpty()) nums = " (" + nums + ")"
                            Text(
                                text = intention.intention + nums,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
//            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val margins = maxWidth * 0.04f
                MAX_LEN = ((DpToPx(maxWidth) / textWidth) / 2.3).toInt()

                Markdown(
                    content = content,
                    padding = markdownPadding(
                        block = 1.dp,
                    ),
                    colors = markdownColor(
                        //linkText = MaterialTheme.colorScheme.onTertiary,
                    ),
                    typography = markdownTypography(
                        text = MaterialTheme.typography.bodySmall,
                        paragraph = MaterialTheme.typography.bodyMedium,
                        quote = MaterialTheme.typography.bodySmall,
                        h2 = MaterialTheme.typography.titleMedium,
                        h3 = MaterialTheme.typography.titleSmall,
                        link = MaterialTheme.typography.labelMedium
                    ),
                    modifier = Modifier.fillMaxSize().padding(horizontal = margins, vertical = 4.dp)
                        .background(color = MaterialTheme.colorScheme.background),
                        //.verticalScroll(scrollState),
                    components = markdownComponents(
                        paragraph = customParagraphComponent,
                    ),
                    success = @Composable  { state, components, modifier ->
                        MyMarkdownSuccess(state, components, modifier, scrollState)
                    },
                )

            }
            if (currentPrayer.prevPrayer != null || currentPrayer.nextPrayer != null) {
                val navColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                Row() {
                    if (currentPrayer.prevPrayer != null) {
                        TextButton(
                            onClick = {
                                currentPrayer = currentPrayer.prevPrayer!!
                                changed = !changed
                                coroutineScope.launch {
                                    scrollState.scrollToItem(0)
                                    endProcessed = false
                                }
                                prayerChangedCallback(currentPrayer)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                                contentDescription = "Previous prayer",
                                //tint = MaterialTheme.colorScheme.onBackground,
                                tint = navColor,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = getPrayerTitle(
                                    firstLang,
                                    currentPrayer.prevPrayer!!,
                                    config,
                                    MAX_LEN
                                ),
                                color = navColor
                            )
                        }
                    }
                    if (currentPrayer.nextPrayer != null) {
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                currentPrayer = currentPrayer.nextPrayer!!
                                changed = !changed
                                coroutineScope.launch {
                                    scrollState.scrollToItem(0)
                                    endProcessed = false
                                }
                                prayerChangedCallback(currentPrayer)
                            }
                        ) {
                            Text(
                                text = getPrayerTitle(
                                    firstLang,
                                    currentPrayer.nextPrayer!!,
                                    config,
                                    MAX_LEN
                                ),
                                color =  navColor
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                                contentDescription = "Next prayer",
                                tint = navColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}