package com.gracekim.saju.data

import android.content.Context
import java.time.LocalDate
import java.time.YearMonth

/** 복주머니 출석 체크 상태 (휴대폰 안에만 저장) */
class BokStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("saju_bok", Context.MODE_PRIVATE)

    data class State(
        val count: Int,
        val streak: Int,
        val checkedToday: Boolean,
        val adUsedToday: Boolean,
        val unlockedMonth: String,
    )

    fun state(today: LocalDate = LocalDate.now()): State {
        val day = today.toEpochDay()
        val last = prefs.getLong(KEY_LAST_CHECK, Long.MIN_VALUE)
        val streak = prefs.getInt(KEY_STREAK, 0)
        return State(
            count = prefs.getInt(KEY_COUNT, 0),
            streak = if (last == day || last == day - 1) streak else 0,
            checkedToday = last == day,
            adUsedToday = prefs.getLong(KEY_LAST_AD, Long.MIN_VALUE) == day,
            unlockedMonth = prefs.getString(KEY_UNLOCKED, "") ?: "",
        )
    }

    /** 출석 체크. 받은 복주머니 수를 돌려줍니다 (이미 했으면 0). 7일 연속마다 보너스 3개. */
    fun checkIn(today: LocalDate = LocalDate.now()): Int {
        val day = today.toEpochDay()
        val last = prefs.getLong(KEY_LAST_CHECK, Long.MIN_VALUE)
        if (last == day) return 0
        val streak = if (last == day - 1) prefs.getInt(KEY_STREAK, 0) + 1 else 1
        val gained = 1 + if (streak % 7 == 0) 3 else 0
        prefs.edit()
            .putLong(KEY_LAST_CHECK, day)
            .putInt(KEY_STREAK, streak)
            .putInt(KEY_COUNT, prefs.getInt(KEY_COUNT, 0) + gained)
            .apply()
        return gained
    }

    /** 광고 보상: 하루 한 번 2개 */
    fun adReward(today: LocalDate = LocalDate.now()): Int {
        val day = today.toEpochDay()
        if (prefs.getLong(KEY_LAST_AD, Long.MIN_VALUE) == day) return 0
        prefs.edit()
            .putLong(KEY_LAST_AD, day)
            .putInt(KEY_COUNT, prefs.getInt(KEY_COUNT, 0) + 2)
            .apply()
        return 2
    }

    /** 복주머니 [cost]개로 이번 달 처방전 열기 */
    fun unlockMonth(ym: YearMonth, cost: Int = COST): Boolean {
        val count = prefs.getInt(KEY_COUNT, 0)
        if (prefs.getString(KEY_UNLOCKED, "") == ym.toString()) return true
        if (count < cost) return false
        prefs.edit().putInt(KEY_COUNT, count - cost).putString(KEY_UNLOCKED, ym.toString()).apply()
        return true
    }

    companion object {
        const val COST = 3
        private const val KEY_COUNT = "count"
        private const val KEY_STREAK = "streak"
        private const val KEY_LAST_CHECK = "last_check"
        private const val KEY_LAST_AD = "last_ad"
        private const val KEY_UNLOCKED = "unlocked_month"
    }
}
