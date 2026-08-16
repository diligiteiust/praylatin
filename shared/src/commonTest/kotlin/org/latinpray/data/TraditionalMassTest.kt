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
    fun sunday16Aug2026IsTwelfthAfterPentecostPlusJoachim() {
        val day = LiturgicalOrdo.forDay(LocalDate(2026, 8, 16))
        assertTrue(day.mass.id.contains("Pent12"), "mass=${day.mass.id}")
        assertTrue(day.saint?.id?.contains("08-16") == true, "saint=${day.saint?.id}")
    }

    @Test
    fun ashWednesday2026() {
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 2, 18)).id.contains("Quadp3-3"))
    }

    @Test
    fun advent1_2026() {
        assertTrue(LiturgicalOrdo.celebration(LocalDate(2026, 11, 29)).id.contains("Adv1-0"))
    }
}
