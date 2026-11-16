package com.gracekim.saju.core

import java.time.LocalDate
import kotlin.math.floor

data class LunarDate(val year: Int, val month: Int, val day: Int, val isLeapMonth: Boolean) {
    override fun toString(): String =
        "${year}년 ${if (isLeapMonth) "윤" else ""}${month}월 ${day}일"
}

/**
 * 한국 음력(한국 표준시 UTC+9 기준) 양력 ↔ 음력 변환.
 * 천문 계산(합삭·중기)으로 만들며 1900~2100년 범위를 대상으로 합니다.
 */
object LunarCalendar {
    private data class LunarMonth(
        val year: Int,
        val month: Int,
        val isLeap: Boolean,
        val startEpochDay: Long,
        val length: Int,
    )

    private val spanCache = HashMap<Int, List<LunarMonth>>()

    /** 해당 날짜 00:00(KST) 시점의 태양 황경 구간(30도 단위). */
    private fun zhongqiIndex(epochDay: Long): Int =
        floor(Astronomy.solarLongitude(Astronomy.jdOfKstMidnight(epochDay)) / 30.0).toInt()

    private fun winterSolsticeEpochDay(year: Int): Long {
        val approx = Astronomy.jdFromInstant(
            LocalDate.of(year, 12, 21).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()
        )
        return Astronomy.kstEpochDay(Astronomy.solarTermJd(270.0, approx))
    }

    /**
     * (solsticeYear-1)년 동지가 든 달(11월)부터 solsticeYear 동지가 든 달 직전까지의 달 목록.
     */
    @Synchronized
    private fun span(solsticeYear: Int): List<LunarMonth> = spanCache.getOrPut(solsticeYear) {
        val kStart = Astronomy.newMoonIndexOnOrBefore(winterSolsticeEpochDay(solsticeYear - 1))
        val kEnd = Astronomy.newMoonIndexOnOrBefore(winterSolsticeEpochDay(solsticeYear))
        val starts = (kStart..kEnd).map { Astronomy.kstEpochDay(Astronomy.newMoonJd(it)) }
        val count = (kEnd - kStart).toInt()
        val hasLeap = count == 13

        val result = ArrayList<LunarMonth>(count)
        var month = 11
        var year = solsticeYear - 1
        var leapAssigned = false
        for (i in 0 until count) {
            val start = starts[i]
            val next = starts[i + 1]
            var isLeap = false
            if (i > 0) {
                if (hasLeap && !leapAssigned && zhongqiIndex(start) == zhongqiIndex(next)) {
                    isLeap = true
                    leapAssigned = true
                } else {
                    month = month % 12 + 1
                    if (month == 1) year = solsticeYear
                }
            }
            result.add(LunarMonth(year, month, isLeap, start, (next - start).toInt()))
        }
        result
    }

    fun solarToLunar(date: LocalDate): LunarDate {
        val day = date.toEpochDay()
        for (sy in listOf(date.year, date.year + 1)) {
            val m = span(sy).firstOrNull { day >= it.startEpochDay && day < it.startEpochDay + it.length }
            if (m != null) return LunarDate(m.year, m.month, (day - m.startEpochDay).toInt() + 1, m.isLeap)
        }
        error("음력 변환 실패: $date")
    }

    /** 음력 → 양력. 존재하지 않는 날짜(예: 없는 윤달, 30일이 없는 달)는 IllegalArgumentException. */
    fun lunarToSolar(year: Int, month: Int, day: Int, isLeapMonth: Boolean): LocalDate {
        require(month in 1..12) { "월은 1~12 사이여야 합니다." }
        val sy = if (month >= 11) year + 1 else year
        val m = span(sy).firstOrNull { it.year == year && it.month == month && it.isLeap == isLeapMonth }
            ?: throw IllegalArgumentException(
                if (isLeapMonth) "${year}년에는 윤${month}월이 없습니다." else "음력 날짜를 찾을 수 없습니다."
            )
        require(day in 1..m.length) { "음력 ${year}년 ${if (isLeapMonth) "윤" else ""}${month}월은 ${m.length}일까지 있습니다." }
        return LocalDate.ofEpochDay(m.startEpochDay + day - 1)
    }

    /** 해당 음력 연도의 윤달 (없으면 null). */
    fun leapMonthOf(year: Int): Int? =
        (span(year) + span(year + 1)).firstOrNull { it.year == year && it.isLeap }?.month
}
