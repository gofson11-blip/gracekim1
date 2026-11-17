package com.gracekim.saju.core

import java.time.LocalTime
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MonthGuideTest {
    @Test
    fun `이달의 좋은 날 3개`() {
        val c = SajuCalculator.calculate(BirthInput("", Gender.FEMALE, CalendarType.SOLAR, 1995, 3, 15, false, LocalTime.of(14, 30)))
        val g = MonthGuide.of(c, YearMonth.of(2026, 11))
        assertEquals(3, g.goodDays.size)
        assertTrue(g.goodDays.all { it.date.monthValue == 11 })
        assertTrue(g.goodDays.all { it.score >= g.cautionDay.score })
        assertTrue(g.monthText.isNotBlank())
    }
}
