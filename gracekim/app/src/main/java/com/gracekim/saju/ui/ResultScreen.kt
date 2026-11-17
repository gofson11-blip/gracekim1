package com.gracekim.saju.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.ads.BannerAd
import com.gracekim.saju.core.Detail
import com.gracekim.saju.core.Element
import com.gracekim.saju.core.Explain
import com.gracekim.saju.core.Interpretation
import com.gracekim.saju.core.Pillar
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.core.SajuChart
import com.gracekim.saju.core.TenGod
import com.gracekim.saju.core.YearReport
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(chart: SajuChart, onBack: () -> Unit, onCompatibility: () -> Unit) {
    val name = chart.input.name.ifBlank { "나" }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${name}의 사주", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("👥 목록", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = { BannerAd() },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { BirthInfo(chart) }
            item {
                ResultTabs(selected = tab, onSelect = { tab = it })
            }
            if (tab == 0) {
                item { SummaryCard(chart) }
                item { DailyFortuneCard(chart) }
                item { BokCard(chart) }
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
                item { YearReportCard(chart) }
                item { TarotSection(chart) }
                item { ZodiacSection(chart) }
                item { SettingsCard() }
                item {
                    TextButton(onClick = { tab = 1 }, modifier = Modifier.fillMaxWidth()) {
                        Text("📜 타고난 내 사주 풀이 보러 가기 →")
                    }
                }
            } else {
                item { PillarSection(chart) }
                item { OhaengSection(chart) }
                item { PersonaCard(chart) }
                item { AbilityCard(chart) }
                item { PalaceCard(chart) }
                item { DaeunCard(chart) }
                item { GlossaryCard() }
            }
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

// ── 공통 부품 ────────────────────────────────────────────────

/** 🔮 운세 / 📜 내 사주 탭 */
@Composable
private fun ResultTabs(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        listOf("🔮 운세", "📜 내 사주").forEachIndexed { i, label ->
            val on = i == selected
            Box(
                Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (on) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (on) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

/** 눌러서 펼치는 쉬운 설명 상자 */
@Composable
private fun ExplainBox(question: String, answer: String) {
    var open by rememberSaveable(question) { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { open = !open },
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "💡 $question",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    if (open) "접기 ▲" else "보기 ▼",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (open) {
                Spacer(Modifier.height(6.dp))
                Text(answer, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LifeLine(emoji: String, title: String, body: String) {
    Row {
        Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun KeywordChip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)) {
        Text(
            "#$text",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// ── 생년월일 ─────────────────────────────────────────────────

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
            "$time · ${chart.input.gender.korean}성 · ${Explain.animalEmoji(chart.year.branch)} ${chart.year.branch.animal}띠",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── 한눈에 보는 나 ───────────────────────────────────────────

@Composable
private fun SummaryCard(chart: SajuChart) {
    val name = chart.input.name.ifBlank { "나" }
    val s = chart.dayMaster
    val persona = Explain.persona(s)
    val counts = chart.elementCounts
    val maxCount = counts.values.max()
    val minCount = counts.values.min()
    val strongest = counts.filter { it.value == maxCount }.keys
    val weakest = counts.filter { it.value == minCount }.keys
    fun names(set: Set<Element>) = set.joinToString(" ") { "${Explain.element(it).emoji}${it.korean}" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "✨ 한눈에 보는 나",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(s.element.color()),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(Explain.stemEmoji(s), fontSize = 34.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("${name}님은", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "'${Explain.stemImage(s)}' 같은 사람",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        "타고난 기운: ${s.korean}${s.element.korean}(${s.hanja}${s.element.hanja})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                persona.keywords.forEach { KeywordChip(it) }
            }
            Text(
                "${Explain.animalEmoji(chart.year.branch)} ${chart.year.branch.animal}띠 — ${Explain.animalPersonality(chart.year.branch)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("💪 가장 강한 기운: ${names(strongest)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    if (minCount == 0) "🌱 없어서 채우면 좋은 기운: ${names(weakest)}" else "🌱 상대적으로 약한 기운: ${names(weakest)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text("⚖️ ${Explain.strengthFriendly(chart)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

// ── 사주팔자 ─────────────────────────────────────────────────

@Composable
private fun PillarSection(chart: SajuChart) {
    // 선택한 글자: 기둥 번호 * 2 + (0=위 글자, 1=아래 글자). 처음에는 '나'(일간)를 보여 줍니다.
    var selected by rememberSaveable { mutableIntStateOf(2) }
    val dm = chart.dayMaster
    val pillars = listOf(chart.hour, chart.day, chart.month, chart.year)

    InfoCard("📜 사주팔자 — 타고난 여덟 글자") {
        Text(
            "태어난 해·달·날·시간을 네 개의 기둥으로 세우고, 기둥마다 하늘 글자(위)와 땅 글자(아래)를 하나씩 놓아 모두 8글자로 나타낸 거예요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        ExplainBox(
            "이 표는 어떻게 읽나요?",
            "• 오른쪽부터 년주(태어난 해) → 월주(달) → 일주(날) → 시주(시간) 순서예요.\n" +
                "• 위 글자는 '천간'(하늘의 기운), 아래 글자는 '지지'(땅의 기운·띠 동물)라고 해요.\n" +
                "• ⭐ 표시된 일주의 위 글자가 바로 '나'예요. 모든 풀이의 중심이에요.\n" +
                "• 칸의 색은 오행이에요. 초록=나무, 빨강=불, 노랑=흙, 회색=쇠, 파랑=물.\n" +
                "• 글자 위아래의 작은 글씨(비견, 정재 등)는 '나'와 그 글자의 관계(십신)예요.",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            pillars.forEachIndexed { col, p ->
                val role = Explain.pillarRole(col)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(role.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(
                        role.short,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                    )
                    if (p == null) {
                        Text(
                            "시간\n모름",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 56.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            if (col == 1) "⭐ 나" else TenGod.of(dm, p.stem).korean,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        GanjiBox(
                            emoji = Explain.stemEmoji(p.stem),
                            hanja = p.stem.hanja,
                            caption = p.stem.korean + p.stem.element.korean,
                            element = p.stem.element,
                            selected = selected == col * 2,
                            onClick = { selected = col * 2 },
                        )
                        GanjiBox(
                            emoji = Explain.animalEmoji(p.branch),
                            hanja = p.branch.hanja,
                            caption = p.branch.korean + p.branch.element.korean,
                            element = p.branch.element,
                            selected = selected == col * 2 + 1,
                            onClick = { selected = col * 2 + 1 },
                        )
                        Text(
                            TenGod.of(dm, p.branch).korean,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        Text(
            "👆 글자를 누르면 뜻을 알려 드려요",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        val detail = Explain.charDetail(chart, selected / 2, selected % 2 == 0)
        if (detail != null) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(detail.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    detail.lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

@Composable
private fun GanjiBox(
    emoji: String,
    hanja: String,
    caption: String,
    element: Element,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(shape)
            .background(element.color())
            .then(if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 15.sp)
            Text(hanja, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(caption, color = Color.White, fontSize = 11.sp)
        }
    }
}

// ── 오행 ─────────────────────────────────────────────────────

@Composable
private fun OhaengSection(chart: SajuChart) {
    val counts = chart.elementCounts
    val total = counts.values.sum().coerceAtLeast(1)
    val max = counts.values.max()
    InfoCard("🌈 오행 — 나를 이루는 다섯 가지 기운") {
        Text(
            "동양에서는 세상이 나무·불·흙·쇠·물, 다섯 가지 기운으로 이루어져 있다고 봐요. " +
                "내 사주 ${total}글자에 어떤 기운이 몇 개 있는지 보면 타고난 성향과 균형을 알 수 있어요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            OhaengDiagram(counts)
        }
        Text(
            "━ 금색 원과 화살표: 서로 살려 주는 관계(상생)\n    🌳나무 → 🔥불 → ⛰️흙 → 💎쇠 → 💧물 → 🌳나무\n" +
                "┅ 안쪽 점선 별: 서로 누르는 관계(상극)\n" +
                "● 원이 클수록 그 기운이 많고, 흐린 원은 없는 기운이에요.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Element.entries.forEach { e ->
            val n = counts.getValue(e)
            val info = Explain.element(e)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${info.emoji} ${e.korean}(${e.hanja})",
                        modifier = Modifier
                            .widthIn(min = 76.dp)
                            .padding(end = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
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
                    Text(
                        "${n}개 · ${Explain.elementStatus(n)}",
                        modifier = Modifier
                            .widthIn(min = 86.dp)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (n == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
                Text(
                    "${info.thing} · ${info.keywords}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(Interpretation.overallSummary(chart), style = MaterialTheme.typography.bodyMedium)
        counts.filter { it.value == 0 }.keys.forEach {
            Text("🌱 " + Interpretation.elementAdvice(it), style = MaterialTheme.typography.bodyMedium)
        }
        counts.filter { it.value >= 3 && it.value == max }.keys.forEach {
            Text("🔆 " + Interpretation.elementExcess(it), style = MaterialTheme.typography.bodyMedium)
        }
        ExplainBox(
            "다섯 기운은 각각 무슨 뜻인가요?",
            Element.entries.joinToString("\n") { e ->
                val i = Explain.element(e)
                "${i.emoji} ${e.korean}(${e.hanja}) — ${i.meaning}"
            },
        )
        ExplainBox(
            "오행 균형이 왜 중요한가요?",
            "어느 한쪽으로 치우치지 않고 다섯 기운이 고르게 있을수록 안정적이라고 봐요. " +
                "없는 기운은 그 기운의 색깔·방향·생활 습관으로 채우고, 너무 많은 기운은 조금 덜어 내면 좋아요. " +
                "개수는 사주 8글자(시간을 모르면 6글자)에 드러난 오행을 센 것이에요.",
        )
    }
}

/** 오행 상생·상극 관계도 */
@Composable
private fun OhaengDiagram(counts: Map<Element, Int>) {
    val gold = Color(0xFFE8B66F)
    val clash = Color(0x88E57373)
    val sizeDp = 280f
    val ringDp = sizeDp * 0.36f
    Box(Modifier.size(sizeDp.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val c = center
            val r = size.minDimension * 0.36f
            fun pt(deg: Double) = Offset(
                c.x + r * cos(Math.toRadians(deg)).toFloat(),
                c.y + r * sin(Math.toRadians(deg)).toFloat(),
            )
            val pts = (0 until 5).map { pt(-90.0 + 72 * it) }
            // 상극: 별 모양 점선
            for (i in 0 until 5) {
                drawLine(
                    color = clash,
                    start = pts[i],
                    end = pts[(i + 2) % 5],
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
                )
            }
            // 상생: 금색 원 + 시계 방향 화살표
            drawCircle(color = gold, radius = r, center = c, style = Stroke(width = 2.dp.toPx()))
            for (i in 0 until 5) {
                val deg = -90.0 + 72 * i + 36
                val p = pt(deg)
                val rad = Math.toRadians(deg)
                val tx = -sin(rad).toFloat()
                val ty = cos(rad).toFloat()
                val nx = cos(rad).toFloat()
                val ny = sin(rad).toFloat()
                val len = 10.dp.toPx()
                val wid = 8.dp.toPx()
                val path = Path().apply {
                    moveTo(p.x + tx * len / 2, p.y + ty * len / 2)
                    lineTo(p.x - tx * len / 2 + nx * wid / 2, p.y - ty * len / 2 + ny * wid / 2)
                    lineTo(p.x - tx * len / 2 - nx * wid / 2, p.y - ty * len / 2 - ny * wid / 2)
                    close()
                }
                drawPath(path, gold)
            }
        }
        Element.entries.forEachIndexed { i, e ->
            val n = counts.getValue(e)
            val d = 46f + 8f * n.coerceAtMost(4)
            val rad = Math.toRadians(-90.0 + 72 * i)
            val cx = sizeDp / 2 + ringDp * cos(rad).toFloat()
            val cy = sizeDp / 2 + ringDp * sin(rad).toFloat()
            Box(
                modifier = Modifier
                    .offset(x = (cx - d / 2).dp, y = (cy - d / 2).dp)
                    .size(d.dp)
                    .clip(CircleShape)
                    .background(e.color().copy(alpha = if (n == 0) 0.35f else 1f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Explain.element(e).emoji, fontSize = 15.sp)
                    Text(
                        "${e.korean} $n",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

// ── 성격과 삶 ────────────────────────────────────────────────

@Composable
private fun PersonaCard(chart: SajuChart) {
    val s = chart.dayMaster
    val r = Interpretation.dayMaster(s)
    val p = Explain.persona(s)
    InfoCard("🧭 나의 성격과 삶") {
        Text(
            "${Explain.stemEmoji(s)} ${r.title}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(r.nature, style = MaterialTheme.typography.bodyMedium)
        val deep = Detail.deepPersona(s)
        deep.paragraphs.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        LifeLine("👍", "강점", r.strengths)
        LifeLine("⚠️", "주의할 점", r.cautions)
        LifeLine("🤝", "대인관계", deep.relationship)
        LifeLine("💕", "연애·결혼", p.love)
        LifeLine("💼", "잘 맞는 일", p.career)
        LifeLine("💰", "재물 습관", p.money)
        LifeLine("🍀", "건강 관리", p.health)
        val percent = (chart.strengthRatio * 100).toInt()
        LifeLine("⚖️", "${Explain.strengthFriendly(chart)} · 나를 돕는 기운 ${percent}%", Interpretation.strength(chart))
        val lucky = Detail.lucky(chart)
        Surface(shape = RoundedCornerShape(12.dp), color = lucky.element.color().copy(alpha = 0.18f)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "🍀 나에게 힘이 되는 것 (개운법)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(lucky.reason, style = MaterialTheme.typography.bodyMedium)
                Text("🎨 행운의 색: ${lucky.color}", style = MaterialTheme.typography.bodyMedium)
                Text("🔢 행운의 숫자: ${lucky.numbers}", style = MaterialTheme.typography.bodyMedium)
                Text("🧭 좋은 방향: ${lucky.direction}", style = MaterialTheme.typography.bodyMedium)
                Text("✨ 도움이 되는 것: ${lucky.items}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ── 다섯 가지 능력 (십신 분포) ────────────────────────────────

@Composable
private fun AbilityCard(chart: SajuChart) {
    val counts = Detail.tenGroupCounts(chart)
    val total = counts.values.sum().coerceAtLeast(1)
    InfoCard("📊 나의 다섯 가지 능력") {
        Text(
            "'나'(일간)를 뺀 나머지 ${total}글자가 나에게 어떤 역할을 하는지 다섯 가지로 나눠 봤어요. 많은 쪽이 타고난 강점, 없는 쪽은 채우면 좋은 부분이에요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Detail.TenGroup.entries.forEach { g ->
            val n = counts.getValue(g)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${g.emoji} ${g.ability}",
                        modifier = Modifier
                            .widthIn(min = 120.dp)
                            .padding(end = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        if (n > 0) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(n.toFloat() / total)
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                    Text(
                        "${n}개 · ${g.status(n)}",
                        modifier = Modifier
                            .widthIn(min = 72.dp)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (n == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
                Text(g.describe(n), style = MaterialTheme.typography.bodySmall)
            }
        }
        ExplainBox(
            "다섯 가지 능력은 어떻게 나눈 건가요?",
            Detail.TenGroup.entries.joinToString("\n") { "${it.emoji} ${it.title} — ${it.meaning}" } +
                "\n\n사주 용어로는 '십신'이라고 해요. 비견·겁재, 식신·상관처럼 두 개씩 짝지어 다섯 묶음으로 봐요.",
        )
    }
}

// ── 배우자 자리 · 사회생활 자리 ───────────────────────────────

@Composable
private fun PalaceCard(chart: SajuChart) {
    val dm = chart.dayMaster
    val spouseGod = TenGod.of(dm, chart.day.branch)
    val socialGod = TenGod.of(dm, chart.month.branch)
    InfoCard("💑 결혼·가정과 사회생활") {
        Text(
            "사주에서 태어난 날의 아래 글자는 '배우자 자리', 태어난 달의 아래 글자는 '사회생활 자리'로 봐요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        LifeLine(
            "🏠",
            "배우자 자리: ${chart.day.branch.hanja} ${chart.day.branch.korean} · ${spouseGod.korean}(${Explain.tenGodShort(spouseGod)})",
            Detail.spousePalace(spouseGod),
        )
        LifeLine(
            "🏢",
            "사회생활 자리: ${chart.month.branch.hanja} ${chart.month.branch.korean} · ${socialGod.korean}(${Explain.tenGodShort(socialGod)})",
            Detail.socialPalace(socialGod),
        )
    }
}

// ── 대운 ─────────────────────────────────────────────────────

@Composable
private fun DaeunCard(chart: SajuChart) {
    val age = LocalDate.now().year - chart.solarDate.year + 1 // 세는나이
    val current = chart.daeunAt(age)
    val currentIndex = chart.daeun.indexOf(current).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (currentIndex - 1).coerceAtLeast(0))
    InfoCard("🗓️ 대운 — 10년마다 바뀌는 인생의 계절") {
        Text(
            "대운은 10년 단위로 바뀌는 큰 운의 흐름이에요. 지금 내가 어떤 계절을 지나고 있는지, 다음엔 어떤 계절이 오는지 보여 드려요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "${chart.daeunStartAge}세부터 10년마다 바뀌어요 · ${if (chart.isDaeunForward) "순행" else "역행"} · 나이는 세는나이 기준 · 옆으로 밀어 보세요 👉",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chart.daeun) { period ->
                val isNow = period == current
                val god = TenGod.of(chart.dayMaster, period.pillar.stem)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNow) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        Modifier
                            .padding(10.dp)
                            .width(62.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(if (isNow) "지금" else "${period.startAge}세", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        if (isNow) Text("${period.startAge}세~", style = MaterialTheme.typography.labelSmall)
                        PillarText(period.pillar, isNow)
                        Text(god.korean, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(Explain.tenGodShort(god), style = MaterialTheme.typography.labelSmall, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
        if (current != null) {
            val god = TenGod.of(chart.dayMaster, current.pillar.stem)
            LifeLine(
                "📍",
                "지금은 ${current.pillar.korean}(${current.pillar.hanja}) 대운 · ${current.startAge}~${current.startAge + 9}세",
                Explain.daeunMeaning(god),
            )
            val areas = Detail.daeunAreas(god)
            Column(Modifier.padding(start = 30.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("💼 일: ${areas.work}", style = MaterialTheme.typography.bodyMedium)
                Text("💰 재물: ${areas.money}", style = MaterialTheme.typography.bodyMedium)
                Text("💕 인연: ${areas.love}", style = MaterialTheme.typography.bodyMedium)
            }
            val next = chart.daeun.getOrNull(currentIndex + 1)
            if (next != null) {
                val nextGod = TenGod.of(chart.dayMaster, next.pillar.stem)
                LifeLine(
                    "⏭️",
                    "다음 대운 · ${next.startAge}세부터 ${next.pillar.korean}(${next.pillar.hanja})",
                    Explain.daeunMeaning(nextGod),
                )
            }
        }
    }
}

@Composable
private fun PillarText(p: Pillar, onHighlight: Boolean) {
    val dark = Color(0xFF2E1146)
    Text(Explain.stemEmoji(p.stem), fontSize = 13.sp)
    Text(p.stem.hanja, color = if (onHighlight) dark else p.stem.element.color(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(p.branch.hanja, color = if (onHighlight) dark else p.branch.element.color(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(p.korean, style = MaterialTheme.typography.labelSmall)
}

// ── 신년운세 (올해 / 내년) ─────────────────────────────────────

@Composable
private fun YearReportCard(chart: SajuChart) {
    val today = LocalDate.now()
    val baseYear = Detail.sajuYearOf(today)
    // 11월부터는 다음 해 신년운세를 먼저 보여 줍니다.
    var year by rememberSaveable { mutableIntStateOf(if (today.monthValue >= 11) baseYear + 1 else baseYear) }
    var showAllMonths by rememberSaveable { mutableStateOf(false) }
    val age = year - chart.solarDate.year + 1
    val report = remember(chart, year) { YearReport.build(chart, year, age) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "🎊 신년운세",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                FilterChip(selected = year == baseYear, onClick = { year = baseYear }, label = { Text("${baseYear}년") })
                FilterChip(selected = year == baseYear + 1, onClick = { year = baseYear + 1 }, label = { Text("${baseYear + 1}년") })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(report.pillar.stem.element.color()),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(Explain.animalEmoji(report.pillar.branch), fontSize = 22.sp)
                        Text("${report.score}점", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        report.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        "올해의 키워드: ${report.keyword}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            report.paragraphs.forEach {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            LifeLine("💰", "재물운", report.areas.money)
            LifeLine("💼", "일·학업운", report.areas.work)
            LifeLine("💕", "애정운", report.areas.love)
            LifeLine("🍀", "건강운", report.areas.health)

            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("🌟 좋은 달 TOP 3", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    report.bestMonths.forEach { m -> MonthLine(m, highlight = true) }
                    Spacer(Modifier.height(4.dp))
                    Text("⚠️ 조심하면 좋은 달", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    report.cautionMonths.forEach { m -> MonthLine(m, highlight = false) }
                }
            }
            TextButton(onClick = { showAllMonths = !showAllMonths }) {
                Text(if (showAllMonths) "12개월 운세 접기 ▲" else "📅 12개월 운세 모두 보기 ▼")
            }
            if (showAllMonths) {
                Text(
                    "사주에서 한 해는 입춘(2월 4일경)에 시작하고, 매달은 절기(4~8일경)에 바뀌어요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                report.months.forEach { m -> MonthLine(m, highlight = false, showYear = true) }
            }
        }
    }
}

@Composable
private fun MonthLine(m: YearReport.ScoredMonth, highlight: Boolean, showYear: Boolean = false) {
    val today = LocalDate.now()
    val isNow = m.month.calendarYear == today.year && m.month.calendarMonth == today.monthValue
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.widthIn(min = 70.dp)) {
            Text(
                (if (isNow) "▶ " else "") + (if (showYear && m.month.calendarMonth == 1) "${m.month.calendarYear}년 " else "") + "${m.month.calendarMonth}월",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "${m.score}점 · ${m.month.god.korean}",
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(m.month.text, style = MaterialTheme.typography.bodyMedium)
            m.month.relation?.let {
                Text("· $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// ── 용어 사전 ────────────────────────────────────────────────

@Composable
private fun GlossaryCard() {
    InfoCard("📖 사주 용어, 쉽게 알기") {
        Explain.glossary.forEach { (term, meaning) -> ExplainBox(term, meaning) }
    }
}
