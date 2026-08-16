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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.number
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Gregorian computus (Anonymous Gregorian / Meeus). Western Easter.
 * Returns month (1–12) and day.
 */
fun calculateEaster(year: Int): Pair<Int, Int> {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val n = h + l - 7 * m + 114
    val month = n / 31
    val day = (n % 31) + 1
    return Pair(month, day)
}

fun easterDate(year: Int): LocalDate {
    val (month, day) = calculateEaster(year)
    return LocalDate(year, month, day)
}

private fun LocalDate.plusDays(days: Int): LocalDate = plus(days, DateTimeUnit.DAY)

private fun previousOrSameSunday(date: LocalDate): LocalDate {
    var d = date
    while (d.dayOfWeek != DayOfWeek.SUNDAY) {
        d = d.plusDays(-1)
    }
    return d
}

private fun firstSundayOnOrAfter(date: LocalDate): LocalDate {
    var d = date
    while (d.dayOfWeek != DayOfWeek.SUNDAY) {
        d = d.plusDays(1)
    }
    return d
}

/** First Sunday of Advent: Sunday from 27 Nov through 3 Dec. */
fun adventSunday(year: Int): LocalDate = firstSundayOnOrAfter(LocalDate(year, 11, 27))

/** 1962: last Sunday of October. */
fun christTheKingDate(year: Int): LocalDate {
    var d = LocalDate(year, 10, 31)
    while (d.dayOfWeek != DayOfWeek.SUNDAY) {
        d = d.plusDays(-1)
    }
    return d
}

private fun holyNameDate(year: Int): LocalDate {
    val sunday = firstSundayOnOrAfter(LocalDate(year, 1, 2))
    return if (sunday.month.number == 1 && sunday.day <= 5) sunday else LocalDate(year, 1, 2)
}

data class MassLesson(
    val start: String,
    val end: String,
)

data class TraditionalMass(
    val id: String,
    val titleLa: String,
    val titleEn: String,
    val epistle: MassLesson,
    val gospel: MassLesson,
) {
    fun titleFor(uiLang: String): String = when (uiLang) {
        "la" -> titleLa
        else -> "$titleLa — $titleEn"
    }

    fun ranges(): List<ReadingRange> = listOf(
        ReadingRange(BookRef(epistle.start), BookRef(epistle.end)),
        ReadingRange(BookRef(gospel.start), BookRef(gospel.end)),
    )
}

private fun mass(
    id: String,
    la: String,
    en: String,
    epStart: String,
    epEnd: String,
    gStart: String,
    gEnd: String,
) = TraditionalMass(id, la, en, MassLesson(epStart, epEnd), MassLesson(gStart, gEnd))

/**
 * 1962 Roman Missal — Epistle + Gospel for Sundays, major temporal days, and a few Class I feasts.
 * Weekdays use the preceding Sunday (feria). Not a full sanctoral or transferred-feast engine.
 */
object TraditionalMassLectionary {
    val advent1 = mass("advent-1", "Dominica I Adventus", "1st Sunday of Advent", "Rz 13, 11", "Rz 13, 14", "Łk 21, 25", "Łk 21, 33")
    val advent2 = mass("advent-2", "Dominica II Adventus", "2nd Sunday of Advent", "Rz 15, 4", "Rz 15, 13", "Mt 11, 2", "Mt 11, 10")
    val advent3 = mass("advent-3", "Dominica III Adventus", "3rd Sunday of Advent (Gaudete)", "Flp 4, 4", "Flp 4, 7", "J 1, 19", "J 1, 28")
    val advent4 = mass("advent-4", "Dominica IV Adventus", "4th Sunday of Advent", "1Kor 4, 1", "1Kor 4, 5", "Łk 3, 1", "Łk 3, 6")

