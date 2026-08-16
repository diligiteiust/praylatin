package org.latinpray.data.mass

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.latinpray.data.easterDate

data class Observance(
    val id: String,
    val date: LocalDate,
) {
    val rank: Int = parseRank(id)
    val kind: String = id.substringBefore(':')
    val isSancti: Boolean = kind == "sancti"
    val isTempora: Boolean = kind == "tempora" || kind == "commune"
    val isSunday: Boolean = Regex("""tempora:.*-0r*:""").containsMatchIn(id) ||
        id.contains("-0:") || id.contains("Nat1-0") || id.contains("Nat2-0") ||
        id.contains("10-DU")
}

private fun parseRank(id: String): Int {
    val parts = id.split(':')
    for (p in parts) {
        val d = p.takeWhile { it.isDigit() }
        if (d.isNotEmpty() && d.length == 1) return d.toInt()
    }
    return 4
}

private fun LocalDate.plusDays(n: Int) = plus(n, DateTimeUnit.DAY)

private fun firstSundayOnOrAfter(date: LocalDate): LocalDate {
    var d = date
    while (d.dayOfWeek != DayOfWeek.SUNDAY) d = d.plusDays(1)
    return d
}

internal fun adventSunday(year: Int): LocalDate = firstSundayOnOrAfter(LocalDate(year, 11, 27))

internal fun christTheKingDate(year: Int): LocalDate {
    var d = LocalDate(year, 10, 31)
    while (d.dayOfWeek != DayOfWeek.SUNDAY) d = d.plusDays(-1)
    return d
}

internal fun holyFamilyDate(year: Int): LocalDate = firstSundayOnOrAfter(LocalDate(year, 1, 6).plusDays(1))

internal fun holyNameDate(year: Int): LocalDate {
    var d = LocalDate(year, 1, 1)
    while (d.day <= 7) {
        if (d.dayOfWeek == DayOfWeek.SUNDAY && d.day in listOf(1, 6, 7)) return LocalDate(year, 1, 2)
        if (d.dayOfWeek == DayOfWeek.SUNDAY) return d
        d = d.plusDays(1)
    }
    return LocalDate(year, 1, 2)
}

internal fun emberWednesdaySeptember(year: Int): LocalDate {
    var d = LocalDate(year, 9, 1)
    while (d.month.number == 9) {
        if (d.dayOfWeek == DayOfWeek.SUNDAY && d.day in 15..21) break
        d = d.plusDays(1)
    }
    return d.plusDays(3)
}

internal fun sundayInChristmasOctave(year: Int): LocalDate? {
    var d = LocalDate(year, 12, 26)
    while (d.year == year) {
        if (d.dayOfWeek == DayOfWeek.SUNDAY) return d
        d = d.plusDays(1)
    }
    return null
}

private fun insertBlock(
    map: MutableMap<LocalDate, MutableList<Observance>>,
    start: LocalDate,
    block: List<List<String>>,
    reverse: Boolean = false,
    overwrite: Boolean = true,
    stopExclusive: LocalDate? = null,
) {
    val days = if (reverse) block.asReversed() else block
    for ((i, ids) in days.withIndex()) {
        if (ids.isEmpty()) continue
        val date = start.plusDays(if (reverse) -i else i)
        if (stopExclusive != null && date >= stopExclusive) break
        if (!overwrite && map[date]?.isNotEmpty() == true) break
        val list = ids.map { Observance(it, date) }.toMutableList()
        if (overwrite) map[date] = list else map.getOrPut(date) { mutableListOf() }.addAll(list)
    }
}

private fun sanctiFor(date: LocalDate): List<Observance> {
    val key = date.toString().substring(5) // MM-DD
    return OrdoIds.sancti.filter { it.startsWith("sancti:$key") }.map { Observance(it, date) }
}

data class DayOrdo(
    val mass: Observance,
    val saint: Observance? = null,
) {
    fun items(): List<Observance> =
        listOfNotNull(mass, saint?.takeIf { it.id != mass.id })
}

