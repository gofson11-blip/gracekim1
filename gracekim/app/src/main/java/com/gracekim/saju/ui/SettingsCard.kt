package com.gracekim.saju.ui

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gracekim.saju.notify.MorningNotifier
import com.gracekim.saju.widget.FortuneWidget

/** ⏰ 아침 운세 알림 · 📱 홈 화면 위젯 */
@Composable
fun SettingsCard() {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(MorningNotifier.isEnabled(context) && MorningNotifier.hasPermission(context)) }
    var info by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            MorningNotifier.setEnabled(context, true)
            enabled = true
            info = "내일 아침 8시부터 운세 알림을 보내 드릴게요."
        } else {
            info = "알림 권한이 꺼져 있어요. 휴대폰 설정 → 애플리케이션 → 사주사랑 → 알림에서 켤 수 있어요."
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("⚙️ 알림과 위젯", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("⏰ 매일 아침 8시 운세 알림", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "저장된 첫 번째 사람(지금 보고 있는 사람)의 오늘 운세를 알려 드려요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = { on ->
                        if (!on) {
                            MorningNotifier.setEnabled(context, false)
                            enabled = false
                            info = "아침 운세 알림을 껐어요."
                        } else if (Build.VERSION.SDK_INT >= 33 && !MorningNotifier.hasPermission(context)) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            MorningNotifier.setEnabled(context, true)
                            enabled = true
                            info = "내일 아침 8시부터 운세 알림을 보내 드릴게요."
                        }
                    },
                )
            }
            Text("📱 홈 화면 위젯", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(
                "홈 화면에서 오늘의 점수와 한 줄 운세를 바로 볼 수 있어요. 홈 화면 빈 곳을 길게 누르기 → 위젯 → 사주사랑을 골라도 돼요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val manager = remember { AppWidgetManager.getInstance(context) }
            if (manager.isRequestPinAppWidgetSupported) {
                OutlinedButton(
                    onClick = {
                        FortuneWidget.updateAll(context)
                        manager.requestPinAppWidget(ComponentName(context, FortuneWidget::class.java), null, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("홈 화면에 위젯 추가하기") }
            }
            info?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
        }
    }
}
