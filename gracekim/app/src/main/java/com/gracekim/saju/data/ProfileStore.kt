package com.gracekim.saju.data

import android.content.Context
import com.gracekim.saju.core.CalendarType
import com.gracekim.saju.core.Gender
import com.gracekim.saju.ui.FormData
import org.json.JSONArray
import org.json.JSONObject

/**
 * 저장된 사람들의 생년월일 정보. 휴대폰 안(SharedPreferences)에만 저장하며 외부로 보내지 않습니다.
 * 목록의 첫 번째 사람이 앱을 열 때 바로 보여 줄 사람입니다.
 */
class ProfileStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("saju_profiles", Context.MODE_PRIVATE)

    fun load(): List<FormData> = runCatching {
        val arr = JSONArray(prefs.getString(KEY, "[]"))
        (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    private fun save(list: List<FormData>) {
        val arr = JSONArray()
        list.forEach { arr.put(toJson(it)) }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    /** 같은 이름(또는 똑같은 정보)이 있으면 바꾸고, 맨 앞으로 올립니다. */
    fun upsert(f: FormData): List<FormData> {
        val name = f.name.trim()
        val rest = load().filterNot { it.sameAs(f) || (name.isNotEmpty() && it.name.trim() == name) }
        val list = (listOf(f) + rest).take(MAX)
        save(list)
        return list
    }

    /** 고른 사람을 맨 앞으로 (다음에 앱을 열면 이 사람을 보여 줌) */
    fun moveToFront(f: FormData): List<FormData> {
        val list = load()
        val found = list.firstOrNull { it.sameAs(f) } ?: return list
        val newList = listOf(found) + list.filterNot { it.sameAs(f) }
        save(newList)
        return newList
    }

    fun delete(f: FormData): List<FormData> {
        val list = load().filterNot { it.sameAs(f) }
        save(list)
        return list
    }

    private fun toJson(f: FormData) = JSONObject().apply {
        put("name", f.name)
        put("gender", f.gender.name)
        put("calendar", f.calendarType.name)
        put("leap", f.isLeapMonth)
        put("year", f.year)
        put("month", f.month)
        put("day", f.day)
        put("hour", f.hour)
        put("minute", f.minute)
        put("timeUnknown", f.timeUnknown)
        put("correction", f.longitudeCorrection)
    }

    private fun fromJson(o: JSONObject) = FormData(
        name = o.optString("name"),
        gender = runCatching { Gender.valueOf(o.optString("gender")) }.getOrDefault(Gender.FEMALE),
        calendarType = runCatching { CalendarType.valueOf(o.optString("calendar")) }.getOrDefault(CalendarType.SOLAR),
        isLeapMonth = o.optBoolean("leap"),
        year = o.optString("year"),
        month = o.optString("month"),
        day = o.optString("day"),
        hour = o.optString("hour"),
        minute = o.optString("minute"),
        timeUnknown = o.optBoolean("timeUnknown"),
        longitudeCorrection = o.optBoolean("correction", true),
    )

    companion object {
        private const val KEY = "profiles"
        private const val MAX = 30
    }
}

/** 저장 여부 체크 상태는 빼고 생년월일 정보만 비교합니다. */
fun FormData.sameAs(o: FormData): Boolean = copy(saveProfile = true) == o.copy(saveProfile = true)
