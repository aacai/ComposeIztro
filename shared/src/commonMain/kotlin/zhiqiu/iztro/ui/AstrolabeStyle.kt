package zhiqiu.iztro.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 盘面密度：手机窄屏用 compact，桌面用 comfortable。
 */
data class AstrolabeStyle(
    val compact: Boolean,
    val cellGap: Dp,
    val cellPad: Dp,
    /** 外圈/中宫垂直权重 */
    val rowWeightOuter: Float,
    val rowWeightMid: Float,
    val majorStarSp: TextUnit,
    val minorStarSp: TextUnit,
    val brightSp: TextUnit,
    val adjStarSp: TextUnit,
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
            cellGap = 1.5.dp,
            cellPad = 3.dp,
            rowWeightOuter = 1f,
            rowWeightMid = 2f,
            majorStarSp = 12.sp,
            minorStarSp = 11.sp,
            brightSp = 9.sp,
            adjStarSp = 9.sp,
            palaceNameSp = 11.sp,
            stemBranchSp = 12.sp,
            ageSp = 9.sp,
            centerTitleSp = 12.sp,
            centerBodySp = 10.sp,
            centerBtnSp = 11.sp,
            showPalaceDecor = true,
        )

        val Compact = AstrolabeStyle(
            compact = true,
            cellGap = 1.dp,
            cellPad = 2.dp,
            // 正方形盘内仍用经典 1:2:1，保证中宫够用、外圈不至于过高
            rowWeightOuter = 1f,
            rowWeightMid = 2f,
            majorStarSp = 10.sp,
            minorStarSp = 9.sp,
            brightSp = 8.sp,
            adjStarSp = 7.sp,
            palaceNameSp = 10.sp,
            stemBranchSp = 11.sp,
            ageSp = 8.sp,
            centerTitleSp = 11.sp,
            centerBodySp = 9.sp,
            centerBtnSp = 10.sp,
            showPalaceDecor = false,
        )

        fun forWidth(width: Dp): AstrolabeStyle =
            if (width < 600.dp) Compact else Comfortable
    }
}

val LocalAstrolabeStyle = compositionLocalOf { AstrolabeStyle.Comfortable }

@Composable
fun rememberAstrolabeStyle(maxWidth: Dp): AstrolabeStyle =
    remember(maxWidth) { AstrolabeStyle.forWidth(maxWidth) }
