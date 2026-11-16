package com.gracekim.saju.core

/**
 * 전통 명리학의 일반적인 해석을 바탕으로 한 참고용 풀이 문구.
 */
object Interpretation {

    data class DayMasterReading(val title: String, val nature: String, val strengths: String, val cautions: String)

    fun dayMaster(stem: Stem): DayMasterReading = when (stem) {
        Stem.GAP -> DayMasterReading(
            "갑목(甲木) · 곧게 뻗은 큰 나무",
            "하늘을 향해 곧게 자라는 큰 나무처럼 목표가 분명하고 추진력이 강합니다. 정의감과 리더십이 있어 사람들을 이끄는 자리에 잘 어울립니다.",
            "개척 정신, 책임감, 꾸준한 성장, 정직함",
            "고집이 세고 굽히기 어려워 충돌이 생길 수 있습니다. 때로는 유연하게 숙이는 지혜가 필요합니다.",
        )
        Stem.EUL -> DayMasterReading(
            "을목(乙木) · 부드러운 풀과 덩굴",
            "바람에 휘어도 꺾이지 않는 풀처럼 유연하고 적응력이 뛰어납니다. 섬세하고 친화력이 좋아 사람들과 잘 어울립니다.",
            "생활력, 인내심, 사교성, 현실 감각",
            "주변 시선에 흔들리기 쉽고 걱정이 많을 수 있습니다. 스스로의 기준을 세우면 더 단단해집니다.",
        )
        Stem.BYEONG -> DayMasterReading(
            "병화(丙火) · 세상을 비추는 태양",
            "태양처럼 밝고 열정적이며 숨김이 없습니다. 어디서든 눈에 띄고 분위기를 환하게 만드는 사람입니다.",
            "적극성, 표현력, 낙천성, 너그러움",
            "성급하고 쉽게 달아올랐다 식을 수 있습니다. 끝맺음과 꾸준함을 챙기면 좋습니다.",
        )
        Stem.JEONG -> DayMasterReading(
            "정화(丁火) · 어둠을 밝히는 촛불",
            "촛불처럼 따뜻하고 섬세하며 속이 깊습니다. 한 분야를 파고드는 집중력과 남을 배려하는 마음이 큽니다.",
            "섬세함, 헌신, 예술적 감수성, 통찰력",
            "감정을 속으로 삭이다 예민해질 수 있습니다. 마음을 표현하는 연습이 도움이 됩니다.",
        )
        Stem.MU -> DayMasterReading(
            "무토(戊土) · 든든한 큰 산",
            "큰 산처럼 묵직하고 믿음직합니다. 쉽게 흔들리지 않고 사람들을 품어 주는 포용력이 있습니다.",
            "신뢰감, 안정감, 포용력, 중재 능력",
            "변화에 느리고 속마음을 잘 드러내지 않아 답답해 보일 수 있습니다.",
        )
        Stem.GI -> DayMasterReading(
            "기토(己土) · 곡식을 키우는 논밭",
            "기름진 땅처럼 실속 있고 꼼꼼하며 남을 길러 내는 힘이 있습니다. 현실적인 계획과 관리에 강합니다.",
            "실용성, 세심함, 자기관리, 교육·양육 능력",
            "생각이 많아 결정을 미루거나 의심이 깊어질 수 있습니다.",
        )
        Stem.GYEONG -> DayMasterReading(
            "경금(庚金) · 단단한 바위와 무쇠",
            "강철처럼 결단력 있고 의리가 강합니다. 옳다고 믿는 일에는 망설임 없이 나서는 용기가 있습니다.",
            "결단력, 의리, 실행력, 강인함",
            "말과 행동이 거칠게 비칠 수 있습니다. 부드러운 표현을 곁들이면 신뢰가 더 커집니다.",
        )
        Stem.SIN -> DayMasterReading(
            "신금(辛金) · 빛나는 보석",
            "잘 다듬어진 보석처럼 깔끔하고 자존심이 강하며 미적 감각이 뛰어납니다. 완성도를 중시합니다.",
            "섬세함, 원칙, 미적 감각, 분석력",
            "예민하고 상처를 잘 받을 수 있습니다. 완벽주의를 조금 내려놓으면 편안해집니다.",
        )
        Stem.IM -> DayMasterReading(
            "임수(壬水) · 넓은 바다와 큰 강",
            "큰 물처럼 포부가 크고 지혜로우며 흐름을 읽는 능력이 뛰어납니다. 자유롭고 활동 범위가 넓습니다.",
            "지혜, 포용력, 기획력, 적응력",
            "생각이 넓은 만큼 산만해지거나 방향을 자주 바꿀 수 있습니다.",
        )
        Stem.GYE -> DayMasterReading(
            "계수(癸水) · 촉촉한 비와 이슬",
            "만물을 적시는 빗물처럼 조용하지만 영향력이 깊습니다. 직관과 상상력이 뛰어나고 배려심이 많습니다.",
            "직관력, 총명함, 인내, 공감 능력",
            "속마음을 감추고 혼자 고민하는 경우가 많습니다. 믿을 만한 사람과 나누면 좋습니다.",
        )
    }

