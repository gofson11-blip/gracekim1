package com.gracekim.saju.core

/** 천간·지지 사이의 합·충 관계 */
object Relations {
    /** 천간합: 갑기, 을경, 병신, 정임, 무계 */
    fun isStemCombination(a: Stem, b: Stem): Boolean = Math.abs(a.ordinal - b.ordinal) == 5

    /** 육합: 자축, 인해, 묘술, 진유, 사신, 오미 */
    fun isSixCombination(a: Branch, b: Branch): Boolean = (a.ordinal + b.ordinal) % 12 == 1

    private val tripleGroups = listOf(
        setOf(Branch.SHIN, Branch.JA, Branch.JIN),   // 수국
        setOf(Branch.HAE, Branch.MYO, Branch.MI),    // 목국
        setOf(Branch.IN, Branch.O, Branch.SUL),      // 화국
        setOf(Branch.SA, Branch.YU, Branch.CHUK),    // 금국
    )

    /** 삼합 (같은 무리의 서로 다른 두 지지) */
    fun isTripleCombination(a: Branch, b: Branch): Boolean =
        a != b && tripleGroups.any { a in it && b in it }

    /** 충: 서로 마주 보는 지지 (자오, 축미, 인신, 묘유, 진술, 사해) */
    fun isClash(a: Branch, b: Branch): Boolean = Math.floorMod(a.ordinal - b.ordinal, 12) == 6

    private val wonjin = setOf(
        setOf(Branch.JA, Branch.MI), setOf(Branch.CHUK, Branch.O), setOf(Branch.IN, Branch.YU),
        setOf(Branch.MYO, Branch.SHIN), setOf(Branch.JIN, Branch.HAE), setOf(Branch.SA, Branch.SUL),
    )

    /** 원진: 서로 미워하고 꺼리는 관계 */
    fun isWonjin(a: Branch, b: Branch): Boolean = setOf(a, b) in wonjin
}
