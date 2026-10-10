package zhiqiu.iztro.bazi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 盘面日/夜间模式 */
enum class BaziColorMode {
    Light,
    Dark,
    ;

    fun toggle(): BaziColorMode = if (this == Light) Dark else Light

    val label: String
        get() = when (this) {
            Light -> "日间"
            Dark -> "夜间"
        }
}

/**
 * 八字盘色板：界面色 + 五行语义色。
 * 通过 [LocalBaziColors] / [BaziTheme] 读取，用 [BaziThemeProvider] 切换。
 */
data class BaziColors(
    val isDark: Boolean,
    val page: Color,
    val panel: Color,
    val ink: Color,
    val muted: Color,
    val label: Color,
    val sectionMuted: Color,
    val phaseBarBg: Color,
    val phaseDivider: Color,
    val softDivider: Color,
    val selectedBg: Color,
    val natalLuckDivider: Color,
    val wood: Color,
    val fire: Color,
    val earth: Color,
    val metal: Color,
    val water: Color,
) {
    fun ofElement(name: String): Color = when (name) {
        "木" -> wood
        "火" -> fire
        "土" -> earth
        "金" -> metal
        "水" -> water
        else -> ink
    }

    fun ofStem(stem: String): Color = when (stem) {
        "甲", "乙" -> wood
        "丙", "丁" -> fire
        "戊", "己" -> earth
        "庚", "辛" -> metal
        "壬", "癸" -> water
        else -> ink
    }

    fun ofBranch(branch: String): Color = when (branch) {
        "寅", "卯" -> wood
        "巳", "午" -> fire
        "辰", "戌", "丑", "未" -> earth
        "申", "酉" -> metal
        "亥", "子" -> water
        else -> ink
    }

    /** 纳音以尾字五行着色 */
    fun ofNayin(nayin: String): Color = ofElement(when {
        nayin.endsWith("木") -> "木"
        nayin.endsWith("火") -> "火"
        nayin.endsWith("土") -> "土"
        nayin.endsWith("金") -> "金"
        nayin.endsWith("水") -> "水"
        else -> ""
    })

    companion object {
        val Light = BaziColors(
            isDark = false,
            page = Color(0xFFFAFAFA),
            panel = Color(0xFFF5F0ED),
            ink = Color(0xFF222222),
            muted = Color(0xFF666666),
            label = Color(0xFF5A5A5A),
            sectionMuted = Color(0xFF999999),
            phaseBarBg = Color(0xFFF7F2E9),
            phaseDivider = Color(0xFFD8D0C6),
            softDivider = Color(0xFFE0D8D0),
            selectedBg = Color(0xFFF2E8DC),
            natalLuckDivider = Color(0xFFD0C8C0),
            wood = Color(0xFF43A047),
            fire = Color(0xFFE57373),
            earth = Color(0xFFA1887F),
            metal = Color(0xFFE0A84A),
            water = Color(0xFF42A5F5),
        )

        /** 深色下五行色整体提亮，保证暗底可读 */
        val Dark = BaziColors(
            isDark = true,
            page = Color(0xFF1C1B19),
            panel = Color(0xFF2A2825),
            ink = Color(0xFFE8E4DC),
            muted = Color(0xFF9E988C),
            label = Color(0xFF9E988C),
            sectionMuted = Color(0xFF7E7A72),
            phaseBarBg = Color(0xFF2F2B24),
            phaseDivider = Color(0xFF4A443B),
            softDivider = Color(0xFF3F3B34),
            selectedBg = Color(0xFF453A2C),
            natalLuckDivider = Color(0xFF4A443B),
            wood = Color(0xFF81C784),
            fire = Color(0xFFEF9A9A),
            earth = Color(0xFFBCAAA4),
            metal = Color(0xFFFFD54F),
            water = Color(0xFF90CAF9),
        )

        fun forMode(mode: BaziColorMode): BaziColors = when (mode) {
            BaziColorMode.Light -> Light
            BaziColorMode.Dark -> Dark
        }
    }
}

/** 成套八字主题：lightTheme / darkTheme 可整体替换 */
data class BaziThemeConfig(
    val lightTheme: BaziColors = BaziColors.Light,
    val darkTheme: BaziColors = BaziColors.Dark,
) {
    fun colors(mode: BaziColorMode): BaziColors = when (mode) {
        BaziColorMode.Light -> lightTheme
        BaziColorMode.Dark -> darkTheme
    }

    companion object {
        val Default = BaziThemeConfig()
    }
}

val LocalBaziColors = staticCompositionLocalOf { BaziColors.Light }

val LocalBaziThemeConfig = staticCompositionLocalOf { BaziThemeConfig.Default }

/** 当前色板（须在 [BaziThemeProvider] 内使用） */
val BaziTheme: BaziColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBaziColors.current

@Composable
fun BaziThemeProvider(
    mode: BaziColorMode,
    config: BaziThemeConfig = BaziThemeConfig.Default,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalBaziThemeConfig provides config,
        LocalBaziColors provides config.colors(mode),
        content = content,
    )
}

/** 兼容旧引用：五行/界面色取自当前 [BaziThemeProvider] 色板 */
object WuXingColors {
    val Wood: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.wood
    val Fire: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.fire
    val Earth: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.earth
    val Metal: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.metal
    val Water: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.water
    val Ink: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.ink
    val Muted: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.muted
    val Panel: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.panel
    val Page: Color
        @Composable @ReadOnlyComposable get() = BaziTheme.page

    @Composable
    @ReadOnlyComposable
    fun ofElement(name: String): Color = BaziTheme.ofElement(name)

    @Composable
    @ReadOnlyComposable
    fun ofStem(stem: String): Color = BaziTheme.ofStem(stem)

    @Composable
    @ReadOnlyComposable
    fun ofBranch(branch: String): Color = BaziTheme.ofBranch(branch)
}
