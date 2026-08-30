package zhiqiu.iztro.bazi.ui

import androidx.compose.ui.graphics.Color

/** 五行着色（对齐常见原盘配色） */
object WuXingColors {
    val Wood = Color(0xFF43A047)
    val Fire = Color(0xFFE57373)
    val Earth = Color(0xFFA1887F)
    val Metal = Color(0xFFE0A84A)
    val Water = Color(0xFF42A5F5)
    val Ink = Color(0xFF222222)
    val Muted = Color(0xFF666666)
    val Panel = Color(0xFFF5F0ED)
    val Page = Color(0xFFFAFAFA)

    fun ofElement(name: String): Color = when (name) {
        "木" -> Wood
        "火" -> Fire
        "土" -> Earth
        "金" -> Metal
        "水" -> Water
        else -> Ink
    }

    fun ofStem(stem: String): Color = when (stem) {
        "甲", "乙" -> Wood
        "丙", "丁" -> Fire
        "戊", "己" -> Earth
        "庚", "辛" -> Metal
        "壬", "癸" -> Water
        else -> Ink
    }

    fun ofBranch(branch: String): Color = when (branch) {
        "寅", "卯" -> Wood
        "巳", "午" -> Fire
        "辰", "戌", "丑", "未" -> Earth
        "申", "酉" -> Metal
        "亥", "子" -> Water
        else -> Ink
    }
}
