package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import zhiqiu.app.destiny.oinkcompat.Text
import androidx.compose.runtime.Composable
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
    showDecadal: Boolean,
    showYearly: Boolean,
    showMonthly: Boolean,
    onSelectDecadal: (palaceIndex: Int, solarDate: String) -> Unit,
    onSelectYear: (solarDate: String) -> Unit,
    onSelectMonth: (solarDate: String) -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(IztroTheme.centerBg)
            .border(1.dp, IztroTheme.gridLine)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
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
        Text(
            text = "运限日 $horoscopeDate · ${horoscope.lunarDate}",
            fontSize = 9.sp,
            color = IztroTheme.textMuted,
            modifier = Modifier.padding(top = 1.dp),
        )
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
