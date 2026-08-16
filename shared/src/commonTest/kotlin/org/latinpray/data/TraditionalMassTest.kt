package org.latinpray.data

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class TraditionalMassTest {
    @Test
    fun easterWesternDates() {
        assertEquals(LocalDate(2024, 3, 31), easterDate(2024))
        assertEquals(LocalDate(2025, 4, 20), easterDate(2025))
        assertEquals(LocalDate(2026, 4, 5), easterDate(2026))
    }

    @Test
    fun knownDays2026() {
        assertEquals("easter", TraditionalMassLectionary.massFor(LocalDate(2026, 4, 5)).id)
        assertEquals("ash-wednesday", TraditionalMassLectionary.massFor(LocalDate(2026, 2, 18)).id)
        assertEquals("advent-1", TraditionalMassLectionary.massFor(LocalDate(2026, 11, 29)).id)
        assertEquals("christmas", TraditionalMassLectionary.massFor(LocalDate(2026, 12, 25)).id)
        assertEquals("assumption", TraditionalMassLectionary.massFor(LocalDate(2026, 8, 15)).id)
        assertEquals("pentecost-12", TraditionalMassLectionary.massFor(LocalDate(2026, 8, 16)).id)
    }
}
