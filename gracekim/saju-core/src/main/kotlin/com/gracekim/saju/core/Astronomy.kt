package com.gracekim.saju.core

import java.time.Instant
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

/**
 * 절기(태양 황경)와 합삭(새달) 계산.
 * Jean Meeus, "Astronomical Algorithms" 의 저정밀 공식을 사용합니다.
 * 절기 시각 오차는 대략 수 분~15분 이내입니다.
 *
 * 모든 JD 는 UT 기준이며, 내부적으로 ΔT 를 더해 역학시(TT)로 바꿔 계산합니다.
 */
object Astronomy {
    private const val UNIX_EPOCH_JD = 2440587.5
    private const val TROPICAL_YEAR = 365.2422
    private const val SYNODIC_MONTH = 29.530588861

    /** 한국 표준시(UTC+9) 오프셋 (일 단위). */
    const val KST_OFFSET_DAYS = 9.0 / 24.0

    fun jdFromInstant(instant: Instant): Double =
        instant.epochSecond / 86400.0 + instant.nano / 86_400_000_000_000.0 + UNIX_EPOCH_JD

    fun instantFromJd(jd: Double): Instant {
        val millis = Math.round((jd - UNIX_EPOCH_JD) * 86_400_000.0)
        return Instant.ofEpochMilli(millis)
    }

    /** UT JD 를 한국 시간 기준 날짜(epochDay)로 변환. */
    fun kstEpochDay(jdUt: Double): Long = floor(jdUt - UNIX_EPOCH_JD + KST_OFFSET_DAYS).toLong()

    fun kstDate(jdUt: Double): LocalDate = LocalDate.ofEpochDay(kstEpochDay(jdUt))

    /** 한국 시간 기준 해당 날짜 00:00 의 UT JD. */
    fun jdOfKstMidnight(epochDay: Long): Double = epochDay + UNIX_EPOCH_JD - KST_OFFSET_DAYS

    /** ΔT = TT - UT (초). Espenak & Meeus 다항식 근사. */
    fun deltaTSeconds(year: Double): Double {
        val y = year
        return when {
            y < 1900 -> { val u = (y - 1820) / 100; -20 + 32 * u * u }
            y < 1920 -> { val t = y - 1900; -2.79 + 1.494119 * t - 0.0598939 * t * t + 0.0061966 * t * t * t - 0.000197 * t * t * t * t }
            y < 1941 -> { val t = y - 1920; 21.20 + 0.84493 * t - 0.076100 * t * t + 0.0020936 * t * t * t }
            y < 1961 -> { val t = y - 1950; 29.07 + 0.407 * t - t * t / 233 + t * t * t / 2547 }
            y < 1986 -> { val t = y - 1975; 45.45 + 1.067 * t - t * t / 260 - t * t * t / 718 }
            y < 2005 -> { val t = y - 2000; 63.86 + 0.3345 * t - 0.060374 * t * t + 0.0017275 * t * t * t + 0.000651814 * t * t * t * t + 0.00002373599 * t * t * t * t * t }
            y < 2050 -> { val t = y - 2000; 62.92 + 0.32217 * t + 0.005589 * t * t }
            y < 2150 -> { val u = (y - 1820) / 100; -20 + 32 * u * u - 0.5628 * (2150 - y) }
            else -> { val u = (y - 1820) / 100; -20 + 32 * u * u }
        }
    }

    private fun yearOfJd(jd: Double): Double = 2000.0 + (jd - 2451545.0) / 365.25

    fun utToTt(jdUt: Double): Double = jdUt + deltaTSeconds(yearOfJd(jdUt)) / 86400.0

    fun ttToUt(jdTt: Double): Double = jdTt - deltaTSeconds(yearOfJd(jdTt)) / 86400.0

    private fun rad(deg: Double) = Math.toRadians(deg)

    private fun norm360(x: Double): Double {
        val r = x % 360.0
        return if (r < 0) r + 360.0 else r
    }

    /** 태양의 겉보기 황경(도), 입력은 UT JD. */
    fun solarLongitude(jdUt: Double): Double {
        val t = (utToTt(jdUt) - 2451545.0) / 36525.0
        val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
        val m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(rad(m)) +
            (0.019993 - 0.000101 * t) * sin(rad(2 * m)) +
            0.000289 * sin(rad(3 * m))
        val omega = 125.04 - 1934.136 * t
        val lambda = l0 + c - 0.00569 - 0.00478 * sin(rad(omega))
        return norm360(lambda)
    }

