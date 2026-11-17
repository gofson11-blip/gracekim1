package com.gracekim.saju.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gracekim.saju.core.BirthInput
import com.gracekim.saju.core.CalendarType
import com.gracekim.saju.core.Gender
import com.gracekim.saju.core.LunarCalendar
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

/** 입력 화면 상태 (화면 회전 시에도 유지되도록 Serializable) */
data class FormData(
    val name: String = "",
    val gender: Gender = Gender.FEMALE,
    val calendarType: CalendarType = CalendarType.SOLAR,
    val isLeapMonth: Boolean = false,
    val year: String = "",
    val month: String = "",
    val day: String = "",
    val hour: String = "",
    val minute: String = "",
    val timeUnknown: Boolean = false,
    val longitudeCorrection: Boolean = true,
    /** 이 사람 정보를 휴대폰에 저장할지 */
    val saveProfile: Boolean = true,
) : Serializable {
    fun toBirthInput(): BirthInput = BirthInput(
        name = name.trim(),
        gender = gender,
        calendarType = calendarType,
        year = year.toInt(),
        month = month.toInt(),
        day = day.toInt(),
        isLeapMonth = calendarType == CalendarType.LUNAR && isLeapMonth,
        time = if (timeUnknown) null else LocalTime.of(hour.toInt(), minute.ifBlank { "0" }.toInt()),
        applyLongitudeCorrection = longitudeCorrection,
    )

    /** 입력 오류 메시지, 문제 없으면 null */
    fun validate(): String? {
        val y = year.toIntOrNull() ?: return "태어난 연도를 입력해 주세요."
        val m = month.toIntOrNull() ?: return "태어난 월을 입력해 주세요."
        val d = day.toIntOrNull() ?: return "태어난 일을 입력해 주세요."
        if (y !in 1900..2100) return "1900년부터 2100년 사이만 지원합니다."
        if (m !in 1..12) return "월은 1~12 사이로 입력해 주세요."
        if (calendarType == CalendarType.SOLAR) {
            runCatching { LocalDate.of(y, m, d) }.onFailure { return "존재하지 않는 날짜입니다." }
        } else {
            runCatching { LunarCalendar.lunarToSolar(y, m, d, isLeapMonth) }
                .onFailure { return it.message ?: "존재하지 않는 음력 날짜입니다." }
        }
        if (!timeUnknown) {
            val h = hour.toIntOrNull() ?: return "태어난 시를 입력하거나 '시간 모름'을 선택해 주세요."
            if (h !in 0..23) return "시는 0~23 사이로 입력해 주세요."
            val min = minute.ifBlank { "0" }.toIntOrNull() ?: return "분을 숫자로 입력해 주세요."
            if (min !in 0..59) return "분은 0~59 사이로 입력해 주세요."
        }
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    form: FormData,
    onFormChange: (FormData) -> Unit,
    onSubmit: (FormData) -> Unit,
    title: String = "사주사랑",
    intro: String = "태어난 날과 시간을 입력하면\n사주팔자와 오행, 대운, 오늘의 운세를 풀어 드립니다.",
    submitLabel: String = "사주 보기",
    onBack: (() -> Unit)? = null,
    savedProfiles: List<FormData> = emptyList(),
    onPickProfile: (FormData) -> Unit = {},
    onDeleteProfile: (FormData) -> Unit = {},
) {
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val numberKeyboard = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
    fun digits(s: String, max: Int) = s.filter { it.isDigit() }.take(max)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text("〈 뒤로", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (savedProfiles.isNotEmpty()) {
                SavedProfiles(savedProfiles, onPickProfile, onDeleteProfile)
                Text("➕ 새로 입력하기", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(
                intro,
                style = MaterialTheme.typography.bodyLarge,
            )

            OutlinedTextField(
                value = form.name,
                onValueChange = { onFormChange(form.copy(name = it.take(20))) },
                label = { Text("이름 (선택)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Section("성별") {
                OptionRow(
                    options = listOf(Gender.FEMALE to "여성", Gender.MALE to "남성"),
                    selected = form.gender,
                    onSelect = { onFormChange(form.copy(gender = it)) },
                )
            }

            Section("생년월일") {
                OptionRow(
                    options = listOf(CalendarType.SOLAR to "양력", CalendarType.LUNAR to "음력"),
                    selected = form.calendarType,
                    onSelect = { onFormChange(form.copy(calendarType = it)) },
                )
                if (form.calendarType == CalendarType.LUNAR) {
                    CheckRow("윤달", form.isLeapMonth) { onFormChange(form.copy(isLeapMonth = it)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = form.year,
                        onValueChange = { onFormChange(form.copy(year = digits(it, 4))) },
                        label = { Text("년") },
                        placeholder = { Text("1990") },
                        singleLine = true,
                        keyboardOptions = numberKeyboard,
                        modifier = Modifier.weight(1.4f),
                    )
                    OutlinedTextField(
                        value = form.month,
                        onValueChange = { onFormChange(form.copy(month = digits(it, 2))) },
                        label = { Text("월") },
                        singleLine = true,
                        keyboardOptions = numberKeyboard,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = form.day,
                        onValueChange = { onFormChange(form.copy(day = digits(it, 2))) },
                        label = { Text("일") },
                        singleLine = true,
                        keyboardOptions = numberKeyboard,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Section("태어난 시간") {
                CheckRow("시간을 몰라요", form.timeUnknown) { onFormChange(form.copy(timeUnknown = it)) }
                if (!form.timeUnknown) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = form.hour,
                            onValueChange = { onFormChange(form.copy(hour = digits(it, 2))) },
                            label = { Text("시 (0~23)") },
                            singleLine = true,
                            keyboardOptions = numberKeyboard,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = form.minute,
                            onValueChange = { onFormChange(form.copy(minute = digits(it, 2))) },
                            label = { Text("분") },
                            singleLine = true,
                            keyboardOptions = numberKeyboard.copy(imeAction = ImeAction.Done),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    CheckRow("서울 기준 시간 보정 (-32분)", form.longitudeCorrection) {
                        onFormChange(form.copy(longitudeCorrection = it))
                    }
                    Text(
                        "한국 표준시는 실제 태양 시간보다 약 30분 빠릅니다. 보정하면 전통 방식에 더 가깝게 시주를 계산합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            CheckRow("이 사람 정보 저장하기 (휴대폰 안에만 저장돼요)", form.saveProfile) {
                onFormChange(form.copy(saveProfile = it))
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    val message = form.validate()
                    error = message
                    if (message == null) onSubmit(form)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(submitLabel, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** 저장된 사람 목록: 누르면 바로 결과, ✕로 삭제 */
@Composable
private fun SavedProfiles(
    profiles: List<FormData>,
    onPick: (FormData) -> Unit,
    onDelete: (FormData) -> Unit,
) {
    var confirmDelete by remember { mutableStateOf<FormData?>(null) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("👥 저장된 사람", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "이름을 누르면 바로 사주를 볼 수 있어요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            profiles.forEach { p ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(p) }
                        .padding(vertical = 8.dp),
                ) {
                    Text(if (p.gender == Gender.FEMALE) "👩" else "👨", modifier = Modifier.padding(end = 10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.name.ifBlank { "이름 없음" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "${if (p.calendarType == CalendarType.LUNAR) "음력" else "양력"} ${p.year}.${p.month}.${p.day}" +
                                (if (p.timeUnknown) " · 시간 모름" else " · ${p.hour}시 ${p.minute.ifBlank { "0" }}분"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(onClick = { confirmDelete = p }) { Text("✕ 삭제") }
                }
            }
        }
    }
    confirmDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text("삭제할까요?") },
            text = { Text("${target.name.ifBlank { "이름 없음" }}님의 정보를 휴대폰에서 지웁니다.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(target)
                    confirmDelete = null
                }) { Text("삭제") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = null }) { Text("취소") } },
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun <T> OptionRow(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        options.forEach { (value, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = value == selected,
                    onClick = { onSelect(value) },
                    role = Role.RadioButton,
                ),
            ) {
                RadioButton(selected = value == selected, onClick = null)
                Text(label, modifier = Modifier.padding(start = 4.dp, end = 8.dp))
            }
        }
    }
}

@Composable
private fun CheckRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.toggleable(value = checked, onValueChange = onChange, role = Role.Checkbox),
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(label, modifier = Modifier.padding(start = 4.dp))
    }
}
