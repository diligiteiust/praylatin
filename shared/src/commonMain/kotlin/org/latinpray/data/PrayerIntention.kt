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

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class PrayerIntention @OptIn(ExperimentalUuidApi::class) constructor(
    var intention : String,
    var days : Int = 0,
    var active : Boolean = true,
    var currentIntention: Boolean = false,
    var totalNum : Int = 0,
    var inrowNum : Int = 0,
    val id : Int = Uuid.random().hashCode()
) {

    companion object {
        fun fromPropsString(s: String): PrayerIntention {
            val props = s.split(',')
            var inten = ""
            for (i in 6 until props.size) {
                inten += props[i] + ","
            }
            inten = inten.substring(0, inten.length - 1)
            return PrayerIntention(
                intention = inten,
                days = props[0].toInt(),
                active = props[1].toBoolean(),
                currentIntention = props[2].toBoolean(),
                totalNum = props[3].toInt(),
                inrowNum = props[4].toInt(),
                id = props[5].toInt()
            )
        }
    }

    fun toPropsString(): String {
        return "$days,$active,$currentIntention,$totalNum,$inrowNum,$id,$intention"
    }

}
