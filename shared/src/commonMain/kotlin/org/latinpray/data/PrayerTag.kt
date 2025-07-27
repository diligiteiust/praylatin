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

import org.latinpray.loc.Language

data class PrayerTag(val isoFormat : String, val value : String)

val angels = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Angels"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Ángeles"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Ángelos"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Do Aniołów")
)
val basic = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Basic"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Básico"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Primus"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Podstawowe")
)
val blessedVirginMary = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Blessed Virgin Mary"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Santisima Virgen Maria"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Beata Virgo Maria"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Do Matki Bożej")
)
val holyMass = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Holy Mass"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Santa Misa"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Missa Sacra"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Msza Święta")
)
val jesusChrist = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Jesus Christ"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Jesucristo"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Iesus Christus"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Do Pana Jezusa")
)
val holyEucharist = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Holy Eucharist"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Eucharistia"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Eucharistia"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Najświętszy Sacrament")
)
val song = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Song"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Canticum"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Canticum"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Pieśń")
)
val rosary = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Rosary"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Rosario"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Rosarium"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Różaniec")
)
val communion = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Communion"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "El Eucaristia"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Communio"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Komunia Święta")
)
val confession = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Confession"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Confesión"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Confessio"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Spowiedź")
)
val litany = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Litany"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Letanía"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Litaniae"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Litania")
)
val saints = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Saints"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Santos"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Sanctos"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Do Świętych")
)
val saintJoseph = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Saint Joseph"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "San José"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Sancti Ioseph"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Święty Józef")
)
val novena = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Novena"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Novena"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Novena"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Nowenna")
)
val devotion = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Devotion"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Devoción"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Devotio"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Nabożeństwo")
)
val psalms = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Psalms"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "Salmos"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Psalmi"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Psalm")
)
val sacretHeart = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "The Most Sacred Heart of Jesus"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "El Sagrado Corazón de Jesús"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Sacratissimum Cor Iesu"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Najświętsze Serce Pana Jezusa")
)
val holySpirit = mapOf<String, PrayerTag>(
    Language.English.isoFormat to PrayerTag(Language.English.isoFormat, "Holy Spirit"),
    Language.Spanish.isoFormat to PrayerTag(Language.Spanish.isoFormat, "El Espíritu Santo"),
    Language.Latin.isoFormat to PrayerTag(Language.Latin.isoFormat, "Spiritus Sanctus"),
    Language.Polish.isoFormat to PrayerTag(Language.Polish.isoFormat, "Duch Święty")
)

class AllTags {
    val allTags = listOf(
        angels,
        basic,
        blessedVirginMary,
        communion,
        confession,
        devotion,
        holyEucharist,
        holyMass,
        holySpirit,
        jesusChrist,
        litany,
        novena,
        psalms,
        rosary,
        sacretHeart,
        saintJoseph,
        saints,
        song,
    )
    val reverseTags = mutableMapOf<String, Map<String, PrayerTag>>()

    init {
        allTags.forEach { tag_map ->
            tag_map.values.forEach { prayerTag ->
                reverseTags[prayerTag.value] = tag_map
            }
        }
    }

    fun getTagForLanguage(language : String, value : String) : String {
        return reverseTags[value]?.get(language)?.value ?: value
    }

}

val allTags = AllTags()