/**
 * 1962 ordo: temporal blocks + sanctoral + occurrence/transfer.
 * Calendar construction follows Missale Meum / Divinum Officium (MIT).
 */
object LiturgicalOrdo {

    fun celebration(date: LocalDate): Observance = forDay(date).mass

    fun forDay(date: LocalDate): DayOrdo {
        val year = date.year
        val easter = easterDate(year)
        val sept = easter.plusDays(-63)
        val advent = adventSunday(year)
        val lastPent = advent.plusDays(-7)
        val satBeforeLast = lastPent.plusDays(-1)
        val map = mutableMapOf<LocalDate, MutableList<Observance>>()

        insertBlock(map, holyFamilyDate(year), OrdoBlocks.postEpiphany)
        insertBlock(map, sept, OrdoBlocks.preLentToPentecost)
        insertBlock(map, satBeforeLast, OrdoBlocks.postEpiphany, reverse = true, overwrite = false)
        insertBlock(map, lastPent, OrdoBlocks.week24Pentecost)
        insertBlock(map, advent, OrdoBlocks.advent, stopExclusive = LocalDate(year, 12, 24))
        insertBlock(map, emberWednesdaySeptember(year), OrdoBlocks.emberSeptember)

        map[holyNameDate(year)] = mutableListOf(Observance("tempora:Nat2-0:2:w", holyNameDate(year)))
        map[christTheKingDate(year)] = mutableListOf(Observance("sancti:10-DU:1:w", christTheKingDate(year)))
        sundayInChristmasOctave(year)?.let { s ->
            map[s] = mutableListOf(Observance("tempora:Nat1-0:2:w", s))
        }
        for (day in 29..31) {
            val d = LocalDate(year, 12, day)
            if (map[d].isNullOrEmpty()) {
                map[d] = mutableListOf(Observance("tempora:Nat1-1:2:w", d))
            }
        }

        val tempora = map[date].orEmpty()
        val sancti = sanctiFor(date).toMutableList()
        val leap = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        if (leap && date.month.number == 2 && date.day == 24) {
            sancti.removeAll { it.id.startsWith("sancti:02-24") }
        }
        if (leap && date.month.number == 2 && date.day == 25) {
            sancti += Observance("sancti:02-24:2:r", date)
        }
        if (leap && date.month.number == 2 && date.day == 27) {
            sancti.removeAll { it.id.startsWith("sancti:02-27") }
        }
        if (leap && date.month.number == 2 && date.day == 28) {
            sancti += Observance("sancti:02-27:3:w", date)
        }
        if (date.month.number == 11 && date.day == 2 && date.dayOfWeek == DayOfWeek.SUNDAY) {
            sancti.removeAll { it.id.startsWith("sancti:11-02") }
        }
        if (date.month.number == 11 && date.day == 3) {
            val nov2 = LocalDate(year, 11, 2)
            if (nov2.dayOfWeek == DayOfWeek.SUNDAY) {
                sancti += Observance("sancti:11-02m1:1:b", date)
            }
        }

        val observances = (tempora + sancti).toMutableList()
        val mass = pickMass(date, observances, tempora, map, advent, easter)
        val saint = sancti.filter { it.rank <= 3 }.minByOrNull { it.rank }
            ?.takeIf { it.id != mass.id }
        return DayOrdo(mass, saint)
    }

    private fun pickMass(
        date: LocalDate,
        observances: List<Observance>,
        tempora: List<Observance>,
        map: Map<LocalDate, List<Observance>>,
        advent: LocalDate,
        easter: LocalDate,
    ): Observance {
        if (date.month.number == 12 && date.day == 24) {
            return Observance("sancti:12-24:1:v", date)
        }
        if (date.month.number == 12 && date.day == 8) {
            return Observance("sancti:12-08:1:w", date)
        }

        val holyWeekAsh = setOf(
            "tempora:Quadp3-3:1:v",
            "tempora:Quad6-1:1:v",
            "tempora:Quad6-2:1:v",
            "tempora:Quad6-3:1:v",
            "tempora:Quad6-4r:1:w",
            "tempora:Quad6-5r:1:bv",
            "tempora:Quad6-6r:1:vw",
            "tempora:Pasc0-0:1:w",
        )
        observances.firstOrNull { it.id in holyWeekAsh }?.let { return it }

        val sundayMass = tempora.firstOrNull { it.isTempora && (it.rank <= 2 || it.id.contains("-0")) }
            ?: sundayOfWeek(date, map)

        // Sundays: the Mass of the Sunday, not the occurring saint.
        if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            sundayMass?.let { return it }
        }

