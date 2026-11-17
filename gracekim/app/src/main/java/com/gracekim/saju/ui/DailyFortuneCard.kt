package com.gracekim.saju.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.core.DailyFortune
import com.gracekim.saju.core.Detail
import com.gracekim.saju.core.SajuChart
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyFortuneCard(chart: SajuChart) {
    val today = LocalDate.now()
    var selectedDay by rememberSaveable { mutableLongStateOf(today.toEpochDay()) }
    val date = LocalDate.ofEpochDay(selectedDay)
    val f = remember(chart, date) { DailyFortune.of(chart, date) }
    val fmt = DateTimeFormatter.ofPattern("M월 d일")
    val context = LocalContext.current

    // 그제 ~ 모레 5일 점수
    val week = remember(chart, today) {
        (-2..2).map { off -> today.plusDays(off.toLong()) to DailyFortune.of(chart, today.plusDays(off.toLong())).score }
    }
    val labels = listOf("그제", "어제", "오늘", "내일", "모레")
    val title = when (date) {
        today -> "오늘의 운세"
        today.plusDays(1) -> "내일의 운세"
        today.minusDays(1) -> "어제의 운세"
        else -> "${date.format(fmt)}의 운세"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                FilterChip(
                    selected = date == today,
                    onClick = { selectedDay = today.toEpochDay() },
                    label = { Text("오늘") },
                )
                FilterChip(
                    selected = date != today && week.none { it.first == date },
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> selectedDay = LocalDate.of(y, m + 1, d).toEpochDay() },
                            date.year,
                            date.monthValue - 1,
                            date.dayOfMonth,
                        ).show()
                    },
                    label = { Text("📅 날짜 선택") },
                )
            }

            // 5일 운세 그래프
            ScoreGraph(
                scores = week.map { it.second },
                labels = labels,
                selectedIndex = week.indexOfFirst { it.first == date },
                onSelect = { i -> selectedDay = week[i].first.toEpochDay() },
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${f.score}", color = MaterialTheme.colorScheme.onPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("점", color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp)
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
            val todo = Detail.dailyTodo(f.tenGod)
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

/** 5일 점수 꺾은선 그래프. 날짜를 누르면 그날 운세를 보여 줍니다. */
@Composable
private fun ScoreGraph(scores: List<Int>, labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val lineColor = MaterialTheme.colorScheme.primary
    val dotSelected = MaterialTheme.colorScheme.secondary
    Box(
        Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
    ) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(top = 14.dp),
        ) {
            val n = scores.size
            val w = size.width / n
            fun y(s: Int) = size.height * (1f - (s - 40f) / 60f)
            val pts = scores.mapIndexed { i, s -> Offset(w * (i + 0.5f), y(s)) }
            val path = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                for (i in 1 until pts.size) {
                    val prev = pts[i - 1]
                    val cur = pts[i]
                    val mid = (prev.x + cur.x) / 2
                    cubicTo(mid, prev.y, mid, cur.y, cur.x, cur.y)
                }
            }
            drawPath(path, lineColor, style = Stroke(width = 3.dp.toPx()))
            pts.forEachIndexed { i, p ->
                val sel = i == selectedIndex
                drawCircle(if (sel) dotSelected else lineColor, radius = if (sel) 7.dp.toPx() else 4.5.dp.toPx(), center = p)
                drawCircle(Color.White, radius = if (sel) 3.dp.toPx() else 2.dp.toPx(), center = p)
            }
        }
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            scores.forEachIndexed { i, s ->
                val sel = i == selectedIndex
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelect(i) }
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        "$s",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                        color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        labels[i],
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
