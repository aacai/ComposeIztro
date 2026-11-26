package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
 * - 小限默认在主星下方；下方被四化角标/运星/footer 占满时自动挪到星曜右侧空白
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Izpalace(
    palace: DemoPalace,
    horoscope: DemoHoroscope?,
    highlight: PalaceHighlight,
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

    val decadalStars = if (showDecadal) {
        horoscope?.decadal?.stars?.getOrNull(palace.index).orEmpty()
    } else emptyList()
    val yearlyStars = if (showYearly) {
        horoscope?.yearly?.stars?.getOrNull(palace.index).orEmpty()
    } else emptyList()
    val horoStarCount = decadalStars.size + yearlyStars.size
    val hasHoroStars = horoStarCount > 0
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(style.cellCorner))
            .border(1.dp, IztroTheme.gridLine, RoundedCornerShape(style.cellCorner))
            .background(bg)
            .hoverable(palaceInteraction)
            .clickable { onClickPalace(palace.index) }
            .padding(style.cellPad),
    ) {
        val cellContentWidth = maxWidth
        // 主星区可滚动；footer 钉在最底；小限由 PalaceLayout 按剩余空白自动落位
        // 星曜：实测整行宽后按可用宽整体缩放（含运星/gap/padding）
        PalaceLayout(
            modifier = Modifier.fillMaxSize(),
            stars = { maxStarHeightPx ->
                val maxStarHeight = with(LocalDensity.current) { maxStarHeightPx.toDp() }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopStart,
                ) {
                    StarFitRow(
                        modifier = Modifier.fillMaxWidth(),
                        availableWidth = cellContentWidth,
                        maxHeight = maxStarHeight,
                        edgePad = 4.dp,
                    ) { starScale ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            palace.majorStars.forEach { star ->
                                PalaceStar(
                                    star, palace.heavenlyStem, activeHeavenlyStem,
                                    hoverHeavenlyStem, horoscopeMutagens, starScale,
                                )
                            }
                            palace.minorStars.forEach { star ->
                                PalaceStar(
                                    star, palace.heavenlyStem, activeHeavenlyStem,
                                    hoverHeavenlyStem, horoscopeMutagens, starScale,
                                )
                            }
                            if (hasHoroStars) {
                                FlowRow(
                                    modifier = Modifier.padding(start = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalArrangement = Arrangement.spacedBy(1.dp),
                                    maxItemsInEachRow = 2,
                                ) {
                                    decadalStars.forEach { HoroStar(it, IztroTheme.active, starScale) }
                                    yearlyStars.forEach { HoroStar(it, IztroTheme.decorator2, starScale) }
                                }
                            }
                        }
                    }
                }
            },
            ages = { narrow ->
                AgesBlock(
                    palace = palace,
                    horoscope = horoscope,
                    horoscopeNames = horoscopeNames,
                    narrow = narrow,
                    contentScale = 1f,
                    onToggleScope = onToggleScope,
                )
            },
            footer = {
                // 身份信息固定：宫名、干支、长生；杂曜略跟星区疏密，但仍不放大
                val palaceNameSp = style.palaceNameSp
                val stemSp = style.stemBranchSp
                val ageSp = style.ageSp
                val decorSp = style.adjStarSp * 0.92f
                val adjScale = 1f
                Row(
                    Modifier
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
                                    col.forEach { AdjStar(it, nameSizeScale = adjScale) }
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
                            Text(palace.boshi12, fontSize = decorSp, color = IztroTheme.decorator1, maxLines = 1, lineHeight = decorSp * 1.1f)
                            Text(
                                text = if (showYearly) {
                                    horoscope?.yearly?.suiqian12?.getOrNull(palace.index) ?: palace.suiqian12
                                } else palace.suiqian12,
                                fontSize = decorSp, color = IztroTheme.decorator1, maxLines = 1, lineHeight = decorSp * 1.1f,
                            )
                            Text(
                                text = if (showYearly) {
                                    horoscope?.yearly?.jiangqian12?.getOrNull(palace.index) ?: palace.jiangqian12
                                } else palace.jiangqian12,
                                fontSize = decorSp, color = IztroTheme.decorator2, maxLines = 1, lineHeight = decorSp * 1.1f,
                            )
                        }
                    }
                    // 运限动态宫名改挂在中央宫名旁，这里不再横排全称（避免迁移等挤乱左下）
                }

                // 中：大限年龄 + 运限宫名横排 + 本命宫名（宫名固定字号）
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp),
                ) {
                    Text(
                        text = palace.decadalRangeText,
                        fontSize = ageSp,
                        color = IztroTheme.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = ageSp * 1.15f,
                    )
                    val scopeNames = buildList {
                        if (showDecadal) {
                            horoscope?.decadal?.palaceNames?.getOrNull(palace.index)?.let {
                                add(formatScopePalaceName("运", it) to IztroTheme.scopePalaceDecadal)
                            }
                        }
                        if (showYearly) {
                            horoscope?.yearly?.palaceNames?.getOrNull(palace.index)?.let {
                                add(formatScopePalaceName("年", it) to IztroTheme.scopePalaceYearly)
                            }
                        }
                        if (showMonthly) {
                            horoscope?.monthly?.palaceNames?.getOrNull(palace.index)?.let {
                                add(formatScopePalaceName("月", it) to IztroTheme.scopePalaceMonthly)
                            }
                        }
                        if (showDaily) {
                            horoscope?.daily?.palaceNames?.getOrNull(palace.index)?.let {
                                add(formatScopePalaceName("日", it) to IztroTheme.scopePalaceDaily)
                            }
                        }
                        if (showHourly) {
                            horoscope?.hourly?.palaceNames?.getOrNull(palace.index)?.let {
                                add(formatScopePalaceName("时", it) to IztroTheme.scopePalaceHourly)
                            }
                        }
                    }
                    // 运限宫名略小于本命宫名，多开时再收一点，但不跟星曜密度乱跳
                    val scopeNameScale = when {
                        scopeNames.size >= 3 -> 0.82f
                        scopeNames.size >= 2 -> 0.9f
                        else -> 0.95f
                    }
                    if (scopeNames.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            scopeNames.forEach { (text, color) ->
                                DynamicPalaceName(text = text, color = color, scale = scopeNameScale)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = palace.name,
                            fontSize = palaceNameSp,
                            fontWeight = FontWeight.Bold,
                            color = IztroTheme.starMajorRed,
                            maxLines = 1,
                            lineHeight = palaceNameSp * 1.15f,
                        )
                        if (palace.isBodyPalace) {
                            Text(
                                text = "·身",
                                fontSize = palaceNameSp,
                                fontWeight = FontWeight.Bold,
                                color = IztroTheme.starMajorRed,
                                modifier = Modifier.padding(start = 1.dp),
                                lineHeight = palaceNameSp * 1.15f,
                            )
                        }
                    }
                }

                // 右：干支固定字号（如 庚 / 子）；长生随 decor，compact 隐藏减轻右下角
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .hoverable(stemInteraction)
                        .clickable { onToggleFlyStar(palace.heavenlyStem) },
                ) {
                    if (style.showPalaceDecor) {
                        Text(
                            text = palace.changsheng12,
                            fontSize = decorSp,
                            color = IztroTheme.decorator1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = decorSp * 1.1f,
                        )
                    }
                    Text(
                        text = palace.heavenlyStem,
                        fontSize = stemSp,
                        fontWeight = FontWeight.Medium,
                        color = IztroTheme.nice,
                        lineHeight = stemSp * 1.1f,
                    )
                    Text(
                        text = palace.earthlyBranch,
                        fontSize = stemSp,
                        fontWeight = FontWeight.Medium,
                        color = IztroTheme.nice,
                        lineHeight = stemSp * 1.1f,
                    )
                }
                }
            },
        )
    }
}

