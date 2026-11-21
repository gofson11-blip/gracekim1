package com.gracekim.saju.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.gracekim.saju.ads.rememberRewardedAd
import com.gracekim.saju.core.MonthGuide
import com.gracekim.saju.core.SajuChart
import com.gracekim.saju.data.BokStore
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/** 🧧 복주머니 출석 체크 + 이달의 행운 처방전 */
@Composable
fun BokCard(chart: SajuChart, bokTick: Int = 0, onBokChanged: () -> Unit = {}) {
    val context = LocalContext.current
    val store = remember { BokStore(context) }
    var state by remember(bokTick) { mutableStateOf(store.state()) }
    var message by remember { mutableStateOf<String?>(null) }
    val rewardedAd = rememberRewardedAd()
    val ym = YearMonth.now()
    val personKey = personKeyOf(chart)
    val whose = chart.input.name.takeIf { it.isNotBlank() }?.let { "${it}님의" } ?: "나의"
    var unlocked by remember(bokTick, personKey) { mutableStateOf(store.isUnlocked(ym, personKey)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧧 오늘의 복주머니", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("보유 ${state.count}개", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                "매일 출석하면 복주머니 1개, 7일 연속이면 보너스 3개! 지금 연속 ${state.streak}일째예요.",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(
                onClick = {
                    val got = store.checkIn()
                    state = store.state()
                    onBokChanged()
                    message = if (got > 1) "🎉 7일 연속 출석! 복주머니 ${got}개를 받았어요." else "🧧 복주머니 1개를 받았어요!"
                },
                enabled = !state.checkedToday,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.checkedToday) "✅ 오늘 출석 완료" else "출석하고 복주머니 받기 (+1)")
            }
            OutlinedButton(
                onClick = {
                    rewardedAd.show {
                        store.adReward()
                        state = store.state()
                        onBokChanged()
                        message = "🧧 복주머니 2개를 받았어요!"
                    }
                },
                enabled = !state.adUsedToday,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.adUsedToday) "오늘 광고 보상은 받았어요" else "▶ 광고 보고 복주머니 2개 더 받기")
            }
            message?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary) }

            HorizontalDivider()
            Text(
                "🔮 $whose ${ym.monthValue}월 행운 처방전 (복주머니 ${BokStore.COST}개)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (unlocked) {
                MonthGuideContent(MonthGuide.of(chart, ym))
                Text(
                    "👨‍👩‍👧 목록에 저장한 가족·친구의 처방전도 한 사람당 복주머니 ${BokStore.COST}개로 열 수 있어요. (위 👥 목록에서 사람을 골라 보세요)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    "이번 달 나에게 좋은 날 TOP 3, 조심할 날, 이달의 흐름과 행운 아이템을 알려 드려요.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = {
                        if (store.unlockMonth(ym, personKey)) {
                            unlocked = true
                            state = store.state()
                            onBokChanged()
                        }
                    },
                    enabled = state.count >= BokStore.COST,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.count >= BokStore.COST) "복주머니 ${BokStore.COST}개로 열기" else "복주머니를 ${BokStore.COST - state.count}개 더 모아 주세요")
                }
            }
        }
    }
}

@Composable
private fun MonthGuideContent(g: MonthGuide.Guide) {
    val fmt = DateTimeFormatter.ofPattern("M월 d일 (E)")
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (g.monthText.isNotBlank()) Text("📅 이달의 흐름: ${g.monthText}", style = MaterialTheme.typography.bodyMedium)
            Text("🌟 좋은 날 TOP 3", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            g.goodDays.forEach {
                Text("· ${it.date.format(fmt)} ${it.score}점 — ${it.headline}", style = MaterialTheme.typography.bodyMedium)
            }
            Text("⚠️ 조심하면 좋은 날", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("· ${g.cautionDay.date.format(fmt)} ${g.cautionDay.score}점 — ${g.cautionDay.headline}", style = MaterialTheme.typography.bodyMedium)
            if (g.advice.isNotBlank()) Text("💡 ${g.advice}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "🍀 이달의 행운: ${g.lucky.color} · 숫자 ${g.lucky.numbers} · ${g.lucky.direction} · ${g.lucky.items}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
