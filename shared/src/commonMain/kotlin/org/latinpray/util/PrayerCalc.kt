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

package org.latinpray.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.PrayerIntention

/* Allows to say prayers until 6AM next day after the day on which the prayer should be said.
   Otherwise the inrowNum is reset.
 */
const val TWO_DAYS_AND_6_HOURS_IN_HOURS = 56


enum class PrayerTime {
    TODAY,
    YESTERDAY,
    /* Allows to say prayers until 6AM next day after the day on which the prayer should be said.
       Otherwise the inrowNum is reset.
     */
    SIX_HOURS_LATE,
    DAYS_GAP
}

enum class DisplayLang {
    BOTH,
    FIRST,
    SECOND;

    fun next(): DisplayLang {
        val values = entries
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }
}

fun LocalDateTime.truncateToHour(): LocalDateTime {
    return LocalDateTime(
        year = this.year,
        monthNumber = this.monthNumber,
        dayOfMonth = this.dayOfMonth,
        hour = this.hour,
        minute = 0,
        second = 0,
        nanosecond = 0
    )
}

fun calcPrayerTime(last_date: LocalDate): PrayerTime {
    val curr_date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    if (curr_date <= last_date) return PrayerTime.TODAY
    if (last_date.daysUntil(curr_date) == 1) return PrayerTime.YESTERDAY

    val curr_time_millis = Clock.System.now()
    val last_date_millis =
        last_date.atTime(0, 0).toInstant(TimeZone.currentSystemDefault())
    val diff = curr_time_millis - last_date_millis

    if (diff.inWholeHours <= TWO_DAYS_AND_6_HOURS_IN_HOURS) return PrayerTime.SIX_HOURS_LATE

    return PrayerTime.DAYS_GAP
}

fun findNextActiveIntention(curIntention: PrayerIntention, prayerIntentions: List<PrayerIntention>): PrayerIntention {
    //println("Current intention: ${curIntention?.toPropsString()}")
    //println("All intentions: ${prayerIntentions.map { it.toPropsString() }}")
    if (prayerIntentions.size > 1) {
        var idx = prayerIntentions.indexOf( curIntention )
        //println("Current intention index: $idx")
        var nextInten: PrayerIntention
        do {
            if (idx < prayerIntentions.size - 1) {
                idx += 1
            } else {
                idx = 0
            }
            nextInten = prayerIntentions[idx]
        } while (!nextInten.active && nextInten.id != curIntention.id)
        return nextInten
    }
    //println("Current intention: ${curIntention?.toPropsString()}")
    return curIntention
}

fun getCurrentIntention(prayer: Prayer, config: Config): PrayerIntention? {
    val allIntentions = config.loadIntentions(prayer)
    var curr_intent: PrayerIntention? = allIntentions.find { it.currentIntention }

    if (curr_intent == null) {
        curr_intent = allIntentions.find { it.active }
        if (curr_intent != null) {
            curr_intent.currentIntention = true
            config.saveIntention(prayer, curr_intent)
        }
    }

    if (curr_intent != null
        && curr_intent.days > 1
        && curr_intent.inrowNum >= curr_intent.days) {

        // I am not sure if copy() is needed here. Probably for data class is the default
        // any way I want to make sure we do not pass reference to the same object but
        // a copy of the object.
        val old_intent = curr_intent.copy()

        if (old_intent.inrowNum == old_intent.days) {
            if (calcPrayerTime(prayer.nums.lastRecorded) != PrayerTime.TODAY) {
                old_intent.totalNum += 1
                old_intent.inrowNum = 0
                curr_intent = findNextActiveIntention(curr_intent, allIntentions)
            }
        } else {
            // This should not actually happen...
            old_intent.totalNum += 1
            old_intent.inrowNum = 1
            curr_intent = findNextActiveIntention(curr_intent, allIntentions)
        }

        if (curr_intent.id != old_intent.id) {
            curr_intent.currentIntention = true
            old_intent.currentIntention = false
            config.saveIntention(prayer, curr_intent)
        }
        config.saveIntention(prayer, old_intent)
    }

    return curr_intent
}