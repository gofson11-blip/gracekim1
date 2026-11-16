package com.gracekim.saju.core

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class LunarCalendarTest {

    private fun check(solar: LocalDate, y: Int, m: Int, d: Int, leap: Boolean = false) {
        assertEquals(LunarDate(y, m, d, leap), LunarCalendar.solarToLunar(solar), "양력 $solar")
        assertEquals(solar, LunarCalendar.lunarToSolar(y, m, d, leap), "음력 $y-$m-$d")
    }

    @Test
    fun `설날`() {
        check(LocalDate.of(2026, 2, 17), 2026, 1, 1)
        check(LocalDate.of(2025, 1, 29), 2025, 1, 1)
        check(LocalDate.of(2024, 2, 10), 2024, 1, 1)
        check(LocalDate.of(2000, 2, 5), 2000, 1, 1)
        check(LocalDate.of(1990, 1, 27), 1990, 1, 1)
    }

    @Test
    fun `추석`() {
        check(LocalDate.of(2026, 9, 25), 2026, 8, 15)
        check(LocalDate.of(2024, 9, 17), 2024, 8, 15)
    }

    @Test
    fun `윤달`() {
        check(LocalDate.of(2023, 3, 22), 2023, 2, 1, leap = true)
        check(LocalDate.of(2020, 5, 23), 2020, 4, 1, leap = true)
        check(LocalDate.of(2017, 6, 24), 2017, 5, 1, leap = true)
        assertEquals(2, LunarCalendar.leapMonthOf(2023))
        assertEquals(null, LunarCalendar.leapMonthOf(2024))
        assertEquals(6, LunarCalendar.leapMonthOf(2025))
    }

    @Test
    fun `연말 음력 11월 12월`() {
        // 2025-12-20 = 음력 2025년 11월 1일
        check(LocalDate.of(2025, 12, 20), 2025, 11, 1)
    }
}