/**
 * 宫位三段式布局：星曜区（可滚动）、footer（钉最底）、小限（自动落位）。
 * 小限默认落在星曜下方；当星曜因四化角标/运星变高、下方被 footer 占满时，
 * 自动改落到星曜右侧的空白处，避免被压住。
 */
@Composable
private fun PalaceLayout(
    modifier: Modifier = Modifier,
    /** maxHeightPx = 星曜区可用高度（px），字号据此自适应缩放，避免星曜/四化被裁 */
    stars: @Composable (maxHeightPx: Int) -> Unit,
    /** narrow = 空间紧张（放右侧空白），年龄个数随之压缩 */
    ages: @Composable (narrow: Boolean) -> Unit,
    footer: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val gap = 2.dp.roundToPx()
        val minRightSpace = 28.dp.roundToPx()

        // footer 自然高度（不裁切杂曜/宫名），钉在最底；各宫星曜区高度差异由全盘统一缩放消化
        val footerPlaceable = subcompose(PalaceSlot.Footer) { footer() }
            .first()
            .measure(Constraints.fixedWidth(w).copy(maxHeight = h))
        val footerH = footerPlaceable.height.coerceIn(0, h)
        val footerTop = h - footerH

        // 先测窄版小限高度，从星曜区预留一条带，避免像午宫那样年龄叠在亮度上
        val agesNarrowProbe = subcompose(PalaceSlot.AgesNarrow) { ages(true) }
            .first()
            .measure(constraints.copy(minWidth = 0, minHeight = 0, maxWidth = w))
        // 小限区按实际所需高度预留，不能被 footerTop/3 砍掉，否则流年/小限会被裁掉半行
        val agesReserve = (agesNarrowProbe.height + gap).coerceAtMost(footerTop)

        val starsMaxH = (footerTop - agesReserve).coerceAtLeast(1)
        val starsPlaceable = subcompose(PalaceSlot.Stars) {
            stars(starsMaxH)
        }
            .first()
            .measure(constraints.copy(minWidth = 0, minHeight = 0, maxHeight = starsMaxH))

        var agesPlaceable = agesNarrowProbe
        var agesX = 0
        var agesY = (footerTop - agesPlaceable.height - gap).coerceAtLeast(0)

        val rightSpace = w - starsPlaceable.width - gap
        if (rightSpace >= minRightSpace) {
            // 窄版探针已按窄版内容测过：宽度够就直接复用，省一次 subcompose
            val rightAges = if (
                agesNarrowProbe.width <= rightSpace && agesNarrowProbe.height <= starsMaxH
            ) {
                agesNarrowProbe
            } else {
                subcompose(PalaceSlot.Ages) { ages(true) }
                    .first()
                    .measure(
                        constraints.copy(
                            minWidth = 0,
                            minHeight = 0,
                            maxWidth = rightSpace,
                            maxHeight = starsMaxH,
                        ),
                    )
            }
            if (rightAges.width <= rightSpace) {
                agesPlaceable = rightAges
                agesX = w - rightAges.width
                agesY = (starsPlaceable.height - rightAges.height).coerceAtLeast(0)
                    .coerceAtMost((footerTop - rightAges.height).coerceAtLeast(0))
            }
        } else {
            // 星列占满宽：用预留带横铺，不再压回星曜亮度区
            agesPlaceable = subcompose(PalaceSlot.AgesWide) { ages(false) }
                .first()
                .measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                        maxWidth = w,
                        maxHeight = (footerTop - gap).coerceAtLeast(1),
                    ),
                )
            agesX = 0
            agesY = (footerTop - agesPlaceable.height - gap).coerceAtLeast(0)
        }

        layout(w, h) {
            starsPlaceable.place(0, 0)
            footerPlaceable.place(0, footerTop)
            agesPlaceable.place(agesX, agesY)
        }
    }
}

