package com.gracekim.saju.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.core.Element
import com.gracekim.saju.core.Interpretation
import com.gracekim.saju.core.Pillar
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.core.SajuChart
import com.gracekim.saju.core.TenGod
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(chart: SajuChart, onBack: () -> Unit, onCompatibility: () -> Unit) {
    val name = chart.input.name.ifBlank { "나" }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${name}의 사주", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("〈 다시", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { BirthInfo(chart) }
            item { DailyFortuneCard(chart) }
            item {
                Button(
                    onClick = onCompatibility,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    Text("💞 궁합 보기", style = MaterialTheme.typography.titleMedium)
                }
            }
            item { PillarTable(chart) }
            item { ElementChart(chart) }
            item { DayMasterCard(chart) }
            item { DaeunCard(chart) }
            item { YearFortuneCard(chart) }
            item {
                Text(
                    "※ 전통 명리학 이론에 따른 참고용 풀이입니다. 중요한 결정은 스스로의 판단으로 내려 주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun BirthInfo(chart: SajuChart) {
    val fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    val time = chart.input.time?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "시간 모름"
    Column {
        Text(
            "양력 ${chart.solarDate.format(fmt)} · 음력 ${chart.lunarDate}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "$time · ${chart.input.gender.korean}성 · ${chart.year.branch.animal}띠",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PillarTable(chart: SajuChart) {
    val dm = chart.dayMaster
    val columns = listOf("시주" to chart.hour, "일주" to chart.day, "월주" to chart.month, "년주" to chart.year)
    InfoCard("사주팔자 (四柱八字)") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            columns.forEach { (label, pillar) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    if (pillar == null) {
                        Text("모름", modifier = Modifier.padding(vertical = 40.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val stemGod = if (pillar === chart.day) "일간(나)" else TenGod.of(dm, pillar.stem).korean
                        Text(stemGod, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        GanjiBox(pillar.stem.hanja, pillar.stem.korean + pillar.stem.element.korean, pillar.stem.element)
                        GanjiBox(pillar.branch.hanja, pillar.branch.korean + pillar.branch.element.korean, pillar.branch.element)
                        Text(TenGod.of(dm, pillar.branch).korean, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun GanjiBox(hanja: String, caption: String, element: Element) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(element.color()),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(hanja, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(caption, color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ElementChart(chart: SajuChart) {
    val counts = chart.elementCounts
    val total = counts.values.sum().coerceAtLeast(1)
    val max = counts.values.maxOrNull() ?: 0
    InfoCard("오행 분포") {
        Element.entries.forEach { e ->
            val n = counts.getValue(e)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${e.korean}(${e.hanja})", modifier = Modifier.width(56.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    if (n > 0) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(n.toFloat() / total)
                                .background(e.color()),
                        )
                    }
                }
                Text("${n}개", modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(Interpretation.overallSummary(chart), style = MaterialTheme.typography.bodyMedium)
        counts.filter { it.value == 0 }.keys.forEach {
            Text("• " + Interpretation.elementAdvice(it), style = MaterialTheme.typography.bodyMedium)
        }
        counts.filter { it.value >= 3 && it.value == max }.keys.forEach {
            Text("• " + Interpretation.elementExcess(it), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DayMasterCard(chart: SajuChart) {
    val r = Interpretation.dayMaster(chart.dayMaster)
    InfoCard("나의 본성 · ${r.title}") {
        Text(r.nature, style = MaterialTheme.typography.bodyMedium)
        Text("강점: ${r.strengths}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text("주의할 점: ${r.cautions}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        val percent = (chart.strengthRatio * 100).toInt()
        Text("신강·신약 (간이 판정, 일간을 돕는 기운 ${percent}%)", fontWeight = FontWeight.SemiBold)
        Text(Interpretation.strength(chart), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DaeunCard(chart: SajuChart) {
    val age = LocalDate.now().year - chart.solarDate.year + 1 // 세는나이
    val current = chart.daeunAt(age)
    InfoCard("대운 (10년 운의 흐름)") {
        Text(
            "대운수 ${chart.daeunStartAge} · ${if (chart.isDaeunForward) "순행" else "역행"} (나이는 세는나이 기준)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chart.daeun) { period ->
                val isNow = period == current
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNow) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        Modifier
                            .padding(10.dp)
                            .width(52.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("${period.startAge}세", style = MaterialTheme.typography.labelMedium)
                        PillarText(period.pillar)
                        Text(TenGod.of(chart.dayMaster, period.pillar.stem).korean, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        if (current != null) {
            val god = TenGod.of(chart.dayMaster, current.pillar.stem)
            Text(
                "지금은 ${current.pillar.korean}(${current.pillar.hanja}) 대운(${current.startAge}~${current.startAge + 9}세)입니다. " +
                    "${god.korean}의 기운 — ${Interpretation.tenGodMeaning(god)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun PillarText(p: Pillar) {
    Text(p.stem.hanja, color = p.stem.element.color(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(p.branch.hanja, color = p.branch.element.color(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(p.korean, style = MaterialTheme.typography.labelSmall)
}

@Composable
private fun YearFortuneCard(chart: SajuChart) {
    val year = LocalDate.now().year
    val pillar = SajuCalculator.yearPillarOf(year)
    val stemGod = TenGod.of(chart.dayMaster, pillar.stem)
    val branchGod = TenGod.of(chart.dayMaster, pillar.branch)
    InfoCard("${year}년 ${pillar.korean}(${pillar.hanja})년 운세") {
        Text(Interpretation.yearlyFortune(stemGod), style = MaterialTheme.typography.bodyMedium)
        Text(
            "올해 천간은 ${stemGod.korean}, 지지는 ${branchGod.korean}의 기운입니다.\n" +
                "· ${stemGod.korean}: ${Interpretation.tenGodMeaning(stemGod)}\n" +
                "· ${branchGod.korean}: ${Interpretation.tenGodMeaning(branchGod)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
