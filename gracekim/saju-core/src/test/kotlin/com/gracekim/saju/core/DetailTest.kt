package com.gracekim.saju.core

import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetailTest {
    private fun chart(y: Int, m: Int, d: Int, h: Int?, g: Gender = Gender.FEMALE) =
        SajuCalculator.calculate(BirthInput("", g, CalendarType.SOLAR, y, m, d, false, h?.let { LocalTime.of(it, 0) }))

    @Test
    fun `십신 그룹 개수는 일간을 뺀 글자 수`() {
        assertEquals(7, Detail.tenGroupCounts(chart(1995, 3, 15, 14)).values.sum())
        assertEquals(5, Detail.tenGroupCounts(chart(1995, 3, 15, null)).values.sum())
    }

    @Test
    fun `월별 운세 12달, 2026년은 경인월부터`() {
        val m = Detail.monthlyFortunes(chart(1995, 3, 15, 14), 2026)
        assertEquals(12, m.size)
        assertEquals("경인", m.first().pillar.korean)
        assertEquals(2, m.first().calendarMonth)
        assertEquals(1, m.last().calendarMonth)
        assertEquals(2027, m.last().calendarYear)
        assertEquals(2025, Detail.sajuYearOf(LocalDate.of(2026, 1, 20)))
        assertEquals(2026, Detail.sajuYearOf(LocalDate.of(2026, 11, 17)))
    }

    @Test
    fun `궁합 상세`() {
        val r = CompatibilityDetail.analyze(chart(1995, 3, 15, 14), chart(1992, 12, 10, 9, Gender.MALE))
        assertEquals(4, r.categories.size)
        assertTrue(r.categories.all { it.score in 40..98 })
        assertEquals(3, r.tips.size)
    }
}
