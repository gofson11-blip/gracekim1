package com.gracekim.saju.core

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YearReportTest {
    @Test
    fun `2027년은 정미년 붉은 양의 해`() {
        val c = SajuCalculator.calculate(BirthInput("", Gender.MALE, CalendarType.SOLAR, 1971, 8, 21, false, LocalTime.of(12, 0)))
        val r = YearReport.build(c, 2027, 2027 - 1971 + 1)
        assertEquals("정미", r.pillar.korean)
        assertTrue(r.title.contains("붉은 양"))
        assertEquals(12, r.months.size)
        assertEquals(3, r.bestMonths.size)
        assertEquals(2, r.cautionMonths.size)
        assertTrue(r.paragraphs.size >= 4)
    }
}
