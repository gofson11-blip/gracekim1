package com.gracekim.saju.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.gracekim.saju.MainActivity
import com.gracekim.saju.R
import com.gracekim.saju.core.DailyFortune
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.data.ProfileStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** 홈 화면 위젯: 저장된 첫 번째 사람의 오늘 운세를 보여 줍니다. */
class FortuneWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, FortuneWidget::class.java))
            ids.forEach { update(context, manager, it) }
        }

        private fun update(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_fortune)
            val profile = ProfileStore(context).load().firstOrNull()
            val chart = profile?.let { runCatching { SajuCalculator.calculate(it.toBirthInput()) }.getOrNull() }
            val today = LocalDate.now()
            if (profile == null || chart == null) {
                views.setTextViewText(R.id.widget_score, "--")
                views.setTextViewText(R.id.widget_headline, context.getString(R.string.widget_empty))
                views.setTextViewText(R.id.widget_sub, "")
                views.setTextViewText(R.id.widget_lucky, "")
            } else {
                val f = DailyFortune.of(chart, today)
                val name = profile.name.ifBlank { "나" }
                views.setTextViewText(R.id.widget_title, "사주사랑 · ${name}의 오늘")
                views.setTextViewText(R.id.widget_score, f.score.toString())
                views.setTextViewText(R.id.widget_headline, f.headline)
                views.setTextViewText(
                    R.id.widget_sub,
                    "${today.format(DateTimeFormatter.ofPattern("M월 d일"))} · ${f.dayPillar.korean}일 · ${f.tenGod.korean}",
                )
                views.setTextViewText(R.id.widget_lucky, "행운의 색 ${f.luckyColor} · 숫자 ${f.luckyNumbers} · ${f.luckyDirection}")
            }
            val pending = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            views.setOnClickPendingIntent(R.id.widget_root, pending)
            manager.updateAppWidget(id, views)
        }
    }
}
