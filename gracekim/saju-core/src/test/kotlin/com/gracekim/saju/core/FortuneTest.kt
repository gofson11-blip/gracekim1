package com.gracekim.saju.core

import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FortuneTest {
    private fun chart(y: Int, m: Int, d: Int, h: Int, g: Gender) = SajuCalculator.calculate(
        BirthInput(gender = g, calendarType = CalendarType.SOLAR, year = y, month = m, day = d, time = LocalTime.of(h, 0))
    )

    @Test
    fun `합과 충 관계`() {
        assertTrue(Relations.isStemCombination(Stem.GAP, Stem.GI))
        assertTrue(Relations.isSixCombination(Branch.JA, Branch.CHUK))
        assertTrue(Relations.isSixCombination(Branch.O, Branch.MI))
        assertTrue(Relations.isTripleCombination(Branch.SHIN, Branch.JIN))
        assertTrue(Relations.isClash(Branch.JA, Branch.O))
        assertTrue(Relations.isWonjin(Branch.MI, Branch.JA))
    }

    @Test
    fun `일진 계산`() {
        assertEquals("무오", SajuCalculator.dayPillarOf(LocalDate.of(2000, 1, 1)).korean)
        assertEquals("기미", SajuCalculator.dayPillarOf(LocalDate.of(2000, 1, 2)).korean)
    }

    @Test
    fun `오늘의 운세와 궁합 점수 범위`() {
        val a = chart(1990, 5, 15, 10, Gender.FEMALE)
        val b = chart(1988, 11, 3, 22, Gender.MALE)
        var date = LocalDate.of(2026, 1, 1)
        repeat(60) {
            val f = DailyFortune.of(a, date)
            assertTrue(f.score in 40..98)
            date = date.plusDays(1)
        }
        val c = Compatibility.analyze(a, b)
        assertTrue(c.score in 30..99)
        assertTrue(c.points.isNotEmpty())
        assertEquals(c.score, Compatibility.analyze(a, b).score)
    }
}