    val christmas = mass("christmas", "In Nativitate Domini", "Christmas Day", "Hbr 1, 1", "Hbr 1, 12", "J 1, 1", "J 1, 14")
    val circumcision = mass("circumcision", "In Circumcisione Domini", "Circumcision of the Lord", "Tt 2, 11", "Tt 2, 15", "Łk 2, 21", "Łk 2, 21")
    val holyName = mass("holy-name", "Sanctissimi Nominis Iesu", "Most Holy Name of Jesus", "Dz 4, 8", "Dz 4, 12", "Łk 2, 21", "Łk 2, 21")
    val epiphany = mass("epiphany", "In Epiphania Domini", "Epiphany", "Iz 60, 1", "Iz 60, 6", "Mt 2, 1", "Mt 2, 12")
    val holyFamily = mass("holy-family", "Sanctae Familiae", "Holy Family", "Kol 3, 12", "Kol 3, 17", "Łk 2, 42", "Łk 2, 52")
    val epiphany2 = mass("epiphany-2", "Dominica II post Epiphaniam", "2nd Sunday after Epiphany", "Rz 12, 6", "Rz 12, 16", "J 2, 1", "J 2, 11")
    val epiphany3 = mass("epiphany-3", "Dominica III post Epiphaniam", "3rd Sunday after Epiphany", "Rz 12, 16", "Rz 12, 21", "Mt 8, 1", "Mt 8, 13")
    val epiphany4 = mass("epiphany-4", "Dominica IV post Epiphaniam", "4th Sunday after Epiphany", "Rz 13, 8", "Rz 13, 10", "Mt 8, 23", "Mt 8, 27")
    val epiphany5 = mass("epiphany-5", "Dominica V post Epiphaniam", "5th Sunday after Epiphany", "Kol 3, 12", "Kol 3, 17", "Mt 13, 24", "Mt 13, 30")
    val epiphany6 = mass("epiphany-6", "Dominica VI post Epiphaniam", "6th Sunday after Epiphany", "1Tes 1, 2", "1Tes 1, 10", "Mt 13, 31", "Mt 13, 35")

