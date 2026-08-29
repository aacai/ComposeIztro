package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.DemoHoroscope
import zhiqiu.iztro.DemoPalace
import zhiqiu.iztro.DemoStar

/**
 * 宫位布局：
 * - 主星/辅星（含六煞等）顶部竖列
 * - 杂曜（奏书/月煞/伏兵/贯索等）左下横向、上下层叠
 * - 宫名底部偏右
 * - 干支右下角上下堆叠
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Izpalace(
    palace: DemoPalace,
    horoscope: DemoHoroscope?,
    focusedIndex: Int?,
    activeHeavenlyStem: String?,
    hoverHeavenlyStem: String?,
    showDecadal: Boolean,
    showYearly: Boolean,
    showMonthly: Boolean,
    showDaily: Boolean,
    showHourly: Boolean,
    onFocus: (Int?) -> Unit,
    onClickPalace: (Int) -> Unit,
    onToggleScope: (String) -> Unit,
    onToggleFlyStar: (String) -> Unit,
    onHoverStem: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = LocalAstrolabeStyle.current
    val highlight = palaceHighlight(focusedIndex, palace.index)
    val bg = when (highlight) {
        PalaceHighlight.Focused -> IztroTheme.focusedPalaceBg
        PalaceHighlight.Opposite -> IztroTheme.oppositePalaceBg
        PalaceHighlight.Surrounded -> IztroTheme.surroundedPalaceBg
        PalaceHighlight.None -> IztroTheme.palaceBg
    }

    // 默认原盘：运限标签仅在对应开关打开后显示；小限/大运年龄在 footer 常驻
    val horoscopeNames = buildList {
        horoscope?.let { h ->
            if (showDecadal && h.decadal.index == palace.index) {
                add(FateTag(h.decadal.name, h.decadal.heavenlyStem, "decadal", true))
            }
            if (showYearly && h.yearly.index == palace.index) {
                add(FateTag(h.yearly.name, h.yearly.heavenlyStem, "yearly", true))
            }
            if (showMonthly && h.monthly.index == palace.index) {
                add(FateTag(h.monthly.name, h.monthly.heavenlyStem, "monthly", true))
            }
            if (showDaily && h.daily.index == palace.index) {
                add(FateTag(h.daily.name, h.daily.heavenlyStem, "daily", true))
            }
            if (showHourly && h.hourly.index == palace.index) {
                add(FateTag(h.hourly.name, h.hourly.heavenlyStem, "hourly", true))
            }
        }
    }

    // 运限四化角标：各层都显示；字号由 Izstar 按数量自动缩小
    val horoscopeMutagens = horoscope?.let {
        listOf(
            HoroscopeMutagenDisplay("decadal", it.decadal.mutagen, showDecadal),
            HoroscopeMutagenDisplay("yearly", it.yearly.mutagen, showYearly),
            HoroscopeMutagenDisplay("monthly", it.monthly.mutagen, showMonthly),
            HoroscopeMutagenDisplay("daily", it.daily.mutagen, showDaily),
            HoroscopeMutagenDisplay("hourly", it.hourly.mutagen, showHourly),
        )
    } ?: emptyList()

    val stemInteraction = remember { MutableInteractionSource() }
    val stemHovered by stemInteraction.collectIsHoveredAsState()
    LaunchedEffect(stemHovered) {
        if (stemHovered) onHoverStem(palace.heavenlyStem) else onHoverStem(null)
    }

    val palaceInteraction = remember { MutableInteractionSource() }
    val palaceHovered by palaceInteraction.collectIsHoveredAsState()
    LaunchedEffect(palaceHovered) {
        onFocus(if (palaceHovered) palace.index else null)
    }

    Box(
        modifier = modifier
            .border(1.dp, IztroTheme.border)
            .background(bg)
            .hoverable(palaceInteraction)
            .clickable { onClickPalace(palace.index) }
            .padding(style.cellPad),
    ) {
        // 主星区可滚动；footer（含大限年龄）align(Bottom) 钉在宫格最底。
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                val decadalStars = if (showDecadal) {
                    horoscope?.decadal?.stars?.getOrNull(palace.index).orEmpty()
                } else emptyList()
                val yearlyStars = if (showYearly) {
                    horoscope?.yearly?.stars?.getOrNull(palace.index).orEmpty()
                } else emptyList()
                val hasHoroStars = decadalStars.isNotEmpty() || yearlyStars.isNotEmpty()

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    palace.majorStars.forEach { star ->
                        PalaceStar(star, palace.heavenlyStem, activeHeavenlyStem, hoverHeavenlyStem, horoscopeMutagens)
                    }
                    palace.minorStars.forEach { star ->
                        PalaceStar(star, palace.heavenlyStem, activeHeavenlyStem, hoverHeavenlyStem, horoscopeMutagens)
                    }
                    if (hasHoroStars) {
                        FlowRow(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            decadalStars.forEach { HoroStar(it, IztroTheme.active) }
                            yearlyStars.forEach { HoroStar(it, IztroTheme.decorator2) }
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }

                // 小限靠左；运限 chip 同排靠右，避免浮层压住小限
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = palace.ages.take(if (style.compact) 5 else 7).joinToString(","),
                        fontSize = style.ageSp,
                        color = IztroTheme.textMuted,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = style.ageSp * 1.15f,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 4.dp),
                    )
                    if (horoscopeNames.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            horoscopeNames.forEach { tag ->
                                FateScopeLabel(tag, compact = style.compact) {
                                    if (tag.scope != "age") onToggleScope(tag.scope)
                                }
                            }
                        }
                    }
                }
            }

            // footer 钉在宫格最底：左杂曜/小注、中（大限在上+宫名在下）、右干支，三者底对齐。
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(bg),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(Modifier.weight(1f)) {
                    if (palace.adjectiveStars.isNotEmpty()) {
                        val maxPerCol = if (style.compact) 5 else 4
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(bottom = 2.dp),
                        ) {
                            palace.adjectiveStars.chunked(maxPerCol).forEach { col ->
                                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                    col.forEach { AdjStar(it) }
                                }
                            }
                        }
                    }
                    // 博士/岁前/将前等横向小注：放左下，与杂曜同区（十二长生改到天干上方）
                    if (style.showPalaceDecor) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.padding(bottom = 2.dp),
                        ) {
                            Text(palace.boshi12, fontSize = 11.sp, color = IztroTheme.decorator1, maxLines = 1, lineHeight = 12.sp)
                            Text(
                                text = if (showYearly) {
                                    horoscope?.yearly?.suiqian12?.getOrNull(palace.index) ?: palace.suiqian12
                                } else palace.suiqian12,
                                fontSize = 11.sp, color = IztroTheme.decorator1, maxLines = 1, lineHeight = 12.sp,
                            )
                            Text(
                                text = if (showYearly) {
                                    horoscope?.yearly?.jiangqian12?.getOrNull(palace.index) ?: palace.jiangqian12
                                } else palace.jiangqian12,
                                fontSize = 11.sp, color = IztroTheme.decorator2, maxLines = 1, lineHeight = 12.sp,
                            )
                        }
                    }
                    // 运限动态宫名改挂在中央宫名旁，这里不再横排全称（避免迁移等挤乱左下）
                }

                // 中：大限年龄；其下 Row 左=时/日/月（底对齐上叠），右=年→运→本命宫名（同列紧贴，不被顶开）
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp),
                ) {
                    Text(
                        text = palace.decadalRangeText,
                        fontSize = style.ageSp,
                        color = IztroTheme.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = style.ageSp * 1.15f,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        // 时↑日↑月：贴本命宫名左侧，由下往上叠，不挤开年/运
                        if (showHourly || showDaily || showMonthly) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(end = 2.dp),
                            ) {
                                if (showHourly) {
                                    horoscope?.hourly?.palaceNames?.getOrNull(palace.index)?.let { full ->
                                        DynamicPalaceName(
                                            text = formatScopePalaceName("时", full),
                                            color = IztroTheme.scopePalaceHourly,
                                        )
                                    }
                                }
                                if (showDaily) {
                                    horoscope?.daily?.palaceNames?.getOrNull(palace.index)?.let { full ->
                                        DynamicPalaceName(
                                            text = formatScopePalaceName("日", full),
                                            color = IztroTheme.scopePalaceDaily,
                                        )
                                    }
                                }
                                if (showMonthly) {
                                    horoscope?.monthly?.palaceNames?.getOrNull(palace.index)?.let { full ->
                                        DynamicPalaceName(
                                            text = formatScopePalaceName("月", full),
                                            color = IztroTheme.scopePalaceMonthly,
                                        )
                                    }
                                }
                            }
                        }
                        // 年/运紧贴本命宫名上方，与时日月分列，激活流月流日时不会被顶走
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (showYearly) {
                                horoscope?.yearly?.palaceNames?.getOrNull(palace.index)?.let { full ->
                                    DynamicPalaceName(
                                        text = formatScopePalaceName("年", full),
                                        color = IztroTheme.scopePalaceYearly,
                                    )
                                }
                            }
                            if (showDecadal) {
                                horoscope?.decadal?.palaceNames?.getOrNull(palace.index)?.let { full ->
                                    DynamicPalaceName(
                                        text = formatScopePalaceName("运", full),
                                        color = IztroTheme.scopePalaceDecadal,
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = palace.name,
                                    fontSize = style.palaceNameSp,
                                    fontWeight = FontWeight.Bold,
                                    color = IztroTheme.starMajorRed,
                                    maxLines = 1,
                                    lineHeight = style.palaceNameSp * 1.15f,
                                )
                                if (palace.isBodyPalace) {
                                    Text(
                                        text = "·身",
                                        fontSize = style.palaceNameSp,
                                        fontWeight = FontWeight.Bold,
                                        color = IztroTheme.starMajorRed,
                                        modifier = Modifier.padding(start = 1.dp),
                                        lineHeight = style.palaceNameSp * 1.15f,
                                    )
                                }
                            }
                        }
                    }
                }

                // 右：十二长生固定在天干上方（不受 compact 隐藏 decor 影响），其下干支
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .hoverable(stemInteraction)
                        .clickable { onToggleFlyStar(palace.heavenlyStem) },
                ) {
                    Text(
                        text = palace.changsheng12,
                        fontSize = 11.sp,
                        color = IztroTheme.decorator1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp,
                    )
                    Text(
                        text = palace.heavenlyStem,
                        fontSize = style.stemBranchSp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.nice,
                        lineHeight = style.stemBranchSp * 1.1f,
                    )
                    Text(
                        text = palace.earthlyBranch,
                        fontSize = style.stemBranchSp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.nice,
                        lineHeight = style.stemBranchSp * 1.1f,
                    )
                }
            }
        }
    }
}

@Composable
private fun PalaceStar(
    star: DemoStar,
    palaceStem: String,
    activeStem: String?,
    hoverStem: String?,
    horoscopeMutagens: List<HoroscopeMutagenDisplay>,
) {
    Izstar(star, palaceStem, activeStem, hoverStem, horoscopeMutagens, horizontal = false)
}

@Composable
private fun AdjStar(star: DemoStar) {
    Izstar(star, "", null, null, emptyList(), horizontal = true)
}

@Composable
private fun HoroStar(star: DemoStar, color: Color) {
    val style = LocalAstrolabeStyle.current
    // 运星统一小号，明显小于主星，避免挡主体；各星同字号
    val size = style.horoStarSp
    val nameColor = when (star.type) {
        "lucun", "tough" -> IztroTheme.awesome
        "tianma", "helper" -> IztroTheme.active
        "flower" -> IztroTheme.happy
        else -> color
    }
    Text(
        text = star.name,
        fontSize = size,
        fontWeight = FontWeight.Medium,
        color = nameColor,
        maxLines = 1,
        lineHeight = size * 1.1f,
    )
}

@Composable
private fun DynamicPalaceName(text: String, color: Color) {
    // 与本命宫名（迁移/父母等）同字号
    val style = LocalAstrolabeStyle.current
    val size = style.palaceNameSp
    Text(
        text = text,
        fontSize = size,
        color = color,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        lineHeight = size * 1.15f,
    )
}

/** 全称宫名 → 运/年/月/日/时 + 单字简称，如 官禄→运官、疾厄→日凶 */
private fun formatScopePalaceName(prefix: String, fullName: String): String =
    prefix + shortPalaceName(fullName)

