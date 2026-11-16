package com.gracekim.saju.core

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

enum class Gender(val korean: String) { MALE("남"), FEMALE("여") }

enum class CalendarType { SOLAR, LUNAR }

data class BirthInput(
    val name: String = "",
    val gender: Gender,
    val calendarType: CalendarType,
    val year: Int,
    val month: Int,
    val day: Int,
    val isLeapMonth: Boolean = false,
    /** null 이면 태어난 시간을 모르는 것 */
    val time: LocalTime?,
    /** 서울 기준 진태양시 보정(-32분) 적용 여부 */
    val applyLongitudeCorrection: Boolean = true,
)

data class DaeunPeriod(val startAge: Int, val pillar: Pillar)

data class SajuChart(
    val input: BirthInput,
    val solarDate: LocalDate,
    val lunarDate: LunarDate,
    val year: Pillar,
    val month: Pillar,
    val day: Pillar,
    /** 시간을 모르면 null */
    val hour: Pillar?,
    val isDaeunForward: Boolean,
    val daeunStartAge: Int,
    val daeun: List<DaeunPeriod>,
) {
    val dayMaster: Stem get() = day.stem

    val pillars: List<Pillar> get() = listOfNotNull(hour, day, month, year)

    /** 천간·지지에 드러난 오행 개수 */
    val elementCounts: Map<Element, Int>
        get() {
            val map = Element.entries.associateWith { 0 }.toMutableMap()
            for (p in pillars) {
                map[p.stem.element] = map.getValue(p.stem.element) + 1
                map[p.branch.element] = map.getValue(p.branch.element) + 1
            }
            return map
        }

    /**
     * 간이 신강/신약 점수. 일간을 돕는 글자(비겁·인성) 비율, 월지는 2배 가중.
     * 0.5 이상이면 신강으로 봅니다.
     */
    val strengthRatio: Double
        get() {
            val me = dayMaster.element
            fun helps(e: Element) = e == me || e == me.generatedBy
            var support = 0.0
            var total = 0.0
            val chars = buildList {
                listOfNotNull(hour, year, month).forEach { add(it.stem.element to 1.0) }
                listOfNotNull(hour, day, year).forEach { add(it.branch.element to 1.0) }
                add(month.branch.element to 2.0) // 월지(계절)는 2배 가중
            }
            for ((e, w) in chars) {
                total += w
                if (helps(e)) support += w
            }
            return if (total == 0.0) 0.5 else support / total
        }

    val isStrong: Boolean get() = strengthRatio >= 0.5

    fun daeunAt(age: Int): DaeunPeriod? = daeun.lastOrNull { age >= it.startAge }
}

object SajuCalculator {
    /** 서울(동경 약 127°)과 표준 자오선(135°)의 시차 */
    const val SEOUL_LONGITUDE_CORRECTION_MINUTES = 32L

    private val KST = ZoneOffset.ofHours(9)

    /** 입춘 등 12절(節)의 황경: 315, 345, 15, 45, ... */
    private fun jeolIndex(longitude: Double): Int {
        val fromIpchun = ((longitude - 315.0) % 360.0 + 360.0) % 360.0
        return floor(fromIpchun / 30.0).toInt().coerceIn(0, 11)
    }

    fun calculate(input: BirthInput): SajuChart {
        val solarDate = when (input.calendarType) {
            CalendarType.SOLAR -> LocalDate.of(input.year, input.month, input.day)
            CalendarType.LUNAR -> LunarCalendar.lunarToSolar(input.year, input.month, input.day, input.isLeapMonth)
        }
        require(solarDate.year in 1900..2100) { "1900년 ~ 2100년 사이의 날짜만 지원합니다." }
        val lunarDate = LunarCalendar.solarToLunar(solarDate)

        val clockTime = input.time ?: LocalTime.NOON
        val birthLocal = LocalDateTime.of(solarDate, clockTime)
        val birthJd = Astronomy.jdFromInstant(birthLocal.toInstant(KST))

        // 년주·월주: 절기(태양 황경) 기준
        val longitude = Astronomy.solarLongitude(birthJd)
        val monthOffset = jeolIndex(longitude) // 0 = 인월(입춘~경칩)
        val sajuYear = if (monthOffset >= 10 && solarDate.monthValue <= 2) solarDate.year - 1 else solarDate.year
        val yearPillar = Pillar.ofIndex(sajuYear - 4)
        val monthStem = Stem.entries[Math.floorMod(yearPillar.stem.ordinal % 5 * 2 + 2 + monthOffset, 10)]
        val monthBranch = Branch.entries[(2 + monthOffset) % 12]
        val monthPillar = Pillar.of(monthStem, monthBranch)

        // 일주·시주: 진태양시 보정 후, 23시(자시)부터 다음 날로 봅니다.
        val corrected = if (input.applyLongitudeCorrection)
            birthLocal.minusMinutes(SEOUL_LONGITUDE_CORRECTION_MINUTES) else birthLocal
        var dayDate = corrected.toLocalDate()
        if (input.time != null && corrected.hour == 23) dayDate = dayDate.plusDays(1)
        if (input.time == null) dayDate = solarDate
        val dayPillar = dayPillarOf(dayDate)

        val hourPillar = input.time?.let {
            val minutes = corrected.hour * 60 + corrected.minute
            val hb = ((minutes + 60) / 120) % 12
            val hs = (dayPillar.stem.ordinal % 5 * 2 + hb) % 10
            Pillar.of(Stem.entries[hs], Branch.entries[hb])
        }

        // 대운: 양남음녀 순행, 음남양녀 역행. 절입까지의 날수 ÷ 3 = 대운수
        val forward = yearPillar.stem.isYang == (input.gender == Gender.MALE)
        val prevJeolLon = Math.floorMod(315 + 30 * monthOffset, 360).toDouble()
        val prevJeolJd = Astronomy.solarTermJd(prevJeolLon, birthJd - 15)
        val nextJeolJd = Astronomy.solarTermJd((prevJeolLon + 30) % 360, birthJd + 15)
        val days = if (forward) abs(nextJeolJd - birthJd) else abs(birthJd - prevJeolJd)
        val startAge = (days / 3.0).roundToInt().coerceIn(1, 10)
        val daeun = (1..10).map { i ->
            DaeunPeriod(startAge + (i - 1) * 10, monthPillar.plus(if (forward) i else -i))
        }

        return SajuChart(
            input = input,
            solarDate = solarDate,
            lunarDate = lunarDate,
            year = yearPillar,
            month = monthPillar,
            day = dayPillar,
            hour = hourPillar,
            isDaeunForward = forward,
            daeunStartAge = startAge,
            daeun = daeun,
        )
    }

    /** 해당 날짜의 일진(일주) */
    fun dayPillarOf(date: LocalDate): Pillar = Pillar.ofIndex(((date.toEpochDay() + 2440588L + 49) % 60).toInt())

    /** 해당 연도(입춘 이후 기준)의 세운 간지 */
    fun yearPillarOf(year: Int): Pillar = Pillar.ofIndex(year - 4)
}
