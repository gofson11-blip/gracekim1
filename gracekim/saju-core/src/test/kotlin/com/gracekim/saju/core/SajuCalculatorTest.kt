package com.gracekim.saju.core

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SajuCalculatorTest {

    private fun solar(y: Int, m: Int, d: Int, h: Int?, min: Int = 0, correction: Boolean = false, gender: Gender = Gender.MALE) =
        SajuCalculator.calculate(
            BirthInput(
                gender = gender,
                calendarType = CalendarType.SOLAR,
                year = y, month = m, day = d,
                time = h?.let { LocalTime.of(it, min) },
                applyLongitudeCorrection = correction,
            )
        )

    @Test
    fun `2000년 1월 1일은 무오일이고 기묘년 병자월`() {
        val c = solar(2000, 1, 1, 12)
        assertEquals("무오", c.day.korean)
        assertEquals("기묘", c.year.korean) // 입춘 전이므로 1999년
        assertEquals("병자", c.month.korean)
        assertEquals("무오", c.hour!!.korean) // 무일 오시
    }

    @Test
    fun `자시 시주와 23시 다음날 처리`() {
        assertEquals("임자", solar(2000, 1, 1, 0, 30).hour!!.korean)
        val late = solar(1999, 12, 31, 23, 30)
        assertEquals("무오", late.day.korean)
        assertEquals("임자", late.hour!!.korean)
    }

    @Test
    fun `2024년 입춘(2월 4일 17시 27분 무렵) 전후로 년주와 월주가 바뀐다`() {
        val before = solar(2024, 2, 4, 16)
        assertEquals("계묘", before.year.korean)
        assertEquals("을축", before.month.korean)
        val after = solar(2024, 2, 4, 19)
        assertEquals("갑진", after.year.korean)
        assertEquals("병인", after.month.korean)
    }

    @Test
    fun `2026년은 병오년, 입춘 직후는 경인월`() {
        val c = solar(2026, 2, 10, 12)
        assertEquals("병오", c.year.korean)
        assertEquals("경인", c.month.korean)
        assertEquals("병오", SajuCalculator.yearPillarOf(2026).korean)
    }

    @Test
    fun `절기 시각이 알려진 값과 30분 이내로 맞는다`() {
        // 2024 입춘 2024-02-04 17:27 KST, 2025 동지 2025-12-22 00:03 KST
        val ipchun = Astronomy.solarTermJd(315.0, Astronomy.jdFromInstant(LocalDate.of(2024, 2, 4).atStartOfDay(ZoneOffset.UTC).toInstant()))
        val expected = Astronomy.jdFromInstant(java.time.LocalDateTime.of(2024, 2, 4, 17, 27).toInstant(ZoneOffset.ofHours(9)))
        assertTrue(kotlin.math.abs(ipchun - expected) * 1440 < 30, "입춘 오차 ${(ipchun - expected) * 1440}분")

        val dongji = Astronomy.solarTermJd(270.0, Astronomy.jdFromInstant(LocalDate.of(2025, 12, 21).atStartOfDay(ZoneOffset.UTC).toInstant()))
        val expected2 = Astronomy.jdFromInstant(java.time.LocalDateTime.of(2025, 12, 22, 0, 3).toInstant(ZoneOffset.ofHours(9)))
        assertTrue(kotlin.math.abs(dongji - expected2) * 1440 < 30, "동지 오차 ${(dongji - expected2) * 1440}분")
    }

    @Test
    fun `시간을 모르면 시주가 없다`() {
        val c = solar(1990, 5, 15, null)
        assertNull(c.hour)
        assertEquals(6, c.elementCounts.values.sum())
    }

    @Test
    fun `대운은 월주에서 순행 또는 역행한다`() {
        // 2024 갑진년(양) 남자 → 순행, 여자 → 역행
        val male = solar(2024, 6, 1, 12, gender = Gender.MALE)
        val female = solar(2024, 6, 1, 12, gender = Gender.FEMALE)
        assertTrue(male.isDaeunForward)
        assertTrue(!female.isDaeunForward)
        assertEquals(male.month.plus(1), male.daeun.first().pillar)
        assertEquals(female.month.plus(-1), female.daeun.first().pillar)
        assertTrue(male.daeunStartAge in 1..10)
    }

    @Test
    fun `십신 계산`() {
        assertEquals(TenGod.BIGYEON, TenGod.of(Stem.GAP, Stem.GAP))
        assertEquals(TenGod.GEOPJAE, TenGod.of(Stem.GAP, Stem.EUL))
        assertEquals(TenGod.SIKSIN, TenGod.of(Stem.GAP, Stem.BYEONG))
        assertEquals(TenGod.JEONGJAE, TenGod.of(Stem.GAP, Stem.GI))
        assertEquals(TenGod.PYEONGWAN, TenGod.of(Stem.GAP, Stem.GYEONG))
        assertEquals(TenGod.JEONGIN, TenGod.of(Stem.GAP, Stem.GYE))
    }

    @Test
    fun `음력 입력도 계산된다`() {
        val c = SajuCalculator.calculate(
            BirthInput(gender = Gender.FEMALE, calendarType = CalendarType.LUNAR, year = 2026, month = 1, day = 1, time = null)
        )
        assertEquals(LocalDate.of(2026, 2, 17), c.solarDate)
    }

    @Test
    fun `없는 윤달은 오류`() {
        assertFailsWith<IllegalArgumentException> { LunarCalendar.lunarToSolar(2026, 3, 1, true) }
    }
}
