package zhiqiu.iztro.bazi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.bazi.original.ElementPhase
import zhiqiu.iztro.bazi.original.OriginalChart
import zhiqiu.iztro.bazi.original.PillarView

private val LabelWidth = 36.dp
private val LabelColor = Color(0xFF5A5A5A)
private val PhaseBarBg = Color(0xFFF7F2E9)
private val PhaseDivider = Color(0xFFD8D0C6)

/**
 * 原局盘面。宿主负责排盘：传入 [chart]（如 `profile.toOriginalChart()`）。
 * 排盘失败由宿主处理（构造不出 chart 就不进本页）。
 */
@Composable
fun BaziOriginalPage(chart: OriginalChart) {
    OriginalBody(chart)
}

@Composable
fun OriginalBody(chart: OriginalChart) {
    val pillars = chart.pillars
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WuXingColors.Page)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        BaziChartHeader(solarLabel = chart.solarLabel, termLabel = chart.termLabel)
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WuXingColors.Panel, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 12.dp),
        ) {
            // 柱头：左侧空出标签位
            LabeledRow(label = "") {
                PillarCells(pillars) { p ->
                    Text(
                        p.title,
                        color = WuXingColors.Muted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            // 干神↔天干同属干支区紧贴；天干↔地支保留一点间隔
            LabeledRow("干神") {
                PillarCells(pillars) { p ->
                    Text(
                        p.stemGod,
                        color = WuXingColors.ofStem(p.stem),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 12.sp,
                    )
                }
            }
            LabeledRow("天干") {
                PillarCells(pillars) { p ->
                    Text(
                        p.stem,
                        color = WuXingColors.ofStem(p.stem),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("地支") {
                PillarCells(pillars) { p ->
                    Text(
                        p.branch,
                        color = WuXingColors.ofBranch(p.branch),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // 藏干格内多行贴紧；藏干行与支神行之间略隔开
            LabeledRow("藏干", alignTop = true) {
                PillarCells(pillars) { p ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        p.hideStems.forEach { h ->
                            Text(
                                "${h.stem}·${h.element}",
                                color = WuXingColors.ofElement(h.element),
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("支神", alignTop = true) {
                PillarCells(pillars) { p ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        p.hideStems.forEach { h ->
                            Text(
                                h.tenGod,
                                color = WuXingColors.ofElement(h.element),
                                fontSize = 11.sp,
                                lineHeight = 11.sp,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // 纳音 / 空亡 / 地势 / 自坐：各是不同行，行间略隔开
            LabeledRow("纳音") {
                PillarCells(pillars) { p ->
                    Text(
                        p.nayin,
                        color = nayinColor(p.nayin),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 13.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("空亡") {
                PillarCells(pillars) { p ->
                    Text(p.kongWang, color = WuXingColors.Ink, fontSize = 13.sp, lineHeight = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("地势") {
                PillarCells(pillars) { p ->
                    Text(p.diShi, color = WuXingColors.Ink, fontSize = 13.sp, lineHeight = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("自坐") {
                PillarCells(pillars) { p ->
                    Text(p.ziZuo, color = WuXingColors.Ink, fontSize = 13.sp, lineHeight = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // 神煞格内多行贴紧；字号与纳音等行一致
            LabeledRow("神煞", alignTop = true) {
                PillarCells(pillars) { p ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (p.shenSha.isEmpty()) {
                            Text("—", color = WuXingColors.Muted, fontSize = 13.sp, lineHeight = 13.sp)
                        } else {
                            p.shenSha.forEach { name ->
                                Text(name, color = WuXingColors.Ink, fontSize = 13.sp, lineHeight = 13.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 天干 / 地支作用 + 旺相休囚死
            LabeledRow("天干") {
                Text(
                    text = chart.stemRelations,
                    color = WuXingColors.Ink,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LabeledRow("地支") {
                Text(
                    text = chart.branchRelations,
                    color = WuXingColors.Ink,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ElementPhaseBar(chart.elementPhases)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LabeledRow(
    label: String,
    alignTop: Boolean = false,
    content: @Composable () -> Unit,
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
                Text(
                    text = label,
                    color = LabelColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun PillarCells(
    pillars: List<PillarView>,
    content: @Composable (PillarView) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        pillars.forEach { p ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                content(p)
            }
        }
    }
}

@Composable
private fun ElementPhaseBar(phases: List<ElementPhase>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(PhaseBarBg, RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        phases.forEachIndexed { index, phase ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.5f)
                        .background(PhaseDivider),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${phase.element}${phase.phase}",
                    color = WuXingColors.ofElement(phase.element),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun nayinColor(nayin: String): Color {
    val wx = when {
        nayin.endsWith("木") -> "木"
        nayin.endsWith("火") -> "火"
        nayin.endsWith("土") -> "土"
        nayin.endsWith("金") -> "金"
        nayin.endsWith("水") -> "水"
        else -> ""
    }
    return WuXingColors.ofElement(wx)
}
