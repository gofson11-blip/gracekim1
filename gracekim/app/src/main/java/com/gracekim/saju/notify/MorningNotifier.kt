package com.gracekim.saju.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gracekim.saju.MainActivity
import com.gracekim.saju.R
import com.gracekim.saju.core.DailyFortune
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.data.ProfileStore
import com.gracekim.saju.widget.FortuneWidget
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/** 매일 아침 8시쯤 오늘의 운세 알림 (휴대폰 안에서만 계산, 서버 없음) */
object MorningNotifier {
    private const val CHANNEL = "morning_fortune"
    private const val WORK = "morning_fortune_work"
    private const val PREFS = "saju_settings"
    private const val KEY_ENABLED = "morning_notify"
    private val NOTIFY_TIME: LocalTime = LocalTime.of(8, 0)

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, enabled).apply()
        schedule(context)
    }

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** 켜져 있으면 다음 아침 8시부터 매일 실행되도록 예약합니다. */
    fun schedule(context: Context) {
        val wm = WorkManager.getInstance(context)
        if (!isEnabled(context)) {
            wm.cancelUniqueWork(WORK)
            return
        }
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(NOTIFY_TIME)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delay = Duration.between(now, next).toMillis()
        val request = PeriodicWorkRequestBuilder<MorningWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL, context.getString(R.string.notify_channel), NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    fun post(context: Context) {
        if (!isEnabled(context) || !hasPermission(context)) return
        val profile = ProfileStore(context).load().firstOrNull() ?: return
        val chart = runCatching { SajuCalculator.calculate(profile.toBirthInput()) }.getOrNull() ?: return
        val f = DailyFortune.of(chart, LocalDate.now())
        val name = profile.name.ifBlank { "나" }
        ensureChannel(context)
        val pending = PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_moon)
            .setContentTitle("🌙 ${name}님, 오늘의 운세가 도착했어요")
            .setContentText("오늘 ${f.score}점 · ${f.headline}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("오늘 ${f.score}점 · ${f.headline}\n${f.message}\n행운의 색 ${f.luckyColor} · 숫자 ${f.luckyNumbers}"))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        runCatching { NotificationManagerCompat.from(context).notify(1001, notification) }
    }
}

class MorningWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        runCatching { FortuneWidget.updateAll(applicationContext) }
        MorningNotifier.post(applicationContext)
        return Result.success()
    }
}
