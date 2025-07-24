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

import com.ucasoft.kcron.core.builders.Builder
import com.ucasoft.kcron.kotlinx.datetime.CronLocalDateTime
import com.ucasoft.kcron.kotlinx.datetime.CronLocalDateTimeProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Link {
    @SerialName("youtube")
    @Serializable
    data class Youtube(val url: String, val title: String? = null) : Link()
}

@Serializable
data class BasicPrayer(
    val title: String,
    val lang: String,
    val language: String,
    val lines: MutableList<String?>,
    val dates: List<String>? = null,
    val tags: Set<String>? = null,
    val links: List<Link>? = null,
    val notes: String? = null
)

data class Prayer(
    val id: Int,
    val name: String,
    val langs: MutableMap<String, BasicPrayer>,
    var prevPrayer: Prayer? = null,
    var nextPrayer: Prayer? = null,
    var nums: PrayerNums = PrayerNums(
        lastRecorded = LocalDate(1970, 1,1),
        totalNum = 0,
        inrowNum = 0
    ),
    val dates: MutableSet<Builder<LocalDateTime, CronLocalDateTime, CronLocalDateTimeProvider>> = mutableSetOf(),
) {
    @Transient
    var externalChangeListeners = ArrayList<() -> Unit>()

    fun prayedToday(): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return nums.lastRecorded.daysUntil(today) <= 0
    }

    fun externalChange(prNums: PrayerNums) {
        nums = prNums
        externalChangeListeners.forEach { listener ->
            listener()
        }
    }

    fun addExternalChangeListener(listener: () -> Unit) {
        externalChangeListeners.add(listener)
    }

    fun isTodayAndNow(): Boolean {
        return isTodayAndNow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour)
    }

    fun isTodayAndNow(curHour: Int): Boolean {
        dates.forEach { builder ->
            if (builder.nextRun?.date == Clock.System.todayIn(TimeZone.currentSystemDefault())
                && builder.nextRun?.time?.hour == curHour) {
                return true
            }
        }
        return false
    }

}

val HIDE_TAG = "Hide"

