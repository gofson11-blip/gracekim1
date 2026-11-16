package com.gracekim.saju.core

import java.time.LocalDate

data class DailyFortuneResult(
    val date: LocalDate,
    val dayPillar: Pillar,
    val tenGod: TenGod,
    val score: Int,
    val headline: String,
    val message: String,
    val money: String,
    val love: String,
    val health: String,
    val relationNote: String?,
    val luckyElement: Element,
    val luckyColor: String,
    val luckyNumbers: String,
    val luckyDirection: String,
)

/** 오늘의 운세(일진): 그날의 일주가 내 일간·일지와 어떤 관계인지로 봅니다. */
object DailyFortune {

    fun of(chart: SajuChart, date: LocalDate): DailyFortuneResult {
        val today = SajuCalculator.dayPillarOf(date)
        val dm = chart.dayMaster
        val god = TenGod.of(dm, today.stem)

        var score = when (god) {
            TenGod.SIKSIN, TenGod.JEONGJAE, TenGod.JEONGGWAN, TenGod.JEONGIN -> 85
            TenGod.PYEONJAE -> 80
            TenGod.BIGYEON, TenGod.SANGGWAN, TenGod.PYEONIN -> 72
            TenGod.GEOPJAE, TenGod.PYEONGWAN -> 62
        }

        // 신강하면 힘을 빼 주는 기운(식상·재성·관성), 신약하면 힘을 보태는 기운(인성·비겁)이 반갑습니다.
        val helpsMe = god in setOf(TenGod.BIGYEON, TenGod.GEOPJAE, TenGod.PYEONIN, TenGod.JEONGIN)
        score += if (helpsMe != chart.isStrong) 5 else -3

        val myBranch = chart.day.branch
        val relationNote = when {
            Relations.isSixCombination(myBranch, today.branch) -> {
                score += 8; "오늘의 지지와 내 일지가 합(合)을 이룹니다. 사람들과 손발이 잘 맞는 날입니다."
            }
            Relations.isTripleCombination(myBranch, today.branch) -> {
                score += 5; "오늘의 지지와 내 일지가 삼합을 이룹니다. 협력하면 일이 술술 풀립니다."
            }
            Relations.isClash(myBranch, today.branch) -> {
                score -= 12; "오늘의 지지가 내 일지와 충(沖)합니다. 이동·계약·말다툼을 조심하고 일정에 여유를 두세요."
            }
            Relations.isWonjin(myBranch, today.branch) -> {
                score -= 6; "오늘의 지지가 내 일지와 원진입니다. 괜한 오해가 생기지 않도록 말을 아끼세요."
            }
            else -> null
        }
        score = score.coerceIn(40, 98)

        val counts = chart.elementCounts
        val lucky = Element.entries.minBy { counts.getValue(it) }

        val (headline, message) = texts(god)
        val (money, love, health) = details(god)

        return DailyFortuneResult(
            date = date,
            dayPillar = today,
            tenGod = god,
            score = score,
            headline = headline,
            message = message,
            money = money,
            love = love,
            health = health,
            relationNote = relationNote,
            luckyElement = lucky,
            luckyColor = when (lucky) {
                Element.WOOD -> "초록색"; Element.FIRE -> "빨간색"; Element.EARTH -> "노란색"
                Element.METAL -> "흰색"; Element.WATER -> "검정·남색"
            },
            luckyNumbers = when (lucky) {
                Element.WOOD -> "3, 8"; Element.FIRE -> "2, 7"; Element.EARTH -> "5, 10"
                Element.METAL -> "4, 9"; Element.WATER -> "1, 6"
            },
            luckyDirection = when (lucky) {
                Element.WOOD -> "동쪽"; Element.FIRE -> "남쪽"; Element.EARTH -> "중앙"
                Element.METAL -> "서쪽"; Element.WATER -> "북쪽"
            },
        )
    }

