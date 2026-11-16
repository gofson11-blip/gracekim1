package com.gracekim.saju.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gracekim.saju.ads.BannerAd
import com.gracekim.saju.ads.rememberRewardedAd
import com.gracekim.saju.core.CompatibilityResult
import com.gracekim.saju.core.SajuChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(me: SajuChart, partner: SajuChart, result: CompatibilityResult, onBack: () -> Unit) {
    val myName = me.input.name.ifBlank { "나" }
    val partnerName = partner.input.name.ifBlank { "상대방" }
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
                            Text("🔒 상세 궁합 풀이 ${result.points.size}가지", fontWeight = FontWeight.Bold)
                            Text(
                                "두 사람의 본성, 배우자 자리, 띠, 오행 보완까지\n자세한 풀이를 확인해 보세요.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                            Button(onClick = { rewardedAd.show { unlocked = true } }) {
                                Text("▶ 광고 보고 상세 풀이 열기")
                            }
                        }
                    }
                }
            }
            if (unlocked) items(result.points) { point ->
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
private fun PersonSummary(name: String, chart: SajuChart, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "${chart.dayMaster.korean}${chart.dayMaster.element.korean} 일간",
            color = chart.dayMaster.element.color(),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "${chart.year.branch.animal}띠 · ${chart.day.korean}일주",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
