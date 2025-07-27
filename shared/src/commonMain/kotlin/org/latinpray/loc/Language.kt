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

package org.latinpray.loc

sealed class Language(val isoFormat : String, val name : String) {
    data object English : Language("en", "English")
    data object Spanish : Language("es", "Español")
    data object Latin : Language("la", "Latina")
    data object Polish : Language("pl", "Polski")
    data object Italian : Language("it", "Italiano")
    data object German : Language("de", "Deutsch")
    data object French : Language("fr", "Français")
    data object Portuguese : Language("pt", "Português")
    data object Russian : Language("ru", "Русский")
    data object Turkish : Language("tr", "Türkçe")
    data object Hebrew : Language("he", "עברית")
    data object Unknown : Language("unknown", "Unknown")
}

fun getLanguage(isoFormat : String) : Language {
    return when (isoFormat) {
        Language.English.isoFormat -> Language.English
        Language.Spanish.isoFormat -> Language.Spanish
        Language.Latin.isoFormat -> Language.Latin
        Language.Polish.isoFormat -> Language.Polish
        else -> Language.Unknown
    }
}