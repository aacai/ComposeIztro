package zhiqiu.iztro.bazi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.bazi.flow.DecadeOption
import zhiqiu.iztro.bazi.flow.FlowChart
import zhiqiu.iztro.bazi.flow.FlowSelection
import zhiqiu.iztro.bazi.flow.MonthOption
import zhiqiu.iztro.bazi.flow.YearOption
import zhiqiu.iztro.bazi.original.PillarView
import zhiqiu.iztro.bazi.original.tenGodAbbrev

private val LabelWidth = 28.dp
private val LabelColor = Color(0xFF6A6A6A)
private val SelectedBg = Color(0xFFF2E8DC)
private val NatalLuckDivider = Color(0xFFD0C8C0)
private val SectionMuted = Color(0xFF999999)
private val SoftDivider = Color(0xFFE0D8D0)
/** 大运 / 流年 / 流月单列上限，避免宽屏被拉太开 */
private val LuckCellMaxWidth = 60.dp

/**
 * 流盘页面。宿主负责排盘与选择状态：
 * 点击大运/流年/流月通过 [onSelectionChange] 上抛，宿主以新 [FlowSelection] 重排并传入新 [chart]。
 */
@Composable
fun BaziFlowPage(
    chart: FlowChart,
    onSelectionChange: (FlowSelection) -> Unit = {},
) {
    FlowBody(
        chart = chart,
        onSelectDecade = { i ->
            onSelectionChange(FlowSelection(decadeIndex = i, yearIndex = 0, monthIndex = 0))
        },
        onSelectYear = { i ->
            val cur = chart.selection
            onSelectionChange(FlowSelection(cur.decadeIndex, i, 0))
        },
        onSelectMonth = { i ->
            val cur = chart.selection
            onSelectionChange(FlowSelection(cur.decadeIndex, cur.yearIndex, i))
        },
    )
}

@Composable
private fun FlowBody(
    chart: FlowChart,
    onSelectDecade: (Int) -> Unit,
    onSelectYear: (Int) -> Unit,
    onSelectMonth: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WuXingColors.Page),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        item {
            BaziChartHeader(
                solarLabel = chart.solarLabel,
                termLabel = chart.termLabel,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        item {
            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .background(WuXingColors.Panel, RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
            ) {
                SevenPillarPanel(chart.pillars)

                SectionTitle("大运", chart.qiYunLabel)
                DecadeRow(chart.decades, chart.selection.decadeIndex, onSelectDecade)

                SoftLine()
                SectionTitle("流年")
                YearRow(chart.years, chart.selection.yearIndex, onSelectYear)

                SoftLine()
                SectionTitle("流月")
                MonthRow(chart.months, chart.selection.monthIndex, onSelectMonth)

                Spacer(modifier = Modifier.height(10.dp))
                RelationBlock(
                    natalStem = chart.natalStemRelations,
                    luckStem = chart.luckStemRelations,
                    natalBranch = chart.natalBranchRelations,
                    luckBranch = chart.luckBranchRelations,
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(title, color = WuXingColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
        if (subtitle != null) {
            Text(subtitle, color = SectionMuted, fontSize = 10.sp, lineHeight = 12.sp)
        }
    }
}

@Composable
private fun SoftLine() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        thickness = 0.5.dp,
        color = SoftDivider,
    )
}

@Composable
private fun LuckCell(
    selected: Boolean,
    onClick: () -> Unit,
    top: String,
    second: String,
    stem: String,
    stemGod: String,
    branch: String,
    branchGod: String,
    modifier: Modifier = Modifier,
    footer: String? = null,
    stemSize: Int = 15,
) {
    Column(
        modifier = modifier
            .background(if (selected) SelectedBg else Color.Transparent, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(top, color = WuXingColors.Muted, fontSize = 9.sp, lineHeight = 10.sp)
        Text(second, color = WuXingColors.Ink, fontSize = 10.sp, lineHeight = 11.sp)
        StemBranchWithGod(stem, stemGod, branch, branchGod, stemSize)
        if (footer != null) {
            Text(footer, color = WuXingColors.Muted, fontSize = 8.sp, lineHeight = 9.sp)
        }
    }
}

@Composable
private fun DecadeRow(
    decades: List<DecadeOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val count = decades.size.coerceAtLeast(1)
        val cellWidth = minOf(maxWidth / count, LuckCellMaxWidth)
        LazyRow {
            items(decades, key = { it.index }) { d ->
                LuckCell(
                    modifier = Modifier.width(cellWidth),
                    selected = d.index == selected,
                    onClick = { onSelect(d.index) },
                    top = "${d.startAge}岁",
                    second = "${d.startYear}",
                    stem = d.cycle.getHeavenStem().getName(),
                    stemGod = d.stemGod,
                    branch = d.cycle.getEarthBranch().getName(),
                    branchGod = d.branchGod,
                )
            }
        }
    }
}

@Composable
private fun YearRow(
    years: List<YearOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val count = years.size.coerceAtLeast(1)
        val cellWidth = minOf(maxWidth / count, LuckCellMaxWidth)
        LazyRow {
            itemsIndexed(years, key = { _, y -> y.year }) { idx, y ->
                LuckCell(
                    modifier = Modifier.width(cellWidth),
                    selected = idx == selected,
                    onClick = { onSelect(idx) },
                    top = "${y.age}岁",
                    second = "${y.year}",
                    stem = y.cycle.getHeavenStem().getName(),
                    stemGod = y.stemGod,
                    branch = y.cycle.getEarthBranch().getName(),
                    branchGod = y.branchGod,
                    footer = y.hideBrief,
                )
            }
        }
    }
}

@Composable
private fun MonthRow(
    months: List<MonthOption>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val count = months.size.coerceAtLeast(1)
        val cellWidth = minOf(maxWidth / count, LuckCellMaxWidth)
        LazyRow {
            items(months, key = { it.index }) { m ->
                LuckCell(
                    modifier = Modifier.width(cellWidth),
                    selected = m.index == selected,
                    onClick = { onSelect(m.index) },
                    top = m.label,
                    second = m.termName,
                    stem = m.cycle.getHeavenStem().getName(),
                    stemGod = m.stemGod,
                    branch = m.cycle.getEarthBranch().getName(),
                    branchGod = m.branchGod,
                    stemSize = 14,
                )
            }
        }
    }
}

