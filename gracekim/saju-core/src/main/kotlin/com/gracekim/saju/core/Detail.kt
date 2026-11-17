package com.gracekim.saju.core

import java.time.LocalDate

/**
 * 더 자세한 풀이: 성격 상세, 다섯 가지 능력(십신 분포), 배우자·사회 자리,
 * 올해 분야별·월별 운세, 대운 분야별 풀이, 오늘의 할 일.
 */
object Detail {

    // ── 성격 상세 · 대인관계 ─────────────────────────────────
    data class DeepPersona(val paragraphs: List<String>, val relationship: String)

    fun deepPersona(s: Stem): DeepPersona = when (s) {
        Stem.GAP -> DeepPersona(
            listOf(
                "갑목은 열 개의 하늘 글자 중 첫 번째로, 시작과 우두머리를 뜻해요. 위로 곧게 자라는 나무처럼 목표가 생기면 앞만 보고 나아가고, 남에게 기대기보다 스스로 길을 만드는 것을 좋아해요.",
                "의리와 정의감이 강해 약한 사람을 보면 그냥 지나치지 못해요. 다만 한번 정한 생각을 잘 바꾸지 않아서, 융통성이 필요할 때는 '꺾이는 게 아니라 잠시 휘는 것'이라고 생각해 보세요.",
            ),
            "사람들을 이끌고 챙기는 큰형·큰언니 역할을 자주 맡아요. 아랫사람에게는 든든하지만 윗사람과는 자존심 싸움이 생기기 쉬우니 한 발 물러서는 여유가 필요해요.",
        )
        Stem.EUL -> DeepPersona(
            listOf(
                "을목은 부드러운 풀과 덩굴처럼 어떤 환경에서도 살아남는 힘이 뛰어나요. 겉으로는 여리고 순해 보이지만 속은 생각보다 질기고 단단해요.",
                "사람과 상황을 읽는 눈치가 빠르고, 필요한 곳에 자연스럽게 스며들어요. 혼자보다 함께일 때 더 빛나며, 아름다운 것을 알아보는 감각도 좋아요.",
            ),
            "누구와도 무난하게 지내는 친화력이 있어요. 다만 거절을 잘 못 해서 손해 볼 때가 있으니, 내 시간과 마음을 지키는 선을 정해 두세요.",
        )
        Stem.BYEONG -> DeepPersona(
            listOf(
                "병화는 하늘의 태양처럼 모든 것을 환하게 비추는 기운이에요. 숨기는 것이 없고, 있는 그대로를 드러내는 솔직하고 시원한 성격이에요.",
                "어디서든 분위기를 띄우는 주인공 기질이 있어 사람들이 자연스럽게 모여요. 기분이 얼굴에 바로 드러나고, 금방 달아올랐다 금방 식는 면도 있어요.",
            ),
            "밝고 너그러워 인기가 많아요. 많은 사람에게 마음을 나눠 주다 보니 가까운 사람이 서운해할 수 있으니, 소중한 사람에게는 따로 시간을 내어 주세요.",
        )
        Stem.JEONG -> DeepPersona(
            listOf(
                "정화는 어둠 속 촛불처럼 가까운 곳을 따뜻하게 밝히는 기운이에요. 겉은 조용해도 속에는 꺼지지 않는 열정을 품고 있어요.",
                "한 가지에 몰입하는 집중력과 섬세한 감수성이 뛰어나 예술·연구·기술 분야에서 빛나요. 다른 사람의 마음을 잘 헤아려 주는 배려심도 깊어요.",
            ),
            "넓은 관계보다 깊은 관계를 좋아해요. 마음을 연 사람에게는 한없이 헌신하지만, 서운함을 속으로 삭이다 한 번에 터질 수 있으니 그때그때 표현하세요.",
        )
        Stem.MU -> DeepPersona(
            listOf(
                "무토는 우뚝 선 큰 산처럼 묵직하고 흔들림이 없어요. 쉽게 감정을 드러내지 않고, 늘 그 자리에서 사람들을 품어 주는 든든한 존재예요.",
                "신중하게 생각하고 천천히 움직이지만, 한번 결정하면 끝까지 지켜 내요. 중재 능력이 있어 다툼이 생기면 자연스럽게 가운데에 서게 돼요.",
            ),
            "믿음직해서 사람들이 고민을 털어놓는 상대예요. 다만 속마음을 잘 말하지 않아 무뚝뚝해 보일 수 있으니, 고마움과 애정은 말로 꼭 표현해 주세요.",
        )
        Stem.GI -> DeepPersona(
            listOf(
                "기토는 곡식을 키우는 기름진 논밭처럼 무언가를 길러 내고 가꾸는 힘이 있어요. 현실적이고 실속을 챙기며, 작은 것도 허투루 넘기지 않아요.",
                "꼼꼼한 계획과 관리 능력이 뛰어나 살림이나 조직을 알뜰하게 꾸려요. 다만 생각이 많아 걱정이 앞서거나 결정을 미루는 경우가 있어요.",
            ),
            "주변 사람을 세심하게 챙기는 엄마 같은 존재예요. 쉽게 마음을 열지 않지만 한번 믿으면 오래가요. 의심이 들 때는 혼자 판단하지 말고 직접 물어보세요.",
        )
        Stem.GYEONG -> DeepPersona(
            listOf(
                "경금은 단단한 바위나 다듬어지지 않은 무쇠처럼 강하고 결단력 있는 기운이에요. 옳고 그름이 분명하고, 할 말은 하는 시원시원한 성격이에요.",
                "어려운 상황일수록 강해지는 승부사 기질이 있어요. 의리를 중요하게 여겨 내 사람은 끝까지 지키지만, 거친 말투 때문에 오해를 살 수 있어요.",
            ),
            "뒤끝 없이 화끈해서 따르는 사람이 많아요. 다만 직설적인 말이 상처가 될 수 있으니, 같은 말도 부드럽게 전하는 연습을 하면 관계가 훨씬 좋아져요.",
        )
        Stem.SIN -> DeepPersona(
            listOf(
                "신금은 잘 다듬어진 보석처럼 섬세하고 빛나는 기운이에요. 깔끔하고 세련된 것을 좋아하며, 자기만의 기준과 자존심이 뚜렷해요.",
                "분석력과 판단력이 뛰어나 작은 실수도 놓치지 않아요. 완벽을 추구하는 만큼 스스로에게도 엄격하니, 가끔은 '이만하면 충분하다'고 다독여 주세요.",
            ),
            "아무에게나 마음을 열지 않지만, 가까운 사이에는 정 깊고 의리 있어요. 예민해서 작은 말에도 상처받기 쉬우니, 서운한 일은 솔직하게 이야기하세요.",
        )
        Stem.IM -> DeepPersona(
            listOf(
                "임수는 넓은 바다나 큰 강처럼 스케일이 크고 자유로운 기운이에요. 생각의 폭이 넓고 흐름을 읽는 지혜가 있어 큰 그림을 잘 그려요.",
                "새로운 곳, 새로운 사람에 대한 호기심이 많고 적응력도 뛰어나요. 다만 한곳에 오래 머무는 걸 답답해해서 계획이 자주 바뀔 수 있어요.",
            ),
            "사람을 넓게 사귀고 포용력이 있어 인맥이 넓어요. 깊은 속마음은 잘 드러내지 않으니, 가까운 사람에게는 먼저 마음을 열어 보세요.",
        )
        Stem.GYE -> DeepPersona(
            listOf(
                "계수는 만물을 적시는 빗물이나 이슬처럼 조용하지만 깊이 스며드는 기운이에요. 직관과 상상력이 뛰어나고, 말하지 않아도 분위기를 잘 알아차려요.",
                "총명하고 아이디어가 많으며, 남을 돕는 일에서 보람을 느껴요. 감수성이 풍부한 만큼 걱정이 많고 기분의 영향을 많이 받는 편이에요.",
            ),
            "다정하고 공감을 잘해서 사람들이 위로를 받으러 와요. 남의 감정을 너무 떠안지 않도록, 나를 돌보는 시간도 꼭 챙기세요.",
        )
    }

