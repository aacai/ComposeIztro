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
    /** 卡片风格子圆角 */
    val cellCorner: Dp,
    /** 外圈/中宫垂直权重 */
    val rowWeightOuter: Float,
    val rowWeightMid: Float,
    val majorStarSp: TextUnit,
    val minorStarSp: TextUnit,
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
            // 外圈略大于中行一半；基准字偏舒适，挤时靠宫内 densityScale 动态收
            rowWeightOuter = 1.15f,
            rowWeightMid = 1.7f,
            majorStarSp = 15.sp,
            minorStarSp = 14.sp,
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
            // 中宫略矮腾给十二宫；基准字保持可读，疏密靠每宫 densityScale
            rowWeightOuter = 1.2f,
            rowWeightMid = 1.55f,
            majorStarSp = 13.sp,
            minorStarSp = 12.sp,
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

        fun forWidth(width: Dp): AstrolabeStyle =
            if (width < 600.dp) Compact else Comfortable
    }
}

val LocalAstrolabeStyle = compositionLocalOf { AstrolabeStyle.Comfortable }

@Composable
fun rememberAstrolabeStyle(maxWidth: Dp): AstrolabeStyle =
    remember(maxWidth) { AstrolabeStyle.forWidth(maxWidth) }
