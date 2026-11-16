package com.gracekim.saju.core

enum class Element(val korean: String, val hanja: String) {
    WOOD("목", "木"), FIRE("화", "火"), EARTH("토", "土"), METAL("금", "金"), WATER("수", "水");

    /** 내가 생(生)하는 오행 */
    val generates: Element get() = entries[(ordinal + 1) % 5]

    /** 내가 극(剋)하는 오행 */
    val controls: Element get() = entries[(ordinal + 2) % 5]

    /** 나를 생하는 오행 */
    val generatedBy: Element get() = entries[(ordinal + 4) % 5]
}

enum class Stem(val korean: String, val hanja: String, val element: Element, val isYang: Boolean) {
    GAP("갑", "甲", Element.WOOD, true),
    EUL("을", "乙", Element.WOOD, false),
    BYEONG("병", "丙", Element.FIRE, true),
    JEONG("정", "丁", Element.FIRE, false),
    MU("무", "戊", Element.EARTH, true),
    GI("기", "己", Element.EARTH, false),
    GYEONG("경", "庚", Element.METAL, true),
    SIN("신", "辛", Element.METAL, false),
    IM("임", "壬", Element.WATER, true),
    GYE("계", "癸", Element.WATER, false);
}

enum class Branch(
    val korean: String,
    val hanja: String,
    val element: Element,
    val isYang: Boolean,
    val animal: String,
    /** 지장간 정기(본기) */
    val mainHiddenStem: Stem,
    val hourRange: String,
) {
    JA("자", "子", Element.WATER, true, "쥐", Stem.GYE, "23:00~01:00"),
    CHUK("축", "丑", Element.EARTH, false, "소", Stem.GI, "01:00~03:00"),
    IN("인", "寅", Element.WOOD, true, "호랑이", Stem.GAP, "03:00~05:00"),
    MYO("묘", "卯", Element.WOOD, false, "토끼", Stem.EUL, "05:00~07:00"),
    JIN("진", "辰", Element.EARTH, true, "용", Stem.MU, "07:00~09:00"),
    SA("사", "巳", Element.FIRE, false, "뱀", Stem.BYEONG, "09:00~11:00"),
    O("오", "午", Element.FIRE, true, "말", Stem.JEONG, "11:00~13:00"),
    MI("미", "未", Element.EARTH, false, "양", Stem.GI, "13:00~15:00"),
    SHIN("신", "申", Element.METAL, true, "원숭이", Stem.GYEONG, "15:00~17:00"),
    YU("유", "酉", Element.METAL, false, "닭", Stem.SIN, "17:00~19:00"),
    SUL("술", "戌", Element.EARTH, true, "개", Stem.MU, "19:00~21:00"),
    HAE("해", "亥", Element.WATER, false, "돼지", Stem.IM, "21:00~23:00");
}

/** 60갑자 중 하나. index 0 = 갑자 */
data class Pillar(val index: Int) {
    init {
        require(index in 0 until 60)
    }

    val stem: Stem get() = Stem.entries[index % 10]
    val branch: Branch get() = Branch.entries[index % 12]

    val korean: String get() = stem.korean + branch.korean
    val hanja: String get() = stem.hanja + branch.hanja

    fun plus(n: Int): Pillar = Pillar(Math.floorMod(index + n, 60))

    override fun toString(): String = "$korean($hanja)"

    companion object {
        fun of(stem: Stem, branch: Branch): Pillar {
            require(stem.isYang == branch.isYang) { "음양이 맞지 않는 간지입니다." }
            return Pillar(Math.floorMod(6 * stem.ordinal - 5 * branch.ordinal, 60))
        }

        fun ofIndex(i: Int): Pillar = Pillar(Math.floorMod(i, 60))
    }
}

enum class TenGod(val korean: String, val hanja: String) {
    BIGYEON("비견", "比肩"),
    GEOPJAE("겁재", "劫財"),
    SIKSIN("식신", "食神"),
    SANGGWAN("상관", "傷官"),
    PYEONJAE("편재", "偏財"),
    JEONGJAE("정재", "正財"),
    PYEONGWAN("편관", "偏官"),
    JEONGGWAN("정관", "正官"),
    PYEONIN("편인", "偏印"),
    JEONGIN("정인", "正印");

    companion object {
        /** 일간 [dayStem] 기준으로 [other] 천간의 십신 */
        fun of(dayStem: Stem, other: Stem): TenGod {
            val rel = Math.floorMod(other.element.ordinal - dayStem.element.ordinal, 5)
            val same = dayStem.isYang == other.isYang
            return when (rel) {
                0 -> if (same) BIGYEON else GEOPJAE
                1 -> if (same) SIKSIN else SANGGWAN
                2 -> if (same) PYEONJAE else JEONGJAE
                3 -> if (same) PYEONGWAN else JEONGGWAN
                else -> if (same) PYEONIN else JEONGIN
            }
        }

        fun of(dayStem: Stem, branch: Branch): TenGod = of(dayStem, branch.mainHiddenStem)
    }
}