    private fun texts(god: TenGod): Pair<String, String> = when (god) {
        TenGod.BIGYEON -> "내 뜻대로 움직이는 날" to "주관이 뚜렷해지고 스스로 해내려는 힘이 강한 날입니다. 혼자 하는 일에 집중하면 좋고, 친구나 동료와의 만남도 즐겁습니다."
        TenGod.GEOPJAE -> "경쟁과 지출을 조심하는 날" to "경쟁심이 올라오고 예상치 못한 지출이 생기기 쉽습니다. 충동구매와 돈 빌려주기는 오늘만큼은 미뤄 두세요."
        TenGod.SIKSIN -> "여유롭고 즐거운 날" to "마음이 편안하고 재능이 자연스럽게 드러나는 날입니다. 맛있는 음식, 취미, 창작 활동에 행운이 따릅니다."
        TenGod.SANGGWAN -> "아이디어가 반짝이는 날" to "표현력과 아이디어가 빛납니다. 다만 말이 앞서 윗사람과 부딪힐 수 있으니 한 번 더 생각하고 말하세요."
        TenGod.PYEONJAE -> "기회가 찾아오는 날" to "활동 범위가 넓어지고 뜻밖의 기회나 수입이 생길 수 있습니다. 적극적으로 움직이되 큰 투자는 신중하게."
        TenGod.JEONGJAE -> "성실함이 보상받는 날" to "꼼꼼하게 챙긴 일이 결실을 맺는 날입니다. 계획적인 소비와 저축, 밀린 정리에 좋습니다."
        TenGod.PYEONGWAN -> "책임이 무거운 날" to "갑작스러운 일이나 부담이 생길 수 있습니다. 서두르지 말고 하나씩 처리하면 오히려 능력을 인정받습니다."
        TenGod.JEONGGWAN -> "인정받는 날" to "규칙을 지키고 맡은 일을 해내면 칭찬과 신뢰가 따르는 날입니다. 면접, 보고, 공식적인 자리에 좋습니다."
        TenGod.PYEONIN -> "직감이 예리한 날" to "직관과 영감이 잘 떠오릅니다. 공부와 연구에는 좋지만 생각이 많아질 수 있으니 가벼운 산책을 권합니다."
        TenGod.JEONGIN -> "도움을 받는 날" to "주변에서 도움의 손길이 오는 날입니다. 배우기, 계약서 검토, 시험 준비에 좋은 기운이 있습니다."
    }

    private fun details(god: TenGod): Triple<String, String, String> = when (god) {
        TenGod.BIGYEON -> Triple("돈은 나가고 들어옴이 비슷합니다. 동업 제안은 신중히.", "편한 친구 같은 대화가 관계를 가깝게 합니다.", "기운은 좋은 편, 과한 운동만 조심하세요.")
        TenGod.GEOPJAE -> Triple("지출 주의! 지갑을 꼭 닫아 두세요.", "질투나 경쟁심이 생기기 쉬우니 너그럽게.", "스트레스성 두통·소화불량을 조심하세요.")
        TenGod.SIKSIN -> Triple("작은 즐거움에 쓰는 돈은 아깝지 않은 날.", "다정한 말 한마디가 큰 호감을 부릅니다.", "잘 먹고 잘 쉬는 날, 과식만 조심하세요.")
        TenGod.SANGGWAN -> Triple("아이디어가 돈이 될 수 있으니 메모해 두세요.", "솔직함은 좋지만 날카로운 말은 피하세요.", "목과 입 주변 건강, 수분 섭취에 신경 쓰세요.")
        TenGod.PYEONJAE -> Triple("뜻밖의 수입 운이 있습니다. 다만 크게 걸지는 마세요.", "새로운 만남의 기회가 있는 날입니다.", "바쁘게 움직이니 틈틈이 쉬어 주세요.")
        TenGod.JEONGJAE -> Triple("안정적인 재물운. 저축과 가계부 정리에 좋습니다.", "믿음직한 모습이 상대에게 신뢰를 줍니다.", "규칙적인 식사가 컨디션을 지켜 줍니다.")
        TenGod.PYEONGWAN -> Triple("급한 지출이 생길 수 있으니 여유 자금을 챙기세요.", "상대에게 내 기준을 강요하지 않도록 주의.", "피로가 쌓이기 쉬운 날, 일찍 잠자리에 드세요.")
        TenGod.JEONGGWAN -> Triple("정당한 보상과 평가를 받는 날입니다.", "예의 바른 태도가 호감을 높입니다.", "무난한 컨디션, 바른 자세를 유지하세요.")
        TenGod.PYEONIN -> Triple("돈보다 배움에 투자하기 좋은 날.", "혼자만의 시간이 필요할 수 있어요. 솔직하게 말해 주세요.", "생각이 많아 잠을 설칠 수 있으니 명상을 해 보세요.")
        TenGod.JEONGIN -> Triple("문서·계약 관련 이득이 있습니다.", "따뜻하게 챙겨 주는 사람이 곁에 있습니다.", "몸과 마음이 편안한 날, 충분히 쉬어 가세요.")
    }
}