    // ── 나에게 힘이 되는 것 (개운법) ────────────────────────
    data class Lucky(val element: Element, val color: String, val numbers: String, val direction: String, val items: String, val reason: String)

    fun lucky(chart: SajuChart): Lucky {
        val me = chart.dayMaster.element
        val e = if (chart.isStrong) me.generates else me.generatedBy
        val reason = if (chart.isStrong)
            "스스로 힘이 센 사주라, 그 힘을 밖으로 펼쳐 주는 ${e.korean}(${e.hanja}) 기운이 도움이 돼요."
        else
            "주변과 함께 크는 사주라, 나를 북돋아 주는 ${e.korean}(${e.hanja}) 기운이 도움이 돼요."
        return when (e) {
            Element.WOOD -> Lucky(e, "초록색 · 청록색", "3, 8", "동쪽", "화분 기르기, 아침 산책, 나무 소품, 새로운 공부 시작", reason)
            Element.FIRE -> Lucky(e, "빨간색 · 분홍색 · 보라색", "2, 7", "남쪽", "햇볕 쬐기, 땀 흘리는 운동, 밝은 조명, 사람 만나기", reason)
            Element.EARTH -> Lucky(e, "노란색 · 베이지 · 갈색", "5, 10", "중앙(집 가운데)", "도자기 소품, 등산, 규칙적인 식사, 꾸준한 루틴", reason)
            Element.METAL -> Lucky(e, "흰색 · 금색 · 은색", "4, 9", "서쪽", "금속 액세서리, 시계, 정리정돈, 원칙 세우기", reason)
            Element.WATER -> Lucky(e, "검정색 · 남색", "1, 6", "북쪽", "물 자주 마시기, 반신욕, 독서·명상, 충분한 수면", reason)
        }
    }

