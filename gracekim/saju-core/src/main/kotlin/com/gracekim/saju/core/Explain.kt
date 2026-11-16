package com.gracekim.saju.core

/**
 * 사주를 처음 보는 사람도 이해할 수 있도록 돕는 쉬운 설명·그림 문자 모음.
 */
object Explain {

    /** 받침 유무에 따라 조사를 고릅니다. 예) josa("용", "을", "를") = "용을" */
    fun josa(word: String, withBatchim: String, withoutBatchim: String): String {
        val c = word.lastOrNull { it in '가'..'힣' } ?: return word + withoutBatchim
        return word + if ((c.code - 0xAC00) % 28 != 0) withBatchim else withoutBatchim
    }

    // ── 천간(하늘 글자)의 자연 이미지 ────────────────────────────
    fun stemEmoji(s: Stem): String = when (s) {
        Stem.GAP -> "🌳"; Stem.EUL -> "🌿"; Stem.BYEONG -> "☀️"; Stem.JEONG -> "🕯️"; Stem.MU -> "⛰️"
        Stem.GI -> "🌾"; Stem.GYEONG -> "🪨"; Stem.SIN -> "💎"; Stem.IM -> "🌊"; Stem.GYE -> "💧"
    }

    fun stemImage(s: Stem): String = when (s) {
        Stem.GAP -> "큰 나무"; Stem.EUL -> "풀과 꽃"; Stem.BYEONG -> "태양"; Stem.JEONG -> "촛불"; Stem.MU -> "큰 산"
        Stem.GI -> "논밭"; Stem.GYEONG -> "바위와 쇠"; Stem.SIN -> "보석"; Stem.IM -> "바다"; Stem.GYE -> "빗물"
    }

    // ── 지지(땅 글자): 띠 동물, 계절 ────────────────────────────
    fun animalEmoji(b: Branch): String = when (b) {
        Branch.JA -> "🐭"; Branch.CHUK -> "🐮"; Branch.IN -> "🐯"; Branch.MYO -> "🐰"; Branch.JIN -> "🐲"; Branch.SA -> "🐍"
        Branch.O -> "🐴"; Branch.MI -> "🐑"; Branch.SHIN -> "🐵"; Branch.YU -> "🐔"; Branch.SUL -> "🐶"; Branch.HAE -> "🐷"
    }

    fun season(b: Branch): String = when (b) {
        Branch.JA -> "한겨울(12월)"; Branch.CHUK -> "늦겨울(1월)"; Branch.IN -> "초봄(2월)"; Branch.MYO -> "봄(3월)"
        Branch.JIN -> "늦봄(4월)"; Branch.SA -> "초여름(5월)"; Branch.O -> "한여름(6월)"; Branch.MI -> "늦여름(7월)"
        Branch.SHIN -> "초가을(8월)"; Branch.YU -> "가을(9월)"; Branch.SUL -> "늦가을(10월)"; Branch.HAE -> "초겨울(11월)"
    }

    fun animalPersonality(b: Branch): String = when (b) {
        Branch.JA -> "영리하고 눈치가 빠르며 부지런해요. 기회를 잘 잡는 재주가 있어요."
        Branch.CHUK -> "성실하고 우직해요. 한번 시작한 일은 끝까지 해내는 끈기가 있어요."
        Branch.IN -> "용감하고 당당해요. 리더십이 있고 정의감이 강해요."
        Branch.MYO -> "온화하고 센스 있어요. 사람들과 부드럽게 잘 어울려요."
        Branch.JIN -> "꿈이 크고 자신감이 넘쳐요. 어디서든 존재감이 빛나요."
        Branch.SA -> "지혜롭고 신중해요. 겉은 차분하지만 속은 열정적이에요."
        Branch.O -> "활발하고 자유로워요. 추진력과 행동력이 뛰어나요."
        Branch.MI -> "다정하고 평화를 좋아해요. 예술 감각과 배려심이 깊어요."
        Branch.SHIN -> "재치 있고 다재다능해요. 문제를 해결하는 능력이 뛰어나요."
        Branch.YU -> "꼼꼼하고 부지런해요. 자기 관리가 철저하고 원칙을 지켜요."
        Branch.SUL -> "의리 있고 정직해요. 한번 맺은 인연을 소중히 지켜요."
        Branch.HAE -> "너그럽고 복이 많아요. 솔직하고 정이 많아 주변에 사람이 모여요."
    }

