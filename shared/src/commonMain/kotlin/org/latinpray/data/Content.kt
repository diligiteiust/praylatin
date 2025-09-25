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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlinx.serialization.Transient
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

    @OptIn(ExperimentalTime::class)
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