        val firstClass = observances.filter { it.rank == 1 }
        if (firstClass.size > 1) {
            return firstClass.minBy { precedence(it) }
        }
        if (firstClass.size == 1) return firstClass.first()

        val lent = observances.filter {
            it.id.contains(":Quad") && !it.id.contains("Quadp1") &&
                !it.id.contains("Quadp2") && it.id.startsWith("tempora:")
        }
        val sanctiObs = observances.filter { it.isSancti }
        if (lent.isNotEmpty()) {
            val L = lent.minBy { it.rank }
            val S = sanctiObs.minByOrNull { it.rank }
            return if (S != null && S.rank < L.rank) S else L
        }

        val emberOrAdv = observances.firstOrNull {
            it.id.contains("Adv") && it.rank <= 3 ||
                it.id.contains("093-") ||
                it.id.contains("Quad1-3") || it.id.contains("Quad1-5") || it.id.contains("Quad1-6") ||
                it.id.contains("Pasc7-3") || it.id.contains("Pasc7-5") || it.id.contains("Pasc7-6")
        }
        val sanctiSame = sanctiObs.minByOrNull { it.rank }
        if (emberOrAdv != null && date.dayOfWeek != DayOfWeek.SUNDAY) {
            return if (sanctiSame != null && sanctiSame.rank <= emberOrAdv.rank) sanctiSame else emberOrAdv
        }

        if (date.dayOfWeek == DayOfWeek.SATURDAY) {
            val ranks = observances.map { it.rank }.toSet()
            if (ranks.isEmpty() || ranks == setOf(4)) {
                return saturdayBmv(date, advent, easter)
            }
        }

        return observances.minWithOrNull(compareBy<Observance> { it.rank }.thenBy { if (it.isSancti) 0 else 1 })
            ?: sundayMass
            ?: Observance("tempora:Pent24-0:2:g", date)
    }

    private fun saturdayBmv(date: LocalDate, advent: LocalDate, easter: LocalDate): Observance {
        val id = when {
            date >= advent -> "commune:C10a:4:v"
            date.month.number == 12 && date.day >= 25 || date.month.number == 1 ||
                date.month.number == 2 && date.day < 2 -> "commune:C10b:4:w"
            date < easter.plusDays(-4) && (date.month.number > 2 || date.month.number == 2 && date.day >= 2) ->
                "commune:C10c:4:w"
            date >= easter && date < easter.plusDays(56) -> "commune:C10Pasc:4:w"
            else -> "commune:C10t:4:w"
        }
        return Observance(id, date)
    }

    private fun sundayOfWeek(date: LocalDate, map: Map<LocalDate, List<Observance>>): Observance? {
        var d = date
        while (d.dayOfWeek != DayOfWeek.SUNDAY) d = d.plusDays(-1)
        return map[d]?.firstOrNull()
    }

    private fun precedence(o: Observance): Int {
        val table = listOf(
            "sancti:12-25", "tempora:Pasc0-0", "tempora:Pasc7-0", "tempora:Quad6-4",
            "tempora:Quad6-5", "tempora:Quad6-6", "sancti:01-06", "tempora:Pasc5-4",
            "tempora:Pent01-0", "tempora:Pent01-4", "tempora:Pent02-5", "sancti:10-DU",
            "sancti:12-08", "sancti:08-15", "sancti:12-24", "sancti:01-01",
        )
        val i = table.indexOfFirst { o.id.startsWith(it) }
        return if (i >= 0) i else 100 + o.rank
    }
}