    // ── 오행 ───────────────────────────────────────────────
    data class ElementInfo(val emoji: String, val thing: String, val keywords: String, val meaning: String)

    fun element(e: Element): ElementInfo = when (e) {
        Element.WOOD -> ElementInfo("🌳", "나무", "성장 · 시작 · 배려", "봄처럼 쭉쭉 뻗어 나가는 기운이에요. 새로운 시작, 성장, 인자함을 뜻해요.")
        Element.FIRE -> ElementInfo("🔥", "불", "열정 · 표현 · 밝음", "여름 햇살처럼 뜨거운 기운이에요. 열정, 표현력, 예의를 뜻해요.")
        Element.EARTH -> ElementInfo("⛰️", "흙", "안정 · 믿음 · 포용", "모든 것을 받쳐 주는 땅의 기운이에요. 안정감, 신뢰, 중재를 뜻해요.")
        Element.METAL -> ElementInfo("💎", "쇠·바위", "결단 · 정리 · 의리", "가을 열매처럼 단단하게 맺는 기운이에요. 결단력, 원칙, 정리를 뜻해요.")
        Element.WATER -> ElementInfo("💧", "물", "지혜 · 휴식 · 유연함", "겨울 물처럼 깊고 고요한 기운이에요. 지혜, 생각, 유연함을 뜻해요.")
    }

    /** 오행 개수를 쉬운 말로 */
    fun elementStatus(count: Int): String = when {
        count == 0 -> "없음"
        count == 1 -> "조금 부족"
        count == 2 -> "적당"
        count == 3 -> "많은 편"
        else -> "아주 많음"
    }

    // ── 네 기둥의 의미 ───────────────────────────────────────
    data class PillarRole(val name: String, val short: String, val meaning: String)

    /** index: 0=시주, 1=일주, 2=월주, 3=년주 */
    fun pillarRole(index: Int): PillarRole = when (index) {
        0 -> PillarRole("시주", "자녀·노년", "태어난 시간의 기둥 · 열매. 자녀, 노년(60세 이후), 마무리를 봐요.")
        1 -> PillarRole("일주", "나·배우자", "태어난 날의 기둥 · 꽃. 나 자신과 배우자, 중년(40~60세)을 봐요.")
        2 -> PillarRole("월주", "부모·사회", "태어난 달의 기둥 · 줄기. 부모·형제, 사회생활, 청년기(20~40세)를 봐요.")
        else -> PillarRole("년주", "조상·어린시절", "태어난 해의 기둥 · 뿌리. 조상과 집안, 어린 시절(~20세)을 봐요.")
    }

    // ── 십신 쉬운 이름 ───────────────────────────────────────
    fun tenGodShort(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "친구·동료"
        TenGod.GEOPJAE -> "경쟁·승부"
        TenGod.SIKSIN -> "재능·여유"
        TenGod.SANGGWAN -> "끼·말솜씨"
        TenGod.PYEONJAE -> "큰돈·사업"
        TenGod.JEONGJAE -> "월급·저축"
        TenGod.PYEONGWAN -> "도전·책임"
        TenGod.JEONGGWAN -> "명예·직장"
        TenGod.PYEONIN -> "직감·재주"
        TenGod.JEONGIN -> "배움·도움"
    }

    fun tenGodExplain(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "나와 똑같은 기운이에요. 자립심, 친구·형제, 함께하는 힘을 뜻해요."
        TenGod.GEOPJAE -> "나와 닮았지만 경쟁하는 기운이에요. 승부욕과 추진력, 그리고 지출·경쟁을 뜻해요."
        TenGod.SIKSIN -> "내가 만들어 내는 기운이에요. 재능, 먹을 복, 마음의 여유를 뜻해요."
        TenGod.SANGGWAN -> "내가 밖으로 표현하는 기운이에요. 창의력, 말솜씨, 개성을 뜻해요."
        TenGod.PYEONJAE -> "내가 다루는 큰 재물이에요. 사업 감각, 큰돈, 넓은 활동을 뜻해요."
        TenGod.JEONGJAE -> "내가 다루는 안정된 재물이에요. 꾸준한 수입, 성실함, 알뜰함을 뜻해요."
        TenGod.PYEONGWAN -> "나를 단련시키는 기운이에요. 책임감, 카리스마, 도전과 압박을 뜻해요."
        TenGod.JEONGGWAN -> "나를 바르게 이끄는 기운이에요. 직장, 명예, 규칙, 신뢰를 뜻해요."
        TenGod.PYEONIN -> "나를 돕는 특별한 기운이에요. 직감, 전문 기술, 독특한 아이디어를 뜻해요."
        TenGod.JEONGIN -> "나를 돕는 따뜻한 기운이에요. 배움, 자격증, 어머니 같은 후원, 문서 운을 뜻해요."
    }

