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

val avemaryjaLa = BasicPrayer(
    title = "Ave Maryja",
    lang = "la",
    language = "Latina",
    lines = mutableListOf(
        "Ave Maria, gratia plena,",
        "Dominus tecum.",
        "Benedicta tu in mulieribus,",
        "et benedictus fructus ventris tui, Iesus.",
        "Sancta Maria, Mater Dei,",
        "ora pro nobis peccatoribus,",
        "nunc, et in hora mortis nostrae."
    )
)
val avemaryjaPl = BasicPrayer(
    title = "Zdrowaś Maryjo",
    lang = "pl",
    language = "Polski",
    lines = mutableListOf(
        "Zdrowaś Maryjo, łaski pełna",
        "Pan z Tobą",
        "Błogosławionaś Ty między niewiastami",
        "i błogosławiony owoc żywota Twojego, Jezus.",
        "Święta Maryjo, Matko Boża",
        "Módl się za nami grzesznikami,",
        "Teraz, i w godzinie śmierci naszej."
    )
)

val paternosterLa = BasicPrayer(
    title = "Pater Noster",
    lang = "la",
    language = "Latine",
    lines = mutableListOf(
        "Pater Noster,",
        "qui es in caelis,",
        "sanctificetur nomen tuum.",
        "Adveniat regnum tuum.",
        "Fiat voluntas tua,",
        "sicut in caelo et in terra.",
        "Panem nostrum quotidianum da nobis hodie,",
        "et dimitte nobis debita nostra",
        "sicut et nos dimittimus debitoribus nostris.",
        "Et ne nos inducas in tentationem,",
        "sed libera nos a malo."
    )
)
val paternosterPl = BasicPrayer(
    title = "Ojcze nasz",
    lang = "pl",
    language = "Polski",
    lines = mutableListOf(
        "Ojcze nasz,",
        "któryś jest w niebie,",
        "święć się imię Twoje,",
        "przyjdź królestwo Twoje,",
        "bądź wola Twoja,",
        "jako w niebie, tak i na ziemi.",
        "Chleba naszego powszedniego daj nam dzisiaj,",
        "i odpuść nam nasze winy,",
        "jako i my odpuszczamy naszym winowajcom.",
        "I nie wódź nas na pokuszenie,",
        "ale nas zbaw ode złego."
    )
)

val avemaria = Prayer(
    1,
    "avemaria",
    mutableMapOf(
        "la" to avemaryjaLa,
        "pl" to avemaryjaPl
    )
)

val paternoster = Prayer(
    2,
    "paternoster",
    mutableMapOf(
        "la" to paternosterLa,
        "pl" to paternosterPl
    )
)

val samplePrayers = listOf(avemaria, paternoster)

var sampleConfig = Config(
    "en",
    "la",
    "en",
    true,
    true)
//var defConfig : Config? = sampleConfig

var terms: String = ""
var privacy: String = ""
