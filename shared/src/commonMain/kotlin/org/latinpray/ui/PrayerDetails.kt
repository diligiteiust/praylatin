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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
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
import com.ucasoft.kcron.Cron
import com.ucasoft.kcron.core.common.WeekDays
import com.ucasoft.kcron.core.exceptions.WrongCronExpression
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.BibleBasicContent
import org.latinpray.data.BibleContent
import org.latinpray.data.BssicContent
import org.latinpray.data.Config
import org.latinpray.data.Content
import org.latinpray.data.Link
import org.latinpray.data.Prayer
import org.latinpray.data.PrayerIntention
import org.latinpray.data.loadBibleContent
import org.latinpray.getPlatform
import org.latinpray.shared.Res
import org.latinpray.shared.notes
import org.latinpray.util.DisplayLang
import kotlin.time.ExperimentalTime

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

@Composable
fun dpToPx(dp: Dp): Float {
    val density = LocalDensity.current.density
    return dp.value * density
}

fun getTitle(dispayLang: DisplayLang, content: Content, config: Config, maxLen: Int = 100): String {
    var pr = content.langs[config.prayerLang]
    if (pr == null || (dispayLang == DisplayLang.SECOND)) {
        pr = content.langs[config.secondLang]
    }
    var bibleContent = content.langs[config.firstBible] as? BibleBasicContent
    if (bibleContent == null) {
        bibleContent = content.langs[config.secondBible] as? BibleBasicContent
    }
    if (bibleContent != null) {
        pr = bibleContent
    }

    var result = pr?.title ?: ""
    if (result.length > maxLen) {
        result = result.substring(0, maxLen) + "..."
    }
    return result
}

fun preparePrayer(
    dispayLang: DisplayLang,
    content: Content,
    config: Config,
    prayers: MutableList<Prayer>,
    indent: String = "",
    extras: Boolean = true,
    listMode: Boolean = false,
    title: Boolean = false,
    notesRes: String = ""
): String {

    var lang1: String? = config.prayerLang
    var lang2: String? = config.secondLang

    if (content is BibleContent) {
        lang1 = config.firstBible
        lang2 = config.secondBible
    }

    var list = listMode
    var langCont2: BssicContent? = null
    var langCont1 = content.langs[lang1]
    // If content for the primary language is not found,
    // we use content for secondary language as primary
    if (langCont1 == null || (dispayLang == DisplayLang.SECOND)) {
        langCont1 = content.langs[lang2]
    } else {
        langCont2 = if (dispayLang != DisplayLang.FIRST) content.langs[lang2] else null
    }
    if ((dispayLang == DisplayLang.BOTH) && config.preferTranslation && content.langs[lang2 + TRANSLATION_TRAIL] != null) {
        langCont2 = content.langs[lang2 + TRANSLATION_TRAIL]
    }

    val result = StringBuilder()
    var prayerStart = true

    langCont1?.lines?.forEachIndexed { i, it ->
        if (prayerStart && title) {
            result.append("## ").append(langCont1.title).append("\n\n")
            if (langCont2?.title != null) {
                result.append("### ").append(langCont2.title).append("\n\n")
            }
            result.append(EMPTY_LINE).append("\n\n")
            prayerStart = false
        }

        if (it != null && it.trim().startsWith(EMBEDDED)) {
            if (i == 0) {
                list = true
            }
            var file = it.trim().substring(1)
            if (file.isEmpty()) {
                return@forEachIndexed
            }
            var t = false
            if (file.startsWith(EMBEDDED)) {
                t = true
                file = file.substring(1)
            }
            if (isPrayerForNow(file)) {
                file = getPrayerNameForNow(file)
            } else {
                return@forEachIndexed
            }
            var subprayer: Content? = prayers.find { p -> p.name == file }
            if (subprayer == null) {
                subprayer = loadBibleContent(file, config)
            }
            result.append(
                if (subprayer != null) {
                    preparePrayer(
                        dispayLang, subprayer, config, prayers,
                        indent + (if (list || t) "" else INDENT), false, list, t, notesRes
                    )
                } else {
                    "$indent *$it* not found\n\n"
                }
            )
            return@forEachIndexed
        }
        result.append(indent).append(it ?: "").append("\n\n")
        if (langCont2?.lines != null
            && langCont2.lines.size > i
            && langCont2.lines[i]?.isNotEmpty() == true
            && langCont2.lines[i]?.trim()?.startsWith(EMBEDDED) == false
            && langCont2.lines[i]?.trim() != EMPTY_LINE
        ) {
            var line = langCont2.lines[i]
            if (line?.startsWith(QUOTE) == true) {
                line = line.substring(1).trim()
            }
            val nline = MULTI_REGEX.replace(line ?: "", "")
            if (nline.trim().isEmpty()) return@forEachIndexed
            result.append(TRANSLATION).append(indent).append(line).append("\n\n")
        }
    }

    if (extras) {
        if (langCont1?.links != null && (langCont1.links as Collection<Any?>).isNotEmpty()) {
            (langCont1.links as Iterable<Any?>).forEach { link ->
                if (link is Link.Youtube) {
                    val url_title = link.title ?: "Listen on YouTube"
                    val yt_link = "[$url_title](${link.url})"
                    result.append(EMPTY_LINE).append("\n\n").append(yt_link).append("\n\n")
                }
            }
        }
        if (langCont2?.notes != null && langCont2.notes!!.isNotEmpty()) {
            result.append(EMPTY_LINE).append("\n\n__").append(notesRes).append(":__\n\n").append(langCont2.notes)
        } else if (langCont2 == null && langCont1?.notes != null && langCont1.notes!!.isNotEmpty()) {
            result.append(EMPTY_LINE).append("\n\n__").append(notesRes).append(":__\n\n").append(langCont1.notes)
        }
    }

    return result.toString()
}