    // ── 다섯 가지 능력 (십신 그룹) ─────────────────────────
    enum class TenGroup(
        val emoji: String,
        val title: String,
        val ability: String,
        val meaning: String,
        val many: String,
        val some: String,
        val none: String,
    ) {
        PEER(
            "🤝", "비겁 (나와 같은 기운)", "자립심 · 추진력",
            "스스로 일어서는 힘과 친구·형제·동료와의 관계를 보여 줘요.",
            "자존심과 독립심이 강해 남의 간섭을 싫어해요. 리더십이 있지만 고집으로 부딪힐 수 있고, 돈이 나눠 쓰거나 경쟁으로 새어 나가기 쉬워요. 동업보다 단독 결정이 맞는 편이에요.",
            "자기 주관과 협동심이 적당히 균형을 이루고 있어요. 필요할 때는 내 의견을 분명히 말할 줄 알아요.",
            "혼자 밀어붙이기보다 주변과 조화를 이루는 편이에요. 자기주장을 조금 더 하고, 체력과 자신감을 기르면 좋아요.",
        ),
        OUTPUT(
            "🎨", "식상 (내가 만드는 기운)", "표현력 · 재능",
            "말솜씨, 재능, 창의력, 자녀 운을 보여 줘요.",
            "재주가 많고 아이디어가 넘쳐요. 말과 표현으로 인정받지만, 할 말을 참지 못해 오해를 사거나 일을 벌이기만 하고 마무리가 약할 수 있어요.",
            "필요할 때 자기 생각을 잘 표현하고, 재능을 적당히 드러내는 균형 잡힌 편이에요.",
            "속마음을 표현하는 데 서툴 수 있어요. 생각을 글이나 말로 꺼내는 연습을 하면 숨은 재능이 더 드러나요.",
        ),
        WEALTH(
            "💰", "재성 (내가 다스리는 기운)", "재물 감각 · 현실감",
            "재물, 현실 감각, 활동력을 보여 주고, 남성에게는 아내·연인을 뜻하기도 해요.",
            "돈 버는 감각과 활동력이 좋아요. 다만 욕심이 앞서면 무리하기 쉽고 몸이 피곤해질 수 있으니 실속을 챙기세요.",
            "현실 감각과 재물 관리가 무난한 편이에요. 꾸준히 모으면 안정적으로 불어나요.",
            "돈 자체보다 가치나 의미를 중시하는 편이에요. 재무 계획과 자동 저축 습관을 일부러 만들면 재물이 안정돼요.",
        ),
        OFFICER(
            "🏛️", "관성 (나를 다스리는 기운)", "책임감 · 사회성",
            "직장, 명예, 규칙, 책임감을 보여 주고, 여성에게는 남편·연인을 뜻하기도 해요.",
            "책임감이 강하고 조직에서 인정받지만, 부담과 스트레스를 많이 떠안을 수 있어요. 스스로를 너무 몰아붙이지 마세요.",
            "규칙을 지키면서도 자기 색을 잃지 않는 편이에요. 조직 생활에 무리 없이 적응해요.",
            "규칙이나 조직에 얽매이는 걸 답답해해요. 자유로운 일, 전문직, 프리랜서가 잘 맞을 수 있어요.",
        ),
        RESOURCE(
            "📚", "인성 (나를 돕는 기운)", "학습력 · 도움 운",
            "학문, 자격, 생각하는 힘, 어머니와 윗사람의 도움을 보여 줘요.",
            "생각이 깊고 배우기를 좋아해요. 도움을 잘 받지만 생각만 많고 실행이 늦어질 수 있으니, 결정했으면 바로 움직여 보세요.",
            "배움과 실천이 적당히 어우러진 편이에요. 필요할 때 도움을 주는 사람이 곁에 있어요.",
            "남의 도움보다 스스로 부딪혀 배우는 타입이에요. 꾸준한 공부 습관과 믿을 만한 멘토를 두면 큰 힘이 돼요.",
        );

        fun describe(count: Int): String = when {
            count == 0 -> none
            count >= 3 -> many
            else -> some
        }

        fun status(count: Int): String = when {
            count == 0 -> "없음"
            count >= 3 -> "강함"
            else -> "보통"
        }

        companion object {
            fun of(g: TenGod): TenGroup = when (g) {
                TenGod.BIGYEON, TenGod.GEOPJAE -> PEER
                TenGod.SIKSIN, TenGod.SANGGWAN -> OUTPUT
                TenGod.PYEONJAE, TenGod.JEONGJAE -> WEALTH
                TenGod.PYEONGWAN, TenGod.JEONGGWAN -> OFFICER
                TenGod.PYEONIN, TenGod.JEONGIN -> RESOURCE
            }
        }
    }

