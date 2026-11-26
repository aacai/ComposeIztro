package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import zhiqiu.app.destiny.oinkcompat.Card
import zhiqiu.app.destiny.oinkcompat.Section
import zhiqiu.app.destiny.oinkcompat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.DemoChart
import zhiqiu.iztro.DemoHoroscope
import zhiqiu.iztro.DemoPalace
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.data.CALENDAR_EARTHLY_BRANCHES
import zhiqiu.iztro.data.CALENDAR_HEAVENLY_STEMS
import zhiqiu.iztro.data.FIVE_TIGER
import zhiqiu.iztro.data.MONTHLY_EARTHLY_BRANCHES
/**
 * 底部三重运限表：十年大运 / 年运 / 月运。
 * 点击后改运限日期并打开对应层级，盘面大限宫、流年星、流月宫名随之更新。
 */
@Composable
fun HoroscopeTimeline(
    chart: DemoChart,
    horoscope: DemoHoroscope,
    horoscopeDate: String,
    horoscopeHour: Int,
    showDecadal: Boolean,
    showYearly: Boolean,
    showMonthly: Boolean,
    showDaily: Boolean,
    onSelectDecadal: (palaceIndex: Int, solarDate: String) -> Unit,
    onSelectYear: (solarDate: String) -> Unit,
    onSelectMonth: (solarDate: String) -> Unit,
    onSelectDay: () -> Unit,
    onSetHoroscopeDate: (solarDate: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val decadals = remember(chart) {
        chart.palaces
            .sortedBy { it.decadalStart }
            .map { it.toDecadalChip() }
    }
    val selectedDecadal = chart.palaces.getOrNull(horoscope.decadal.index)
    val yearChips = remember(selectedDecadal, chart.rawLunar.lunarYear) {
        val p = selectedDecadal ?: return@remember emptyList()
        (p.decadalStart..p.decadalEnd).map { age ->
            val lunarYear = chart.rawLunar.lunarYear + age - 1
            YearChip(age = age, lunarYear = lunarYear, ganZhi = yearGanZhi(lunarYear))
        }
    }
    val selectedLunarYear = horoscope.rawLunar.lunarYear
    val selectedLunarMonth = horoscope.rawLunar.lunarMonth
    val monthYear = selectedLunarYear.takeIf { it > 0 }
        ?: yearChips.firstOrNull()?.lunarYear
        ?: (chart.rawLunar.lunarYear + (selectedDecadal?.decadalStart ?: 1) - 1)

    val daily = horoscope.daily
    val dailyPalace = chart.palaces.getOrNull(daily.index)
    val dailyStars = daily.stars.getOrNull(daily.index).orEmpty()
    val monthDates = remember(horoscopeDate) {
        val base = runCatching { SolarDate.parse(horoscopeDate) }.getOrNull()
            ?: return@remember emptyList<SolarDate>()
        val lastDay = SolarDate(base.year, base.month, 1).plusMonths(1).plusDays(-1).day
        (1..lastDay).map { SolarDate(base.year, base.month, it) }
    }
    val dayBranchByDate = remember(monthDates, horoscopeHour) {
        monthDates.associate { date ->
            date.toIso() to Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
                date.toIso(),
                horoscopeHour,
            ).daily.second
        }
    }
    val currentDate = runCatching { SolarDate.parse(horoscopeDate) }.getOrNull()
    val todayIndex = monthDates.indexOfFirst { date ->
        currentDate != null && date.year == currentDate.year &&
            date.month == currentDate.month && date.day == currentDate.day
    }.takeIf { it >= 0 } ?: 0
    val firstVisible = (todayIndex - 2).coerceAtLeast(0)
    val dayListState = rememberLazyListState(initialFirstVisibleItemIndex = firstVisible)
    LaunchedEffect(horoscopeDate) {
        dayListState.animateScrollToItem(firstVisible)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Section(title = "运限") {
            Card(contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    TimelineRow(label = "大运") {
                        decadals.forEach { chip ->
                            val selected = showDecadal && chip.palaceIndex == horoscope.decadal.index
                            TimelineChip(
                                primary = "${chip.start}-${chip.end}",
                                secondary = chip.palaceName,
                                selected = selected,
                                selectedColor = IztroTheme.scopePalaceDecadal,
                                onClick = {
                                    val midAge = (chip.start + chip.end) / 2
                                    onSelectDecadal(chip.palaceIndex, solarDateForAge(chart, midAge))
                                },
                            )
                        }
                    }
                    TimelineRow(label = "年运") {
                        if (yearChips.isEmpty()) {
                            Text("先选大运", fontSize = 10.sp, color = IztroTheme.textMuted)
                        } else {
                            yearChips.forEach { chip ->
                                val selected = showYearly && chip.lunarYear == selectedLunarYear
                                TimelineChip(
                                    primary = chip.lunarYear.toString(),
                                    secondary = "${chip.ganZhi}年",
                                    selected = selected,
                                    selectedColor = IztroTheme.scopePalaceYearly,
                                    onClick = { onSelectYear(solarDateForLunar(chip.lunarYear, 6, 15)) },
                                )
                            }
                        }
                    }
                    TimelineRow(label = "月运") {
                        MONTH_LABELS.forEachIndexed { i, name ->
                            val month = i + 1
                            val selected = showMonthly && month == selectedLunarMonth &&
                                yearChips.any { it.lunarYear == selectedLunarYear }
                            TimelineChip(
                                primary = name,
                                secondary = monthGanZhi(monthYear, month),
                                selected = selected,
                                selectedColor = IztroTheme.scopePalaceMonthly,
                                onClick = { onSelectMonth(solarDateForLunar(monthYear, month, 15)) },
                            )
                        }
                    }
                    TimelineRow(label = "日运") {
                        monthDates.forEach { date ->
                            val selected = showDaily && currentDate != null &&
                                date.year == currentDate.year &&
                                date.month == currentDate.month && date.day == currentDate.day
                            TimelineChip(
                                primary = "${date.day}日",
                                secondary = dayBranchByDate[date.toIso()].orEmpty(),
                                selected = selected,
                                selectedColor = IztroTheme.scopePalaceDaily,
                                onClick = {
                                    onSetHoroscopeDate(date.toIso())
                                    onSelectDay()
                                },
                            )
                        }
                    }
                    Text(
                        text = "运限日 $horoscopeDate · ${horoscope.lunarDate}",
                        fontSize = 9.sp,
                        color = IztroTheme.textMuted,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
            }
        }

        Section(title = "日运") {
            Card(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "${daily.heavenlyStem}${daily.earthlyBranch}日 · $horoscopeDate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IztroTheme.scopePalaceDaily,
                    )
                    Text(
                        text = "流日宫：${dailyPalace?.name ?: "—"}（${dailyPalace?.earthlyBranch ?: daily.earthlyBranch}）",
                        fontSize = 10.sp,
                        color = IztroTheme.textPrimary,
                    )
                    if (dailyStars.isNotEmpty()) {
                        Text(
                            text = "流日星：${dailyStars.joinToString("、") { it.name }}",
                            fontSize = 10.sp,
                            color = IztroTheme.textMuted,
                        )
                    }
                    if (daily.mutagen.isNotEmpty()) {
                        Text(
                            text = "流日四化：${daily.mutagen.joinToString("、")}",
                            fontSize = 10.sp,
                            color = IztroTheme.textMuted,
                        )
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        state = dayListState,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        itemsIndexed(monthDates, key = { _, date -> date.toIso() }) { _, date ->
                            val selected = date.year == currentDate?.year &&
                                date.month == currentDate.month && date.day == currentDate.day
                            TimelineChip(
                                primary = "${date.day}日",
                                secondary = dayBranchByDate[date.toIso()].orEmpty(),
                                selected = selected,
                                selectedColor = IztroTheme.scopePalaceDaily,
                                onClick = { onSetHoroscopeDate(date.toIso()) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    label: String,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = IztroTheme.major,
            modifier = Modifier.padding(end = 4.dp),
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/** 双行宫位格：上行主信息、下行干支/宫名（略紧凑，仍保持可读） */
@Composable
private fun TimelineChip(
    primary: String,
    secondary: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    val bg = if (selected) selectedColor.copy(alpha = 0.18f) else IztroTheme.chipInactiveBg
    val primaryFg = if (selected) selectedColor else IztroTheme.chipInactiveFg
    val secondaryFg = if (selected) selectedColor.copy(alpha = 0.88f) else IztroTheme.textMuted
    val shape = RoundedCornerShape(4.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .widthIn(min = 36.dp)
            .background(bg, shape)
            .border(1.dp, if (selected) selectedColor else IztroTheme.gridLine, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Text(
            text = primary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = primaryFg,
            textAlign = TextAlign.Center,
            maxLines = 1,
            lineHeight = 12.sp,
        )
        Text(
            text = secondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal,
            color = secondaryFg,
            textAlign = TextAlign.Center,
            maxLines = 1,
            lineHeight = 10.sp,
        )
    }
}

private data class DecadalChip(
    val palaceIndex: Int,
    val start: Int,
    val end: Int,
    val palaceName: String,
)

private fun DemoPalace.toDecadalChip() = DecadalChip(
    palaceIndex = index,
    start = decadalStart,
    end = decadalEnd,
    palaceName = name.take(2),
)

private data class YearChip(val age: Int, val lunarYear: Int, val ganZhi: String)

private val MONTH_LABELS = listOf(
    "正月", "二月", "三月", "四月", "五月", "六月",
    "七月", "八月", "九月", "十月", "冬月", "腊月",
)

/** 虚岁 → 运限阳历日（取该虚岁对应农历年的近似阳历，不跑历法转换） */
private fun solarDateForAge(chart: DemoChart, age: Int, month: Int = 6, day: Int = 15): String {
    val lunarYear = chart.rawLunar.lunarYear + age - 1
    return solarDateForLunar(lunarYear, month, day)
}

/**
 * 运限锚点用近似阳历日即可：horoscope 内部会再 solar2lunar。
 * 避免每次点选/建表都 lunar2solar。
 */
private fun solarDateForLunar(lunarYear: Int, lunarMonth: Int, lunarDay: Int): String {
    val m = lunarMonth.coerceIn(1, 12)
    val d = lunarDay.coerceIn(1, 28)
    return "%04d-%02d-%02d".format(lunarYear, m, d)
}

/** 农历年干支：公元年与甲子纪年差 4（与历法库一致），O(1) 不查表 */
private fun yearGanZhi(lunarYear: Int): String {
    val stem = CALENDAR_HEAVENLY_STEMS[(lunarYear - 4).mod(10)]
    val branch = CALENDAR_EARTHLY_BRANCHES[(lunarYear - 4).mod(12)]
    return "$stem$branch"
}

/** 五虎遁月柱：正月起寅，按年干定寅月天干 */
private fun monthGanZhi(lunarYear: Int, lunarMonth: Int): String {
    val monthIdx = (lunarMonth - 1).coerceIn(0, 11)
    val yearStemIdx = (lunarYear - 4).mod(10)
    val yinStem = FIVE_TIGER[yearStemIdx]
    val yinStemIdx = CALENDAR_HEAVENLY_STEMS.indexOf(yinStem).coerceAtLeast(0)
    val stem = CALENDAR_HEAVENLY_STEMS[(yinStemIdx + monthIdx).mod(10)]
    val branch = MONTHLY_EARTHLY_BRANCHES[monthIdx]
    return "$stem$branch"
}
