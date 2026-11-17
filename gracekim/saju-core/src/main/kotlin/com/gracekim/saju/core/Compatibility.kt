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

/** 궁합 상세: 분야별 점수, 서로에게 어떤 존재인지, 관계 팁 */
object CompatibilityDetail {

    data class Category(val emoji: String, val name: String, val score: Int, val text: String)

    data class Result(
        val categories: List<Category>,
        /** 상대(b)가 나(a)에게 어떤 존재인지 */
        val bToA: Pair<TenGod, String>,
        /** 나(a)가 상대(b)에게 어떤 존재인지 */
        val aToB: Pair<TenGod, String>,
        val combinedCounts: Map<Element, Int>,
        val tips: List<String>,
        val dateIdea: String,
    )

    fun partnerRole(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "친구 같은 존재예요. 같은 눈높이에서 이야기가 잘 통해요."
        TenGod.GEOPJAE -> "자극을 주는 라이벌 같은 존재예요. 함께하면 승부욕과 활력이 생겨요."
        TenGod.SIKSIN -> "아끼고 챙겨 주고 싶은 존재예요. 함께 있으면 마음에 여유가 생겨요."
        TenGod.SANGGWAN -> "내 개성과 끼를 끌어내 주는 존재예요. 대화가 활기차고 재미있어요."
        TenGod.PYEONJAE -> "설레고 끌리는 존재예요. 함께 새로운 경험을 많이 하게 돼요."
        TenGod.JEONGJAE -> "안정감을 주는 존재예요. 현실적으로 든든한 짝이에요."
        TenGod.PYEONGWAN -> "나를 긴장시키고 단련시키는 존재예요. 강하게 끌리지만 부담도 느낄 수 있어요."
        TenGod.JEONGGWAN -> "믿고 기댈 수 있는 존재예요. 나를 바른 방향으로 이끌어 줘요."
        TenGod.PYEONIN -> "나를 깊이 이해해 주는 존재예요. 새로운 시각과 영감을 줘요."
        TenGod.JEONGIN -> "나를 따뜻하게 보살펴 주는 존재예요. 곁에 있으면 마음이 편안해요."
    }

