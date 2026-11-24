package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zhiqiu.iztro.Algorithm
import zhiqiu.iztro.AstroType
import zhiqiu.iztro.DemoChart
import zhiqiu.iztro.DemoHoroscope
import zhiqiu.iztro.IztrolabeProps
import zhiqiu.iztro.computeHoroscope
import zhiqiu.iztro.defaultLineIndex
import zhiqiu.iztro.rememberIztro

/**
 * 对齐 react-iztro Iztrolabe 公开 API。
 */
@Composable
fun Iztrolabe(
    birthday: String,
    birthTime: Int,
    gender: String,
    birthdayType: String = "solar",
    isLeapMonth: Boolean = false,
    fixLeap: Boolean = true,
    lang: String = "zh-CN",
    name: String = "",
    options: zhiqiu.iztro.IztroConfig? = null,
    astroType: AstroType? = "heaven",
    horoscopeDate: String? = null,
    horoscopeHour: Int? = null,
    centerPalaceAlign: Boolean = false,
    /** null = 内部自管；非 null = 受控，配合 [onColorModeChange] */
    colorMode: IztroColorMode? = null,
    onColorModeChange: ((IztroColorMode) -> Unit)? = null,
    /** 排盘加载态：true = 计算中，false = 已就绪。加载 UI 交给调用方。 */
    onLoadingChange: ((Boolean) -> Unit)? = null,
    themeConfig: IztroThemeConfig = IztroThemeConfig.Default,
    modifier: Modifier = Modifier,
) {
    Iztrolabe(
        props = IztrolabeProps(
            birthday = birthday,
            birthTime = birthTime,
            gender = gender,
            birthdayType = birthdayType,
            isLeapMonth = isLeapMonth,
            fixLeap = fixLeap,
            lang = lang,
            name = name,
            options = options,
            astroType = astroType,
            horoscopeDate = horoscopeDate,
            horoscopeHour = horoscopeHour,
            centerPalaceAlign = centerPalaceAlign,
            colorMode = colorMode,
        ),
        onColorModeChange = onColorModeChange,
        onLoadingChange = onLoadingChange,
        themeConfig = themeConfig,
        modifier = modifier,
    )
}

@Composable
fun Iztrolabe(
    props: IztrolabeProps,
    onColorModeChange: ((IztroColorMode) -> Unit)? = null,
    /** 排盘加载态：true = 计算中，false = 已就绪。加载 UI 交给调用方。 */
    onLoadingChange: ((Boolean) -> Unit)? = null,
    themeConfig: IztroThemeConfig = IztroThemeConfig.Default,
    modifier: Modifier = Modifier,
) {
    var algorithm by remember { mutableStateOf(props.options?.algorithm ?: "default") }
    var localAstroType by remember { mutableStateOf(props.astroType ?: "heaven") }
    // 中宫日±/时±：排盘出生日期与时辰（一变就整盘重算）
    var birthday by remember { mutableStateOf(props.birthday) }
    var birthTime by remember { mutableIntStateOf(props.birthTime) }
    // 色模式：props.colorMode 非 null 为受控，否则内部状态
    var internalColorMode by remember { mutableStateOf(IztroColorMode.Light) }
    val resolvedColorMode = props.colorMode ?: internalColorMode

    LaunchedEffect(props.options?.algorithm) {
        algorithm = props.options?.algorithm ?: "default"
    }
    LaunchedEffect(props.astroType) {
        localAstroType = props.astroType ?: "heaven"
    }
    LaunchedEffect(props.birthday) { birthday = props.birthday }
    LaunchedEffect(props.birthTime) { birthTime = props.birthTime }
    LaunchedEffect(props.colorMode) {
        props.colorMode?.let { internalColorMode = it }
    }

    fun setColorMode(mode: IztroColorMode) {
        if (props.colorMode == null) internalColorMode = mode
        onColorModeChange?.invoke(mode)
    }

    IztroThemeProvider(mode = resolvedColorMode, config = themeConfig) {
        // 不再用 key 强制整棵子树销毁重建：rememberIztro 内部按 input 重算，
        // 宫位状态（hover/scope）沿用即可，换盘时保留旧盘直到新盘就绪，避免闪一下。
        // 只把影响本命盘排盘的字段作为 key：运限日期/色模式变化不应重排本命盘
        val input = remember(
            birthday, birthTime, props.gender, props.birthdayType, props.isLeapMonth,
            props.fixLeap, props.lang, props.name, props.options, algorithm, localAstroType,
        ) {
            props.copy(birthday = birthday, birthTime = birthTime)
                .toInput(algorithm, localAstroType)
        }
        IztrolabeContent(
            input = input,
            props = props,
            algorithm = algorithm,
            astroType = localAstroType,
            onAlgorithmChange = { algorithm = it },
            onAstroTypeChange = { localAstroType = it },
            onBirthdayChange = { birthday = it },
            onBirthTimeChange = { birthTime = it },
            colorMode = resolvedColorMode,
            onToggleColorMode = { setColorMode(resolvedColorMode.toggle()) },
            onLoadingChange = onLoadingChange,
            modifier = modifier,
        )
    }
}