    // ── 일간별 성격·연애·직업·재물·건강 ────────────────────────
    data class Persona(
        val keywords: List<String>,
        val love: String,
        val career: String,
        val money: String,
        val health: String,
    )

    fun persona(s: Stem): Persona = when (s) {
        Stem.GAP -> Persona(
            listOf("리더십", "정직함", "추진력"),
            "한 사람을 오래 지키는 듬직한 연애를 해요. 자존심 때문에 먼저 숙이기 어려우니 마음 표현을 아끼지 마세요.",
            "경영, 기획, 교육, 공직, 건축처럼 큰 그림을 그리고 사람들을 이끄는 일이 잘 맞아요.",
            "목표를 세우면 꾸준히 모으는 편이에요. 체면 때문에 크게 쓰는 지출만 조심하세요.",
            "어깨·목이 쉽게 뭉치고 피로가 쌓여요. 스트레칭과 충분한 휴식을 챙기세요.",
        )
        Stem.EUL -> Persona(
            listOf("유연함", "친화력", "생활력"),
            "다정하고 세심하게 챙겨 주는 연애를 해요. 상대에게 맞추다 지치지 않도록 내 마음도 꼭 말해 주세요.",
            "디자인, 상담, 서비스, 교육, 마케팅처럼 사람과 감각을 쓰는 일이 잘 맞아요.",
            "알뜰하고 현실적인 소비를 해요. 작은 돈을 잘 굴려 모으는 재주가 있어요.",
            "스트레스가 몸으로 오기 쉬워요. 가벼운 산책과 규칙적인 수면을 챙기세요.",
        )
        Stem.BYEONG -> Persona(
            listOf("열정", "밝음", "표현력"),
            "첫눈에 빠지는 뜨거운 연애를 해요. 마음이 식었다고 오해받지 않게 꾸준히 관심을 보여 주세요.",
            "방송, 영업, 강의, 홍보, 공연처럼 사람들 앞에 서서 빛나는 일이 잘 맞아요.",
            "통이 커서 잘 벌고 잘 쓰는 편이에요. 충동 지출만 줄이면 금방 모여요.",
            "열이 많고 쉽게 지칠 수 있어요. 물을 자주 마시고 잠을 충분히 자세요.",
        )
        Stem.JEONG -> Persona(
            listOf("섬세함", "배려", "집중력"),
            "조용히 오래 타오르는 연애를 해요. 서운함을 쌓아 두지 말고 그때그때 표현하세요.",
            "연구, 예술, 돌봄, 글쓰기, 전문 기술처럼 한 분야를 깊이 파고드는 일이 잘 맞아요.",
            "계획적으로 쓰고 모으는 편이에요. 남을 돕다 지출이 커지지 않게 선을 정하세요.",
            "예민해지면 잠을 설칠 수 있어요. 마음을 편히 하는 저녁 습관을 만드세요.",
        )
        Stem.MU -> Persona(
            listOf("신뢰", "포용력", "안정감"),
            "든든한 울타리 같은 연애를 해요. 무뚝뚝해 보일 수 있으니 말로 마음을 전해 주세요.",
            "부동산, 금융, 행정, 관리·중재처럼 믿음과 꾸준함이 중요한 일이 잘 맞아요.",
            "안정을 좋아하는 꾸준한 저축형이에요. 좋은 기회 앞에서 너무 망설이지 않아도 돼요.",
            "소화와 체중 관리에 신경 쓰고, 규칙적으로 몸을 움직이세요.",
        )
        Stem.GI -> Persona(
            listOf("꼼꼼함", "실속", "보살핌"),
            "세심하게 챙기며 천천히 깊어지는 연애를 해요. 의심보다 믿음을 먼저 주세요.",
            "회계, 교육, 요리, 행정, 복지처럼 기르고 챙기고 관리하는 일이 잘 맞아요.",
            "알뜰한 살림꾼이에요. 가계부와 저축 습관으로 재물이 차곡차곡 쌓여요.",
            "생각이 많으면 속이 불편해져요. 규칙적인 식사가 최고의 보약이에요.",
        )
        Stem.GYEONG -> Persona(
            listOf("결단력", "의리", "실행력"),
            "한번 마음먹으면 확실한 연애를 해요. 직설적인 말이 상처가 되지 않게 부드럽게 표현하세요.",
            "경찰·군인, 법조, 엔지니어, 운동, 기술·제조처럼 결단과 실행이 필요한 일이 잘 맞아요.",
            "과감하게 투자하는 편이에요. 나눠서 투자하고 원칙을 지키면 재물이 단단해져요.",
            "호흡기와 피부 건조를 조심하고, 땀 흘리는 운동이 잘 맞아요.",
        )
        Stem.SIN -> Persona(
            listOf("세련됨", "원칙", "완벽주의"),
            "취향이 분명한 연애를 해요. 완벽한 상대보다 편안한 사람을 만날 때 더 행복해져요.",
            "디자인, 패션·뷰티, 금융, 분석, 품질 관리처럼 정교함이 필요한 일이 잘 맞아요.",
            "가치 있는 것에 쓰는 편이에요. 예쁜 것에 지출이 몰리지 않게 예산을 정하세요.",
            "피부와 호흡기가 예민한 편이에요. 보습과 실내 공기 관리에 신경 쓰세요.",
        )
        Stem.IM -> Persona(
            listOf("지혜", "포부", "자유로움"),
            "넓은 마음으로 품어 주는 연애를 해요. 자유를 원하는 만큼 상대에게도 시간을 내어 주세요.",
            "무역, 여행, IT, 기획, 유통, 연구처럼 넓게 움직이고 흐름을 읽는 일이 잘 맞아요.",
            "큰 흐름을 보고 버는 편이에요. 들어오는 만큼 나가기 쉬우니 자동 저축을 만들어 두세요.",
            "몸이 차가워지지 않게 하고, 과음과 무리한 일정은 줄이세요.",
        )
        Stem.GYE -> Persona(
            listOf("직관", "공감", "총명함"),
            "말없이 깊게 이해해 주는 연애를 해요. 속마음을 숨기지 말고 솔직하게 나누세요.",
            "상담, 심리, 예술, 연구, 기획처럼 감성과 직관을 쓰는 일이 잘 맞아요.",
            "아이디어로 버는 재주가 있어요. 기분에 따른 소비만 조심하면 돼요.",
            "몸이 쉽게 붓고 차가워질 수 있어요. 따뜻한 차와 충분한 휴식을 챙기세요.",
        )
    }

