package com.gracekim.saju

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.gracekim.saju.ads.Ads
import com.gracekim.saju.core.Compatibility
import com.gracekim.saju.core.Gender
import com.gracekim.saju.core.SajuCalculator
import com.gracekim.saju.data.ProfileStore
import com.gracekim.saju.ui.CompatibilityScreen
import com.gracekim.saju.ui.FormData
import com.gracekim.saju.ui.InputScreen
import com.gracekim.saju.ui.ResultScreen
import com.gracekim.saju.ui.SajuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Ads.initialize(this)
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
    val context = LocalContext.current
    val store = remember { ProfileStore(context) }
    var profiles by remember { mutableStateOf(store.load()) }

    var screen by rememberSaveable { mutableStateOf(Screen.INPUT) }
    var form by rememberSaveable { mutableStateOf(FormData()) }
    var submitted by rememberSaveable { mutableStateOf<FormData?>(null) }
    var partnerForm by rememberSaveable { mutableStateOf(FormData(gender = Gender.MALE)) }
    var partnerSubmitted by rememberSaveable { mutableStateOf<FormData?>(null) }
    var autoOpened by rememberSaveable { mutableStateOf(false) }

    // 저장된 사람이 있으면 앱을 열자마자 그 사람의 사주를 보여 줍니다.
    LaunchedEffect(Unit) {
        if (!autoOpened) {
            autoOpened = true
            profiles.firstOrNull()?.let {
                submitted = it
                screen = Screen.RESULT
            }
        }
    }

    val me = submitted
    val partner = partnerSubmitted
    val myChart = remember(me) { me?.let { runCatching { SajuCalculator.calculate(it.toBirthInput()) }.getOrNull() } }
    val partnerChart = remember(partner) { partner?.let { runCatching { SajuCalculator.calculate(it.toBirthInput()) }.getOrNull() } }

    fun showMe(f: FormData) {
        if (f.saveProfile) profiles = store.upsert(f)
        submitted = f
        screen = Screen.RESULT
    }

    fun showPartner(f: FormData) {
        if (f.saveProfile) profiles = store.upsert(f)
        partnerSubmitted = f
        screen = Screen.COMPATIBILITY
    }

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
                onSubmit = { showPartner(it) },
                title = "궁합 보기",
                intro = "궁합을 볼 상대방의 정보를 입력해 주세요.",
                submitLabel = "궁합 보기",
                onBack = { screen = Screen.RESULT },
                savedProfiles = profiles.filterNot { it == me },
                onPickProfile = {
                    partnerSubmitted = it
                    screen = Screen.COMPATIBILITY
                },
                onDeleteProfile = { profiles = store.delete(it) },
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
            onSubmit = { showMe(it) },
            savedProfiles = profiles,
            onPickProfile = {
                profiles = store.moveToFront(it)
                submitted = it
                screen = Screen.RESULT
            },
            onDeleteProfile = { profiles = store.delete(it) },
        )
    }
}