/** 兼容旧 DemoChart 直接传入方式 */
@Composable
fun Iztrolabe(
    chart: DemoChart,
    modifier: Modifier = Modifier,
    initialHoroscopeDate: String = currentSolarDate(),
    initialHoroscopeHour: Int = 0,
    lang: String = "zh-CN",
    centerPalaceAlign: Boolean = false,
) {
    IztrolabeContentWithChart(
        chart = chart,
        lang = lang,
        centerPalaceAlign = centerPalaceAlign,
        initialHoroscopeDate = initialHoroscopeDate,
        initialHoroscopeHour = initialHoroscopeHour,
        modifier = modifier,
    )
}

@Composable
private fun IztrolabeContent(
    input: zhiqiu.iztro.IztroInput,
    props: IztrolabeProps,
    algorithm: Algorithm,
    astroType: AstroType,
    onAlgorithmChange: (Algorithm) -> Unit,
    onAstroTypeChange: (AstroType) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onBirthTimeChange: (Int) -> Unit,
    colorMode: IztroColorMode,
    onToggleColorMode: () -> Unit,
    onLoadingChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val iztro = rememberIztro(input, props.horoscopeDate, props.horoscopeHour)
    // 只上报加载态，加载 UI 由调用方（App）决定
    LaunchedEffect(iztro == null) { onLoadingChange?.invoke(iztro == null) }
    if (iztro == null) {
        Box(modifier = modifier.fillMaxSize())
        return
    }

    AstrolabeGrid(
        chart = iztro.chart,
        horoscope = iztro.horoscope,
        horoscopeDate = iztro.horoscopeDate,
        horoscopeHour = iztro.horoscopeHour,
        onSetHoroscopeDate = { iztro.setHoroscope(it, null) },
        onSetHoroscopeHour = { iztro.setHoroscope(iztro.horoscopeDate, it) },
        onBirthdayChange = onBirthdayChange,
        onBirthTimeChange = onBirthTimeChange,
        lang = props.lang,
        centerPalaceAlign = props.centerPalaceAlign,
        algorithm = algorithm,
        astroType = astroType,
        onAlgorithmChange = onAlgorithmChange,
        onAstroTypeChange = onAstroTypeChange,
        colorMode = colorMode,
        onToggleColorMode = onToggleColorMode,
        modifier = modifier,
    )
}

@Composable
private fun IztrolabeContentWithChart(
    chart: DemoChart,
    lang: String,
    centerPalaceAlign: Boolean,
    initialHoroscopeDate: String,
    initialHoroscopeHour: Int,
    modifier: Modifier = Modifier,
) {
    var horoscopeDate by remember(chart, initialHoroscopeDate) { mutableStateOf(initialHoroscopeDate) }
    var horoscopeHour by remember(chart, initialHoroscopeHour) { mutableIntStateOf(initialHoroscopeHour) }
    var colorMode by remember { mutableStateOf(IztroColorMode.Light) }
    val horoscope = remember(chart, horoscopeDate, horoscopeHour) {
        computeHoroscope(chart, horoscopeDate, horoscopeHour)
    }

    IztroThemeProvider(mode = colorMode) {
        AstrolabeGrid(
            chart = chart,
            horoscope = horoscope,
            horoscopeDate = horoscopeDate,
            horoscopeHour = horoscopeHour,
            onSetHoroscopeDate = { horoscopeDate = it },
            onSetHoroscopeHour = { horoscopeHour = it },
            lang = lang,
            centerPalaceAlign = centerPalaceAlign,
            algorithm = "default",
            astroType = "heaven",
            onAlgorithmChange = {},
            onAstroTypeChange = {},
            colorMode = colorMode,
            onToggleColorMode = { colorMode = colorMode.toggle() },
            modifier = modifier,
        )
    }
}