    val septuagesima = mass("septuagesima", "Dominica in Septuagesima", "Septuagesima", "1Kor 9, 24", "1Kor 10, 5", "Mt 20, 1", "Mt 20, 16")
    val sexagesima = mass("sexagesima", "Dominica in Sexagesima", "Sexagesima", "2Kor 11, 19", "2Kor 12, 9", "Łk 8, 4", "Łk 8, 15")
    val quinquagesima = mass("quinquagesima", "Dominica in Quinquagesima", "Quinquagesima", "1Kor 13, 1", "1Kor 13, 13", "Łk 18, 31", "Łk 18, 43")
    val ashWednesday = mass("ash-wednesday", "Feria IV Cinerum", "Ash Wednesday", "Jo 2, 12", "Jo 2, 19", "Mt 6, 16", "Mt 6, 21")
    val lent1 = mass("lent-1", "Dominica I in Quadragesima", "1st Sunday of Lent", "2Kor 6, 1", "2Kor 6, 10", "Mt 4, 1", "Mt 4, 11")
    val lent2 = mass("lent-2", "Dominica II in Quadragesima", "2nd Sunday of Lent", "1Tes 4, 1", "1Tes 4, 7", "Mt 17, 1", "Mt 17, 9")
    val lent3 = mass("lent-3", "Dominica III in Quadragesima", "3rd Sunday of Lent", "Ef 5, 1", "Ef 5, 9", "Łk 11, 14", "Łk 11, 28")
    val lent4 = mass("lent-4", "Dominica IV in Quadragesima", "4th Sunday of Lent (Laetare)", "Ga 4, 22", "Ga 4, 31", "J 6, 1", "J 6, 15")
    val passion = mass("passion", "Dominica de Passione", "Passion Sunday", "Hbr 9, 11", "Hbr 9, 15", "J 8, 46", "J 8, 59")
    val palm = mass("palm", "Dominica in Palmis", "Palm Sunday", "Flp 2, 5", "Flp 2, 11", "Mt 26, 36", "Mt 27, 60")
    val holyThursday = mass("holy-thursday", "Feria V in Cena Domini", "Holy Thursday", "1Kor 11, 20", "1Kor 11, 32", "J 13, 1", "J 13, 15")
    val goodFriday = mass("good-friday", "Feria VI in Parasceve", "Good Friday", "Oz 6, 1", "Oz 6, 6", "J 18, 1", "J 19, 42")
    val easter = mass("easter", "Dominica Resurrectionis", "Easter Sunday", "1Kor 5, 7", "1Kor 5, 8", "Mk 16, 1", "Mk 16, 7")
    val lowSunday = mass("low-sunday", "Dominica in Albis", "Low Sunday", "1J 5, 4", "1J 5, 10", "J 20, 19", "J 20, 31")
    val easter2 = mass("easter-2", "Dominica II post Pascha", "2nd Sunday after Easter", "1P 2, 21", "1P 2, 25", "J 10, 11", "J 10, 16")
    val easter3 = mass("easter-3", "Dominica III post Pascha", "3rd Sunday after Easter", "1P 2, 11", "1P 2, 19", "J 16, 16", "J 16, 22")
    val easter4 = mass("easter-4", "Dominica IV post Pascha", "4th Sunday after Easter", "Jk 1, 17", "Jk 1, 21", "J 16, 5", "J 16, 14")
    val easter5 = mass("easter-5", "Dominica V post Pascha", "5th Sunday after Easter", "Jk 1, 22", "Jk 1, 27", "J 16, 23", "J 16, 30")
    val ascension = mass("ascension", "In Ascensione Domini", "Ascension", "Dz 1, 1", "Dz 1, 11", "Mk 16, 14", "Mk 16, 20")
    val sundayAfterAscension = mass("sunday-after-ascension", "Dominica post Ascensionem", "Sunday after Ascension", "1P 4, 7", "1P 4, 11", "J 15, 26", "J 16, 4")
    val pentecost = mass("pentecost", "Dominica Pentecostes", "Pentecost", "Dz 2, 1", "Dz 2, 11", "J 14, 23", "J 14, 31")
    val trinity = mass("trinity", "Dominica Sanctissimae Trinitatis", "Trinity Sunday", "Rz 11, 33", "Rz 11, 36", "Mt 28, 18", "Mt 28, 20")
    val corpusChristi = mass("corpus-christi", "In festo Corporis Christi", "Corpus Christi", "1Kor 11, 23", "1Kor 11, 29", "J 6, 56", "J 6, 59")
    val sacredHeart = mass("sacred-heart", "Sacratissimi Cordis Iesu", "Sacred Heart", "Ef 3, 8", "Ef 3, 19", "J 19, 31", "J 19, 37")