private enum class PalaceSlot { Stars, Ages, AgesNarrow, AgesWide, Footer }

/** 小限年龄串 + 同排运限 chip；上方常驻流年行，narrow 时压缩年龄个数 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AgesBlock(
    palace: DemoPalace,
    horoscope: DemoHoroscope?,
    horoscopeNames: List<FateTag>,
    narrow: Boolean,
    contentScale: Float = 1f,
    onToggleScope: (String) -> Unit,
) {
    val style = LocalAstrolabeStyle.current
    val ageSp = style.ageSp * contentScale
    val labelSp = (ageSp.value - 2f).coerceAtLeast(7f).sp
    val ageTextSp = (ageSp.value - 1f).coerceAtLeast(8f).sp
    val maxAges = when {
        narrow -> 3
        style.compact -> 5
        else -> 7
    }
    Column {
        // 流年小限：以流年地支起算的每宫年龄串
        val yearlyAges = horoscope?.yearlyAges?.getOrNull(palace.index).orEmpty()
        if (yearlyAges.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "流年:",
                    fontSize = labelSp,
                    color = IztroTheme.textPrimary,
                    maxLines = 1,
                    lineHeight = labelSp * 1.1f,
                    modifier = Modifier.padding(end = 1.dp),
                )
                Text(
                    text = yearlyAges.take(maxAges).joinToString(","),
                    fontSize = ageTextSp,
                    color = IztroTheme.textPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = ageTextSp * 1.15f,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }
        }
        Row(
            modifier = if (narrow) Modifier.padding(top = 1.dp) else Modifier.fillMaxWidth().padding(top = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "小限:",
                    fontSize = labelSp,
                    color = IztroTheme.textPrimary,
                    maxLines = 1,
                    lineHeight = labelSp * 1.1f,
                    modifier = Modifier.padding(end = 1.dp),
                )
                Text(
                    text = palace.ages.take(maxAges).joinToString(","),
                    fontSize = ageTextSp,
                    color = IztroTheme.textPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = ageTextSp * 1.15f,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }
            if (horoscopeNames.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    horoscopeNames.forEach { tag ->
                        FateScopeLabel(tag) {
                            if (tag.scope != "age") onToggleScope(tag.scope)
                        }
                    }
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
    nameSizeScale: Float = 1f,
) {
    Izstar(star, palaceStem, activeStem, hoverStem, horoscopeMutagens, horizontal = false, nameSizeScale = nameSizeScale)
}

@Composable
private fun AdjStar(star: DemoStar, nameSizeScale: Float = 1f) {
    Izstar(star, "", null, null, emptyList(), horizontal = true, nameSizeScale = nameSizeScale)
}

@Composable
private fun HoroStar(star: DemoStar, color: Color, densityScale: Float = 1f) {
    val style = LocalAstrolabeStyle.current
    // 运星随全盘系数一起缩放（可略放大），不再卡死在 1f
    val size = style.horoStarSp * densityScale.coerceIn(0.5f, 1.6f)
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

/**
 * 按真实测得的星曜行宽/高动态缩放字号：每宫按自己的空地灵活放大/缩小。
 * - 宽度有余量就放大（吃掉宫格右侧空地），宽度不够就缩小
 * - 放大后不能超出星曜区高度；星区本就放不下时不额外缩小（可上下滚动）
 */
