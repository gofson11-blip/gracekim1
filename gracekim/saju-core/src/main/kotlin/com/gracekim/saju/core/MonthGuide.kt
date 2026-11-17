package com.gracekim.saju.core

import java.time.LocalDate
import java.time.YearMonth

/** 복주머니로 여는 '이달의 행운 처방전' */
object MonthGuide {

    data class Day(val date: LocalDate, val score: Int, val headline: String)

    data class Guide(
        val yearMonth: YearMonth,
        val goodDays: List<Day>,
        val cautionDay: Day,
        val monthText: String,
        val lucky: Detail.Lucky,
        val advice: String,
    )

    fun of(chart: SajuChart, ym: YearMonth): Guide {
        val days = (1..ym.lengthOfMonth()).map { d ->
            val date = ym.atDay(d)
            val f = DailyFortune.of(chart, date)
            Day(date, f.score, f.headline)
        }
        val good = days.sortedWith(compareByDescending<Day> { it.score }.thenBy { it.date }).take(3).sortedBy { it.date }
        val caution = days.minWith(compareBy<Day> { it.score }.thenBy { it.date })

        // 이번 달(절기 기준 대략 그 달 15일)의 월운
        val mid = ym.atDay(15)
        val sajuYear = Detail.sajuYearOf(mid)
        val month = Detail.monthlyFortunes(chart, sajuYear).firstOrNull { it.calendarMonth == ym.monthValue }
        val monthText = month?.let { "${it.pillar.korean}월 · ${it.god.korean}(${Explain.tenGodShort(it.god)}) — ${it.text}" } ?: ""
        val advice = month?.let { Detail.dailyTodo(it.god) }?.let { "이번 달에는 '${it.first}'에 힘을 쓰고, '${it.second}'은(는) 조심하세요." } ?: ""

        return Guide(ym, good, caution, monthText, Detail.lucky(chart), advice)
    }
}
