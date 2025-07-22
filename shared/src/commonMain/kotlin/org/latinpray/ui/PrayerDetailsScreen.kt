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

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.PrayerIntention
import org.latinpray.shared.Res
import org.latinpray.shared.bookmark_add
import org.latinpray.shared.bookmark_check
import org.latinpray.shared.calendar_add_on
import org.latinpray.shared.calendar_month
import org.latinpray.shared.intention_active
import org.latinpray.shared.intention_add
import org.latinpray.shared.intention_days
import org.latinpray.shared.intention_text
import org.latinpray.shared.intentions_title
import org.latinpray.theme.Gray300
import org.latinpray.theme.Green300
import org.latinpray.theme.Green900
import org.latinpray.theme.Orange900
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
        Column (
            modifier = Modifier.padding(8.dp),
        ){
            OutlinedTextField(
                value = text,
                onValueChange = {
                    item.intention = it
                    text = item.intention
                },
                readOnly = false,
                minLines = 2,
                maxLines = 2,
                label = { Text( text = stringResource(Res.string.intention_text),
                    style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row() {
                Column (
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
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
    onDismissRequest : () -> Unit = {},
    onOkRequest : (List<PrayerIntention>) -> Unit = {},
    prayerIntentions: List<PrayerIntention> = emptyList()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        val itemList = remember { mutableStateListOf<PrayerIntention>(*prayerIntentions.toTypedArray()) }

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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerDetailsScreen(
    startPrayer: Prayer,
    config: Config,
    prayers: MutableList<Prayer>,
    goBack: () -> Unit,
) {
    var prayer by remember { mutableStateOf(startPrayer) }
    val (fraction) = remember { mutableStateOf(0.25f) }
    var firstLang by remember { mutableStateOf(true) }
    var daily by remember { mutableStateOf(config.dailyPrayers.contains(prayer.name)) }
    var favorite by remember { mutableStateOf(config.favorites.contains(prayer.name)) }
    val scope = rememberCoroutineScope()
    //var prayerNums = config.loadPrayerNums(prayer)
    var totalNum by remember { mutableStateOf(prayer.nums.totalNum) }
    var inrowNum by remember { mutableStateOf(prayer.nums.inrowNum) }
    var showDialog by remember { mutableStateOf(false) }
    var endProcessed by remember { mutableStateOf(false) }

    var prayerIntentions by remember { mutableStateOf(config.loadIntentions(prayer) ) }
    var currentIntention by remember { mutableStateOf(getCurrentIntention(prayer, config)) }
    //println("Current currentIntention: ${currentIntention?.toPropsString()}")

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
                    config.saveIntentions(prayer, it)
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
                IconToggleButton(
                    checked = firstLang,
                    onCheckedChange = {
                        firstLang = it
                    }
                ) {
                    if (firstLang) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                            contentDescription = "1st lang On",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Notes,
                            contentDescription = "1st lang Off",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconToggleButton(
                    checked = daily,
                    onCheckedChange = {
                        daily = it
                        scope.launch {
                            if (daily) {
                                config.addDailyPrayer(prayer.name)
                            } else {
                                config.removeDailyPrayer(prayer.name)
                            }
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
                                config.addFavorite(prayer.name)
                            } else {
                                config.removeFavorite(prayer.name)
                            }
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
        //key (totalNum, inrowNum) {
            PrayerDetails(
                firstLang = firstLang,
                prayer = prayer,
                config = config,
                prayers = prayers,
                endReachedCallbackPD = {
                    if (endProcessed) return@PrayerDetails
                    endProcessed = true
                    //println("End reached start: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                    config.incPrayerNum(prayer, prayerIntentions)
                    totalNum = prayer.nums.totalNum
                    inrowNum = prayer.nums.inrowNum
                    prayerIntentions = config.loadIntentions(prayer)
                    currentIntention = prayerIntentions.find { it.currentIntention }
                    //println("End reached end: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                },
                intention = currentIntention,
                prayerChangedCallback = { pr ->
                    //println("Prayer changed start: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                    prayer = pr
                    daily = config.dailyPrayers.contains(prayer.name)
                    favorite = config.favorites.contains(prayer.name)
                    totalNum = prayer.nums.totalNum
                    inrowNum = prayer.nums.inrowNum
                    prayerIntentions = config.loadIntentions(prayer)
                    currentIntention = prayerIntentions.find { it.currentIntention }
                    endProcessed = false
                    //println("Prayer changed end: ${prayer.name} - ${prayer.nums}, ${currentIntention}")
                }
            )
        //}
    }
}
