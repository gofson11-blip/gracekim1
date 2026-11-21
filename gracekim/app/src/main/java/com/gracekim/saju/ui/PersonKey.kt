package com.gracekim.saju.ui

import com.gracekim.saju.core.SajuChart

/** 저장된 사람을 구분하는 키 (타로·복주머니 처방전에 사용) */
fun personKeyOf(chart: SajuChart): String {
    val input = chart.input
    return "${input.name}|${input.year}-${input.month}-${input.day}|${input.time}"
}