    /** 일간(나)을 뺀 나머지 글자들의 십신을 다섯 그룹으로 셉니다. */
    fun tenGroupCounts(chart: SajuChart): Map<TenGroup, Int> {
        val dm = chart.dayMaster
        val map = TenGroup.entries.associateWith { 0 }.toMutableMap()
        val gods = buildList {
            listOfNotNull(chart.hour, chart.month, chart.year).forEach { add(TenGod.of(dm, it.stem)) }
            listOfNotNull(chart.hour, chart.day, chart.month, chart.year).forEach { add(TenGod.of(dm, it.branch)) }
        }
        gods.forEach { val g = TenGroup.of(it); map[g] = map.getValue(g) + 1 }
        return map
    }

    // ── 배우자 자리(일지) · 사회생활 자리(월지) ──────────────
    fun spousePalace(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "배우자와 친구처럼 대등한 관계를 원해요. 서로 존중하면 든든한 동반자가 되지만, 둘 다 고집을 세우면 부딪힐 수 있어요."
        TenGod.GEOPJAE -> "배우자 자리에 경쟁의 기운이 있어 밀고 당기는 관계가 되기 쉬워요. 돈 문제는 투명하게, 서로의 영역을 존중하면 좋아요."
        TenGod.SIKSIN -> "가정에서 여유와 웃음을 중시해요. 배우자에게 다정하고 잘 챙겨 주며, 함께 맛있는 것 먹고 즐기는 평화로운 가정을 꾸려요."
        TenGod.SANGGWAN -> "개성 강하고 재치 있는 배우자와 인연이 많아요. 감정 표현이 솔직해 활기차지만, 말다툼은 한 박자 쉬어 가며 하세요."
        TenGod.PYEONJAE -> "활동적이고 사교적인 가정을 꾸려요. 배우자와 함께 여행·취미를 즐기면 좋고, 씀씀이는 함께 계획하세요."
        TenGod.JEONGJAE -> "성실하고 안정적인 가정을 꾸리는 자리예요. 배우자 복이 있고 살림이 차곡차곡 늘어나는 편이에요."
        TenGod.PYEONGWAN -> "카리스마 있거나 책임감이 큰 배우자와 인연이 많아요. 긴장할 때도 있지만 서로 단련되며 함께 성장해요."
        TenGod.JEONGGWAN -> "바르고 믿음직한 배우자를 만나는 자리예요. 서로 예의를 지키며 안정적이고 모범적인 가정을 이루기 쉬워요."
        TenGod.PYEONIN -> "배우자와 정신적으로 깊이 통하는 관계를 원해요. 각자 시간이 필요하니 혼자만의 공간을 존중해 주세요."
        TenGod.JEONGIN -> "배우자에게 보살핌과 도움을 받는 자리예요. 따뜻하고 포근한 가정을 꾸리며, 집안 어른의 도움도 있어요."
    }