    fun analyze(a: SajuChart, b: SajuChart): Result {
        val sa = a.dayMaster
        val sb = b.dayMaster

        // 1. 성격 궁합 (일간)
        val (pScore, pText) = when {
            Relations.isStemCombination(sa, sb) -> 95 to "두 사람의 본성이 서로 끌어당기는 '천간합'이에요. 처음 만났을 때부터 편안하고, 말하지 않아도 마음이 통하는 사이예요."
            sa.element.generates == sb.element || sb.element.generates == sa.element ->
                85 to "한 사람이 다른 사람을 북돋아 주는 '상생' 관계예요. 서로 부족한 부분을 채워 주며 함께 성장해요."
            sa.element == sb.element -> 75 to "같은 오행이라 성향과 가치관이 비슷해요. 친구처럼 잘 통하지만, 둘 다 고집을 세우면 부딪힐 수 있어요."
            else -> 55 to "서로 다른 기운이 부딪히는 '상극' 관계예요. 처음엔 다름이 크게 느껴지지만, 인정하고 존중하면 서로를 단단하게 만들어 줘요."
        }

        // 2. 생활 궁합 (일지 = 배우자 자리)
        val ba = a.day.branch
        val bb = b.day.branch
        val (lScore, lText) = when {
            Relations.isSixCombination(ba, bb) -> 95 to "배우자 자리가 '육합'을 이뤄요. 생활 리듬과 습관이 잘 맞아 함께 살면 편안하고 화목해요."
            Relations.isTripleCombination(ba, bb) -> 85 to "배우자 자리가 '삼합'을 이뤄요. 같은 목표를 향해 힘을 합치면 가정과 일 모두 잘 풀려요."
            ba == bb -> 75 to "같은 일지라 생활 습관이 닮았어요. 함께 있으면 편하지만 새로운 자극을 일부러 만들어 주세요."
            Relations.isClash(ba, bb) -> 45 to "배우자 자리가 '충'을 이뤄요. 생활 방식이 달라 부딪히기 쉬우니 각자의 공간과 시간을 존중해 주세요."
            Relations.isWonjin(ba, bb) -> 50 to "배우자 자리가 '원진'이에요. 이유 없이 서운함이 쌓이기 쉬우니 오해는 바로바로 대화로 풀어 주세요."
            else -> 70 to "배우자 자리에 특별한 합이나 충이 없어 무난해요. 서로 노력하는 만큼 좋아지는 관계예요."
        }

        // 3. 가족·주변 궁합 (띠)
        val ya = a.year.branch
        val yb = b.year.branch
        val (fScore, fText) = when {
            Relations.isSixCombination(ya, yb) || Relations.isTripleCombination(ya, yb) ->
                90 to "${ya.animal}띠와 ${yb.animal}띠는 합이 드는 좋은 띠 궁합이에요. 양가 어른이나 주변 사람들과의 관계도 원만해요."
            Relations.isClash(ya, yb) -> 50 to "${ya.animal}띠와 ${yb.animal}띠는 서로 마주 보는 띠예요. 기질 차이가 커서, 서로의 집안 문화를 이해하려는 노력이 필요해요."
            Relations.isWonjin(ya, yb) -> 55 to "${ya.animal}띠와 ${yb.animal}띠는 원진 관계예요. 사소한 일로 감정이 상하기 쉬우니 고마움을 자주 표현하세요."
            else -> 75 to "${ya.animal}띠와 ${yb.animal}띠는 무난한 띠 궁합이에요. 주변의 응원 속에서 관계를 이어 갈 수 있어요."
        }

        // 4. 기운 보완 (오행)
        val ca = a.elementCounts
        val cb = b.elementCounts
        val combined = Element.entries.associateWith { ca.getValue(it) + cb.getValue(it) }
        val filled = Element.entries.filter {
            (ca.getValue(it) == 0 && cb.getValue(it) >= 1) || (cb.getValue(it) == 0 && ca.getValue(it) >= 1)
        }
        val stillMissing = combined.filter { it.value == 0 }.keys
        val eScore = (70 + 8 * filled.size - 15 * stillMissing.size).coerceIn(40, 98)
        val eText = buildString {
            if (filled.isNotEmpty()) append("한 사람에게 없는 ${filled.joinToString("·") { it.korean + "(" + it.hanja + ")" }} 기운을 다른 사람이 채워 줘요. ")
            if (stillMissing.isEmpty()) append("두 사람이 함께하면 다섯 가지 기운이 모두 갖춰져 균형이 좋아요.")
            else append("다만 두 사람 모두 ${stillMissing.joinToString("·") { it.korean + "(" + it.hanja + ")" }} 기운이 없어요. 함께 그 기운을 채우는 활동을 해 보세요.")
        }

        val categories = listOf(
            Category("🧡", "성격 궁합", pScore, pText),
            Category("🏠", "생활 궁합 (배우자 자리)", lScore, lText),
            Category("👨‍👩‍👧", "가족·주변 궁합 (띠)", fScore, fText),
            Category("🌈", "기운 보완 (오행)", eScore, eText),
        )

        val bToAGod = TenGod.of(sa, sb)
        val aToBGod = TenGod.of(sb, sa)

        val tips = buildList {
            add(
                when {
                    pScore >= 85 -> "이미 마음이 잘 통하는 사이예요. 익숙함에 소홀해지지 않도록 고마움과 애정을 자주 표현하세요."
                    pScore >= 70 -> "비슷한 점이 많은 만큼 서로의 의견을 번갈아 따르는 규칙을 만들면 다툼이 줄어요."
                    else -> "생각하는 방식이 다르다는 걸 먼저 인정하세요. '틀린 게 아니라 다른 것'이라는 말을 기억하면 좋아요."
                }
            )
            add(
                when {
                    lScore >= 85 -> "생활 리듬이 잘 맞으니 함께하는 루틴(주말 산책, 같이 요리하기)을 만들면 더 행복해져요."
                    lScore >= 65 -> "집안일과 돈 관리처럼 생활 규칙을 미리 정해 두면 큰 다툼 없이 지낼 수 있어요."
                    else -> "각자의 시간과 공간을 존중하는 것이 가장 중요해요. 서운한 일은 쌓아 두지 말고 그날 풀어 주세요."
                }
            )
            add("상대는 나에게 '${Explain.tenGodShort(bToAGod)}'의 기운을 줘요. ${partnerRole(bToAGod)}")
        }

        val weakest = Element.entries.minBy { combined.getValue(it) }
        val dateIdea = when (weakest) {
            Element.WOOD -> "🌳 숲길 산책, 식물원, 함께 화분 키우기처럼 자연과 성장의 기운을 채우는 데이트"
            Element.FIRE -> "🔥 야외 활동, 공연·축제, 함께 운동하기처럼 열정과 활력을 채우는 데이트"
            Element.EARTH -> "⛰️ 등산, 맛집 탐방, 함께 요리하기처럼 안정과 믿음을 쌓는 데이트"
            Element.METAL -> "💎 전시회, 깔끔한 카페, 함께 집 정리하기처럼 정돈된 기운을 채우는 데이트"
            Element.WATER -> "💧 바다·강가 산책, 온천, 조용한 북카페처럼 여유와 휴식을 채우는 데이트"
        }

        return Result(
            categories = categories,
            bToA = bToAGod to partnerRole(bToAGod),
            aToB = aToBGod to partnerRole(aToBGod),
            combinedCounts = combined,
            tips = tips,
            dateIdea = dateIdea,
        )
    }
}
