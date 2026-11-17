package com.gracekim.saju.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.ads.BannerAd
import com.gracekim.saju.ads.rememberRewardedAd
import com.gracekim.saju.core.CompatibilityDetail
import com.gracekim.saju.core.CompatibilityResult
import com.gracekim.saju.core.Element
import com.gracekim.saju.core.Explain
import com.gracekim.saju.core.SajuChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(me: SajuChart, partner: SajuChart, result: CompatibilityResult, onBack: () -> Unit) {
    val myName = me.input.name.ifBlank { "나" }
    val partnerName = partner.input.name.ifBlank { "상대방" }
    val myLabel = me.input.name.takeIf { it.isNotBlank() }?.let { "${it}님" } ?: "나"
    val partnerLabel = partner.input.name.takeIf { it.isNotBlank() }?.let { "${it}님" } ?: "상대방"
    val detail = remember(me, partner) { CompatibilityDetail.analyze(me, partner) }
    var unlocked by rememberSaveable(result) { mutableStateOf(false) }
    val rewardedAd = rememberRewardedAd()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("궁합 결과", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("〈 뒤로", color = MaterialTheme.colorScheme.onPrimary)
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
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    PersonSummary(myName, me, Modifier.weight(1f))
                    Text("💞", fontSize = 28.sp)
                    PersonSummary(partnerName, partner, Modifier.weight(1f))
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("${result.score}점", fontSize = 44.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(result.grade, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(result.summary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            }
            // 분야별 궁합 (무료)
            item {
                CompatCard("📊 분야별 궁합") {
                    detail.categories.forEach { c ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${c.emoji} ${c.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "${c.score}점",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(c.score / 100f)
                                        .background(MaterialTheme.colorScheme.primary),
                                )
                            }
                            Text(c.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            if (!unlocked) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text("🔒 더 깊은 궁합 풀이", fontWeight = FontWeight.Bold)
                            Text(
                                "· 두 사람은 서로에게 어떤 존재일까?\n· 두 사람의 연애 스타일 비교\n· 두 사람의 오행 비교\n· 관계를 더 좋게 하는 팁과 추천 데이트",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Button(onClick = { rewardedAd.show { unlocked = true } }) {
                                Text("▶ 광고 보고 상세 풀이 열기")
                            }
                        }
                    }
                }
            }
            if (unlocked) {
                // 서로에게 어떤 존재인지
                item {
                    CompatCard("👫 두 사람은 서로에게 이런 존재예요") {
                        RoleLine(
                            "${Explain.josa(partnerLabel, "은", "는")} ${myLabel}에게",
                            "${detail.bToA.first.korean} · ${Explain.tenGodShort(detail.bToA.first)}",
                            detail.bToA.second,
                        )
                        RoleLine(
                            "${Explain.josa(myLabel, "은", "는")} ${partnerLabel}에게",
                            "${detail.aToB.first.korean} · ${Explain.tenGodShort(detail.aToB.first)}",
                            detail.aToB.second,
                        )
                    }
                }
                // 연애 스타일 비교
                item {
                    CompatCard("💕 두 사람의 연애 스타일") {
                        LoveStyle(myName, me)
                        LoveStyle(partnerName, partner)
                    }
                }
                // 오행 비교
                item {
                    CompatCard("🌈 두 사람의 오행 비교") {
                        Text(
                            "왼쪽은 ${myLabel}, 오른쪽은 ${partnerLabel}의 기운 개수예요. 한쪽에 없는 기운을 다른 쪽이 채워 주면 균형이 좋아요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        val ca = me.elementCounts
                        val cb = partner.elementCounts
                        Element.entries.forEach { e ->
                            ElementCompareRow(e, ca.getValue(e), cb.getValue(e))
                        }
                    }
                }
                // 상세 포인트
                items(result.points) { point ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                (if (point.isPositive) "😊 " else "⚠️ ") + point.title,
                                fontWeight = FontWeight.Bold,
                                color = if (point.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                            )
                            Text(point.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                // 관계 팁
                item {
                    CompatCard("💡 관계를 더 좋게 하는 팁") {
                        detail.tips.forEachIndexed { i, tip ->
                            Row {
                                Text("${i + 1}.", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                Text(tip, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            ) {
                                Text("추천 데이트", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text(detail.dateIdea, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    "※ 궁합은 전통 명리학에 따른 참고용 풀이입니다. 좋은 관계는 서로의 노력으로 만들어집니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun CompatCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun RoleLine(who: String, tag: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(who, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(tag, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LoveStyle(name: String, chart: SajuChart) {
    val s = chart.dayMaster
    val persona = Explain.persona(s)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            "${Explain.stemEmoji(s)} $name · '${Explain.stemImage(s)}' 같은 사람",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(persona.keywords.joinToString(" ") { "#$it" }, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(persona.love, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ElementCompareRow(e: Element, a: Int, b: Int) {
    val info = Explain.element(e)
    val maxN = 4f
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$a", style = MaterialTheme.typography.labelMedium, modifier = Modifier.widthIn(min = 16.dp), textAlign = TextAlign.End)
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
                .height(12.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((a / maxN).coerceAtMost(1f))
                    .clip(RoundedCornerShape(6.dp))
                    .background(e.color()),
            )
        }
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(e.color().copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(info.emoji, fontSize = 13.sp)
                Text(e.korean, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
                .height(12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((b / maxN).coerceAtMost(1f))
                    .clip(RoundedCornerShape(6.dp))
                    .background(e.color()),
            )
        }
        Text("$b", style = MaterialTheme.typography.labelMedium, modifier = Modifier.widthIn(min = 16.dp))
    }
}

@Composable
private fun PersonSummary(name: String, chart: SajuChart, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(chart.dayMaster.element.color()),
            contentAlignment = Alignment.Center,
        ) {
            Text(Explain.stemEmoji(chart.dayMaster), fontSize = 26.sp)
        }
        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "'${Explain.stemImage(chart.dayMaster)}' · ${chart.dayMaster.korean}${chart.dayMaster.element.korean}",
            color = chart.dayMaster.element.color(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "${Explain.animalEmoji(chart.year.branch)} ${chart.year.branch.animal}띠 · ${chart.day.korean}일주",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