    fun tenGodMeaning(god: TenGod): String = when (god) {
        TenGod.BIGYEON -> "나와 같은 기운. 자립심, 동료, 형제, 경쟁"
        TenGod.GEOPJAE -> "나와 비슷하나 다른 기운. 승부욕, 추진력, 재물 경쟁"
        TenGod.SIKSIN -> "내가 낳는 기운. 표현력, 재능, 여유, 먹을 복"
        TenGod.SANGGWAN -> "내가 낳는 강한 기운. 창의력, 언변, 반골 기질"
        TenGod.PYEONJAE -> "내가 다스리는 큰 재물. 사업 수완, 활동성, 유동 재산"
        TenGod.JEONGJAE -> "내가 다스리는 안정된 재물. 성실함, 꼼꼼한 관리, 고정 수입"
        TenGod.PYEONGWAN -> "나를 다스리는 강한 기운. 카리스마, 책임, 압박과 도전"
        TenGod.JEONGGWAN -> "나를 다스리는 바른 기운. 명예, 직장, 규칙, 신뢰"
        TenGod.PYEONIN -> "나를 돕는 특별한 기운. 직관, 전문 기술, 독특한 생각"
        TenGod.JEONGIN -> "나를 돕는 바른 기운. 학문, 자격, 어머니, 후원"
    }

    fun yearlyFortune(god: TenGod): String = when (god) {
        TenGod.BIGYEON -> "자기 주관이 뚜렷해지고 독립하려는 기운이 강해지는 해입니다. 협업은 좋지만 돈거래와 보증은 조심하세요."
        TenGod.GEOPJAE -> "경쟁이 치열해지고 지출이 늘기 쉬운 해입니다. 무리한 투자보다는 실력을 다지는 데 집중하면 좋습니다."
        TenGod.SIKSIN -> "재능을 마음껏 펼치고 일상에 여유가 생기는 해입니다. 새로운 취미나 창작, 건강 관리에도 좋은 시기입니다."
        TenGod.SANGGWAN -> "아이디어와 표현력이 빛나는 해입니다. 다만 말실수나 윗사람과의 마찰을 조심하면 성과가 커집니다."
        TenGod.PYEONJAE -> "활동 범위가 넓어지고 큰돈이 오가는 해입니다. 기회는 많지만 계획적인 재무 관리가 중요합니다."
        TenGod.JEONGJAE -> "성실하게 노력한 만큼 안정적인 결실을 얻는 해입니다. 저축과 생활 기반 다지기에 좋습니다."
        TenGod.PYEONGWAN -> "책임과 부담이 커지지만 그만큼 성장하는 해입니다. 건강과 스트레스 관리에 신경 쓰세요."
        TenGod.JEONGGWAN -> "인정과 명예가 따르는 해입니다. 승진, 취업, 계약 등 공식적인 일에 좋은 흐름이 있습니다."
        TenGod.PYEONIN -> "직관과 아이디어가 샘솟는 해입니다. 공부, 자격증, 전문 분야 연구에 좋지만 생각이 많아질 수 있습니다."
        TenGod.JEONGIN -> "도움을 주는 사람이 나타나고 배움에 좋은 해입니다. 문서·계약·시험 운이 따릅니다."
    }