    val afterPentecost = listOf(
        mass("pentecost-2", "Dominica II post Pentecosten", "2nd Sunday after Pentecost", "1J 3, 13", "1J 3, 18", "Łk 14, 16", "Łk 14, 24"),
        mass("pentecost-3", "Dominica III post Pentecosten", "3rd Sunday after Pentecost", "1P 5, 6", "1P 5, 11", "Łk 15, 1", "Łk 15, 10"),
        mass("pentecost-4", "Dominica IV post Pentecosten", "4th Sunday after Pentecost", "Rz 8, 18", "Rz 8, 23", "Łk 5, 1", "Łk 5, 11"),
        mass("pentecost-5", "Dominica V post Pentecosten", "5th Sunday after Pentecost", "1P 3, 8", "1P 3, 15", "Mt 5, 20", "Mt 5, 24"),
        mass("pentecost-6", "Dominica VI post Pentecosten", "6th Sunday after Pentecost", "Rz 6, 3", "Rz 6, 11", "Mk 8, 1", "Mk 8, 9"),
        mass("pentecost-7", "Dominica VII post Pentecosten", "7th Sunday after Pentecost", "Rz 6, 19", "Rz 6, 23", "Mt 7, 15", "Mt 7, 21"),
        mass("pentecost-8", "Dominica VIII post Pentecosten", "8th Sunday after Pentecost", "Rz 8, 12", "Rz 8, 17", "Łk 16, 1", "Łk 16, 9"),
        mass("pentecost-9", "Dominica IX post Pentecosten", "9th Sunday after Pentecost", "1Kor 10, 6", "1Kor 10, 13", "Łk 19, 41", "Łk 19, 47"),
        mass("pentecost-10", "Dominica X post Pentecosten", "10th Sunday after Pentecost", "1Kor 12, 2", "1Kor 12, 11", "Łk 18, 9", "Łk 18, 14"),
        mass("pentecost-11", "Dominica XI post Pentecosten", "11th Sunday after Pentecost", "1Kor 15, 1", "1Kor 15, 10", "Mk 7, 31", "Mk 7, 37"),
        mass("pentecost-12", "Dominica XII post Pentecosten", "12th Sunday after Pentecost", "2Kor 3, 4", "2Kor 3, 9", "Łk 10, 23", "Łk 10, 37"),
        mass("pentecost-13", "Dominica XIII post Pentecosten", "13th Sunday after Pentecost", "Ga 3, 16", "Ga 3, 22", "Łk 17, 11", "Łk 17, 19"),
        mass("pentecost-14", "Dominica XIV post Pentecosten", "14th Sunday after Pentecost", "Ga 5, 16", "Ga 5, 24", "Mt 6, 24", "Mt 6, 33"),
        mass("pentecost-15", "Dominica XV post Pentecosten", "15th Sunday after Pentecost", "Ga 5, 25", "Ga 6, 10", "Łk 7, 11", "Łk 7, 16"),
        mass("pentecost-16", "Dominica XVI post Pentecosten", "16th Sunday after Pentecost", "Ef 3, 13", "Ef 3, 21", "Łk 14, 1", "Łk 14, 11"),
        mass("pentecost-17", "Dominica XVII post Pentecosten", "17th Sunday after Pentecost", "Ef 4, 1", "Ef 4, 6", "Mt 22, 34", "Mt 22, 46"),
        mass("pentecost-18", "Dominica XVIII post Pentecosten", "18th Sunday after Pentecost", "1Kor 1, 4", "1Kor 1, 8", "Mt 9, 1", "Mt 9, 8"),
        mass("pentecost-19", "Dominica XIX post Pentecosten", "19th Sunday after Pentecost", "Ef 4, 23", "Ef 4, 28", "Mt 22, 1", "Mt 22, 14"),
        mass("pentecost-20", "Dominica XX post Pentecosten", "20th Sunday after Pentecost", "Ef 5, 15", "Ef 5, 21", "J 4, 46", "J 4, 53"),
        mass("pentecost-21", "Dominica XXI post Pentecosten", "21st Sunday after Pentecost", "Ef 6, 10", "Ef 6, 17", "Mt 18, 23", "Mt 18, 35"),
        mass("pentecost-22", "Dominica XXII post Pentecosten", "22nd Sunday after Pentecost", "Flp 1, 6", "Flp 1, 11", "Mt 22, 15", "Mt 22, 21"),
        mass("pentecost-23", "Dominica XXIII post Pentecosten", "23rd Sunday after Pentecost", "Flp 3, 17", "Flp 4, 3", "Mt 9, 18", "Mt 9, 26"),
        mass("pentecost-24", "Dominica XXIV et ultima post Pentecosten", "Last Sunday after Pentecost", "Kol 1, 9", "Kol 1, 14", "Mt 24, 15", "Mt 24, 35"),
    )

