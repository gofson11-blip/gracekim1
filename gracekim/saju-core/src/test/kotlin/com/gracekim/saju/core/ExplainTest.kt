package com.gracekim.saju.core

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExplainTest {
    @Test
    fun `조사`() {
        assertEquals("용을", Explain.josa("용", "을", "를"))
        assertEquals("돼지를", Explain.josa("돼지", "을", "를"))
        assertEquals("풀과 꽃을", Explain.josa("풀과 꽃", "을", "를"))
    }

    @Test
    fun `모든 글자 설명이 나온다`() {
        val c = SajuCalculator.calculate(BirthInput("", Gender.FEMALE, CalendarType.SOLAR, 1995, 3, 15, false, LocalTime.of(14, 30)))
        for (col in 0..3) for (stem in listOf(true, false)) {
            val d = assertNotNull(Explain.charDetail(c, col, stem))
            assertTrue(d.lines.size >= 3)
        }
        assertTrue(Explain.charDetail(c, 1, true)!!.lines.any { it.contains("'나'(일간)") })
        val noTime = SajuCalculator.calculate(BirthInput("", Gender.FEMALE, CalendarType.SOLAR, 1995, 3, 15, false, null))
        assertNull(Explain.charDetail(noTime, 0, true))
        Stem.entries.forEach { assertEquals(3, Explain.persona(it).keywords.size) }
    }
}
