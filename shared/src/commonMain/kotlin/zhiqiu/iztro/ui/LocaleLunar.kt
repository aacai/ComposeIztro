package zhiqiu.iztro.ui

import zhiqiu.iztro.DemoLunarRaw

private fun ordinal(n: Int): String = when (n) {
    1 -> "${n}st"
    2 -> "${n}nd"
    3 -> "${n}rd"
    else -> "${n}th"
}

/** 对齐 react-iztro locales/index.ts toLocaleLunarStr */
fun toLocaleLunarStr(lunarStr: String, lunarDate: DemoLunarRaw, lang: String): String = when (lang) {
    "zh-TW" -> lunarStr.replace("闰", "閏").replace("腊", "臘")
    "en-US" -> buildString {
        append(lunarDate.lunarYear)
        append("-")
        append(lunarDate.lunarMonth)
        append("-")
        append(lunarDate.lunarDay)
        if (lunarDate.isLeap) {
            append("(Leap ${ordinal(lunarDate.lunarMonth)} Month)")
        }
    }
    "ja-JP" -> buildString {
        append("${lunarDate.lunarYear}年${lunarDate.lunarMonth}月${lunarDate.lunarDay}日")
        if (lunarDate.isLeap) append("（閏${lunarDate.lunarMonth}月）")
    }
    "ko-KR" -> buildString {
        append("${lunarDate.lunarYear}년 ${lunarDate.lunarMonth}월 ${lunarDate.lunarDay}일")
        if (lunarDate.isLeap) append(" (윤${lunarDate.lunarMonth}월)")
    }
    "vi-VN" -> buildString {
        append("${lunarDate.lunarDay} Tháng ${lunarDate.lunarMonth}")
        if (lunarDate.isLeap) append(" Nhuận")
        append(" Năm ${lunarDate.lunarYear}")
    }
    else -> lunarStr
}
