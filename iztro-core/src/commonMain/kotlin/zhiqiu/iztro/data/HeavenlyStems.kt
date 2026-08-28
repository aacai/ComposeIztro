package zhiqiu.iztro.data

data class HeavenlyStemInfo(
    val yinYang: String,
    val fiveElements: String,
    val crash: String? = null,
    val mutagen: List<String>,
)

val heavenlyStems = mapOf(
    "jiaHeavenly" to HeavenlyStemInfo("阳", "木", "gengHeavenly", listOf("lianzhenMaj", "pojunMaj", "wuquMaj", "taiyangMaj")),
    "yiHeavenly" to HeavenlyStemInfo("阴", "木", "xinHeavenly", listOf("tianjiMaj", "tianliangMaj", "ziweiMaj", "taiyinMaj")),
    "bingHeavenly" to HeavenlyStemInfo("阳", "火", "renHeavenly", listOf("tiantongMaj", "tianjiMaj", "wenchangMin", "lianzhenMaj")),
    "dingHeavenly" to HeavenlyStemInfo("阴", "火", "guiHeavenly", listOf("taiyinMaj", "tiantongMaj", "tianjiMaj", "jumenMaj")),
    "wuHeavenly" to HeavenlyStemInfo("阳", "土", mutagen = listOf("tanlangMaj", "taiyinMaj", "youbiMin", "tianjiMaj")),
    "jiHeavenly" to HeavenlyStemInfo("阴", "土", mutagen = listOf("wuquMaj", "tanlangMaj", "tianliangMaj", "wenquMin")),
    "gengHeavenly" to HeavenlyStemInfo("阳", "金", "jiaHeavenly", listOf("taiyangMaj", "wuquMaj", "taiyinMaj", "tiantongMaj")),
    "xinHeavenly" to HeavenlyStemInfo("阴", "金", "yiHeavenly", listOf("jumenMaj", "taiyangMaj", "wenquMin", "wenchangMin")),
    "renHeavenly" to HeavenlyStemInfo("阳", "水", "bingHeavenly", listOf("tianliangMaj", "ziweiMaj", "zuofuMin", "wuquMaj")),
    "guiHeavenly" to HeavenlyStemInfo("阴", "水", "dingHeavenly", listOf("pojunMaj", "jumenMaj", "taiyinMaj", "tanlangMaj")),
)
