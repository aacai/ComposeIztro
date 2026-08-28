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
import androidx.compose.foundation.layout.heightIn
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
    val highlight = palaceHighlight(focusedIndex, palace.index)
    val bg = when (highlight) {
        PalaceHighlight.Focused -> IztroTheme.focusedPalaceBg
        PalaceHighlight.Opposite -> IztroTheme.oppositePalaceBg
        PalaceHighlight.Surrounded -> IztroTheme.surroundedPalaceBg
        PalaceHighlight.None -> Color.White
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
            .padding(3.dp),
    ) {
        // 上下分区：主星区吃剩余高度，footer 只占自身高度，互不遮挡
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
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
                    Spacer(Modifier.weight(1f))
                }

                if (showDecadal || showYearly) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (showDecadal) {
                            horoscope?.decadal?.stars?.getOrNull(palace.index)?.forEach {
                                HoroStar(it, IztroTheme.active)
                            }
                        }
                        if (showYearly) {
                            horoscope?.yearly?.stars?.getOrNull(palace.index)?.forEach {
                                HoroStar(it, IztroTheme.decorator2)
                            }
                        }
                    }
                }

                if (horoscopeNames.isNotEmpty()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 17.dp)
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        horoscopeNames.forEachIndexed { i, tag ->
                            if (i > 0) Spacer(Modifier.padding(horizontal = 2.dp))
                            FateScopeLabel(tag) {
                                if (tag.scope != "age") onToggleScope(tag.scope)
                            }
                        }
                    }
                }
            }

            // footer：自然高度，不叠在主星上
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                if (palace.adjectiveStars.isNotEmpty()) {
                    val maxPerCol = 4
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(end = 4.dp),
                    ) {
                        palace.adjectiveStars.chunked(maxPerCol).forEach { col ->
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                col.forEach { AdjStar(it) }
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = palace.ages.take(7).joinToString(" "),
                        fontSize = 9.sp,
                        color = IztroTheme.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 10.sp,
                    )
                    Text(
                        text = palace.decadalRangeText,
                        fontSize = 9.sp,
                        color = IztroTheme.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        lineHeight = 10.sp,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(top = 1.dp),
                    ) {
                        if (showDecadal) {
                            horoscope?.decadal?.palaceNames?.getOrNull(palace.index)?.let {
                                DynamicPalaceName(it, IztroTheme.active)
                            }
                        }
                        if (showYearly) {
                            horoscope?.yearly?.palaceNames?.getOrNull(palace.index)?.let {
                                DynamicPalaceName(it, IztroTheme.decorator2)
                            }
                        }
                        if (showMonthly) {
                            horoscope?.monthly?.palaceNames?.getOrNull(palace.index)?.let {
                                DynamicPalaceName(it, IztroTheme.nice)
                            }
                        }
                        if (showDaily) {
                            horoscope?.daily?.palaceNames?.getOrNull(palace.index)?.let {
                                DynamicPalaceName(it, IztroTheme.decorator1)
                            }
                        }
                        if (showHourly) {
                            horoscope?.hourly?.palaceNames?.getOrNull(palace.index)?.let {
                                DynamicPalaceName(it, IztroTheme.textMuted)
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 4.dp),
                ) {
                    Text(
                        palace.changsheng12,
                        fontSize = 8.sp,
                        color = IztroTheme.decorator1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 9.sp,
                    )
                    Text(
                        palace.boshi12,
                        fontSize = 8.sp,
                        color = IztroTheme.decorator1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 9.sp,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = palace.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IztroTheme.starMajorRed,
                            maxLines = 1,
                            lineHeight = 13.sp,
                        )
                        if (palace.isBodyPalace) {
                            Text(
                                text = "·身",
                                fontSize = 9.sp,
                                color = IztroTheme.starMajorRed,
                                modifier = Modifier.padding(start = 1.dp),
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .hoverable(stemInteraction)
                        .clickable { onToggleFlyStar(palace.heavenlyStem) },
                ) {
                    Text(
                        text = if (showYearly) {
                            horoscope?.yearly?.suiqian12?.getOrNull(palace.index) ?: palace.suiqian12
                        } else palace.suiqian12,
                        fontSize = 8.sp,
                        color = IztroTheme.decorator1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 9.sp,
                    )
                    Text(
                        text = if (showYearly) {
                            horoscope?.yearly?.jiangqian12?.getOrNull(palace.index) ?: palace.jiangqian12
                        } else palace.jiangqian12,
                        fontSize = 8.sp,
                        color = IztroTheme.decorator2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 9.sp,
                    )
                    // 干支：选中宫位也不改背景/字色
                    Text(
                        text = palace.heavenlyStem,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.nice,
                        lineHeight = 13.sp,
                    )
                    Text(
                        text = palace.earthlyBranch,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IztroTheme.nice,
                        lineHeight = 13.sp,
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
    val nameColor = when (star.type) {
        "lucun", "tough" -> IztroTheme.awesome
        "tianma", "helper" -> IztroTheme.active
        "flower" -> IztroTheme.happy
        else -> color
    }
    Text(
        text = star.name,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = nameColor,
        maxLines = 1,
        lineHeight = 11.sp,
    )
}

@Composable
private fun DynamicPalaceName(name: String, color: Color) {
    Text(name, fontSize = 9.sp, color = color, fontWeight = FontWeight.SemiBold, maxLines = 1)
}

@Composable
private fun FateScopeLabel(tag: FateTag, onClick: () -> Unit) {
    val isActive = tag.show && tag.scope != "age"
    val bgColor = when {
        isActive -> IztroTheme.scopeColor(tag.scope)
        tag.scope == "age" -> Color.Transparent
        else -> IztroTheme.major
    }
    Text(
        text = buildString {
            append(tag.name)
            tag.heavenlyStem?.let { append("·$it") }
        },
        fontSize = 10.sp,
        color = if (tag.scope == "age") IztroTheme.textMuted else Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .then(if (tag.scope != "age") Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 3.dp, vertical = 1.dp),
    )
}

private data class FateTag(
    val name: String,
    val heavenlyStem: String?,
    val scope: String,
    val show: Boolean,
)
