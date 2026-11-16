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
import com.gracekim.saju.core.SajuCalculator
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

@Composable
fun SajuApp() {
    var form by rememberSaveable { mutableStateOf(FormData()) }
    var submitted by rememberSaveable { mutableStateOf<FormData?>(null) }

    val current = submitted
    if (current == null) {
        InputScreen(
            form = form,
            onFormChange = { form = it },
            onSubmit = { submitted = it },
        )
    } else {
        val chart = remember(current) { SajuCalculator.calculate(current.toBirthInput()) }
        BackHandler { submitted = null }
        ResultScreen(chart = chart, onBack = { submitted = null })
    }
}
