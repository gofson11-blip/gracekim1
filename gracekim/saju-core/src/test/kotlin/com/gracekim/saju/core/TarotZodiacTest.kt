package com.gracekim.saju.core

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TarotZodiacTest {
    @Test
    fun `타로 22장, 같은 날 같은 카드`() {
        assertEquals(22, Tarot.cards.size)
        assertEquals((0..21).toList(), Tarot.cards.map { it.number })
        val d = LocalDate.of(2026, 11, 17)
        assertEquals(Tarot.draw("a", d, 1), Tarot.draw("a", d, 1))
        assertEquals("XXI", Tarot.cards[21].roman)
    }

    @Test
    fun `띠별 운세 12띠`() {
        val list = Zodiac.daily(LocalDate.of(2026, 11, 17))
        assertEquals(12, list.size)
        assertTrue(list.all { it.score in 45..98 })
        assertEquals(listOf(1971, 1983, 1995, 2007, 2019), Zodiac.birthYears(Branch.HAE, 2026, 5))
        assertEquals(12, Zodiac.yearly(2027).size)
    }
}
