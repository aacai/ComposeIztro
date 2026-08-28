package zhiqiu.iztro.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
        ),
        modifier = modifier,
    )
}

@Composable
fun Iztrolabe(
    props: IztrolabeProps,
    modifier: Modifier = Modifier,
) {
    var algorithm by remember { mutableStateOf(props.options?.algorithm ?: "default") }
    var localAstroType by remember { mutableStateOf(props.astroType ?: "heaven") }
    // 中宫日±/时±：排盘出生日期与时辰（一变就整盘重算）
    var birthday by remember { mutableStateOf(props.birthday) }
    var birthTime by remember { mutableIntStateOf(props.birthTime) }

    LaunchedEffect(props.options?.algorithm) {
        algorithm = props.options?.algorithm ?: "default"
    }
    LaunchedEffect(props.astroType) {
        localAstroType = props.astroType ?: "heaven"
    }
    LaunchedEffect(props.birthday) { birthday = props.birthday }
    LaunchedEffect(props.birthTime) { birthTime = props.birthTime }

    // birthday/birthTime 必须进 key，否则 rememberIztro / 宫位状态可能残留
    key(birthday, birthTime, algorithm, localAstroType) {
        val input = props.copy(birthday = birthday, birthTime = birthTime)
            .toInput(algorithm, localAstroType)
        IztrolabeContent(
            input = input,
            props = props,
            algorithm = algorithm,
            astroType = localAstroType,
            onAlgorithmChange = { algorithm = it },
            onAstroTypeChange = { localAstroType = it },
            onBirthdayChange = { birthday = it },
            onBirthTimeChange = { birthTime = it },
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
    modifier: Modifier = Modifier,
) {
    val iztro = rememberIztro(input, props.horoscopeDate, props.horoscopeHour)

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
    val horoscope = remember(chart, horoscopeDate, horoscopeHour) {
        computeHoroscope(chart, horoscopeDate, horoscopeHour)
    }

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
        modifier = modifier,
    )
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

    fun toggleScope(scope: String) {
        when (scope) {
            "decadal" -> showDecadal = !showDecadal
            "yearly" -> showYearly = !showYearly
            "monthly" -> showMonthly = !showMonthly
            "daily" -> showDaily = !showDaily
            "hourly" -> showHourly = !showHourly
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // 正方形盘：边长 = min(宽, 高)，竖屏手机按宽度定边并垂直居中
        val boardSide = if (maxWidth < maxHeight) maxWidth else maxHeight
        val style = rememberAstrolabeStyle(boardSide)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalAstrolabeStyle provides style) {
                Column(Modifier.size(boardSide)) {
                    Row(Modifier.weight(style.rowWeightOuter).fillMaxWidth()) {
                        listOf(3, 4, 5, 6).forEach { idx ->
                            PalaceSlot(
                                chart, horoscope, chart.palaces[idx], idx, previewIndex,
                                activeHeavenlyStem, hoverHeavenlyStem,
                                showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                onFocus = ::onPalaceFocus,
                                onClickPalace = ::onPalaceClick,
                                onToggleScope = ::toggleScope,
                                onToggleFlyStar = { stem -> activeHeavenlyStem = if (activeHeavenlyStem == stem) null else stem },
                                onHoverStem = { hoverHeavenlyStem = it },
                                modifier = Modifier.weight(1f).fillMaxHeight().padding(style.cellGap),
                            )
                        }
                    }
                    Row(Modifier.weight(style.rowWeightMid).fillMaxWidth()) {
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            listOf(2, 1).forEach { idx ->
                                PalaceSlot(
                                    chart, horoscope, chart.palaces[idx], idx, previewIndex,
                                    activeHeavenlyStem, hoverHeavenlyStem,
                                    showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                    onFocus = ::onPalaceFocus,
                                    onClickPalace = ::onPalaceClick,
                                    onToggleScope = ::toggleScope,
                                    onToggleFlyStar = { stem -> activeHeavenlyStem = if (activeHeavenlyStem == stem) null else stem },
                                    onHoverStem = { hoverHeavenlyStem = it },
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
                            modifier = Modifier.weight(2f).fillMaxHeight().padding(style.cellGap),
                        )
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            listOf(7, 8).forEach { idx ->
                                PalaceSlot(
                                    chart, horoscope, chart.palaces[idx], idx, previewIndex,
                                    activeHeavenlyStem, hoverHeavenlyStem,
                                    showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                    onFocus = ::onPalaceFocus,
                                    onClickPalace = ::onPalaceClick,
                                    onToggleScope = ::toggleScope,
                                    onToggleFlyStar = { stem -> activeHeavenlyStem = if (activeHeavenlyStem == stem) null else stem },
                                    onHoverStem = { hoverHeavenlyStem = it },
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(style.cellGap),
                                )
                            }
                        }
                    }
                    Row(Modifier.weight(style.rowWeightOuter).fillMaxWidth()) {
                        listOf(0, 11, 10, 9).forEach { idx ->
                            PalaceSlot(
                                chart, horoscope, chart.palaces[idx], idx, previewIndex,
                                activeHeavenlyStem, hoverHeavenlyStem,
                                showDecadal, showYearly, showMonthly, showDaily, showHourly,
                                onFocus = ::onPalaceFocus,
                                onClickPalace = ::onPalaceClick,
                                onToggleScope = ::toggleScope,
                                onToggleFlyStar = { stem -> activeHeavenlyStem = if (activeHeavenlyStem == stem) null else stem },
                                onHoverStem = { hoverHeavenlyStem = it },
                                modifier = Modifier.weight(1f).fillMaxHeight().padding(style.cellGap),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PalaceSlot(
    chart: DemoChart,
    horoscope: DemoHoroscope,
    palace: zhiqiu.iztro.DemoPalace,
    index: Int,
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
    Izpalace(
        palace = palace,
        horoscope = horoscope,
        focusedIndex = focusedIndex,
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