    // ── 대운(10년) 쉬운 풀이 ─────────────────────────────────
    fun daeunMeaning(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "스스로 서는 10년이에요. 독립·창업이나 동료와 함께하는 일에 힘이 실려요."
        TenGod.GEOPJAE -> "경쟁 속에서 크는 10년이에요. 도전은 좋지만 돈 관리와 동업은 신중하게 하세요."
        TenGod.SIKSIN -> "재능이 꽃피는 10년이에요. 좋아하는 일을 즐기며 여유와 먹을 복이 따라요."
        TenGod.SANGGWAN -> "개성이 빛나는 10년이에요. 새로운 시도가 성과로 이어지지만 말조심은 필수예요."
        TenGod.PYEONJAE -> "활동 무대가 넓어지는 10년이에요. 사업·투자 기회가 많지만 크게 걸 때는 신중히."
        TenGod.JEONGJAE -> "차곡차곡 쌓는 10년이에요. 성실함이 안정된 재물과 가정의 행복으로 돌아와요."
        TenGod.PYEONGWAN -> "단련되는 10년이에요. 책임과 부담이 크지만 이겨 내면 한 단계 성장해요."
        TenGod.JEONGGWAN -> "인정받는 10년이에요. 직장·명예·결혼 등 사회적으로 자리를 잡기 좋아요."
        TenGod.PYEONIN -> "깊어지는 10년이에요. 공부·전문 기술·자기 탐구에 좋고 혼자만의 시간이 필요해요."
        TenGod.JEONGIN -> "도움을 받는 10년이에요. 배움, 자격, 문서 운이 좋고 귀인의 후원이 따라요."
    }

    // ── 글자 하나를 눌렀을 때 보여 줄 설명 ─────────────────────
    data class CharDetail(val title: String, val lines: List<String>)