    fun socialPalace(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "동료와 함께하는 일, 독립적인 일에 강해요. 자기 사업이나 팀 리더 역할이 잘 맞아요."
        TenGod.GEOPJAE -> "경쟁이 있는 환경에서 오히려 힘을 내요. 영업, 스포츠, 승부가 있는 일에 강하지만 동업은 신중하게 하세요."
        TenGod.SIKSIN -> "전문 기술과 재능으로 인정받는 타입이에요. 요리, 교육, 연구, 창작처럼 꾸준히 실력을 쌓는 일이 잘 맞아요."
        TenGod.SANGGWAN -> "아이디어와 말솜씨로 승부하는 타입이에요. 기획, 방송, 마케팅, 강의처럼 표현하는 일이 잘 맞아요."
        TenGod.PYEONJAE -> "넓게 움직이며 기회를 잡는 타입이에요. 사업, 무역, 투자, 유통처럼 활동 범위가 넓은 일이 잘 맞아요."
        TenGod.JEONGJAE -> "꼼꼼하고 성실해서 맡은 일을 확실히 해내요. 금융, 회계, 관리처럼 안정적인 일이 잘 맞아요."
        TenGod.PYEONGWAN -> "어려운 일을 해결하는 해결사 타입이에요. 경찰·군인, 의료, 법, 위기관리처럼 강단이 필요한 일이 잘 맞아요."
        TenGod.JEONGGWAN -> "조직에서 신뢰받고 승진하는 타입이에요. 공직, 대기업, 교육기관처럼 체계 있는 곳이 잘 맞아요."
        TenGod.PYEONIN -> "남다른 시각과 전문성으로 승부해요. IT, 연구, 디자인, 상담처럼 깊이 있는 일이 잘 맞아요."
        TenGod.JEONGIN -> "배우고 가르치며 성장하는 타입이에요. 교육, 학문, 자격이 필요한 전문직, 문서 관련 일이 잘 맞아요."
    }

    // ── 올해 분야별 운세 ───────────────────────────────────
    data class AreaFortune(val money: String, val work: String, val love: String, val health: String)

