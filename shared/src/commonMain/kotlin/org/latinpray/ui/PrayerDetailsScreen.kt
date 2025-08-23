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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.PrayerIntention
import org.latinpray.shared.Res
import org.latinpray.shared.added_to_daily
import org.latinpray.shared.added_to_favorites
import org.latinpray.shared.align_end_24dp
import org.latinpray.shared.align_start_24dp
import org.latinpray.shared.bookmark_add
import org.latinpray.shared.bookmark_check
import org.latinpray.shared.calendar_add_on
import org.latinpray.shared.calendar_month
import org.latinpray.shared.intention_active
import org.latinpray.shared.intention_add
import org.latinpray.shared.intention_days
import org.latinpray.shared.intention_text
import org.latinpray.shared.intentions_title
import org.latinpray.shared.removed_from_daily
import org.latinpray.shared.removed_from_favorites
import org.latinpray.theme.Gray300
import org.latinpray.theme.Green300
import org.latinpray.theme.Green900
import org.latinpray.theme.Orange900
import org.latinpray.util.DisplayLang
import org.latinpray.util.findNextActiveIntention
import org.latinpray.util.getCurrentIntention

@Composable
fun IntentionsForm(item: PrayerIntention) {

    var text by remember { mutableStateOf(item.intention) }
    var days by remember { mutableStateOf(item.days) }
    var active by remember { mutableStateOf(item.active) }

    val color = if (item.currentIntention) Green300 else Gray300

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .border(1.dp, color = color, shape = MaterialTheme.shapes.medium),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    item.intention = it
                    text = item.intention
                },
                readOnly = false,
                minLines = 2,
                maxLines = 2,
                label = {
                    Text(
                        text = stringResource(Res.string.intention_text),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row() {
                Column(
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.intention_active),
                    )
                    Checkbox(
                        checked = active,
                        onCheckedChange = {
                            item.active = it
                            active = item.active
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
                OutlinedTextField(
                    value = days.toString(),
                    onValueChange = { newValue ->
                        item.days = newValue.toIntOrNull() ?: 0
                        days = item.days
                    },
                    readOnly = false,
                    singleLine = true,
                    modifier = Modifier.widthIn(min = 150.dp, max = 300.dp),
                    label = { Text(stringResource(Res.string.intention_days)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
    }
}

@Composable
fun IntentionsDialog(
    onDismissRequest: () -> Unit = {},
    onOkRequest: (List<PrayerIntention>) -> Unit = {},
    prayerIntentions: List<PrayerIntention> = emptyList()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        val itemList =
            remember { mutableStateListOf<PrayerIntention>(*prayerIntentions.toTypedArray()) }

        // Your form content here (e.g., TextFields, Buttons)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    //modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    text = stringResource(Res.string.intentions_title),
                    textAlign = TextAlign.Center,
                )
                ElevatedButton(
                    onClick = {
                        val current = itemList.size == 0
                        itemList.add(PrayerIntention("", 0, true, current))
                    }
                ) {
                    Text(stringResource(Res.string.intention_add))
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Save",
                        tint = Green900,
                        modifier = Modifier.size(30.dp)
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(itemList) { item ->
                        // Your composable function to display the item
                        IntentionsForm(item)
                    }
                }
                Row(Modifier.padding(horizontal = 48.dp)) {
                    IconButton(
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = "Cancel",
                            tint = Orange900,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            onOkRequest(itemList.toList())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = "Save",
                            tint = Green900,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun MessageDialog(
    message: String,
    show: Boolean = true,
    onDismissRequest: () -> Unit = {},
) {
    var showMessage by remember { mutableStateOf(show) }
    val scope = rememberCoroutineScope()
    scope.launch {
        delay(2000)
        showMessage = false
        onDismissRequest()
    }
    AnimatedVisibility(
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        visible = showMessage,
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                    )
                    IconButton(
                        onClick = {
                            showMessage = false
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = "Ok",
                            tint = Green900,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerDetailsScreen(
    startContent: ContentItem,
    config: Config,
    prayers: MutableList<Prayer>,
    goBack: () -> Unit,
) {
    var contentItem by remember { mutableStateOf(startContent) }
    var prayer: Prayer? by remember { mutableStateOf(null) }
    prayer = contentItem.content as? Prayer
    val (fraction) = remember { mutableStateOf(0.25f) }
    var firstLang by remember { mutableStateOf(true) }
    var displayLang by remember { mutableStateOf(DisplayLang.BOTH) }
    var daily by remember { mutableStateOf(config.dailyPrayers.contains(contentItem.content.name)) }
    var favorite by remember { mutableStateOf(config.favorites.contains(contentItem.content.name)) }
    val scope = rememberCoroutineScope()
    //var prayerNums = config.loadPrayerNums(prayer)
    var showDialog by remember { mutableStateOf(false) }
    var endProcessed by remember { mutableStateOf(false) }

    var totalNum: Int by remember { mutableStateOf(0) }
    var inrowNum: Int by remember { mutableStateOf(0) }
    var prayerIntentions: List<PrayerIntention> by remember { mutableStateOf(listOf<PrayerIntention>()) }
    var currentIntention: PrayerIntention? by remember { mutableStateOf(null) }
    totalNum = contentItem.content.nums.totalNum
    inrowNum = contentItem.content.nums.inrowNum
    prayer?.let {
      prayerIntentions = config.loadIntentions(it)
      currentIntention = getCurrentIntention(it, config)
    }

    var dlgMessage by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    val added_to_daily = stringResource(Res.string.added_to_daily)
    val removed_from_daily = stringResource(Res.string.removed_from_daily)
    val added_to_favorites = stringResource(Res.string.added_to_favorites)
    val removed_from_favorites = stringResource(Res.string.removed_from_favorites)

    if (showMessage) {
        MessageDialog(
            message = dlgMessage,
            onDismissRequest = { showMessage = false }
        )
    }

    if (showDialog) {
        IntentionsDialog(
            onDismissRequest = { showDialog = false },
            onOkRequest = {
                showDialog = false
                prayerIntentions = it
                var intent = it.find { it.currentIntention }
                if (intent != null && !intent.active) {
                    intent.currentIntention = false
                    val nextInten = findNextActiveIntention(intent, it)
                    if (nextInten != intent) {
                        nextInten.currentIntention = true
                        intent = nextInten
                    } else {
                        intent = null
                    }
                }
                if (intent == null) {
                    intent = it.find { it.active }
                    if (intent != null) {
                        intent.currentIntention = true
                    }
                }
                scope.launch {
                    prayer?.let { it1 -> config.saveIntentions(it1, it) }
                }
                currentIntention = intent
            },
            prayerIntentions = prayerIntentions
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(bottom = 20.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp).alpha(fraction),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        goBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = "Go back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (config.showNumbers) {
                    Text(text = "$totalNum / $inrowNum")
                }
                Spacer(Modifier.weight(1f))
                if (prayer != null) {
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "Intentions",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconToggleButton(
                    checked = firstLang,
                    onCheckedChange = {
                        firstLang = it
                        displayLang = displayLang.next()
                    }
                ) {
                    when (displayLang) {
                        DisplayLang.FIRST -> {
                            Icon(
                                painter = painterResource(Res.drawable.align_start_24dp),
                                contentDescription = "2nd lang Off",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        DisplayLang.SECOND -> {
                            Icon(
                                painter = painterResource(Res.drawable.align_end_24dp),
                                contentDescription = "1st lang Off",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        DisplayLang.BOTH -> {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                                contentDescription = "Both langs",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                if (prayer != null) {
                    IconToggleButton(
                        checked = daily,
                        onCheckedChange = {
                            daily = it
                            scope.launch {
                                if (daily) {
                                    config.addDailyPrayer(prayer!!.name)
                                    dlgMessage = added_to_daily
                                } else {
                                    config.removeDailyPrayer(prayer!!.name)
                                    dlgMessage = removed_from_daily
                                }
                                showMessage = true
                            }
                        }
                    ) {
                        if (daily) {
                            Icon(
                                painter = painterResource(Res.drawable.calendar_month),
                                contentDescription = "In daily prayers",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.calendar_add_on),
                                contentDescription = "Add to daily prayers",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    IconToggleButton(
                        checked = favorite,
                        onCheckedChange = {
                            favorite = it
                            scope.launch {
                                if (favorite) {
                                    config.addFavorite(prayer!!.name)
                                    dlgMessage = added_to_favorites
                                } else {
                                    config.removeFavorite(prayer!!.name)
                                    dlgMessage = removed_from_favorites
                                }
                                showMessage = true
                            }
                        }
                    ) {
                        if (favorite) {
                            Icon(
                                painter = painterResource(Res.drawable.bookmark_check),
                                contentDescription = "In favorites",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.bookmark_add),
                                contentDescription = "Add to favorites",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
        //key (totalNum, inrowNum) {
        ContentDetails(
            displayLang = displayLang,
            contentItem = contentItem,
            config = config,
            prayers = prayers,
            endReachedCallbackPD = {
                if (endProcessed) return@ContentDetails
                endProcessed = true
                //println("End reached start: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                config.incContentNum(contentItem.content, prayerIntentions)
                totalNum = contentItem.content.nums.totalNum
                inrowNum = contentItem.content.nums.inrowNum
                prayer?.let { it1 ->
                    currentIntention = getCurrentIntention(it1, config)
                }
                //prayerIntentions = config.loadIntentions(prayer)
                //println("End reached end: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
            },
            intention = currentIntention,
            prayerChangedCallback = { contIt ->
                //println("Prayer changed start: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                contentItem = contIt
                prayer = contentItem.content as? Prayer
                daily = config.dailyPrayers.contains(contentItem.content.name)
                favorite = config.favorites.contains(contentItem.content.name)
                totalNum = contentItem.content.nums.totalNum
                inrowNum = contentItem.content.nums.inrowNum
                prayer?.let { it1 ->
                    prayerIntentions = config.loadIntentions(it1)
                    currentIntention = getCurrentIntention(it1, config)
                }
                endProcessed = false
                //println("Prayer changed end: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
            }
        )
        //}
    }
}