    /** 태양 황경이 [targetLongitude] 가 되는 시각(UT JD). [approxJdUt] 근처(±반년)에서 찾습니다. */
    fun solarTermJd(targetLongitude: Double, approxJdUt: Double): Double {
        var jd = approxJdUt
        repeat(60) {
            var diff = targetLongitude - solarLongitude(jd)
            diff = ((diff % 360.0) + 540.0) % 360.0 - 180.0
            jd += diff * TROPICAL_YEAR / 360.0
            if (abs(diff) < 1e-7) return jd
        }
        return jd
    }

    /** 합삭 시각(UT JD). k 는 2000년 1월 6일 합삭을 0 으로 하는 정수 번호. */
    fun newMoonJd(k: Long): Double {
        val kk = k.toDouble()
        val t = kk / 1236.85
        val t2 = t * t
        val t3 = t2 * t
        val t4 = t3 * t
        var jde = 2451550.09766 + SYNODIC_MONTH * kk + 0.00015437 * t2 - 0.000000150 * t3 + 0.00000000073 * t4
        val e = 1 - 0.002516 * t - 0.0000074 * t2
        val m = rad(2.5534 + 29.10535670 * kk - 0.0000014 * t2 - 0.00000011 * t3)
        val mp = rad(201.5643 + 385.81693528 * kk + 0.0107582 * t2 + 0.00001238 * t3 - 0.000000058 * t4)
        val f = rad(160.7108 + 390.67050284 * kk - 0.0016118 * t2 - 0.00000227 * t3 + 0.000000011 * t4)
        val om = rad(124.7746 - 1.56375588 * kk + 0.0020672 * t2 + 0.00000215 * t3)

        jde += -0.40720 * sin(mp) +
            0.17241 * e * sin(m) +
            0.01608 * sin(2 * mp) +
            0.01039 * sin(2 * f) +
            0.00739 * e * sin(mp - m) -
            0.00514 * e * sin(mp + m) +
            0.00208 * e * e * sin(2 * m) -
            0.00111 * sin(mp - 2 * f) -
            0.00057 * sin(mp + 2 * f) +
            0.00056 * e * sin(2 * mp + m) -
            0.00042 * sin(3 * mp) +
            0.00042 * e * sin(m + 2 * f) +
            0.00038 * e * sin(m - 2 * f) -
            0.00024 * e * sin(2 * mp - m) -
            0.00017 * sin(om) -
            0.00007 * sin(mp + 2 * m) +
            0.00004 * sin(2 * mp - 2 * f) +
            0.00004 * sin(3 * m) +
            0.00003 * sin(mp + m - 2 * f) +
            0.00003 * sin(2 * mp + 2 * f) -
            0.00003 * sin(mp + m + 2 * f) +
            0.00003 * sin(mp - m + 2 * f) -
            0.00002 * sin(mp - m - 2 * f) -
            0.00002 * sin(3 * mp + m) +
            0.00002 * sin(4 * mp)

        // 행성 보정 (주요 항)
        val a1 = rad(299.77 + 0.107408 * kk - 0.009173 * t2)
        val a2 = rad(251.88 + 0.016321 * kk)
        val a3 = rad(251.83 + 26.651886 * kk)
        val a4 = rad(349.42 + 36.412478 * kk)
        val a5 = rad(84.66 + 18.206239 * kk)
        jde += 0.000325 * sin(a1) + 0.000165 * sin(a2) + 0.000164 * sin(a3) +
            0.000126 * sin(a4) + 0.000110 * sin(a5)

        return ttToUt(jde)
    }

    /** 한국 시간 기준으로 [epochDay] 당일 또는 그 이전에 시작하는 가장 가까운 합삭 번호. */
    fun newMoonIndexOnOrBefore(epochDay: Long): Long {
        val jd = jdOfKstMidnight(epochDay) + 1.0
        var k = floor((jd - 2451550.09766) / SYNODIC_MONTH).toLong()
        while (kstEpochDay(newMoonJd(k)) > epochDay) k--
        while (kstEpochDay(newMoonJd(k + 1)) <= epochDay) k++
        return k
    }
}