    fun yearAreas(g: TenGod): AreaFortune = when (g) {
        TenGod.BIGYEON -> AreaFortune(
            "수입은 무난하지만 나눠 쓰는 돈이 늘어요. 돈거래·보증은 피하는 게 좋아요.",
            "독립·창업이나 새 팀을 꾸리기 좋아요. 동료와 협력하면 성과가 커져요.",
            "친구 같은 편안한 인연이 찾아와요. 연인과는 서로의 자존심을 세워 주세요.",
            "체력이 좋은 해예요. 과로와 무리한 운동만 조심하세요.",
        )
        TenGod.GEOPJAE -> AreaFortune(
            "지출과 경쟁이 늘어요. 투자·동업은 신중하게, 비상금을 꼭 마련하세요.",
            "경쟁자가 나타나지만 도전 정신으로 이겨 낼 수 있어요.",
            "질투나 오해가 생기기 쉬워요. 믿음을 주는 행동이 무엇보다 중요해요.",
            "스트레스성 피로를 조심하고 충분히 쉬세요.",
        )
        TenGod.SIKSIN -> AreaFortune(
            "재능으로 버는 돈이 생겨요. 부업이나 취미가 수입이 될 수 있어요.",
            "실력을 인정받고 일이 즐거워져요. 새로운 기술을 배우기 좋아요.",
            "연애운이 좋아요. 함께 맛있는 것 먹고 여행하며 사이가 깊어져요.",
            "컨디션이 좋은 해예요. 과식과 체중 증가만 조심하세요.",
        )
        TenGod.SANGGWAN -> AreaFortune(
            "아이디어가 돈이 되는 해예요. 다만 계약서는 꼼꼼히 확인하세요.",
            "변화와 이직 욕구가 커져요. 윗사람과의 마찰은 피하고 실력으로 보여 주세요.",
            "매력이 돋보여 인기가 많아요. 말 한마디로 다투지 않게 조심하세요.",
            "신경이 예민해질 수 있어요. 목과 호흡기를 잘 챙기세요.",
        )
        TenGod.PYEONJAE -> AreaFortune(
            "큰돈이 오가는 해예요. 기회는 많지만 무리한 투자는 금물이에요.",
            "활동 범위가 넓어지고 출장·이동이 많아요. 영업·사업에 좋아요.",
            "새로운 만남이 많아요. 특히 남성은 이성 인연이 활발해요.",
            "바쁘게 움직이다 지칠 수 있어요. 틈틈이 쉬어 가세요.",
        )
        TenGod.JEONGJAE -> AreaFortune(
            "꾸준한 수입과 저축운이 좋아요. 내 집 마련·적금 계획에 좋은 해예요.",
            "성실함이 인정받아 안정적인 성과를 내요.",
            "진지한 만남과 결혼운이 있어요. 특히 남성은 배우자 인연이 좋아요.",
            "규칙적인 생활로 건강을 지키기 좋은 해예요.",
        )
        TenGod.PYEONGWAN -> AreaFortune(
            "예상치 못한 지출이 생길 수 있어요. 보험·비상금으로 대비하세요.",
            "책임이 큰 일을 맡아요. 힘들지만 이겨 내면 크게 인정받아요.",
            "강렬한 인연이 찾아오지만 갈등도 커질 수 있어요. 특히 여성은 이성 인연이 활발해요.",
            "과로와 스트레스, 사고를 조심하세요. 무리하지 마세요.",
        )
        TenGod.JEONGGWAN -> AreaFortune(
            "정당한 보상과 승진으로 수입이 안정돼요.",
            "승진·취업·합격 운이 좋아요. 공식적인 자리에서 빛나요.",
            "믿음직한 인연과 결혼운이 있어요. 특히 여성은 배우자 인연이 좋아요.",
            "무난한 해예요. 바른 생활 습관을 유지하세요.",
        )
        TenGod.PYEONIN -> AreaFortune(
            "큰돈보다 배움과 자격에 투자하기 좋은 해예요.",
            "전문성을 키우고 새로운 분야를 공부하기 좋아요. 이직보다는 내실을 다지세요.",
            "혼자만의 시간이 필요해 연애가 조금 소극적일 수 있어요.",
            "생각이 많아 잠이 부족해질 수 있어요. 명상과 산책을 권해요.",
        )
        TenGod.JEONGIN -> AreaFortune(
            "문서·계약 운이 좋아 부동산이나 계약에서 이득이 있어요.",
            "시험·자격증·승진에 좋은 해예요. 윗사람의 도움을 받아요.",
            "따뜻하고 배려 깊은 인연이 찾아와요. 가족의 지지도 있어요.",
            "몸과 마음이 편안한 해예요. 충분히 쉬며 에너지를 채우세요.",
        )
    }