    fun elementAdvice(element: Element): String = when (element) {
        Element.WOOD -> "목(木) 기운이 부족합니다. 성장과 계획의 기운을 보충하세요. 초록색, 동쪽, 아침 산책, 식물 기르기, 새로운 배움이 도움이 됩니다."
        Element.FIRE -> "화(火) 기운이 부족합니다. 열정과 표현의 기운을 보충하세요. 붉은색, 남쪽, 햇볕 쬐기, 운동, 사람들과의 교류가 도움이 됩니다."
        Element.EARTH -> "토(土) 기운이 부족합니다. 안정과 신뢰의 기운을 보충하세요. 노란색·갈색, 규칙적인 생활, 등산, 꾸준한 루틴이 도움이 됩니다."
        Element.METAL -> "금(金) 기운이 부족합니다. 결단과 정리의 기운을 보충하세요. 흰색·금색, 서쪽, 정리정돈, 원칙 세우기가 도움이 됩니다."
        Element.WATER -> "수(水) 기운이 부족합니다. 지혜와 휴식의 기운을 보충하세요. 검은색·남색, 북쪽, 충분한 수면, 독서와 명상이 도움이 됩니다."
    }

    fun elementExcess(element: Element): String = when (element) {
        Element.WOOD -> "목(木) 기운이 강합니다. 추진력은 좋지만 고집이 세질 수 있으니 유연함을 챙기세요."
        Element.FIRE -> "화(火) 기운이 강합니다. 열정이 넘치지만 성급함과 감정 기복을 조심하세요."
        Element.EARTH -> "토(土) 기운이 강합니다. 듬직하지만 변화에 둔해질 수 있으니 새로운 시도를 해 보세요."
        Element.METAL -> "금(金) 기운이 강합니다. 결단력은 좋지만 날카로운 말과 냉정함을 조심하세요."
        Element.WATER -> "수(水) 기운이 강합니다. 지혜롭지만 걱정과 생각이 많아질 수 있으니 실행에 옮기는 연습을 하세요."
    }

    fun strength(chart: SajuChart): String = if (chart.isStrong) {
        "신강(身强) 사주에 가깝습니다. 스스로의 힘이 강해 주도적으로 일을 이끌어 가는 편입니다. 재능을 밖으로 펼치고(식상), 재물과 일(재성·관성)로 힘을 쓰면 좋습니다."
    } else {
        "신약(身弱) 사주에 가깝습니다. 섬세하고 주변과 조화를 잘 이룹니다. 배움과 후원(인성), 좋은 동료(비겁)의 도움을 받을 때 힘이 커집니다."
    }

    fun overallSummary(chart: SajuChart): String {
        val counts = chart.elementCounts
        val maxCount = counts.values.max()
        val most = counts.filter { it.value == maxCount }.keys
        val mostText = most.joinToString("·") { "${it.korean}(${it.hanja})" }
        val missing = counts.filter { it.value == 0 }.keys
        val dm = chart.dayMaster
        return buildString {
            append("일간은 ${dm.korean}${dm.element.korean}(${dm.hanja}${dm.element.hanja})으로, ")
            append(if (dm.isYang) "양(陽)의 기운을 지닌 " else "음(陰)의 기운을 지닌 ")
            append("${dm.element.korean}(${dm.element.hanja})의 사람입니다. ")
            append(
                if (most.size == 1) "사주에서 가장 많은 오행은 ${mostText}이고"
                else "사주에서 가장 많은 오행은 ${mostText}로 같은 개수이고"
            )
            if (missing.isEmpty()) append(", 다섯 가지 오행이 모두 갖춰져 있습니다.")
            else append(", ${missing.joinToString("·") { "${it.korean}(${it.hanja})" }} 기운은 보이지 않습니다.")
        }
    }
}
