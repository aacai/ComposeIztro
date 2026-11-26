package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import zhiqiu.app.destiny.oinkcompat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
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
@OptIn(ExperimentalLayoutApi::class)
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
    colorMode: IztroColorMode = IztroColorMode.Light,
    onToggleColorMode: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val style = LocalAstrolabeStyle.current
    val lineIndex = arrowIndex ?: -1
    val align = if (centerPalaceAlign) Alignment.CenterHorizontally else Alignment.Start
    val lunarLine = buildLunarLine(chart)
    val jieqiPillars = chart.jieqiPillars.ifEmpty { chart.pillars }

    // 年±/日±/时±改的是排盘出生年、日与时辰
    val birthDate = SolarDate.parse(chart.solarDate)
    val minDate = SolarDate(1900, 1, 1)
    val maxDate = SolarDate(2100, 12, 31)
    val canYearMinus = birthDate.year > minDate.year
    val canYearPlus = birthDate.year < maxDate.year
    val canDayMinus = birthDate.isAfter(minDate)
    val canDayPlus = birthDate.isBefore(maxDate)
    val canHourMinus = chart.timeIndex > 0
    val canHourPlus = chart.timeIndex < 11
    val gap = if (style.compact) 1.dp else 3.dp
    val pad = if (style.compact) 3.dp else 5.dp
    val nominalAge = horoscope?.age?.nominalAge
    val decadalPalaceName = horoscope?.decadal?.let { d ->
        chart.palaces.getOrNull(d.index)?.name
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(style.cellCorner))
            .border(1.dp, IztroTheme.gridLine, RoundedCornerShape(style.cellCorner))
            .background(IztroTheme.centerBg)
            .padding(pad),
    ) {
        if (chart.earthlyBranchOfSoulPalace.isNotEmpty()) {
            SurroundedLine(
                index = lineIndex,
                scope = arrowScope,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 中宫内容按可用高度自适应缩放，保证命主/四化等一屏看全
        CenterAutoFit(style = style, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = align,
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = chart.name.ifBlank { " " },
                        fontSize = style.centerTitleSp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.major,
                        lineHeight = style.centerTitleSp * 1.15f,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Text(
                        text = chart.genderLabel.ifBlank { chart.gender },
                        fontSize = style.centerBodySp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (chart.genderLabel.contains("男") || chart.gender == "男") {
                            IztroTheme.quan
                        } else {
                            IztroTheme.happy
                        },
                        lineHeight = style.centerBodySp * 1.15f,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                    Text(
                        text = chart.fiveElementsClass,
                        fontSize = style.centerBodySp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.awesome,
                        lineHeight = style.centerBodySp * 1.15f,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                    CenterLine("真太阳时", chart.trueSolarTimeText, style.centerBodySp)
                    CenterLine("农历", lunarLine, style.centerBodySp)
                    CenterLine(
                        "命主",
                        "${chart.soul} 身主：${chart.body} 子斗：${chart.earthlyBranchOfSoulPalace}",
                        style.centerBodySp,
                    )
                    // 虚岁按运限日相对出生推算，便于对照各宫大限年龄段
                    if (nominalAge != null) {
                        val ageValue = buildString {
                            append(nominalAge)
                            append(" 岁")
                            if (!decadalPalaceName.isNullOrEmpty()) {
                                append("　大限·")
                                append(decadalPalaceName)
                            }
                        }
                        CenterLine("虚岁", ageValue, style.centerBodySp)
                    }
                    if (!style.compact) {
                        CenterLine("星座", chart.sign, style.centerBodySp)
                    }

                    if (jieqiPillars.size == 4) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Text(
                                "节气四柱",
                                fontSize = style.centerBodySp,
                                color = IztroTheme.textMuted,
                                lineHeight = style.centerBodySp * 1.15f,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                            FourPillarsRow(jieqiPillars, compact = style.compact)
                        }
                    }
                }

                if (!style.compact) {
                    SelfMutagenLegend()
                } else {
                    SelfMutagenLegendCompact()
                }

                HoroscopeStepBar(
                    canYearMinus = canYearMinus,
                    canYearPlus = canYearPlus,
                    canDayMinus = canDayMinus,
                    canDayPlus = canDayPlus,
                    canHourMinus = canHourMinus,
                    canHourPlus = canHourPlus,
                    onYearMinus = {
                        if (canYearMinus) onBirthdayChange(birthDate.plusYears(-1).toIso())
                    },
                    onYearPlus = {
                        if (canYearPlus) onBirthdayChange(birthDate.plusYears(1).toIso())
                    },
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
                    compact = style.compact,
                    btnSp = style.centerBtnSp,
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (centerPalaceAlign) Arrangement.Center else Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    StatusChip(algorithmLabel(lang, algorithm))
                    if (algorithm != "default") {
                        StatusChip(astroTypeLabel(lang, astroType))
                    }
                }
            }
        }
    }
}

