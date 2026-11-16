package com.gracekim.saju

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.gracekim.saju.core.Compatibility
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.ui.CompatibilityScreen
import com.gracekim.saju.ui.FormData
import com.gracekim.saju.ui.InputScreen
import com.gracekim.saju.ui.ResultScreen
import com.gracekim.saju.ui.SajuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SajuTheme {
                SajuApp()
            }
        }
    }
}

private enum class Screen { INPUT, RESULT, PARTNER_INPUT, COMPATIBILITY }

@Composable
fun SajuApp() {
    var screen by rememberSaveable { mutableStateOf(Screen.INPUT) }
    var form by rememberSaveable { mutableStateOf(FormData()) }
    var submitted by rememberSaveable { mutableStateOf<FormData?>(null) }
    var partnerForm by rememberSaveable { mutableStateOf(FormData(gender = com.gracekim.saju.core.Gender.MALE)) }
    var partnerSubmitted by rememberSaveable { mutableStateOf<FormData?>(null) }

    val me = submitted
    val partner = partnerSubmitted
    val myChart = remember(me) { me?.let { SajuCalculator.calculate(it.toBirthInput()) } }
    val partnerChart = remember(partner) { partner?.let { SajuCalculator.calculate(it.toBirthInput()) } }

    when {
        screen == Screen.RESULT && myChart != null -> {
            BackHandler { screen = Screen.INPUT }
            ResultScreen(
                chart = myChart,
                onBack = { screen = Screen.INPUT },
                onCompatibility = { screen = Screen.PARTNER_INPUT },
            )
        }

        screen == Screen.PARTNER_INPUT && myChart != null -> {
            BackHandler { screen = Screen.RESULT }
            InputScreen(
                form = partnerForm,
                onFormChange = { partnerForm = it },
                onSubmit = {
                    partnerSubmitted = it
                    screen = Screen.COMPATIBILITY
                },
                title = "궁합 보기",
                intro = "궁합을 볼 상대방의 정보를 입력해 주세요.",
                submitLabel = "궁합 보기",
                onBack = { screen = Screen.RESULT },
            )
        }

        screen == Screen.COMPATIBILITY && myChart != null && partnerChart != null -> {
            BackHandler { screen = Screen.PARTNER_INPUT }
            val result = remember(myChart, partnerChart) { Compatibility.analyze(myChart, partnerChart) }
            CompatibilityScreen(
                me = myChart,
                partner = partnerChart,
                result = result,
                onBack = { screen = Screen.PARTNER_INPUT },
            )
        }

        else -> InputScreen(
            form = form,
            onFormChange = { form = it },
            onSubmit = {
                submitted = it
                screen = Screen.RESULT
            },
        )
    }
}