@Composable
private fun StarFitRow(
    modifier: Modifier = Modifier,
    availableWidth: Dp,
    maxHeight: Dp = Dp.Unspecified,
    edgePad: Dp = 4.dp,
    minScale: Float = 0.42f,
    /** 放大上限：星曜最多放大到基准字号的几倍（约 +2 号） */
    maxScale: Float = 1.3f,
    content: @Composable (scale: Float) -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val availPx = availableWidth.roundToPx()
        val maxW = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            availPx
        }.coerceAtLeast(0)
        if (maxW <= 0) {
            return@SubcomposeLayout layout(0, 0) {}
        }
        // 贴左：左边不空，只在右侧留边距给末列（如阴煞）
        val usableW = (maxW - edgePad.roundToPx()).coerceAtLeast(1)
        // 高度预算：星曜区可用高度（含小限区让出的空间），用来防止竖向被裁
        val hardMaxH = when {
            maxHeight != Dp.Unspecified -> maxHeight.roundToPx().coerceAtLeast(1)
            constraints.hasBoundedHeight -> constraints.maxHeight
            else -> Constraints.Infinity
        }
        // 探针：不加约束，量出自然宽高
        val probe = subcompose(StarFitSlot.Probe) { content(1f) }
            .first()
            .measure(Constraints(0, Constraints.Infinity, 0, Constraints.Infinity))
        // 宽度上限：正好铺满可用宽（有余量就放大）
        val wLimit = if (probe.width > 0) {
            usableW.toFloat() / probe.width.toFloat()
        } else {
            1f
        }
        // 高度上限只限制放大，不额外压缩：星区本就放不下时交给纵向滚动
        val hCap = if (hardMaxH != Constraints.Infinity && probe.height > 0) {
            maxOf(1f, hardMaxH.toFloat() / probe.height.toFloat())
        } else {
            Float.MAX_VALUE
        }
        val scale = minOf(wLimit, hCap).coerceIn(minScale, maxScale)
        // 无缩放且不超高时直接复用探针，省一遍宫星组合+测量
        val placeable = if (scale == 1f && probe.height <= hardMaxH) {
            probe
        } else {
            subcompose(StarFitSlot.Fit) {
                content(scale)
            }
                .first()
                .measure(Constraints(0, Constraints.Infinity, 0, hardMaxH))
        }
        // 占满宫格宽，星曜从 x=0 贴左排，避免缩完后看起来居中
        layout(maxW, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

private enum class StarFitSlot { Probe, Fit }

@Composable
private fun DynamicPalaceName(text: String, color: Color, scale: Float = 1f) {
    // 与本命宫名（迁移/父母等）同字号；运星占位时随 contentScale 缩小
    val style = LocalAstrolabeStyle.current
    val size = style.palaceNameSp * scale
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
private fun FateScopeLabel(tag: FateTag, onClick: () -> Unit) {
    val style = LocalAstrolabeStyle.current
    val isActive = tag.show && tag.scope != "age"
    val bgColor = when {
        isActive -> IztroTheme.scopeColor(tag.scope)
        tag.scope == "age" -> Color.Transparent
        else -> IztroTheme.major
    }
    val size = style.horoStarSp
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
