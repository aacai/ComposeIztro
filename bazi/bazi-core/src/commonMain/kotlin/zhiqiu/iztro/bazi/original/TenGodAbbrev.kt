package zhiqiu.iztro.bazi.original

/**
 * 运盘上常用单字十神：才/比/劫/印/枭…
 * 原局主星仍用全称。
 */
fun tenGodAbbrev(full: String): String = when (full) {
    "比肩" -> "比"
    "劫财" -> "劫"
    "食神" -> "食"
    "伤官" -> "伤"
    "偏财" -> "才"
    "正财" -> "财"
    "七杀", "偏官" -> "杀"
    "正官" -> "官"
    "偏印" -> "枭"
    "正印" -> "印"
    else -> full.take(1)
}
