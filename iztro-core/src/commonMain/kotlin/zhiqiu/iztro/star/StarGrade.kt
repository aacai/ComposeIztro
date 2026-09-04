package zhiqiu.iztro.star

import zhiqiu.iztro.i18n.t

/**
 * 星曜等级（甲乙丙丁戊五级）：
 * - 甲级：十四主星、六吉、六煞、禄存天马、四化 —— 已在 minorStars/majorStars 竖排
 * - 乙级：辅曜（红鸾天喜三台八座已直接在 MinorStar 安排，不在此列）
 * - 丙级及以下：长生/博士十二神、截空、天伤天使、流曜 —— 留宫位左下横排
 */
private val GRADE_B_STAR_KEYS = setOf(
    // 月系：天姚 解神 天刑 阴煞 天月 天巫
    "tianyao", "jieshen", "tianxing", "yinsha", "tianyue", "tianwu",
    // 日系：恩光 天贵
    "enguang", "tiangui",
    // 时系：台辅 封诰
    "taifu", "fenggao",
    // 年系：天官 天福 天厨 龙池 凤阁 天才 天寿 华盖 天德 月德
    // 天空 咸池 孤辰 寡宿 蜚廉 破碎 天哭 天虚
    "tianguan", "tianfu", "tianchu", "longchi", "fengge", "tiancai", "tianshou",
    "huagai", "tiande", "yuede", "tiankong", "xianchi", "guchen", "guasu",
    "feilian", "posui", "tianku", "tianxu",
)

/** 乙级星名集合（翻译后），从 adjectiveStars 提升为竖排辅星 */
fun gradeBStarNames(): Set<String> = GRADE_B_STAR_KEYS.map { t<String>(it) }.toSet()