    // ── 월별 운세 ─────────────────────────────────────────
    data class MonthFortune(
        val calendarMonth: Int,
        val calendarYear: Int,
        val pillar: Pillar,
        val god: TenGod,
        val text: String,
        val relation: String?,
    )

    fun monthText(g: TenGod): String = when (g) {
        TenGod.BIGYEON -> "내 뜻대로 밀고 나가는 달. 협력하면 힘이 두 배가 돼요."
        TenGod.GEOPJAE -> "경쟁과 지출을 조심하는 달. 지갑을 단단히 닫으세요."
        TenGod.SIKSIN -> "여유롭고 즐거운 달. 취미·창작·맛있는 음식에 행운이 있어요."
        TenGod.SANGGWAN -> "아이디어가 빛나는 달. 말조심만 하면 좋은 성과가 나요."
        TenGod.PYEONJAE -> "기회가 많은 달. 적극적으로 움직이되 큰 투자는 신중하게."
        TenGod.JEONGJAE -> "성실함이 보상받는 달. 저축과 정리에 좋아요."
        TenGod.PYEONGWAN -> "책임이 무거운 달. 무리하지 말고 하나씩 처리하세요."
        TenGod.JEONGGWAN -> "인정받는 달. 면접·보고 등 공식적인 일에 좋아요."
        TenGod.PYEONIN -> "공부와 연구에 좋은 달. 생각을 정리하는 시간을 가지세요."
        TenGod.JEONGIN -> "도움을 받는 달. 문서·계약·시험에 좋아요."
    }

    private fun relationTag(mine: Branch, other: Branch): String? = when {
        Relations.isSixCombination(mine, other) -> "합 · 손발이 잘 맞아요"
        Relations.isTripleCombination(mine, other) -> "삼합 · 협력하면 술술"
        Relations.isClash(mine, other) -> "충 · 변화·이동 주의"
        Relations.isWonjin(mine, other) -> "원진 · 오해 주의"
        else -> null
    }

    /** 사주 연도(입춘~다음 해 입춘 전)의 12달 운세. 첫 달은 양력 2월(인월)입니다. */
    fun monthlyFortunes(chart: SajuChart, sajuYear: Int): List<MonthFortune> {
        val yearStem = SajuCalculator.yearPillarOf(sajuYear).stem
        return (0 until 12).map { k ->
            val stem = Stem.entries[(yearStem.ordinal % 5 * 2 + 2 + k) % 10]
            val branch = Branch.entries[(2 + k) % 12]
            val pillar = Pillar.of(stem, branch)
            val god = TenGod.of(chart.dayMaster, stem)
            val calMonth = (k + 1) % 12 + 1
            val calYear = if (k == 11) sajuYear + 1 else sajuYear
            MonthFortune(calMonth, calYear, pillar, god, monthText(god), relationTag(chart.day.branch, branch))
        }
    }

    /** 오늘 날짜가 속한 사주 연도 (2월 초 입춘 전이면 전년도) */
    fun sajuYearOf(date: LocalDate): Int =
        if (date.monthValue == 1 || (date.monthValue == 2 && date.dayOfMonth < 4)) date.year - 1 else date.year

    // ── 대운 분야별 ───────────────────────────────────────
    data class DaeunAreas(val work: String, val money: String, val love: String)