@OptIn(ExperimentalTime::class)
fun isPrayerForNow(file: String): Boolean {
    //println("isPrayerForNow: $file")
    val parts = file.split(" ", limit = 2)
    //println("parts: ${parts}")
    if (parts.size == 2) {
        try {
            //print("cron exp: ${parts[1]}")
            val builder = Cron.parseAndBuild(parts[1]) {
                it.firstDayOfWeek = WeekDays.Sunday
            }
            return builder.nextRun?.date == kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
                    && builder.nextRun?.time?.hour == kotlin.time.Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).hour
        }    catch(wexp: WrongCronExpression) {
//            print("cron exp: ${parts[1]}")
//            println("Wrong cron expression: ${wexp.message}")
        } catch (e: Exception) {
            println("Error adding date ${parts[1]} to prayer ${file}, ${e.message}")
        }
    }
    return true
}

fun getPrayerNameForNow(file: String): String {
    val parts = file.split(" ", limit = 2)
    if (parts.size == 2) {
        try {
            val builder = Cron.parseAndBuild(parts[1]) {
                it.firstDayOfWeek = WeekDays.Sunday
            }
            // Cron parse successful...
            return parts[0]
        }    catch(wexp: WrongCronExpression) {
//            print("cron exp: ${parts[1]}")
//            println("Wrong cron expression: ${wexp.message}")
        } catch (e: Exception) {
            println("Error adding date ${parts[1]} to prayer ${file}, ${e.message}")
        }
    }
    return file
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

    if (styledText.text.startsWith(TRANSLATION + INDENT)) {
        val style = mainStyle.copy(
            fontSize = mainStyle.fontSize * 0.7f,
            fontWeight = FontWeight.Light,
        )
        styledText = buildAnnotatedString {
            withStyle(style) {
                append(
                    EMBEDDED_INDENT + TRANSLATION_INDENT + getPlatform().extraIndent + styledText.text.substring(
                        2
                    )
                )
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
            fontSize = mainStyle.fontSize * 0.8f,
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
        it.node,
    )
}

@Composable
fun MyMarkdownSuccess(
    state: State.Success,
    components: MarkdownComponents,
    modifier: Modifier = Modifier,
    savedScrollPosition: Int,
    saveScrollPosition: (LazyListState) -> Unit,
    endReachedCallback: () -> Unit,
    //scrollState: LazyListState,
) {
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = savedScrollPosition)
    var endProcessed by remember { mutableStateOf(false) }
    val endReached by remember {
        derivedStateOf {
            !endProcessed && scrollState.layoutInfo.totalItemsCount > 0 && !scrollState.canScrollForward
        }
    }
    if (endReached) {
        endProcessed = true
        LaunchedEffect(scrollState) {
            saveScrollPosition(scrollState)
            endReachedCallback()
        }
    }

    LazyColumn(modifier = modifier, state = scrollState) {
        items(items = state.node.children) { node ->
            MarkdownElement(
                node,
                components,
                state.content,
                //skipLinkDefinition = state.linksLookedUp
            )
        }
    }

}

@Composable
fun ContentDetails(
    displayLang: DisplayLang,
    contentItem: ContentItem,
    config: Config,
    prayers: MutableList<Prayer>,
    endReachedCallbackPD: () -> Unit,
    prayerChangedCallback: (contIt: ContentItem) -> Unit,
    intention: PrayerIntention? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelLarge
    val text = "A"
    val textLayoutResult = textMeasurer.measure(text, textStyle)
    val textWidth = textLayoutResult.size.width
    //val coroutineScope = rememberCoroutineScope()
    var changed by remember { mutableStateOf(false) }
    var intenTotalNum by remember { mutableStateOf(intention?.totalNum ?: 0) }
    var intenInrowNum by remember { mutableStateOf(intention?.inrowNum ?: 0) }
    intenTotalNum = intention?.totalNum ?: 0
    intenInrowNum = intention?.inrowNum ?: 0
    var savedScrollPosition by remember { mutableStateOf(0) }
    var titleMaxLen by remember { mutableStateOf(15) }
    //println("PrayerDetails, $intenTotalNum, $intenInrowNum")
    //var currentPrayer by remember { mutableStateOf( prayer) }
    val notesRes = stringResource(Res.string.notes)
    val contentColor = MaterialTheme.colorScheme.onBackground

    //println("PrayerDetails - before key(changed) prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
    key(changed, displayLang, config.firstBible, config.secondBible) {
        val content =
            preparePrayer(displayLang, contentItem.content, config, prayers, notesRes = notesRes)
        //println("PrayerDetails - after preparePrayer for  prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
        Column(
            modifier = Modifier.fillMaxSize()
                .drawWithCache {
                    val colFilter = ColorFilter.tint(contentColor)
                    val rect = Rect(offset = Offset.Zero, size = size)
                    val paint = Paint()
                    paint.colorFilter = colFilter
                    onDrawWithContent {
                        drawIntoCanvas { canvas ->
                            canvas.withSaveLayer(rect, paint) { drawContent() }
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getTitle(displayLang, contentItem.content, config),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (intention != null) {
                        var nums = ""
                        if (config.showNumbers) {
                            nums = intenTotalNum.toString()
                        }
                        if (intention.days > 1) {
                            if (nums.isNotEmpty()) nums += " / "
                            nums = nums + intenInrowNum
                        }
                        if (nums.isNotEmpty()) nums = " (" + nums + ")"
                        Text(
                            text = intention.intention + nums,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    } else {
                        var subtitle: String
                        var bibleContent =
                            contentItem.content.langs[config.firstBible] as? BibleBasicContent
                        if (bibleContent == null) {
                            bibleContent =
                                contentItem.content.langs[config.secondBible] as? BibleBasicContent
                        }
                        if (bibleContent != null) {
                            subtitle = bibleContent.subtitle
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val margins = maxWidth * 0.04f
                titleMaxLen = ((dpToPx(maxWidth) / textWidth) / 2.3).toInt()
                //println("Preparing markdown for:  prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
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
                        //link = MaterialTheme.typography.labelMedium
                    ),
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = margins, vertical = 4.dp),
                    //.background(color = MaterialTheme.colorScheme.background)
                    //.verticalScroll(scrollState),
                    components = markdownComponents(
                        paragraph = customParagraphComponent,
                    ),
                    success = @Composable { state, components, modifier ->
                        MyMarkdownSuccess(
                            state, components, modifier,
                            savedScrollPosition,
                            { it ->
                                savedScrollPosition = it.firstVisibleItemIndex
                            },
                            //scrollState,
                            {
                                endReachedCallbackPD()
                                intenTotalNum = intention?.totalNum ?: 0
                                intenInrowNum = intention?.inrowNum ?: 0
                                //println("PrayerDetails endReachedCallback, $intenTotalNum, $intenInrowNum")
                            }
                        )
                    },
                )
                //println("Prepared markdown for:  prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
            }
            if (contentItem.prevContent != null || contentItem.nextContent != null) {
                val navColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                Row() {
                    contentItem.prevContent?.let { prevItem ->
                        //println("PrayerDetails prevPrayer != null - before previous, prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
                        TextButton(
                            onClick = {
                                //currentPrayer = currentPrayer.prevPrayer!!
                                //println("PrayerDetails onClick previous, prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
                                prayerChangedCallback(prevItem)
                                savedScrollPosition = 0
//                                endProcessed = false
//                                coroutineScope.launch {
//                                    scrollState.scrollToItem(0)
//
//                                }
                                changed = !changed
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
                                text = getTitle(
                                    displayLang,
                                    prevItem.content,
                                    config,
                                    titleMaxLen
                                ),
                                color = navColor
                            )
                        }
                    }
                    contentItem.nextContent?.let { nextItem ->
                        //println("PrayerDetails nextPrayer != null - before next, prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                //currentPrayer = currentPrayer.nextPrayer!!
                                //println("PrayerDetails onClick next, prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
                                prayerChangedCallback(nextItem)
                                savedScrollPosition = 0
//                                endProcessed = false
//                                coroutineScope.launch {
//                                    scrollState.scrollToItem(0)
//
//                                }
                                changed = !changed
                            }
                        ) {
                            Text(
                                text = getTitle(
                                    displayLang,
                                    nextItem.content,
                                    config,
                                    titleMaxLen
                                ),
                                color = navColor
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
            //println("PrayerDetails - end of function, prayer: ${prayer.name}, currentPrayer: ${currentPrayer.name}")
        }
    }

}