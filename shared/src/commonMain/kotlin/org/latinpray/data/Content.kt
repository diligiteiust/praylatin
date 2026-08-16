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

package org.latinpray.data

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlinx.serialization.Transient
import org.latinpray.ui.ContentItem
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface BssicContent {
    val title: String
    val lang: String
    val language: String
    val lines: List<String?>
    val tags: Set<String>?
    val links: List<Link>?
    val notes: String?
}

abstract class Content(
    var nums: ContentNums = ContentNums(
        lastRecorded = LocalDate(1970, 1, 1),
        totalNum = 0,
        inrowNum = 0
    )
) {
    abstract val id: Int
    abstract val name: String
    abstract val langs: MutableMap<String, BssicContent>

    @Transient
    var externalChangeListeners = ArrayList<() -> Unit>()

    @Transient
    val mutex = Mutex()

//    @Transient
//    val lock = reentrantLock()

    fun prayedToday(): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        //println("name: $name, today: $today, lastRecorded: ${nums.lastRecorded}")
        return nums.lastRecorded.daysUntil(today) <= 0
    }

    suspend fun addExternalChangeListener(listener: () -> Unit) {
        mutex.withLock {
            externalChangeListeners.add(listener)
        }
    }

    suspend fun externalChange(prNums: ContentNums) {
        mutex.withLock {
            nums = prNums
            externalChangeListeners.forEach { listener ->
                listener()
            }
        }
    }

}

fun getGrouppedContent(config: Config, prayers: MutableList<Prayer>, readingPlan: ReadingPlan?,
                       currentHour: Int, todayAndNowStr: String, favoritePrayersStr: String,
                       dailyPrayersStr: String): List<Any> {
    if (config.grouping) {
        val gp = mutableListOf<Any>()
        val tags = mutableSetOf<String>()
        prayers.forEach { prayer ->
            if ((prayer.langs[config.prayerLang] != null && prayer.langs[config.prayerLang]?.tags != null)
                || (prayer.langs[config.secondLang] != null && prayer.langs[config.secondLang]?.tags != null)
            ) {
                val tmp_tags = mutableSetOf<String>()
                if (prayer.langs[config.prayerLang]?.tags != null) {
                    tmp_tags.addAll(prayer.langs[config.prayerLang]?.tags!!)
                } else if (prayer.langs[config.secondLang]?.tags != null) {
                    tmp_tags.addAll(prayer.langs[config.secondLang]?.tags!!)
                }

                tmp_tags.forEach { tag ->
                    tags.add(allTags.getTagForLanguage(config.uiLang, tag))
                }
                //tags.addAll(prayer.langs[config.prayerLang]?.tags!!)
            }
        }
        tags.remove(HIDE_TAG)
        if (config.todayAndNow) {
            gp.add(todayAndNowStr)
            runBlocking {
                try {
                    DailyReadingTR.massForToday(config)?.let {
                        gp.add(ContentItem(it, it.prayedToday(), todayAndNowStr))
                    }
                } catch (e: Exception) {
                    println("massForToday failed: ${e.message}")
                }
                if (config.biblePlan) {
                    val bible = readingPlan?.bibleForToday(config)
                    bible?.let {
                        gp.add(ContentItem(it, it.prayedToday(), todayAndNowStr))
                    }
                }
            }
            prayers.forEach { prayer ->
                //println("Checking prayer ${prayer.name} for tag $tag with tags ${prayer.langs[config.prayerLang]?.tags} or ${prayer.langs[config.secondLang]?.tags}")
                if ((prayer.langs[config.prayerLang] != null || prayer.langs[config.secondLang] != null)
                    && prayer.isTodayAndNow(currentHour)
                ) {
                    gp.add(ContentItem(prayer, prayer.prayedToday(), todayAndNowStr))
                    //println("Added 1st prayer ${prayer.name} to group: $tag")
                }
            }

        }
        if (config.dailyPrayers.isNotEmpty()) {
            gp.add(dailyPrayersStr)
            var lastPr: ContentItem? = null
            config.dailyPrayers.forEach { prayer ->
                prayers.firstOrNull { it.name == prayer }?.let { pr ->
                    val contItem = ContentItem(pr, pr.prayedToday(), dailyPrayersStr)
                    lastPr?.let { lPr ->
                        lPr.nextContent = contItem
                        contItem.prevContent = lPr
                    }
                    lastPr = contItem
                    gp.add(contItem)
                }
            }
        }
        if (config.favorites.isNotEmpty()) {
            gp.add(favoritePrayersStr)
            config.favorites.forEach { prayer ->
                prayers.firstOrNull { it.name == prayer }?.let {
                    gp.add(ContentItem(it, false, favoritePrayersStr))
                }
            }
        }
        tags.sorted().forEach { tag ->
            gp.add(tag)
            prayers.forEach { prayer ->
                //println("Checking prayer ${prayer.name} for tag $tag with tags ${prayer.langs[config.prayerLang]?.tags} or ${prayer.langs[config.secondLang]?.tags}")
                if ((prayer.langs[config.prayerLang] != null)
                    && (prayer.langs[config.prayerLang]?.tags?.contains(
                        allTags.getTagForLanguage(
                            config.prayerLang,
                            tag
                        )
                    ) == true)
                    && (prayer.langs[config.prayerLang]?.tags?.contains(HIDE_TAG) == false)
                ) {
                    gp.add(ContentItem(prayer, false, tag))
                    //println("Added 1st prayer ${prayer.name} to group: $tag")
                } else if ((prayer.langs[config.secondLang] != null)
                    && (prayer.langs[config.secondLang]?.tags?.contains(
                        allTags.getTagForLanguage(
                            config.secondLang,
                            tag
                        )
                    ) == true)
                    && prayer.langs[config.secondLang]?.tags?.contains(HIDE_TAG) == false
                ) {
                    gp.add(ContentItem(prayer, false, tag))
                    //println("Added 2nd prayer ${prayer.name} to group: $tag")
                }
            }
        }
        //println("Returning grouped content ${gp.size}")
        return gp
    } else {
        return prayers.toList()
    }
}