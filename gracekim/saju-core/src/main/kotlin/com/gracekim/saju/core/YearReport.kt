package com.gracekim.saju.core

/** 신년운세(한 해 운세) 종합 보고서 */
object YearReport {

    data class ScoredMonth(val month: Detail.MonthFortune, val score: Int)

    data class Report(
        val year: Int,
        val pillar: Pillar,
        val title: String,
        val stemGod: TenGod,
        val branchGod: TenGod,
        val score: Int,
        val keyword: String,
        val paragraphs: List<String>,
        val areas: Detail.AreaFortune,
        val months: List<ScoredMonth>,
        val bestMonths: List<ScoredMonth>,
        val cautionMonths: List<ScoredMonth>,
    )

    private fun colorName(e: Element) = when (e) {
        Element.WOOD -> "푸른"
        Element.FIRE -> "붉은"
        Element.EARTH -> "황금"
        Element.METAL -> "하얀"
        Element.WATER -> "검은"
    }

    /** 십신과 내 사주의 강약, 지지 관계로 점수를 냅니다. (오늘의 운세와 같은 기준) */
    fun scoreOf(chart: SajuChart, god: TenGod, branch: Branch): Int {
        var score = when (god) {
            TenGod.SIKSIN, TenGod.JEONGJAE, TenGod.JEONGGWAN, TenGod.JEONGIN -> 85
            TenGod.PYEONJAE -> 80
            TenGod.BIGYEON, TenGod.SANGGWAN, TenGod.PYEONIN -> 72
            TenGod.GEOPJAE, TenGod.PYEONGWAN -> 62
        }
        val helpsMe = god in setOf(TenGod.BIGYEON, TenGod.GEOPJAE, TenGod.PYEONIN, TenGod.JEONGIN)
        score += if (helpsMe != chart.isStrong) 5 else -3
        val mine = chart.day.branch
        score += when {
            Relations.isSixCombination(mine, branch) -> 8
            Relations.isTripleCombination(mine, branch) -> 5
            Relations.isClash(mine, branch) -> -12
            Relations.isWonjin(mine, branch) -> -6
            else -> 0
        }
        return score.coerceIn(40, 98)
    }

    fun keyword(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "독립과 자신감"
        TenGod.GEOPJAE -> "도전과 경쟁"
        TenGod.SIKSIN -> "여유와 재능"
        TenGod.SANGGWAN -> "표현과 변화"
        TenGod.PYEONJAE -> "기회와 확장"
        TenGod.JEONGJAE -> "안정과 결실"
        TenGod.PYEONGWAN -> "책임과 성장"
        TenGod.JEONGGWAN -> "인정과 명예"
        TenGod.PYEONIN -> "배움과 내면"
        TenGod.JEONGIN -> "도움과 문서"
    }

    fun build(chart: SajuChart, year: Int, ageThisYear: Int): Report {
        val pillar = SajuCalculator.yearPillarOf(year)
        val dm = chart.dayMaster
        val stemGod = TenGod.of(dm, pillar.stem)
        val branchGod = TenGod.of(dm, pillar.branch)
        val title = "${year}년 ${pillar.korean}(${pillar.hanja})년 · ${colorName(pillar.stem.element)} ${pillar.branch.animal}의 해"

        val paragraphs = mutableListOf<String>()
        paragraphs += "${year}년은 ${Explain.stemEmoji(pillar.stem)} ${Explain.josa(pillar.stem.korean + pillar.stem.element.korean, "과", "와")} " +
            "${Explain.animalEmoji(pillar.branch)} ${Explain.josa(pillar.branch.korean + pillar.branch.element.korean, "이", "가")} 만난 ${pillar.korean}년이에요. " +
            "${Explain.stemImage(dm)} 같은 기운을 타고난 나에게 올해는 '${keyword(stemGod)}'의 해예요."
        paragraphs += Interpretation.yearlyFortune(stemGod) + " " + Explain.tenGodExplain(stemGod)
        paragraphs += "한 해의 후반으로 갈수록 ${branchGod.korean}(${Explain.tenGodShort(branchGod)})의 기운이 커져요. " +
            Explain.tenGodExplain(branchGod)

        val mine = chart.day.branch
        val myYear = chart.year.branch
        paragraphs += when {
            Relations.isSixCombination(mine, pillar.branch) ->
                "올해의 띠 글자가 내 배우자 자리와 '합'을 이뤄요. 사람 운이 좋아 귀인을 만나거나 좋은 인연이 생기기 쉬운 해예요."
            Relations.isTripleCombination(mine, pillar.branch) ->
                "올해의 띠 글자가 내 배우자 자리와 '삼합'을 이뤄요. 함께하는 사람들과 힘을 합치면 일이 술술 풀려요."
            Relations.isClash(mine, pillar.branch) ->
                "올해의 띠 글자가 내 배우자 자리와 '충'을 이뤄요. 이사·이직·이동 같은 변화가 생기기 쉬우니, 큰 결정은 충분히 준비하고 내리세요."
            Relations.isWonjin(mine, pillar.branch) ->
                "올해의 띠 글자가 내 배우자 자리와 '원진'이에요. 가까운 사람과 사소한 오해가 생기기 쉬우니 말을 한 번 더 고르세요."
            Relations.isClash(myYear, pillar.branch) ->
                "올해는 내 띠와 마주 보는 해예요. 주변 환경이 바뀌기 쉬우니 건강과 안전에 조금 더 신경 쓰세요."
            pillar.branch == myYear ->
                "올해는 내 띠의 해예요. 새로운 마음으로 다시 시작하기 좋지만, 무리하지 않는 것이 중요해요."
            else -> "올해의 띠 글자와 내 사주 사이에 큰 충돌이 없어, 노력한 만큼 무난하게 흘러가는 해예요."
        }
        chart.daeunAt(ageThisYear)?.let { d ->
            val g = TenGod.of(dm, d.pillar.stem)
            paragraphs += "지금 지나고 있는 ${d.pillar.korean} 대운(${d.startAge}~${d.startAge + 9}세)의 큰 흐름은 이래요. ${Explain.daeunMeaning(g)}"
        }

        val months = Detail.monthlyFortunes(chart, year).map { ScoredMonth(it, scoreOf(chart, it.god, it.pillar.branch)) }
        val best = months.sortedByDescending { it.score }.take(3).sortedBy { months.indexOf(it) }
        val caution = months.sortedBy { it.score }.take(2).sortedBy { months.indexOf(it) }

        return Report(
            year = year,
            pillar = pillar,
            title = title,
            stemGod = stemGod,
            branchGod = branchGod,
            score = scoreOf(chart, stemGod, pillar.branch),
            keyword = keyword(stemGod),
            paragraphs = paragraphs,
            areas = Detail.yearAreas(stemGod),
            months = months,
            bestMonths = best,
            cautionMonths = caution,
        )
    }
}
