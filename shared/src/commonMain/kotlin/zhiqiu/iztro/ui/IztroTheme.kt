package zhiqiu.iztro.ui

import androidx.compose.ui.graphics.Color

object IztroTheme {
    val major = Color(0xFF531DAB)
    val focus = Color(0xFF000000)
    val quan = Color(0xFF2F54EB)
    val tough = Color(0xFF612500)
    val awesome = Color(0xFFD4380D)
    val active = Color(0xFF1890FF)
    val happy = Color(0xFFC41D7F)
    val nice = Color(0xFF237804)
    val decorator1 = Color(0xFF90983C)
    val decorator2 = Color(0xFF813359)
    val textMuted = Color(0xFF8C8C8C)
    val border = Color(0x12001529)
    val soulPalaceBg = Color(0xFFFFF8E1)
    val bodyPalaceBg = Color(0xFFE8F5E9)
    val centerBg = Color(0xFFFAFAFA)

    // 点击本宫浅紫；三方四正其余宫浅红
    val focusedPalaceBg = Color(0xFFE8DEF8)
    val oppositePalaceBg = Color(0xFFFFE4E4)
    val surroundedPalaceBg = Color(0xFFFFE4E4)

    /** 四化「忌」用深灰，可读性比纯黑好 */
    val ji = Color(0xFF424242)

    /** 盘面星曜着色 */
    val starMajorRed = Color(0xFFC62828)
    val starToughBlue = Color(0xFF1565C0)
    val starSoftPurple = Color(0xFF6A1B9A)
    /** 科权禄忌统一红底 */
    val mutagenBadgeRed = Color(0xFFD32F2F)

    /** 自化星名背景：禄绿 / 权棕 / 科蓝 / 忌红 */
    val selfLu = Color(0xFF2E7D32)
    val selfQuan = Color(0xFF8D6E63)
    val selfKe = Color(0xFF1565C0)
    val selfJi = Color(0xFFC62828)
    val selfMutagenBgs = listOf(selfLu, selfQuan, selfKe, selfJi)

    val mutagenColors = listOf(awesome, quan, nice, ji)

    fun scopeColor(scope: String): Color = when (scope) {
        "decadal" -> active
        "yearly" -> decorator2
        "monthly" -> nice
        "daily" -> decorator1
        "hourly" -> textMuted
        else -> major
    }
}