private fun shortPalaceName(fullName: String): String = when {
    fullName.contains("命") -> "命"
    fullName.contains("兄") -> "兄"
    fullName.contains("夫") || fullName.contains("妻") -> "夫"
    fullName.contains("子") -> "子"
    fullName.contains("财") -> "财"
    fullName.contains("疾") || fullName.contains("厄") -> "凶"
    fullName.contains("迁") -> "迁"
    fullName.contains("仆") || fullName.contains("友") || fullName.contains("奴") -> "仆"
    fullName.contains("官") -> "官"
    fullName.contains("田") -> "田"
    fullName.contains("福") -> "福"
    fullName.contains("父") || fullName.contains("母") -> "父"
    else -> fullName.takeLast(1).ifEmpty { fullName.take(1) }
}

@Composable
private fun FateScopeLabel(tag: FateTag, compact: Boolean, onClick: () -> Unit) {
    val isActive = tag.show && tag.scope != "age"
    val bgColor = when {
        isActive -> IztroTheme.scopeColor(tag.scope)
        tag.scope == "age" -> Color.Transparent
        else -> IztroTheme.major
    }
    val size = if (compact) 9.sp else 10.sp
    Text(
        text = buildString {
            append(shortScopeLabel(tag.scope, tag.name))
            tag.heavenlyStem?.let { append("·$it") }
        },
        fontSize = size,
        color = if (tag.scope == "age") IztroTheme.textMuted else Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        lineHeight = size * 1.15f,
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bgColor)
            .then(if (tag.scope != "age") Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 3.dp, vertical = 0.dp),
    )
}

/** 大限/流年… → 限/年/月/日/时，缩小占位 */
private fun shortScopeLabel(scope: String, fallback: String): String = when (scope) {
    "decadal" -> "限"
    "yearly" -> "年"
    "monthly" -> "月"
    "daily" -> "日"
    "hourly" -> "时"
    else -> fallback.takeLast(1).ifEmpty { fallback.take(1) }
}

private data class FateTag(
    val name: String,
    val heavenlyStem: String?,
    val scope: String,
    val show: Boolean,
)