    fun daeunAreas(g: TenGod): DaeunAreas = when (g) {
        TenGod.BIGYEON -> DaeunAreas("독립·창업이나 리더 역할이 늘어요.", "버는 만큼 나누는 일도 많아요. 공동 투자는 신중히.", "친구 같은 동반자와 함께 성장해요.")
        TenGod.GEOPJAE -> DaeunAreas("경쟁 속에서 실력이 단련돼요.", "지출 관리가 가장 중요해요.", "관계에서 주도권 다툼을 조심하세요.")
        TenGod.SIKSIN -> DaeunAreas("전문 기술과 재능으로 인정받아요.", "재능이 안정적인 수입으로 이어져요.", "가정이 화목하고 자녀 운이 좋아요.")
        TenGod.SANGGWAN -> DaeunAreas("새로운 분야 도전, 이직·전직이 많을 수 있어요.", "아이디어로 버는 기회가 와요.", "솔직한 표현이 매력이 되는 시기예요.")
        TenGod.PYEONJAE -> DaeunAreas("사업·영업 등 활동적인 일이 커져요.", "큰돈이 들어오고 나가요. 분산 투자가 답이에요.", "사교 활동이 늘고 인연이 많아요.")
        TenGod.JEONGJAE -> DaeunAreas("안정된 직장에서 차근차근 올라가요.", "저축과 자산이 꾸준히 늘어요.", "결혼·가정을 꾸리기 좋은 시기예요.")
        TenGod.PYEONGWAN -> DaeunAreas("중책을 맡거나 큰 시험대에 서요.", "예상 밖 지출에 대비하세요.", "강렬한 인연, 서로 단련되는 관계예요.")
        TenGod.JEONGGWAN -> DaeunAreas("승진·명예·안정된 지위를 얻어요.", "정당한 보상이 따라와요.", "믿음직한 배우자 인연, 결혼운이 있어요.")
        TenGod.PYEONIN -> DaeunAreas("전문 분야 연구, 자격·기술 습득에 좋아요.", "큰돈보다 실력 쌓기에 투자하세요.", "혼자만의 시간이 소중해지는 시기예요.")
        TenGod.JEONGIN -> DaeunAreas("공부·자격·문서 운이 좋아요.", "부동산·계약에서 이득이 있어요.", "따뜻한 보살핌과 가족의 지지가 있어요.")
    }

    // ── 오늘 하면 좋은 일 / 피할 일 ─────────────────────────
    fun dailyTodo(g: TenGod): Pair<String, String> = when (g) {
        TenGod.BIGYEON -> "혼자 집중하는 일, 친구와의 약속" to "돈 빌려주기, 고집 부리기"
        TenGod.GEOPJAE -> "운동으로 에너지 풀기, 지출 기록하기" to "충동구매, 내기·투자 결정"
        TenGod.SIKSIN -> "맛있는 식사, 취미 활동, 창작" to "과식, 해야 할 일 미루기"
        TenGod.SANGGWAN -> "아이디어 메모, 발표·글쓰기" to "윗사람에게 직언, 감정적인 메시지"
        TenGod.PYEONJAE -> "새로운 사람 만나기, 영업·외출" to "큰 금액 투자, 무리한 약속"
        TenGod.JEONGJAE -> "가계부 정리, 밀린 일 처리, 저축" to "즉흥적인 계획 변경"
        TenGod.PYEONGWAN -> "중요한 일 하나씩 처리, 일찍 잠자기" to "과로, 무리한 운전·운동"
        TenGod.JEONGGWAN -> "면접·보고·공식 일정, 예의 갖추기" to "규칙을 어기는 행동"
        TenGod.PYEONIN -> "공부·독서, 혼자 생각 정리, 산책" to "중요한 결정 서두르기"
        TenGod.JEONGIN -> "배우기, 서류 검토, 감사 인사 전하기" to "게으름, 남에게 너무 기대기"
    }
}