    val purification = mass("purification", "In Purificatione B.M.V.", "Purification (Candlemas)", "Ml 3, 1", "Ml 3, 4", "Łk 2, 22", "Łk 2, 32")
    val stJoseph = mass("st-joseph", "S. Ioseph Sponsi B.M.V.", "St Joseph", "Syr 45, 1", "Syr 45, 6", "Mt 1, 18", "Mt 1, 21")
    val annunciation = mass("annunciation", "In Annuntiatione B.M.V.", "Annunciation", "Iz 7, 10", "Iz 7, 15", "Łk 1, 26", "Łk 1, 38")
    val nativityJohn = mass("nativity-john", "In Nativitate S. Ioannis Baptistae", "Nativity of St John the Baptist", "Iz 49, 1", "Iz 49, 7", "Łk 1, 57", "Łk 1, 68")
    val peterPaul = mass("peter-paul", "Ss. Petri et Pauli Apostolorum", "Ss Peter and Paul", "Dz 12, 1", "Dz 12, 11", "Mt 16, 13", "Mt 16, 19")
    val assumption = mass("assumption", "In Assumptione B.M.V.", "Assumption", "Jdt 13, 22", "Jdt 13, 25", "Łk 1, 41", "Łk 1, 50")
    val christTheKing = mass("christ-the-king", "D.N. Iesu Christi Regis", "Christ the King", "Kol 1, 12", "Kol 1, 20", "J 18, 33", "J 18, 37")
    val allSaints = mass("all-saints", "Omnium Sanctorum", "All Saints", "Ap 7, 2", "Ap 7, 12", "Mt 5, 1", "Mt 5, 12")
    val allSouls = mass("all-souls", "In Commemoratione Omnium Fidelium Defunctorum", "All Souls", "1Kor 15, 51", "1Kor 15, 57", "J 5, 25", "J 5, 29")
    val immaculateConception = mass("immaculate-conception", "In Conceptione Immaculata B.M.V.", "Immaculate Conception", "Prz 8, 22", "Prz 8, 35", "Łk 1, 26", "Łk 1, 28")

    private fun fixedFeast(date: LocalDate): TraditionalMass? = when {
        date.month.number == 1 && date.day == 1 -> circumcision
        date.month.number == 1 && date.day == 6 -> epiphany
        date.month.number == 2 && date.day == 2 -> purification
        date.month.number == 3 && date.day == 19 -> stJoseph
        date.month.number == 3 && date.day == 25 -> annunciation
        date.month.number == 6 && date.day == 24 -> nativityJohn
        date.month.number == 6 && date.day == 29 -> peterPaul
        date.month.number == 8 && date.day == 15 -> assumption
        date.month.number == 11 && date.day == 1 -> allSaints
        date.month.number == 11 && date.day == 2 -> allSouls
        date.month.number == 12 && date.day == 8 -> immaculateConception
        date.month.number == 12 && date.day == 25 -> christmas
        else -> null
    }

    private fun epiphanySunday(n: Int): TraditionalMass = when (n) {
        1 -> holyFamily
        2 -> epiphany2
        3 -> epiphany3
        4 -> epiphany4
        5 -> epiphany5
        else -> epiphany6
    }

