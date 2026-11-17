package com.gracekim.saju.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.core.DailyFortune
import com.gracekim.saju.core.SajuChart
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyFortuneCard(chart: SajuChart) {
    var offset by rememberSaveable { mutableIntStateOf(0) }
    val date = LocalDate.now().plusDays(offset.toLong())
    val f = remember(chart, date) { DailyFortune.of(chart, date) }
    val fmt = DateTimeFormatter.ofPattern("M월 d일")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("오늘의 운세", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                FilterChip(selected = offset == 0, onClick = { offset = 0 }, label = { Text("오늘") })
                FilterChip(selected = offset == 1, onClick = { offset = 1 }, label = { Text("내일") })
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${f.score}", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("점", color = Color.White, fontSize = 11.sp)
                    }
                }
                Column {
                    Text(
                        "${date.format(fmt)} · ${f.dayPillar.korean}(${f.dayPillar.hanja})일 · ${f.tenGod.korean}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(f.headline, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Text(f.message, style = MaterialTheme.typography.bodyMedium)
            f.relationNote?.let { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Text("💰 재물  ${f.money}", style = MaterialTheme.typography.bodyMedium)
            Text("💗 애정  ${f.love}", style = MaterialTheme.typography.bodyMedium)
            Text("🍀 건강  ${f.health}", style = MaterialTheme.typography.bodyMedium)
            val todo = com.gracekim.saju.core.Detail.dailyTodo(f.tenGod)
            Text("✅ 하면 좋은 일  ${todo.first}", style = MaterialTheme.typography.bodyMedium)
            Text("⛔ 피하면 좋은 일  ${todo.second}", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier
                        .size(14.dp)
                        .background(f.luckyElement.color(), CircleShape),
                )
                Text(
                    "행운의 색 ${f.luckyColor} · 숫자 ${f.luckyNumbers} · 방향 ${f.luckyDirection}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