    /**
     * @param col 0=시주, 1=일주, 2=월주, 3=년주
     * @param isStem true=위 글자(천간), false=아래 글자(지지)
     */
    fun charDetail(chart: SajuChart, col: Int, isStem: Boolean): CharDetail? {
        val pillar = when (col) { 0 -> chart.hour; 1 -> chart.day; 2 -> chart.month; else -> chart.year } ?: return null
        val role = pillarRole(col)
        val dm = chart.dayMaster
        return if (isStem) {
            val s = pillar.stem
            val el = element(s.element)
            val lines = mutableListOf(
                "${stemEmoji(s)} ${josa(stemImage(s), "을", "를")} 뜻하는 하늘 글자(천간)예요.",
                "오행: ${el.emoji} ${s.element.korean}(${s.element.hanja}) · ${if (s.isYang) "양(陽) — 겉으로 뻗는 기운" else "음(陰) — 안으로 모으는 기운"}",
            )
            if (col == 1) {
                lines += "⭐ 이 글자가 바로 '나'(일간)예요. 사주 풀이는 모두 이 글자를 중심으로 해요."
            } else {
                val g = TenGod.of(dm, s)
                lines += "나와의 관계: ${g.korean}(${tenGodShort(g)}) — ${tenGodExplain(g)}"
            }
            lines += "자리: ${role.meaning}"
            CharDetail("${s.hanja} ${s.korean}${s.element.korean} · ${role.name} 위 글자", lines)
        } else {
            val b = pillar.branch
            val el = element(b.element)
            val g = TenGod.of(dm, b)
            CharDetail(
                "${b.hanja} ${b.korean}${b.element.korean} · ${role.name} 아래 글자",
                listOf(
                    "${animalEmoji(b)} ${josa(b.animal, "을", "를")} 뜻하는 땅 글자(지지)예요. 계절로는 ${season(b)}, 시간으로는 ${b.hourRange}이에요.",
                    "오행: ${el.emoji} ${b.element.korean}(${b.element.hanja})",
                    "나와의 관계: ${g.korean}(${tenGodShort(g)}) — ${tenGodExplain(g)}",
                    "자리: ${role.meaning}",
                ),
            )
        }
    }

    // ── 한 줄 요약 ─────────────────────────────────────────
    fun strengthFriendly(chart: SajuChart): String =
        if (chart.isStrong) "스스로 힘이 센 타입 (신강)" else "주변과 함께 크는 타입 (신약)"

    // ── 용어 사전 ──────────────────────────────────────────
    val glossary: List<Pair<String, String>> = listOf(
        "사주팔자" to "태어난 해·달·날·시간을 네 개의 기둥(사주)으로 세우고, 기둥마다 두 글자씩 모두 여덟 글자(팔자)로 나타낸 것이에요.",
        "천간 (위 글자)" to "기둥의 위쪽 글자예요. 갑·을·병·정·무·기·경·신·임·계 10가지로, 하늘의 기운과 겉으로 드러나는 모습을 뜻해요.",
        "지지 (아래 글자)" to "기둥의 아래쪽 글자예요. 자·축·인·묘… 12가지로, 띠 동물과 계절·시간, 땅의 기운을 뜻해요.",
        "일간" to "태어난 날의 위 글자로, 사주에서 '나 자신'을 뜻해요. 모든 풀이의 중심이에요.",
        "오행" to "세상을 이루는 다섯 가지 기운, 목(나무)·화(불)·토(흙)·금(쇠)·수(물)예요.",
        "상생 · 상극" to "상생은 서로 돕는 관계(나무→불→흙→쇠→물→나무), 상극은 서로 누르는 관계예요.",
        "십신" to "'나'(일간)와 다른 글자 사이의 관계를 10가지로 나눈 것이에요. 성격, 재물, 직업, 인간관계를 볼 때 써요.",
        "신강 · 신약" to "나를 돕는 기운이 많으면 신강(스스로 힘이 센 타입), 적으면 신약(주변과 함께 크는 타입)이에요.",
        "대운" to "10년마다 바뀌는 큰 운의 흐름이에요. 인생의 계절이라고 생각하면 쉬워요.",
        "세운 · 일진" to "세운은 그해의 운, 일진은 그날의 기운이에요. 오늘의 운세는 일진으로 봐요.",
    )
}