    fun massFor(date: LocalDate): TraditionalMass {
        val year = date.year
        val easterDay = easterDate(year)
        val sept = easterDay.plusDays(-63)
        val ash = easterDay.plusDays(-46)
        val palmSun = easterDay.plusDays(-7)
        val asc = easterDay.plusDays(39)
        val pent = easterDay.plusDays(49)
        val trin = easterDay.plusDays(56)
        val corpus = easterDay.plusDays(60)
        val heart = easterDay.plusDays(68)
        val advent = adventSunday(year)
        val king = christTheKingDate(year)
        val epiph = LocalDate(year, 1, 6)

        if (date == ash) return ashWednesday
        if (date == easterDay.plusDays(-3)) return holyThursday
        if (date == easterDay.plusDays(-2)) return goodFriday
        if (date == easterDay) return easter
        if (date == asc) return ascension
        if (date == pent) return pentecost
        if (date == corpus) return corpusChristi
        if (date == heart) return sacredHeart
        if (date == king) return christTheKing
        if (date == LocalDate(year, 12, 25)) return christmas
        if (date == LocalDate(year, 1, 6)) return epiphany
        if (date == holyNameDate(year)) return holyName

        val feast = fixedFeast(date)
        val sunday = previousOrSameSunday(date)
        val privilegedSunday =
            sunday >= advent || (sunday >= sept && sunday <= palmSun) || (sunday >= easterDay && sunday <= pent)

        if (feast != null && !privilegedSunday) return feast
        if (date.dayOfWeek != DayOfWeek.SUNDAY && feast != null) return feast

        return when {
            sunday >= advent -> {
                val n = ((advent.daysUntilSunday(sunday)) / 7) + 1
                when (n.coerceIn(1, 4)) {
                    1 -> advent1
                    2 -> advent2
                    3 -> advent3
                    else -> advent4
                }
            }
            sunday == LocalDate(year, 12, 25).let { previousOrSameSunday(it) } &&
            sunday.month.number == 12 && sunday.day > 25 -> holyName
            sunday > epiph && sunday < sept -> {
                val firstAfter = firstSundayOnOrAfter(epiph.plusDays(1))
                val n = (firstAfter.daysUntilSunday(sunday) / 7) + 1
                epiphanySunday(n.coerceAtLeast(1))
            }
            sunday == sept -> septuagesima
            sunday == easterDay.plusDays(-56) -> sexagesima
            sunday == easterDay.plusDays(-49) -> quinquagesima
            sunday == easterDay.plusDays(-42) -> lent1
            sunday == easterDay.plusDays(-35) -> lent2
            sunday == easterDay.plusDays(-28) -> lent3
            sunday == easterDay.plusDays(-21) -> lent4
            sunday == easterDay.plusDays(-14) -> passion
            sunday == palmSun -> palm
            sunday == easterDay.plusDays(7) -> lowSunday
            sunday == easterDay.plusDays(14) -> easter2
            sunday == easterDay.plusDays(21) -> easter3
            sunday == easterDay.plusDays(28) -> easter4
            sunday == easterDay.plusDays(35) -> easter5
            sunday == easterDay.plusDays(42) -> sundayAfterAscension
            sunday == trin -> trinity
            sunday > trin && sunday < advent -> {
                val first = trin.plusDays(7)
                val lastBeforeAdvent = advent.plusDays(-7)
                if (sunday == lastBeforeAdvent) {
                    afterPentecost.last()
                } else {
                    val n = (first.daysUntilSunday(sunday) / 7) + 2
                    afterPentecost[(n - 2).coerceIn(0, afterPentecost.lastIndex - 1)]
                }
            }
            else -> christmas
        }
    }

    private fun LocalDate.daysUntilSunday(later: LocalDate): Int {
        var n = 0
        var d = this
        while (d < later) {
            d = d.plusDays(1)
            n++
        }
        return n
    }
}

object DailyReadingTR {
    private val lock = Mutex()
    private var cachedDate: LocalDate? = null
    private var cachedUiLang: String? = null
    private var cachedFirst: String? = null
    private var cachedSecond: String? = null
    private var cachedContent: BibleContent? = null

    @OptIn(ExperimentalTime::class)
    suspend fun massForToday(config: Config): BibleContent? {
        lock.withLock {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            if (cachedContent != null &&
                cachedDate == today &&
                cachedUiLang == config.uiLang &&
                cachedFirst == config.firstBible &&
                cachedSecond == config.secondBible
            ) {
                return cachedContent
            }
            cachedDate = today
            cachedUiLang = config.uiLang
            cachedFirst = config.firstBible
            cachedSecond = config.secondBible
            val mass = TraditionalMassLectionary.massFor(today)
            val content = BibleContent(
                id = 3,
                name = "mass-${mass.id}",
                langs = mutableMapOf(),
            )
            try {
                loadBibleContent(
                    config = config,
                    ranges = mass.ranges(),
                    content = content,
                    title = mass.titleFor(config.uiLang),
                    addTitle = true,
                    addSubtitle = true,
                )
                content.nums = config.loadContentNums(content)
                cachedContent = content
            } catch (e: Exception) {
                println("Error loading traditional Mass readings ${mass.id}: ${e.message}")
                cachedContent = null
            }
            return cachedContent
        }
    }
}
