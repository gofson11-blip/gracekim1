package com.gracekim.saju.core

import java.time.LocalDate

/** 띠별 운세 (오늘 / 한 해) */
object Zodiac {

    data class Fortune(val branch: Branch, val score: Int, val text: String, val tip: String)

    /** 최근 출생 연도 (예: 쥐띠 → 1960, 1972, …) */
    fun birthYears(b: Branch, until: Int = LocalDate.now().year, count: Int = 6): List<Int> =
        (1930..until).filter { Math.floorMod(it - 4, 12) == b.ordinal }.takeLast(count)

    private fun score(target: Pillar, animal: Branch): Int {
        var s = 70
        s += when {
            Relations.isSixCombination(target.branch, animal) -> 15
            Relations.isTripleCombination(target.branch, animal) -> 10
            target.branch == animal -> 4
            Relations.isClash(target.branch, animal) -> -15
            Relations.isWonjin(target.branch, animal) -> -8
            else -> 0
        }
        val te = target.stem.element
        val ae = animal.element
        s += when {
            te.generates == ae -> 8
            te == ae -> 3
            ae.controls == te -> 5
            te.controls == ae -> -6
            else -> 0
        }
        return s.coerceIn(45, 98)
    }

    private fun relationText(target: Pillar, animal: Branch, period: String): String = when {
        Relations.isSixCombination(target.branch, animal) -> "${period} 기운과 합이 들어 귀인을 만나거나 좋은 소식이 들려와요."
        Relations.isTripleCombination(target.branch, animal) -> "${period} 기운과 손발이 잘 맞아 함께하는 일이 술술 풀려요."
        target.branch == animal -> "${period} 나와 같은 띠의 기운이라 자신감이 생기지만 고집은 조심하세요."
        Relations.isClash(target.branch, animal) -> "${period} 기운과 부딪혀 변화와 이동이 많아요. 서두르지 말고 안전을 챙기세요."
        Relations.isWonjin(target.branch, animal) -> "${period} 기운과 엇갈려 사소한 오해가 생기기 쉬워요. 말을 한 번 더 고르세요."
        else -> "${period} 큰 충돌 없이 무난하게 흘러가요."
    }

    private fun elementText(target: Pillar, animal: Branch): String {
        val te = target.stem.element
        val ae = animal.element
        return when {
            te.generates == ae -> "주변의 도움과 응원이 따라요."
            te == ae -> "비슷한 사람들과 어울리면 즐거워요."
            ae.controls == te -> "노력한 만큼 재물이나 결과가 들어와요."
            te.controls == ae -> "부담스러운 일이 생길 수 있으니 무리하지 마세요."
            else -> "내가 가진 것을 나누면 더 좋은 기운이 돌아와요."
        }
    }

    private fun tip(score: Int): String = when {
        score >= 88 -> "🍀 행운 지수 최고! 미뤄 둔 일을 시작해 보세요."
        score >= 75 -> "😊 기분 좋은 하루, 먼저 연락해 보세요."
        score >= 62 -> "🙂 무난한 흐름, 하던 일을 꾸준히."
        else -> "🌧️ 조심조심, 중요한 결정은 하루 미뤄요."
    }

    fun daily(date: LocalDate): List<Fortune> {
        val day = SajuCalculator.dayPillarOf(date)
        return Branch.entries.map { b ->
            val s = score(day, b)
            Fortune(b, s, relationText(day, b, "오늘은") + " " + elementText(day, b), tip(s))
        }
    }

    fun yearly(year: Int): List<Fortune> {
        val y = SajuCalculator.yearPillarOf(year)
        return Branch.entries.map { b ->
            val s = score(y, b)
            Fortune(b, s, relationText(y, b, "${year}년은") + " " + elementText(y, b), tip(s))
        }
    }
}
