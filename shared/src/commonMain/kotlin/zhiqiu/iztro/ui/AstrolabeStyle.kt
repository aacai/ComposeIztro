package zhiqiu.iztro.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 按统一密度系数缩放全部字号：十二宫与中宫用同一个系数，保证整盘视觉一致。
 */
fun AstrolabeStyle.scaled(factor: Float): AstrolabeStyle = copy(
    majorStarSp = majorStarSp * factor,
    minorStarSp = minorStarSp * factor,
    gradeBStarSp = gradeBStarSp * factor,
    brightSp = brightSp * factor,
    adjStarSp = adjStarSp * factor,
    horoStarSp = horoStarSp * factor,
    palaceNameSp = palaceNameSp * factor,
    stemBranchSp = stemBranchSp * factor,
    ageSp = ageSp * factor,
    centerTitleSp = centerTitleSp * factor,
    centerBodySp = centerBodySp * factor,
    centerBtnSp = centerBtnSp * factor,
)

/**
 * 盘面密度：手机窄屏用 compact，桌面用 comfortable。
 */
data class AstrolabeStyle(
    val compact: Boolean,
    val cellGap: Dp,
    val cellPad: Dp,
    /** 卡片风格子圆角 */
    val cellCorner: Dp,
    /** 外圈/中宫垂直权重 */
    val rowWeightOuter: Float,
    val rowWeightMid: Float,
    val majorStarSp: TextUnit,
    val minorStarSp: TextUnit,
    /** 乙级星：比辅星小一号 */
    val gradeBStarSp: TextUnit,
    val brightSp: TextUnit,
    val adjStarSp: TextUnit,
    /** 大限/流年运星（运月运魁等），小于主星 */
    val horoStarSp: TextUnit,
    val palaceNameSp: TextUnit,
    val stemBranchSp: TextUnit,
    val ageSp: TextUnit,
    val centerTitleSp: TextUnit,
    val centerBodySp: TextUnit,
    val centerBtnSp: TextUnit,
    /** compact 时隐藏长生/博士/岁前/将前，腾给主星 */
    val showPalaceDecor: Boolean,
) {
    companion object {
        val Comfortable = AstrolabeStyle(
            compact = false,
            cellGap = 3.dp,
            cellPad = 3.dp,
            cellCorner = 8.dp,
            // 外圈行 1 份、中行 2 份：中行左右各叠两宫，十二宫高度完全一致
            rowWeightOuter = 1f,
            rowWeightMid = 2f,
            majorStarSp = 14.sp,
            minorStarSp = 14.sp,
            gradeBStarSp = 13.sp,
            brightSp = 12.sp,
            adjStarSp = 12.sp,
            horoStarSp = 9.sp,
            // 身份锚点：固定、略收；宫名略大于干支，避免右下角抢主星
            palaceNameSp = 13.sp,
            stemBranchSp = 12.sp,
            ageSp = 12.sp,
            centerTitleSp = 14.sp,
            centerBodySp = 12.sp,
            centerBtnSp = 13.sp,
            showPalaceDecor = true,
        )

        val Compact = AstrolabeStyle(
            compact = true,
            cellGap = 2.dp,
            cellPad = 2.dp,
            cellCorner = 5.dp,
            // 中行叠两宫：外圈 1 份、中行 2 份，十二宫高度一致
            rowWeightOuter = 1f,
            rowWeightMid = 2f,
            majorStarSp = 12.sp,
            minorStarSp = 12.sp,
            gradeBStarSp = 11.sp,
            brightSp = 11.sp,
            adjStarSp = 10.sp,
            horoStarSp = 8.sp,
            // 身份锚点：固定不放大；宫名略大于干支，右下角更轻
            palaceNameSp = 11.sp,
            stemBranchSp = 10.sp,
            ageSp = 10.sp,
            centerTitleSp = 13.sp,
            centerBodySp = 11.sp,
            centerBtnSp = 12.sp,
            showPalaceDecor = false,
        )

        fun forWidthClass(widthClass: WindowWidthSizeClass): AstrolabeStyle =
            if (widthClass == WindowWidthSizeClass.Compact) Compact else Comfortable
    }
}

val LocalAstrolabeStyle = compositionLocalOf { AstrolabeStyle.Comfortable }

/** 按窗口宽度划分盘面密度断点：<600dp 紧凑，600~839dp 中等，>=840dp 展开。 */
enum class WindowWidthSizeClass { Compact, Medium, Expanded }

@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val width = with(density) { windowInfo.containerSize.width.toDp() }
    return remember(width) {
        when {
            width < 600.dp -> WindowWidthSizeClass.Compact
            width < 840.dp -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        }
    }
}

@Composable
fun rememberAstrolabeStyle(widthClass: WindowWidthSizeClass): AstrolabeStyle =
    remember(widthClass) { AstrolabeStyle.forWidthClass(widthClass) }
