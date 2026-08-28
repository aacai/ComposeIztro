package zhiqiu.iztro.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 盘面日/夜间模式 */
enum class IztroColorMode {
    Light,
    Dark,
    ;

    fun toggle(): IztroColorMode = if (this == Light) Dark else Light

    val label: String
        get() = when (this) {
            Light -> "日间"
            Dark -> "夜间"
        }
}

/**
 * 盘面色板。通过 [LocalIztroColors] / [IztroTheme] 读取；
 * 用 [IztroThemeProvider] 或 [Iztrolabe] 的 colorMode 切换。
 */
data class IztroColors(
    val isDark: Boolean,
    val major: Color,
    val focus: Color,
    val quan: Color,
    val tough: Color,
    val awesome: Color,
    val active: Color,
    val happy: Color,
    val nice: Color,
    val decorator1: Color,
    val decorator2: Color,
    val textMuted: Color,
    val border: Color,
    val soulPalaceBg: Color,
    val bodyPalaceBg: Color,
    val centerBg: Color,
    /** 未高亮宫位底色（原 Color.White） */
    val palaceBg: Color,
    /** 盘面外层底色 */
    val boardBg: Color,
    val focusedPalaceBg: Color,
    val oppositePalaceBg: Color,
    val surroundedPalaceBg: Color,
    /** 四化「忌」 */
    val ji: Color,
    val starMajorRed: Color,
    val starToughBlue: Color,
    val starSoftPurple: Color,
    val mutagenBadgeRed: Color,
    val selfLu: Color,
    val selfQuan: Color,
    val selfKe: Color,
    val selfJi: Color,
    /** 未激活运限按钮底 */
    val chipInactiveBg: Color,
    val chipInactiveFg: Color,
    /** 步进按钮 */
    val stepBtnBg: Color,
    val stepBtnDisabledBg: Color,
    val stepBtnDisabledFg: Color,
) {
    val selfMutagenBgs: List<Color>
        get() = listOf(selfLu, selfQuan, selfKe, selfJi)

    val mutagenColors: List<Color>
        get() = listOf(awesome, quan, nice, ji)

    fun scopeColor(scope: String): Color = when (scope) {
        "decadal" -> active
        "yearly" -> decorator2
        "monthly" -> nice
        "daily" -> decorator1
        "hourly" -> textMuted
        else -> major
    }

    companion object {
        val Light = IztroColors(
            isDark = false,
            major = Color(0xFF531DAB),
            focus = Color(0xFF000000),
            quan = Color(0xFF2F54EB),
            tough = Color(0xFF612500),
            awesome = Color(0xFFD4380D),
            active = Color(0xFF1890FF),
            happy = Color(0xFFC41D7F),
            nice = Color(0xFF237804),
            decorator1 = Color(0xFF90983C),
            decorator2 = Color(0xFF813359),
            textMuted = Color(0xFF8C8C8C),
            border = Color(0x12001529),
            soulPalaceBg = Color(0xFFFFF8E1),
            bodyPalaceBg = Color(0xFFE8F5E9),
            centerBg = Color(0xFFFAFAFA),
            palaceBg = Color.White,
            boardBg = Color(0xFFF5F5F5),
            focusedPalaceBg = Color(0xFFE8DEF8),
            oppositePalaceBg = Color(0xFFFFE4E4),
            surroundedPalaceBg = Color(0xFFFFE4E4),
            ji = Color(0xFF424242),
            starMajorRed = Color(0xFFC62828),
            starToughBlue = Color(0xFF1565C0),
            starSoftPurple = Color(0xFF6A1B9A),
            mutagenBadgeRed = Color(0xFFD32F2F),
            selfLu = Color(0xFF2E7D32),
            selfQuan = Color(0xFF8D6E63),
            selfKe = Color(0xFF1565C0),
            selfJi = Color(0xFFC62828),
            chipInactiveBg = Color(0xFFF0F0F0),
            chipInactiveFg = Color(0xFF9E9E9E),
            stepBtnBg = Color(0xFFE8EAF6),
            stepBtnDisabledBg = Color(0xFFF0F0F0),
            stepBtnDisabledFg = Color(0xFFBDBDBD),
        )

        /** 柔和深色：避免纯黑刺眼，星曜略提亮保证可读 */
        val Dark = IztroColors(
            isDark = true,
            major = Color(0xFFB39DDB),
            focus = Color(0xFFE8E8E8),
            quan = Color(0xFF7C9CFF),
            tough = Color(0xFFFFAB91),
            awesome = Color(0xFFFF8A65),
            active = Color(0xFF64B5F6),
            happy = Color(0xFFF48FB1),
            nice = Color(0xFF81C784),
            decorator1 = Color(0xFFC5E1A5),
            decorator2 = Color(0xFFCE93D8),
            textMuted = Color(0xFF9E9E9E),
            border = Color(0x33FFFFFF),
            soulPalaceBg = Color(0xFF3E3424),
            bodyPalaceBg = Color(0xFF1E3A2F),
            centerBg = Color(0xFF252528),
            palaceBg = Color(0xFF2C2C30),
            boardBg = Color(0xFF1A1A1D),
            focusedPalaceBg = Color(0xFF3D3554),
            oppositePalaceBg = Color(0xFF4A3030),
            surroundedPalaceBg = Color(0xFF4A3030),
            ji = Color(0xFFBDBDBD),
            starMajorRed = Color(0xFFEF9A9A),
            starToughBlue = Color(0xFF90CAF9),
            starSoftPurple = Color(0xFFCE93D8),
            mutagenBadgeRed = Color(0xFFE57373),
            selfLu = Color(0xFF66BB6A),
            selfQuan = Color(0xFFBCAAA4),
            selfKe = Color(0xFF64B5F6),
            selfJi = Color(0xFFEF5350),
            chipInactiveBg = Color(0xFF3A3A3E),
            chipInactiveFg = Color(0xFF9E9E9E),
            stepBtnBg = Color(0xFF3A3F5C),
            stepBtnDisabledBg = Color(0xFF333338),
            stepBtnDisabledFg = Color(0xFF6E6E6E),
        )

        fun forMode(mode: IztroColorMode): IztroColors = when (mode) {
            IztroColorMode.Light -> Light
            IztroColorMode.Dark -> Dark
        }
    }
}

val LocalIztroColors = staticCompositionLocalOf { IztroColors.Light }

/** 当前色板（须在 [IztroThemeProvider] / [Iztrolabe] 内使用） */
val IztroTheme: IztroColors
    @Composable
    @ReadOnlyComposable
    get() = LocalIztroColors.current

@Composable
fun IztroThemeProvider(
    mode: IztroColorMode,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalIztroColors provides IztroColors.forMode(mode)) {
        content()
    }
}