@Composable
private fun StemBranchWithGod(
    stem: String,
    stemGod: String,
    branch: String,
    branchGod: String,
    stemSize: Int = 13,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stem,
                color = WuXingColors.ofStem(stem),
                fontSize = stemSize.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (stemSize + 1).sp,
            )
            Text("·${tenGodAbbrev(stemGod)}", color = WuXingColors.Muted, fontSize = 8.sp, lineHeight = 9.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                branch,
                color = WuXingColors.ofBranch(branch),
                fontSize = stemSize.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (stemSize + 1).sp,
            )
            Text("·${tenGodAbbrev(branchGod)}", color = WuXingColors.Muted, fontSize = 8.sp, lineHeight = 9.sp)
        }
    }
}

@Composable
private fun SevenPillarPanel(pillars: List<PillarView>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowLabeledRow("") {
            FlowPillarCells(pillars) { p, _ ->
                Text(
                    p.title,
                    color = WuXingColors.Muted,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        FlowLabeledRow("主星") {
            FlowPillarCells(pillars) { p, _ ->
                Text(
                    p.stemGod,
                    color = WuXingColors.ofStem(p.stem),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 14.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        FlowLabeledRow("天干") {
            FlowPillarCells(pillars, showNatalLuckDivider = true) { p, _ ->
                Text(
                    p.stem,
                    color = WuXingColors.ofStem(p.stem),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp,
                )
            }
        }
        FlowLabeledRow("地支") {
            FlowPillarCells(pillars, showNatalLuckDivider = true) { p, _ ->
                Text(
                    p.branch,
                    color = WuXingColors.ofBranch(p.branch),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        FlowLabeledRow("藏干", alignTop = true) {
            FlowPillarCells(pillars) { p, _ ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    p.hideStems.forEach { h ->
                        Text(
                            "${h.stem}·${h.tenGod}",
                            color = WuXingColors.ofElement(h.element),
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        FlowLabeledRow("地势") {
            FlowPillarCells(pillars) { p, _ ->
                Text(p.diShi, color = WuXingColors.Ink, fontSize = 12.sp, lineHeight = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        FlowLabeledRow("自坐") {
            FlowPillarCells(pillars) { p, _ ->
                Text(p.ziZuo, color = WuXingColors.Ink, fontSize = 12.sp, lineHeight = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        FlowLabeledRow("神煞", alignTop = true) {
            FlowPillarCells(pillars) { p, _ ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (p.shenSha.isEmpty()) {
                        Text("—", color = WuXingColors.Muted, fontSize = 11.sp, lineHeight = 13.sp)
                    } else {
                        p.shenSha.forEach { name ->
                            Text(name, color = WuXingColors.Ink, fontSize = 11.sp, lineHeight = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowLabeledRow(
    label: String,
    alignTop: Boolean = false,
    rowContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (alignTop) Alignment.Top else Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(LabelWidth),
            contentAlignment = if (alignTop) Alignment.TopCenter else Alignment.Center,
        ) {
            if (label.isNotEmpty()) {
                Text(label, color = LabelColor, fontSize = 11.sp, lineHeight = 13.sp, textAlign = TextAlign.Center)
            }
        }
        Box(
            modifier = Modifier.weight(1f),
            content = { rowContent() },
        )
    }
}

@Composable
private fun FlowPillarCells(
    pillars: List<PillarView>,
    showNatalLuckDivider: Boolean = false,
    cellContent: @Composable (PillarView, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
    ) {
        pillars.forEachIndexed { index, p ->
            if (showNatalLuckDivider && index == 4) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .width(0.5.dp)
                        .fillMaxHeight()
                        .background(NatalLuckDivider),
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter,
                content = { cellContent(p, index) },
            )
        }
    }
}

@Composable
private fun RelationBlock(
    natalStem: String,
    luckStem: String,
    natalBranch: String,
    luckBranch: String,
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        RelationGroup("天干", natalStem, luckStem)
        Spacer(modifier = Modifier.height(12.dp))
        RelationGroup("地支", natalBranch, luckBranch)
    }
}

@Composable
private fun RelationGroup(category: String, natal: String, luck: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            category,
            color = WuXingColors.Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            modifier = Modifier.width(40.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            RelationLine("本命", natal)
            Spacer(modifier = Modifier.height(6.dp))
            RelationLine("运势", luck)
        }
    }
}

@Composable
private fun RelationLine(label: String, text: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            label,
            color = WuXingColors.Ink,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.width(40.dp),
        )
        Text(
            compactRelationDots(text),
            color = WuXingColors.Ink,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun compactRelationDots(text: String): String =
    text.replace(" · ", "·").replace("· ", "·").replace(" ·", "·")