@Composable
private fun AstrolabeGrid(
    chart: DemoChart,
    horoscope: DemoHoroscope,
    horoscopeDate: String,
    horoscopeHour: Int,
    onSetHoroscopeDate: (String) -> Unit,
    onSetHoroscopeHour: (Int) -> Unit,
    onBirthdayChange: (String) -> Unit = {},
    onBirthTimeChange: (Int) -> Unit = {},
    lang: String,
    centerPalaceAlign: Boolean,
    algorithm: Algorithm,
    astroType: AstroType,
    onAlgorithmChange: (Algorithm) -> Unit,
    onAstroTypeChange: (AstroType) -> Unit,
    colorMode: IztroColorMode = IztroColorMode.Light,
    onToggleColorMode: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var activeHeavenlyStem by remember(chart) { mutableStateOf<String?>(null) }
    var hoverHeavenlyStem by remember(chart) { mutableStateOf<String?>(null) }
    // 点击锁定；悬停临时预览三方四正，移出后回到锁定宫
    var clickedLineIndex by remember(chart) { mutableStateOf<Int?>(null) }
    var hoveredIndex by remember(chart) { mutableStateOf<Int?>(null) }
    var showDecadal by remember(chart) { mutableStateOf(false) }
    var showYearly by remember(chart) { mutableStateOf(false) }
    var showMonthly by remember(chart) { mutableStateOf(false) }
    var showDaily by remember(chart) { mutableStateOf(false) }
    var showHourly by remember(chart) { mutableStateOf(false) }

    val soulLineIndex = remember(chart.earthlyBranchOfSoulPalace) {
        defaultLineIndex(chart.earthlyBranchOfSoulPalace)
    }

    // 悬停优先预览；移出后回到点击宫；都无则命宫
    val previewIndex = hoveredIndex ?: clickedLineIndex

    // 运限 toggle 优先；否则悬停/点击宫位；默认命宫
    val arrow = remember(
        showDecadal, showYearly, showMonthly, showDaily, showHourly,
        horoscope, previewIndex, soulLineIndex,
    ) {
        when {
            showHourly -> horoscope.hourly.index to "hourly"
            showDaily -> horoscope.daily.index to "daily"
            showMonthly -> horoscope.monthly.index to "monthly"
            showYearly -> horoscope.yearly.index to "yearly"
            showDecadal -> horoscope.decadal.index to "decadal"
            previewIndex != null -> previewIndex to null
            else -> soulLineIndex to null
        }
    }

    /** 底部时间轴点选：只打开到目标层，关掉更深层级 */
    fun activateScope(scope: String) {
        when (scope) {
            "decadal" -> {
                showDecadal = true
                showYearly = false
                showMonthly = false
                showDaily = false
                showHourly = false
            }
            "yearly" -> {
                showDecadal = true
                showYearly = true
                showMonthly = false
                showDaily = false
                showHourly = false
            }
            "monthly" -> {
                showDecadal = true
                showYearly = true
                showMonthly = true
                showDaily = false
                showHourly = false
            }
        }
    }

    // 运限层层递进：打开某层则激活其前序；关闭某层则关掉其后所有更深层级
    fun toggleScope(scope: String) {
        when (scope) {
            "decadal" -> {
                if (showDecadal) {
                    showDecadal = false
                    showYearly = false
                    showMonthly = false
                    showDaily = false
                    showHourly = false
                } else {
                    showDecadal = true
                }
            }
            "yearly" -> {
                if (showYearly) {
                    showYearly = false
                    showMonthly = false
                    showDaily = false
                    showHourly = false
                } else {
                    showDecadal = true
                    showYearly = true
                }
            }
            "monthly" -> {
                if (showMonthly) {
                    showMonthly = false
                    showDaily = false
                    showHourly = false
                } else {
                    showDecadal = true
                    showYearly = true
                    showMonthly = true
                }
            }
            "daily" -> {
                if (showDaily) {
                    showDaily = false
                    showHourly = false
                } else {
                    showDecadal = true
                    showYearly = true
                    showMonthly = true
                    showDaily = true
                }
            }
            "hourly" -> {
                if (showHourly) {
                    showHourly = false
                } else {
                    showDecadal = true
                    showYearly = true
                    showMonthly = true
                    showDaily = true
                    showHourly = true
                }
            }
        }
    }

    fun onPalaceClick(index: Int) {
        val stem = chart.palaces.getOrNull(index)?.heavenlyStem
        if (clickedLineIndex == index) {
            clickedLineIndex = null
            activeHeavenlyStem = null
        } else {
            clickedLineIndex = index
            activeHeavenlyStem = stem
        }
    }

    fun onPalaceFocus(index: Int?) {
        hoveredIndex = index
    }

    // 稳定回调引用：12 宫共用，避免每次重组都生成新 lambda 使子宫无法跳过
    val stableOnFocus = remember(chart) { { index: Int? -> onPalaceFocus(index) } }
    val stableOnClick = remember(chart) { { index: Int -> onPalaceClick(index) } }
    val stableOnToggleScope = remember(chart) { { scope: String -> toggleScope(scope) } }
    val stableOnToggleFlyStar = remember(chart) {
        { stem: String -> activeHeavenlyStem = if (activeHeavenlyStem == stem) null else stem }
    }
    val stableOnHoverStem = remember(chart) { { stem: String? -> hoverHeavenlyStem = stem } }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(IztroTheme.boardBg)) {
        // 盘面直接占满可用高度：底部大运/运限时间轴由页面滚动查看，不再预留空间。
        val boardWidth = maxWidth
        val boardHeight = maxHeight.coerceIn(400.dp, 760.dp)
        val baseStyle = rememberAstrolabeStyle(boardWidth)
        // 全盘统一密度系数：按外圈宫格实际高度估算一次，十二宫与中宫共用，保证整盘一致
        val style = remember(baseStyle, boardHeight, chart) {
            val totalWeight = baseStyle.rowWeightOuter * 2f + baseStyle.rowWeightMid
            val outerRowH = boardHeight.value * baseStyle.rowWeightOuter / totalWeight -
                baseStyle.cellGap.value * 2f - baseStyle.cellPad.value * 2f
            // 最拥挤宫的需求：footer(杂曜+长生行) + 小限行 + 星曜区最小高度
            val maxAdj = chart.palaces.maxOf { it.adjectiveStars.size }
            val maxPerCol = if (baseStyle.compact) 5 else 4
            val adjRows = minOf(maxAdj, maxPerCol).coerceAtLeast(1)
            val footerNeed = baseStyle.adjStarSp.value * adjRows * 1.1f +
                baseStyle.adjStarSp.value * 1.15f + 4f
            val agesNeed = baseStyle.ageSp.value * 2f * 1.35f
            val starNeed = baseStyle.majorStarSp.value * 2f * 1.12f +
                baseStyle.brightSp.value * 2f * 1.12f +
                baseStyle.adjStarSp.value * 0.66f * 1.2f
            val factor = (outerRowH / (footerNeed + agesNeed + starNeed)).coerceIn(0.6f, 1f)
            baseStyle.scaled(factor)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(boardHeight),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalAstrolabeStyle provides style) {
                Column(Modifier.size(width = boardWidth, height = boardHeight)) {
                    Row(Modifier.weight(style.rowWeightOuter).fillMaxWidth()) {
                        listOf(3, 4, 5, 6).forEach { idx ->
                            PalaceSlot(
                                chart, horoscope, chart.palaces[idx], idx,
                                palaceHighlight(previewIndex, idx),
                                activeHeavenlyStem, hoverHeavenlyStem,
                                showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                onFocus = stableOnFocus,
                                onClickPalace = stableOnClick,
                                onToggleScope = stableOnToggleScope,
                                onToggleFlyStar = stableOnToggleFlyStar,
                                onHoverStem = stableOnHoverStem,
                                modifier = Modifier.weight(1f).fillMaxHeight().padding(style.cellGap),
                            )
                        }
                    }
                    Row(Modifier.weight(style.rowWeightMid).fillMaxWidth()) {
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            listOf(2, 1).forEach { idx ->
                                PalaceSlot(
                                    chart, horoscope, chart.palaces[idx], idx,
                                    palaceHighlight(previewIndex, idx),
                                    activeHeavenlyStem, hoverHeavenlyStem,
                                    showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                    onFocus = stableOnFocus,
                                    onClickPalace = stableOnClick,
                                    onToggleScope = stableOnToggleScope,
                                    onToggleFlyStar = stableOnToggleFlyStar,
                                    onHoverStem = stableOnHoverStem,
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(style.cellGap),
                                )
                            }
                        }
                        IzpalaceCenter(
                            chart = chart,
                            horoscope = horoscope,
                            horoscopeDate = horoscopeDate,
                            horoscopeHour = horoscopeHour,
                            arrowIndex = arrow.first,
                            arrowScope = arrow.second,
                            onSetHoroscopeDate = onSetHoroscopeDate,
                            onSetHoroscopeHour = onSetHoroscopeHour,
                            onBirthdayChange = onBirthdayChange,
                            onBirthTimeChange = onBirthTimeChange,
                            lang = lang,
                            centerPalaceAlign = centerPalaceAlign,
                            algorithm = algorithm,
                            astroType = astroType,
                            onAlgorithmChange = onAlgorithmChange,
                            onAstroTypeChange = onAstroTypeChange,
                            showDecadal = showDecadal,
                            showYearly = showYearly,
                            showMonthly = showMonthly,
                            showDaily = showDaily,
                            showHourly = showHourly,
                            onToggleScope = ::toggleScope,
                            colorMode = colorMode,
                            onToggleColorMode = onToggleColorMode,
                            modifier = Modifier.weight(2f).fillMaxHeight().padding(style.cellGap),
                        )
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            listOf(7, 8).forEach { idx ->
                                PalaceSlot(
                                    chart, horoscope, chart.palaces[idx], idx,
                                    palaceHighlight(previewIndex, idx),
                                    activeHeavenlyStem, hoverHeavenlyStem,
                                    showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                    onFocus = stableOnFocus,
                                    onClickPalace = stableOnClick,
                                    onToggleScope = stableOnToggleScope,
                                    onToggleFlyStar = stableOnToggleFlyStar,
                                    onHoverStem = stableOnHoverStem,
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(style.cellGap),
                                )
                            }
                        }
                    }
                    Row(Modifier.weight(style.rowWeightOuter).fillMaxWidth()) {
                        listOf(0, 11, 10, 9).forEach { idx ->
                            PalaceSlot(
                                chart, horoscope, chart.palaces[idx], idx,
                                palaceHighlight(previewIndex, idx),
                                activeHeavenlyStem, hoverHeavenlyStem,
                                showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                onFocus = stableOnFocus,
                                onClickPalace = stableOnClick,
                                onToggleScope = stableOnToggleScope,
                                onToggleFlyStar = stableOnToggleFlyStar,
                                onHoverStem = stableOnHoverStem,
                                modifier = Modifier.weight(1f).fillMaxHeight().padding(style.cellGap),
                            )
                        }
                    }
                }
            }
        }
        HoroscopeTimeline(
        chart = chart,
        horoscope = horoscope,
        horoscopeDate = horoscopeDate,
        showDecadal = showDecadal,
        showYearly = showYearly,
        showMonthly = showMonthly,
        onSelectDecadal = { _, date ->
            onSetHoroscopeDate(date)
            activateScope("decadal")
        },
        onSelectYear = { date ->
            onSetHoroscopeDate(date)
            activateScope("yearly")
        },
        onSelectMonth = { date ->
            onSetHoroscopeDate(date)
            activateScope("monthly")
        },
        onSetHoroscopeDate = onSetHoroscopeDate,
        modifier = Modifier.fillMaxWidth(),
    )
        }
    }
}

@Composable
private fun PalaceSlot(
    chart: DemoChart,
    horoscope: DemoHoroscope,
    palace: zhiqiu.iztro.DemoPalace,
    index: Int,
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
    Izpalace(
        palace = palace,
        horoscope = horoscope,
        highlight = highlight,
        activeHeavenlyStem = activeHeavenlyStem,
        hoverHeavenlyStem = hoverHeavenlyStem,
        showDecadal = showDecadal,
        showYearly = showYearly,
        showMonthly = showMonthly,
        showDaily = showDaily,
        showHourly = showHourly,
        onFocus = onFocus,
        onClickPalace = onClickPalace,
        onToggleScope = onToggleScope,
        onToggleFlyStar = onToggleFlyStar,
        onHoverStem = onHoverStem,
        modifier = modifier,
    )
}
