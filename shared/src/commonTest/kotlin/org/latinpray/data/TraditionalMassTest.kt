package org.latinpray.data

import kotlinx.datetime.LocalDate
import org.latinpray.data.mass.LiturgicalOrdo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraditionalMassTest {
    @Test
    fun easterWesternDates() {
        assertEquals(LocalDate(2024, 3, 31), easterDate(2024))
        assertEquals(LocalDate(2025, 4, 20), easterDate(2025))
        assertEquals(LocalDate(2026, 4, 5), easterDate(2026))
    }

    @Test
    fun ordoKnownDays2026() {
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 4, 5)).id.startsWith("tempora:Pasc0-0"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 2, 18)).id.contains("Quadp3-3"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 11, 29)).id.contains("Adv1-0"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 12, 25)).id.contains("12-25"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 8, 15)).id.contains("08-15"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 8, 16)).id.contains("Pent12-0"))
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 12, 8)).id.contains("12-08"))
    }
}
