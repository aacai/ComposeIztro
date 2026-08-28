package zhiqiu.iztro.data

data class StarInfo(
    val brightness: List<String>? = null,
    val fiveElements: String = "",
    val yinYang: String = "",
)

val STARS_INFO = mapOf(
    "ziweiMaj" to StarInfo(listOf("wang", "wang", "de", "wang", "miao", "miao", "wang", "wang", "de", "wang", "ping", "miao"), "土", "阴"),
    "tianjiMaj" to StarInfo(listOf("de", "wang", "li", "ping", "miao", "xian", "de", "wang", "li", "ping", "miao", "xian"), "木", "阴"),
    "taiyangMaj" to StarInfo(listOf("wang", "miao", "wang", "wang", "wang", "de", "de", "xian", "bu", "xian", "xian", "bu")),
    "wuquMaj" to StarInfo(listOf("de", "li", "miao", "ping", "wang", "miao", "de", "li", "miao", "ping", "wang", "miao"), "金", "阴"),
    "tiantongMaj" to StarInfo(listOf("li", "ping", "ping", "miao", "xian", "bu", "wang", "ping", "ping", "miao", "wang", "bu"), "水", "阳"),
    "lianzhenMaj" to StarInfo(listOf("miao", "ping", "li", "xian", "ping", "li", "miao", "ping", "li", "xian", "ping", "li"), "火", "阴"),
    "tianfuMaj" to StarInfo(listOf("miao", "de", "miao", "de", "wang", "miao", "de", "wang", "miao", "de", "miao", "miao"), "土", "阳"),
    "taiyinMaj" to StarInfo(listOf("wang", "xian", "xian", "xian", "bu", "bu", "li", "bu", "wang", "miao", "miao", "miao"), "水", "阴"),
    "tanlangMaj" to StarInfo(listOf("ping", "li", "miao", "xian", "wang", "miao", "ping", "li", "miao", "xian", "wang", "miao"), "水"),
    "jumenMaj" to StarInfo(listOf("miao", "miao", "xian", "wang", "wang", "bu", "miao", "miao", "xian", "wang", "wang", "bu"), "土", "阴"),
    "tianxiangMaj" to StarInfo(listOf("miao", "xian", "de", "de", "miao", "de", "miao", "xian", "de", "de", "miao", "miao"), "水"),
    "tianliangMaj" to StarInfo(listOf("miao", "miao", "miao", "xian", "miao", "wang", "xian", "de", "miao", "xian", "miao", "wang"), "土"),
    "qishaMaj" to StarInfo(listOf("miao", "wang", "miao", "ping", "wang", "miao", "miao", "miao", "miao", "ping", "wang", "miao")),
    "pojunMaj" to StarInfo(listOf("de", "xian", "wang", "ping", "miao", "wang", "de", "xian", "wang", "ping", "miao", "wang"), "水"),
    "wenchangMin" to StarInfo(listOf("xian", "li", "de", "miao", "xian", "li", "de", "miao", "xian", "li", "de", "miao")),
    "wenquMin" to StarInfo(listOf("ping", "wang", "de", "miao", "xian", "wang", "de", "miao", "xian", "wang", "de", "miao")),
    "huoxingMin" to StarInfo(listOf("miao", "li", "xian", "de", "miao", "li", "xian", "de", "miao", "li", "xian", "de")),
    "lingxingMin" to StarInfo(listOf("miao", "li", "xian", "de", "miao", "li", "xian", "de", "miao", "li", "xian", "de")),
    "qingyangMin" to StarInfo(listOf("", "xian", "miao", "", "xian", "miao", "", "xian", "miao", "", "xian", "miao")),
    "tuoluoMin" to StarInfo(listOf("xian", "", "miao", "xian", "", "miao", "xian", "", "miao", "xian", "", "miao")),
)