/** 中宫自适应：先量自然高度，超出可用高度则整体缩放字号（最低 0.55），仍放不下可滚动。 */
private enum class CenterSlot { Probe, Fit, Fit2, Scroll }

private const val CENTER_MIN_SCALE = 0.55f

@Composable
private fun CenterAutoFit(
    style: AstrolabeStyle,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val w = constraints.maxWidth
        if (w <= 0) {
            return@SubcomposeLayout layout(0, 0) {}
        }

        // 1) 基准样式的自然高度
        val probe = subcompose(CenterSlot.Probe) { content() }
            .first()
            .measure(Constraints(maxWidth = w, maxHeight = Constraints.Infinity))

        // 高度无上限时直接用自然高度
        val h = if (constraints.hasBoundedHeight) constraints.maxHeight else probe.height
        if (h <= 0) {
            return@SubcomposeLayout layout(w, 0) {}
        }

        var scale = if (probe.height > h) {
            (h.toFloat() / probe.height.toFloat()).coerceIn(CENTER_MIN_SCALE, 1f)
        } else {
            1f
        }

        fun measureAt(slot: CenterSlot, s: Float) = subcompose(slot) {
            CompositionLocalProvider(LocalAstrolabeStyle provides style.scaled(s)) { content() }
        }
            .first()
            .measure(Constraints(maxWidth = w, maxHeight = Constraints.Infinity))

        // 2) 未缩放时直接复用探针，省一遍中宫内容组合+测量
        var placeable = if (scale == 1f) {
            probe
        } else {
            measureAt(CenterSlot.Fit, scale)
        }
        if (placeable.height > h) {
            val next = (scale * h.toFloat() / placeable.height.toFloat())
                .coerceIn(CENTER_MIN_SCALE, 1f)
            if (next < scale) {
                scale = next
                placeable = measureAt(CenterSlot.Fit2, scale)
            }
        }

        if (placeable.height <= h) {
            layout(w, h) {
                placeable.place(0, 0)
            }
        } else {
            // 缩到最低仍放不下：允许滚动查看
            val scroll = subcompose(CenterSlot.Scroll) {
                CompositionLocalProvider(LocalAstrolabeStyle provides style.scaled(scale)) {
                    Column(Modifier.verticalScroll(rememberScrollState())) { content() }
                }
            }
                .first()
                .measure(Constraints(maxWidth = w, maxHeight = h))
            layout(w, h) {
                scroll.place(0, 0)
            }
        }
    }
}

@Composable
private fun SelfMutagenLegend() {
    val style = LocalAstrolabeStyle.current
    val labelSp = style.centerBodySp
    val itemSp = style.centerBodySp * 1.08f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("点击宫位自化：", fontSize = labelSp, color = IztroTheme.textMuted, lineHeight = labelSp * 1.15f)
        LegendItem("禄", IztroTheme.selfLu, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted, lineHeight = labelSp * 1.15f)
        LegendItem("权", IztroTheme.selfQuan, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted, lineHeight = labelSp * 1.15f)
        LegendItem("科", IztroTheme.selfKe, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted, lineHeight = labelSp * 1.15f)
        LegendItem("忌", IztroTheme.selfJi, itemSp)
    }
}

@Composable
private fun SelfMutagenLegendCompact() {
    val style = LocalAstrolabeStyle.current
    val labelSp = style.centerBodySp
    val itemSp = style.centerBodySp * 1.08f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("自化", fontSize = labelSp, color = IztroTheme.textMuted, lineHeight = labelSp * 1.15f)
        LegendItem("禄", IztroTheme.selfLu, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted)
        LegendItem("权", IztroTheme.selfQuan, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted)
        LegendItem("科", IztroTheme.selfKe, itemSp)
        Text("→", fontSize = labelSp, color = IztroTheme.textMuted)
        LegendItem("忌", IztroTheme.selfJi, itemSp)
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
    size: androidx.compose.ui.unit.TextUnit = 14.sp,
) {
    Text(
        text = label,
        fontSize = size,
        fontWeight = FontWeight.Bold,
        color = color,
        lineHeight = size * 1.15f,
    )
}

@Composable
private fun HoroscopeStepBar(
    canYearMinus: Boolean,
    canYearPlus: Boolean,
    canDayMinus: Boolean,
    canDayPlus: Boolean,
    canHourMinus: Boolean,
    canHourPlus: Boolean,
    onYearMinus: () -> Unit,
    onYearPlus: () -> Unit,
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
    compact: Boolean = false,
    btnSp: androidx.compose.ui.unit.TextUnit = 14.sp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepBtn("日-", enabled = canDayMinus, onClick = onDayMinus, fontSize = btnSp, compact = compact)
            StepBtn("日+", enabled = canDayPlus, onClick = onDayPlus, fontSize = btnSp, compact = compact)
            StepBtn("时-", enabled = canHourMinus, onClick = onHourMinus, fontSize = btnSp, compact = compact)
            StepBtn("时+", enabled = canHourPlus, onClick = onHourPlus, fontSize = btnSp, compact = compact)
            StepBtn("年-", enabled = canYearMinus, onClick = onYearMinus, fontSize = btnSp, compact = compact)
            StepBtn("年+", enabled = canYearPlus, onClick = onYearPlus, fontSize = btnSp, compact = compact)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            ScopeToggle("大限", showDecadal, compact) { onToggleScope("decadal") }
            ScopeToggle("流年", showYearly, compact) { onToggleScope("yearly") }
            ScopeToggle("流月", showMonthly, compact) { onToggleScope("monthly") }
            ScopeToggle("流日", showDaily, compact) { onToggleScope("daily") }
            ScopeToggle("流时", showHourly, compact) { onToggleScope("hourly") }
        }
    }
}

