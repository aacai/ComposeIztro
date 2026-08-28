package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.Algorithm
import zhiqiu.iztro.AstroType
import zhiqiu.iztro.DemoChart
import zhiqiu.iztro.DemoFourPillar
import zhiqiu.iztro.DemoHoroscope
import zhiqiu.iztro.data.earthlyBranches
import zhiqiu.iztro.data.heavenlyStems
import zhiqiu.iztro.i18n.kot

/**
 * 中宫：基本信息 + 自化说明 + 日/时步进
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun IzpalaceCenter(
    chart: DemoChart,
    horoscope: DemoHoroscope?,
    horoscopeDate: String,
    horoscopeHour: Int,
    arrowIndex: Int?,
    arrowScope: String?,
    onSetHoroscopeDate: (String) -> Unit,
    onSetHoroscopeHour: (Int) -> Unit,
    onBirthdayChange: (String) -> Unit = {},
    onBirthTimeChange: (Int) -> Unit = {},
    lang: String = "zh-CN",
    centerPalaceAlign: Boolean = false,
    algorithm: Algorithm = "default",
    astroType: AstroType = "heaven",
    onAlgorithmChange: (Algorithm) -> Unit = {},
    onAstroTypeChange: (AstroType) -> Unit = {},
    showDecadal: Boolean = false,
    showYearly: Boolean = false,
    showMonthly: Boolean = false,
    showDaily: Boolean = false,
    showHourly: Boolean = false,
    onToggleScope: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val lineIndex = arrowIndex ?: -1
    val align = if (centerPalaceAlign) Alignment.CenterHorizontally else Alignment.Start
    val lunarLine = buildLunarLine(chart)
    val jieqiPillars = chart.jieqiPillars.ifEmpty { chart.pillars }

    // 日±/时±改的是排盘出生日期与时辰
    val birthDate = SolarDate.parse(chart.solarDate)
    val minDate = SolarDate(1900, 1, 1)
    val maxDate = SolarDate(2100, 12, 31)
    val canDayMinus = birthDate.isAfter(minDate)
    val canDayPlus = birthDate.isBefore(maxDate)
    val canHourMinus = chart.timeIndex > 0
    val canHourPlus = chart.timeIndex < 11

    Box(
        modifier = modifier
            .border(1.dp, IztroTheme.border)
            .background(IztroTheme.centerBg)
            .padding(6.dp),
    ) {
        if (chart.earthlyBranchOfSoulPalace.isNotEmpty()) {
            SurroundedLine(
                index = lineIndex,
                scope = arrowScope,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = align,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = chart.name.ifBlank { " " },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IztroTheme.major,
                    lineHeight = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Text(
                    text = chart.genderLabel.ifBlank { chart.gender },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (chart.genderLabel.contains("男") || chart.gender == "男") {
                        IztroTheme.quan
                    } else {
                        IztroTheme.happy
                    },
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp),
                )
                Text(
                    text = chart.fiveElementsClass,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IztroTheme.awesome,
                    lineHeight = 13.sp,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CenterLine("真太阳时", chart.trueSolarTimeText)
                CenterLine("农历", lunarLine)
                CenterLine(
                    "命主",
                    "${chart.soul} 身主：${chart.body} 子斗：${chart.earthlyBranchOfSoulPalace}",
                )
                CenterLine("星座", chart.sign)

                if (jieqiPillars.size == 4) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            "节气四柱",
                            fontSize = 10.sp,
                            color = IztroTheme.textMuted,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        FourPillarsRow(jieqiPillars)
                    }
                }
            }

            SelfMutagenLegend()

            HoroscopeStepBar(
                dateText = chart.solarDate,
                hour = chart.timeIndex,
                canDayMinus = canDayMinus,
                canDayPlus = canDayPlus,
                canHourMinus = canHourMinus,
                canHourPlus = canHourPlus,
                onDayMinus = {
                    if (canDayMinus) onBirthdayChange(birthDate.plusDays(-1).toIso())
                },
                onDayPlus = {
                    if (canDayPlus) onBirthdayChange(birthDate.plusDays(1).toIso())
                },
                onHourMinus = {
                    if (canHourMinus) onBirthTimeChange(chart.timeIndex - 1)
                },
                onHourPlus = {
                    if (canHourPlus) onBirthTimeChange(chart.timeIndex + 1)
                },
                showDecadal = showDecadal,
                showYearly = showYearly,
                showMonthly = showMonthly,
                showDaily = showDaily,
                showHourly = showHourly,
                onToggleScope = onToggleScope,
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (centerPalaceAlign) Arrangement.Center else Arrangement.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                AlgorithmSelector(lang, algorithm, onAlgorithmChange)
                if (algorithm != "default") {
                    AstroTypeSelector(lang, astroType, onAstroTypeChange)
                }
            }
        }

        Text(
            text = chart.copyright.ifEmpty { "Powered by iztro" },
            fontSize = 8.sp,
            color = Color.Black.copy(alpha = 0.2f),
            modifier = Modifier.align(Alignment.BottomEnd).padding(1.dp),
        )
    }
}

@Composable
private fun SelfMutagenLegend() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("点击宫位自化：", fontSize = 10.sp, color = IztroTheme.textMuted, lineHeight = 12.sp)
        LegendItem("禄", IztroTheme.selfLu)
        Text("→", fontSize = 10.sp, color = IztroTheme.textMuted, lineHeight = 12.sp)
        LegendItem("权", IztroTheme.selfQuan)
        Text("→", fontSize = 10.sp, color = IztroTheme.textMuted, lineHeight = 12.sp)
        LegendItem("科", IztroTheme.selfKe)
        Text("→", fontSize = 10.sp, color = IztroTheme.textMuted, lineHeight = 12.sp)
        LegendItem("忌", IztroTheme.selfJi)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        lineHeight = 13.sp,
    )
}

@Composable
private fun HoroscopeStepBar(
    dateText: String,
    hour: Int,
    canDayMinus: Boolean,
    canDayPlus: Boolean,
    canHourMinus: Boolean,
    canHourPlus: Boolean,
    onDayMinus: () -> Unit,
    onDayPlus: () -> Unit,
    onHourMinus: () -> Unit,
    onHourPlus: () -> Unit,
    showDecadal: Boolean,
    showYearly: Boolean,
    showMonthly: Boolean,
    showDaily: Boolean,
    showHourly: Boolean,
    onToggleScope: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "排盘 $dateText　${CHINESE_HOURS.getOrElse(hour) { "$hour" }}",
            fontSize = 10.sp,
            color = IztroTheme.focus,
            lineHeight = 12.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepBtn("日-", enabled = canDayMinus, onClick = onDayMinus)
            StepBtn("日+", enabled = canDayPlus, onClick = onDayPlus)
            StepBtn("时-", enabled = canHourMinus, onClick = onHourMinus)
            StepBtn("时+", enabled = canHourPlus, onClick = onHourPlus)
        }
        // 运限开关：默认全关，只看原盘；打开后宫位才显示对应标签/运星
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScopeToggle("大限", showDecadal) { onToggleScope("decadal") }
            ScopeToggle("流年", showYearly) { onToggleScope("yearly") }
            ScopeToggle("流月", showMonthly) { onToggleScope("monthly") }
            ScopeToggle("流日", showDaily) { onToggleScope("daily") }
            ScopeToggle("流时", showHourly) { onToggleScope("hourly") }
        }
    }
}

@Composable
private fun ScopeToggle(label: String, active: Boolean, onClick: () -> Unit) {
    val bg = if (active) IztroTheme.scopeColor(
        when (label) {
            "大限" -> "decadal"
            "流年" -> "yearly"
            "流月" -> "monthly"
            "流日" -> "daily"
            else -> "hourly"
        },
    ) else Color(0xFFF0F0F0)
    val fg = if (active) Color.White else Color(0xFF9E9E9E)
    Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        lineHeight = 12.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun StepBtn(label: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) Color(0xFFE8EAF6) else Color(0xFFF0F0F0)
    val fg = if (enabled) IztroTheme.quan else Color(0xFFBDBDBD)
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        lineHeight = 13.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun CenterLine(label: String, value: String) {
    Text(
        text = "$label：$value",
        fontSize = 10.sp,
        color = IztroTheme.focus,
        lineHeight = 12.sp,
        maxLines = 1,
    )
}

@Composable
private fun FourPillarsRow(pillars: List<DemoFourPillar>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        pillars.forEach { pillar ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    pillar.stem,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = wuxingColor(stemElement(pillar.stem)),
                    lineHeight = 14.sp,
                )
                Text(
                    pillar.branch,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = wuxingColor(branchElement(pillar.branch)),
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

private fun stemElement(stem: String): String {
    val key = kot<String>(stem, "Heavenly")
    return heavenlyStems[key]?.fiveElements ?: ""
}

private fun branchElement(branch: String): String {
    val key = kot<String>(branch, "Earthly")
    return earthlyBranches[key]?.fiveElements ?: ""
}

private fun wuxingColor(element: String): Color = when (element) {
    "木" -> Color(0xFF2E7D32)
    "火" -> Color(0xFFC62828)
    "土" -> Color(0xFF8D6E63)
    "金" -> Color(0xFFB8860B)
    "水" -> Color(0xFF1565C0)
    else -> IztroTheme.focus
}

private fun buildLunarLine(chart: DemoChart): String {
    val yearGanZhi = chart.pillars.getOrNull(0)?.let { "${it.stem}${it.branch}" }.orEmpty()
    val monthNames = listOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊")
    val monthIdx = (chart.rawLunar.lunarMonth - 1).coerceIn(0, 11)
    val monthStr = buildString {
        if (chart.rawLunar.isLeap) append("闰")
        append(monthNames[monthIdx])
        append("月")
    }
    val dayStr = lunarDayName(chart.rawLunar.lunarDay)
    val yearPart = if (yearGanZhi.isNotEmpty()) "${yearGanZhi}年" else ""
    return "$yearPart$monthStr$dayStr ${chart.time}"
}

private fun lunarDayName(day: Int): String {
    val ones = listOf("", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十")
    val body = when (day) {
        10 -> "初十"
        20 -> "二十"
        30 -> "三十"
        in 1..9 -> "初${ones[day]}"
        in 11..19 -> "十${ones[day - 10]}"
        in 21..29 -> "廿${ones[day - 20]}"
        else -> "$day"
    }
    return "${body}日"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlgorithmSelector(lang: String, algorithm: Algorithm, onChange: (Algorithm) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextField(
            value = when (algorithm) {
                "zhongzhou" -> ReactUiStrings.t(lang, "algorithmZhongzhou")
                else -> ReactUiStrings.t(lang, "algorithmDefault")
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(ReactUiStrings.t(lang, "labelAlgorithm"), fontSize = 9.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().padding(end = 4.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(ReactUiStrings.t(lang, "algorithmDefault"), fontSize = 11.sp) },
                onClick = { onChange("default"); expanded = false },
            )
            DropdownMenuItem(
                text = { Text(ReactUiStrings.t(lang, "algorithmZhongzhou"), fontSize = 11.sp) },
                onClick = { onChange("zhongzhou"); expanded = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AstroTypeSelector(lang: String, astroType: AstroType, onChange: (AstroType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextField(
            value = when (astroType) {
                "earth" -> ReactUiStrings.t(lang, "astroTypeEarth")
                "human" -> ReactUiStrings.t(lang, "astroTypeHuman")
                else -> ReactUiStrings.t(lang, "astroTypeHeaven")
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(ReactUiStrings.t(lang, "labelAstroType"), fontSize = 9.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(ReactUiStrings.t(lang, "astroTypeHeaven"), fontSize = 11.sp) },
                onClick = { onChange("heaven"); expanded = false },
            )
            DropdownMenuItem(
                text = { Text(ReactUiStrings.t(lang, "astroTypeEarth"), fontSize = 11.sp) },
                onClick = { onChange("earth"); expanded = false },
            )
            DropdownMenuItem(
                text = { Text(ReactUiStrings.t(lang, "astroTypeHuman"), fontSize = 11.sp) },
                onClick = { onChange("human"); expanded = false },
            )
        }
    }
}
