package com.gracekim.saju.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.core.Detail
import com.gracekim.saju.core.Explain
import com.gracekim.saju.core.SajuChart
import com.gracekim.saju.core.Tarot
import com.gracekim.saju.core.Zodiac
import java.time.LocalDate

private val Gold = Color(0xFFE8B66F)
private val Ink = Color(0xFF2E1146)

// ── 🃏 오늘의 타로 ────────────────────────────────────────────

@Composable
fun TarotSection(chart: SajuChart) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("saju_tarot", Context.MODE_PRIVATE) }
    val today = LocalDate.now()
    val input = chart.input
    val personKey = "${input.name}|${input.year}-${input.month}-${input.day}|${input.time}"
    val prefKey = "${today.toEpochDay()}|$personKey"
    var pick by remember(prefKey) { mutableIntStateOf(prefs.getInt(prefKey, -1)) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🃏 오늘의 타로", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (pick < 0) {
                Text(
                    "마음속으로 오늘 궁금한 것을 떠올리고, 끌리는 카드 한 장을 골라 주세요.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    (0..2).forEach { i ->
                        CardBack(
                            Modifier
                                .weight(1f)
                                .clickable {
                                    pick = i
                                    prefs.edit().putInt(prefKey, i).apply()
                                },
                        )
                    }
                }
            } else {
                val d = Tarot.draw(personKey, today, pick)
                Row(verticalAlignment = Alignment.Top) {
                    CardFace(d, Modifier.width(120.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${d.card.roman}. ${d.card.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(d.card.english, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (d.reversed) "역방향 🔄" else "정방향 ✨",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("키워드: ${d.card.keyword}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
                Text(if (d.reversed) d.card.reversed else d.card.upright, style = MaterialTheme.typography.bodyMedium)
                Text("💕 연애  ${d.card.love}", style = MaterialTheme.typography.bodyMedium)
                Text("💼 일·금전  ${d.card.work}", style = MaterialTheme.typography.bodyMedium)
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)) {
                    Text(
                        "💡 오늘의 한마디: ${d.card.advice}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    )
                }
                Text(
                    "카드는 하루에 한 장이에요. 내일 새로운 카드를 뽑을 수 있어요. (재미로 보는 타로예요)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CardBack(modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .aspectRatio(0.62f)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF6E3F95), Color(0xFF241238))))
            .border(2.dp, Gold, shape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌙", fontSize = 26.sp)
            Text("✦", color = Gold, fontSize = 18.sp)
            Text("터치", color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
private fun CardFace(d: Tarot.Draw, modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .aspectRatio(0.62f)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF4D6), Color(0xFFF2C984))))
            .border(2.dp, Color(0xFFC98E48), shape)
            .rotate(if (d.reversed) 180f else 0f),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 10.dp),
        ) {
            Text(d.card.roman, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(d.card.emoji, fontSize = 44.sp)
            Text(d.card.name, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

// ── 🐯 띠별 운세 ─────────────────────────────────────────────

@Composable
fun ZodiacSection(chart: SajuChart) {
    val today = LocalDate.now()
    val baseYear = Detail.sajuYearOf(today)
    val nextYear = baseYear + 1
    // 0 = 오늘, 그 외 = 연도
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val list = remember(mode, today) { if (mode == 0) Zodiac.daily(today) else Zodiac.yearly(mode) }
    val mine = chart.year.branch

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🐯 띠별 운세", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("오늘") })
                FilterChip(selected = mode == nextYear, onClick = { mode = nextYear }, label = { Text("${nextYear}년") })
            }
            Text(
                "가족·친구의 띠도 찾아보세요. 내 띠(${Explain.animalEmoji(mine)} ${mine.animal}띠)는 맨 위에 보여 드려요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val ordered = list.sortedBy { if (it.branch == mine) -1 else it.branch.ordinal }
            ordered.forEach { f ->
                val isMine = f.branch == mine
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isMine) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent,
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(Explain.animalEmoji(f.branch), fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Column(Modifier.widthIn(min = 92.dp)) {
                                Text(
                                    "${f.branch.animal}띠" + if (isMine) " (나)" else "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    Zodiac.birthYears(f.branch, today.year, 4).joinToString("·") { (it % 100).toString().padStart(2, '0') } + "년생",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(f.score / 100f)
                                        .background(MaterialTheme.colorScheme.primary),
                                )
                            }
                            Text(
                                "${f.score}점",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                        Text(f.text, style = MaterialTheme.typography.bodyMedium)
                        Text(f.tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