@Composable
private fun ScopeToggle(label: String, active: Boolean, compact: Boolean = false, onClick: () -> Unit) {
    val style = LocalAstrolabeStyle.current
    val size = style.centerBtnSp
    val bg = if (active) IztroTheme.scopeColor(
        when (label) {
            "大限" -> "decadal"
            "流年" -> "yearly"
            "流月" -> "monthly"
            "流日" -> "daily"
            else -> "hourly"
        },
    ) else IztroTheme.chipInactiveBg
    val fg = if (active) Color.White else IztroTheme.chipInactiveFg
    Text(
        text = label,
        fontSize = size,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        lineHeight = size * 1.15f,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 5.dp else 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun StepBtn(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    compact: Boolean = false,
) {
    val bg = if (enabled) IztroTheme.stepBtnBg else IztroTheme.stepBtnDisabledBg
    val fg = if (enabled) IztroTheme.quan else IztroTheme.stepBtnDisabledFg
    Text(
        text = label,
        fontSize = fontSize,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        lineHeight = fontSize * 1.15f,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 3.dp),
    )
}

@Composable
private fun CenterLine(
    label: String,
    value: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.sp,
) {
    Text(
        text = "$label：$value",
        fontSize = fontSize,
        color = IztroTheme.focus,
        lineHeight = fontSize * 1.2f,
        maxLines = 2,
    )
}

@Composable
private fun FourPillarsRow(pillars: List<DemoFourPillar>, compact: Boolean = false) {
    val theme = IztroTheme
    val style = LocalAstrolabeStyle.current
    val baseSp = if (compact) style.centerBodySp * 1.25f else style.centerTitleSp * 1.15f
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val spacing = if (compact) 4.dp else 6.dp
        val n = pillars.size.coerceAtLeast(1)
        // 四组干支按可用宽等分，保证最后一柱不被裁掉
        val fitPx = with(density) {
            ((maxWidth.toPx() - spacing.toPx() * (n - 1)) / n).coerceAtLeast(1f)
        }
        val fitSp = with(density) { (fitPx / fontScale).sp }
        val stemSp = if (fitSp.value < baseSp.value) fitSp else baseSp
        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
            pillars.forEach { pillar ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Text(
                        pillar.stem,
                        fontSize = stemSp,
                        fontWeight = FontWeight.Bold,
                        color = wuxingColor(stemElement(pillar.stem), theme),
                        lineHeight = stemSp * 1.1f,
                    )
                    Text(
                        pillar.branch,
                        fontSize = stemSp,
                        fontWeight = FontWeight.Bold,
                        color = wuxingColor(branchElement(pillar.branch), theme),
                        lineHeight = stemSp * 1.1f,
                    )
                }
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

private fun wuxingColor(element: String, theme: IztroColors): Color = when (element) {
    "木" -> if (theme.isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
    "火" -> if (theme.isDark) Color(0xFFEF9A9A) else Color(0xFFC62828)
    "土" -> if (theme.isDark) Color(0xFFBCAAA4) else Color(0xFF8D6E63)
    "金" -> if (theme.isDark) Color(0xFFFFD54F) else Color(0xFFB8860B)
    "水" -> if (theme.isDark) Color(0xFF90CAF9) else Color(0xFF1565C0)
    else -> theme.focus
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

@Composable
private fun StatusChip(text: String) {
    val style = LocalAstrolabeStyle.current
    Text(
        text = text,
        fontSize = style.centerBtnSp,
        color = IztroTheme.quan,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(IztroTheme.stepBtnBg)
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

private fun algorithmLabel(lang: String, algorithm: Algorithm): String =
    when (algorithm) {
        "zhongzhou" -> ReactUiStrings.t(lang, "algorithmZhongzhou")
        else -> ReactUiStrings.t(lang, "algorithmDefault")
    }

private fun astroTypeLabel(lang: String, astroType: AstroType): String =
    when (astroType) {
        "earth" -> ReactUiStrings.t(lang, "astroTypeEarth")
        "human" -> ReactUiStrings.t(lang, "astroTypeHuman")
        else -> ReactUiStrings.t(lang, "astroTypeHeaven")
    }
