package com.gracekim.saju.core

data class CompatibilityPoint(val isPositive: Boolean, val title: String, val description: String)

data class CompatibilityResult(
    val score: Int,
    val grade: String,
    val summary: String,
    val points: List<CompatibilityPoint>,
)

/**
 * 두 사람의 사주로 보는 궁합.
 * 일간(나 자신)의 관계, 일지(배우자 자리)의 합·충, 띠(년지)의 관계, 오행 보완을 함께 봅니다.
 */
object Compatibility {

    fun analyze(a: SajuChart, b: SajuChart): CompatibilityResult {
        val points = mutableListOf<CompatibilityPoint>()
        val nameA = a.input.name.ifBlank { "나" }
        val nameB = b.input.name.ifBlank { "상대방" }
        var score = 60

        // 1. 일간 관계
        val sa = a.dayMaster
        val sb = b.dayMaster
        when {
            Relations.isStemCombination(sa, sb) -> {
                score += 15
                points += CompatibilityPoint(true, "일간 천간합 (${sa.hanja}${sb.hanja}合)",
                    "두 사람의 본성이 서로 끌어당기는 합의 관계입니다. 처음 만났을 때부터 편안하고 자연스럽게 가까워지는 인연입니다.")
            }
            sa.element.generates == sb.element || sb.element.generates == sa.element -> {
                score += 8
                val giver = if (sa.element.generates == sb.element) nameA else nameB
                points += CompatibilityPoint(true, "서로 살려 주는 상생 (${sa.element.korean}·${sb.element.korean})",
                    "${giver} 쪽이 상대를 북돋아 주는 관계입니다. 한쪽이 베풀고 다른 쪽이 힘을 얻어 함께 성장할 수 있습니다.")
            }
            sa.element == sb.element -> {
                score += 4
                points += CompatibilityPoint(true, "같은 오행의 일간 (${sa.element.korean})",
                    "성향과 가치관이 비슷해 친구처럼 잘 통합니다. 다만 둘 다 고집을 부리면 부딪힐 수 있습니다.")
            }
            else -> {
                score -= 6
                points += CompatibilityPoint(false, "서로 다스리는 상극 (${sa.element.korean}·${sb.element.korean})",
                    "한쪽이 상대를 누르거나 통제하려는 기운이 있습니다. 서로 다름을 인정하고 존중하면 오히려 좋은 자극이 됩니다.")
            }
        }

        // 2. 일지(배우자궁) 관계
        val ba = a.day.branch
        val bb = b.day.branch
        when {
            Relations.isSixCombination(ba, bb) -> {
                score += 12
                points += CompatibilityPoint(true, "배우자 자리 육합 (${ba.hanja}${bb.hanja}合)",
                    "함께 사는 생활 리듬이 잘 맞는 좋은 합입니다. 가정과 일상에서 화목하게 지낼 수 있습니다.")
            }
            Relations.isTripleCombination(ba, bb) -> {
                score += 8
                points += CompatibilityPoint(true, "배우자 자리 삼합",
                    "같은 목표를 향해 힘을 합치기 좋은 관계입니다. 함께 일을 도모하면 성과가 납니다.")
            }
            Relations.isClash(ba, bb) -> {
                score -= 10
                points += CompatibilityPoint(false, "배우자 자리 충 (${ba.hanja}${bb.hanja}沖)",
                    "생활 방식이 달라 부딪히기 쉽습니다. 각자의 공간과 시간을 존중해 주는 것이 중요합니다.")
            }
            Relations.isWonjin(ba, bb) -> {
                score -= 8
                points += CompatibilityPoint(false, "배우자 자리 원진",
                    "이유 없이 서운함이 쌓이기 쉬운 관계입니다. 오해가 생기면 바로 대화로 푸는 습관이 필요합니다.")
            }
            ba == bb -> {
                score += 2
                points += CompatibilityPoint(true, "같은 일지",
                    "생활 습관이 닮아 함께 있으면 편안합니다.")
            }
        }

        // 3. 띠(년지) 관계
        val ya = a.year.branch
        val yb = b.year.branch
        when {
            Relations.isSixCombination(ya, yb) || Relations.isTripleCombination(ya, yb) -> {
                score += 6
                points += CompatibilityPoint(true, "띠 궁합 좋음 (${ya.animal}띠·${yb.animal}띠)",
                    "띠끼리 합이 들어 집안 어른이나 주변 사람들과의 관계도 원만한 편입니다.")
            }
            Relations.isClash(ya, yb) -> {
                score -= 6
                points += CompatibilityPoint(false, "띠 충 (${ya.animal}띠·${yb.animal}띠)",
                    "띠끼리 마주 보는 관계라 기질 차이가 큽니다. 서로의 다른 점을 장점으로 봐 주세요.")
            }
            Relations.isWonjin(ya, yb) -> {
                score -= 5
                points += CompatibilityPoint(false, "띠 원진 (${ya.animal}띠·${yb.animal}띠)",
                    "사소한 일로 감정이 상하기 쉽습니다. 작은 고마움을 자주 표현하면 좋습니다.")
            }
        }

        // 4. 오행 보완
        val ca = a.elementCounts
        val cb = b.elementCounts
        val filled = Element.entries.filter { (ca.getValue(it) == 0 && cb.getValue(it) >= 2) || (cb.getValue(it) == 0 && ca.getValue(it) >= 2) }
        if (filled.isNotEmpty()) {
            score += (filled.size * 4).coerceAtMost(12)
            points += CompatibilityPoint(true, "오행 보완 (${filled.joinToString("·") { it.korean + "(" + it.hanja + ")" }})",
                "한 사람에게 부족한 기운을 다른 사람이 채워 줍니다. 함께 있을 때 균형이 잡히는 관계입니다.")
        }

        score = score.coerceIn(30, 99)
        val grade = when {
            score >= 90 -> "천생연분"
            score >= 80 -> "아주 좋은 궁합"
            score >= 70 -> "좋은 궁합"
            score >= 60 -> "무난한 궁합"
            else -> "노력이 필요한 궁합"
        }
        val summary = when {
            score >= 80 -> "서로에게 힘이 되어 주는 좋은 인연입니다. 지금처럼 아끼고 표현하면 오래도록 함께할 수 있습니다."
            score >= 60 -> "장점과 차이가 고루 있는 관계입니다. 대화를 자주 나누면 더 깊어지는 인연입니다."
            else -> "기질 차이가 큰 편이지만, 궁합은 정해진 운명이 아니라 참고일 뿐입니다. 서로를 이해하려는 마음이 가장 중요합니다."
        }
        return CompatibilityResult(score, grade, summary, points)
    }
}
