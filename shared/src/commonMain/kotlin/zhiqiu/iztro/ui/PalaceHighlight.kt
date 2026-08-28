package zhiqiu.iztro.ui

/** 点击宫位后的三方四正高亮：本宫浅紫，对宫/三方浅红 */
enum class PalaceHighlight {
    None,
    Focused,    // 本宫（点击宫）
    Opposite,   // 对宫
    Surrounded, // 三方（财帛/官禄）
}

fun palaceHighlight(focusIndex: Int?, palaceIndex: Int): PalaceHighlight {
    if (focusIndex == null) return PalaceHighlight.None
    if (palaceIndex == focusIndex) return PalaceHighlight.Focused
    val opposite = fixPalaceIndex(focusIndex + 6)
    if (palaceIndex == opposite) return PalaceHighlight.Opposite
    val wealth = fixPalaceIndex(focusIndex + 8)
    val career = fixPalaceIndex(focusIndex + 4)
    if (palaceIndex == wealth || palaceIndex == career) return PalaceHighlight.Surrounded
    return PalaceHighlight.None
}

private fun fixPalaceIndex(index: Int): Int = ((index % 12) + 12) % 12
