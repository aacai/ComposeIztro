package zhiqiu.iztro.data

data class StarInfo(
    val brightness: List<String>? = null,
    val fiveElements: String = "",
    val yinYang: String = "",
)

/** 亮度表索引按宫位序：寅卯辰巳午未申酉戌亥子丑（与 fixIndex 一致）。 */

/** 全宫同亮度。 */
private val ALL_PING = List(12) { "ping" }
private val ALL_XIAN = List(12) { "xian" }
private val ALL_MIAO = List(12) { "miao" }

/** 庙位在丑辰未戌三合宫，其余补「平」，保证任一宫都读得到亮度。 */
private val MIAO_CHEN_WEI_XU_CHOU_WITH_PING =
    listOf("ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping", "miao")

/** 庙位在寅巳申亥三合宫，其余补「平」。 */
private val MIAO_YIN_SI_SHEN_HAI_WITH_PING =
    listOf("miao", "ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping")

/**
 * 红鸾天喜：《紫微斗数全书》原文「入丑寅卯辰戌亥为入庙」「于子、巳、午、未、申、酉为落陷」，
 * 全书对这两星只分入庙/落陷两档，故吉六宫记庙、凶六宫记陷，不另分旺/平。
 */
private val HONG_LUAN_TIAN_XI =
    listOf("miao", "miao", "miao", "xian", "xian", "xian", "xian", "xian", "miao", "miao", "xian", "miao")

val STARS_INFO = mapOf(
    "ziweiMaj" to StarInfo(listOf("wang", "wang", "de", "wang", "miao", "miao", "wang", "wang", "de", "wang", "ping", "miao"), "土", "阴"),
    "tianjiMaj" to StarInfo(listOf("de", "wang", "li", "ping", "miao", "xian", "de", "wang", "li", "ping", "miao", "xian"), "木", "阴"),
    "taiyangMaj" to StarInfo(listOf("wang", "miao", "wang", "wang", "wang", "de", "de", "ping", "bu", "xian", "xian", "bu")),
    "wuquMaj" to StarInfo(listOf("de", "li", "miao", "ping", "wang", "miao", "de", "li", "miao", "ping", "wang", "miao"), "金", "阴"),
    "tiantongMaj" to StarInfo(listOf("li", "ping", "ping", "miao", "xian", "bu", "wang", "ping", "ping", "miao", "wang", "bu"), "水", "阳"),
    "lianzhenMaj" to StarInfo(listOf("miao", "ping", "li", "xian", "ping", "li", "miao", "ping", "li", "xian", "ping", "li"), "火", "阴"),
    "tianfuMaj" to StarInfo(listOf("miao", "de", "miao", "de", "wang", "miao", "de", "wang", "miao", "de", "miao", "miao"), "土", "阳"),
    "taiyinMaj" to StarInfo(listOf("wang", "xian", "xian", "xian", "bu", "bu", "li", "wang", "wang", "miao", "miao", "miao"), "水", "阴"),
    "tanlangMaj" to StarInfo(listOf("ping", "li", "miao", "xian", "wang", "miao", "ping", "li", "miao", "xian", "wang", "miao"), "水"),
    "jumenMaj" to StarInfo(listOf("miao", "miao", "xian", "wang", "wang", "bu", "miao", "miao", "xian", "wang", "wang", "bu"), "土", "阴"),
    "tianxiangMaj" to StarInfo(listOf("miao", "xian", "de", "de", "miao", "de", "miao", "xian", "de", "de", "miao", "miao"), "水"),
    "tianliangMaj" to StarInfo(listOf("miao", "miao", "miao", "xian", "miao", "wang", "xian", "de", "miao", "xian", "miao", "wang"), "土"),
    "qishaMaj" to StarInfo(listOf("miao", "wang", "miao", "ping", "wang", "miao", "miao", "wang", "miao", "ping", "wang", "miao")),
    "pojunMaj" to StarInfo(listOf("de", "xian", "wang", "ping", "miao", "wang", "de", "xian", "wang", "ping", "miao", "wang"), "水"),
    "wenchangMin" to StarInfo(listOf("xian", "li", "de", "miao", "xian", "li", "de", "miao", "xian", "li", "de", "miao")),
    "wenquMin" to StarInfo(listOf("ping", "wang", "de", "miao", "xian", "wang", "de", "miao", "xian", "wang", "de", "miao")),
    "huoxingMin" to StarInfo(listOf("miao", "li", "xian", "de", "miao", "li", "xian", "de", "miao", "li", "xian", "de")),
    "lingxingMin" to StarInfo(listOf("miao", "li", "xian", "de", "miao", "li", "xian", "de", "miao", "li", "xian", "de")),
    // 上游 iztro《STARS_INFO》原文（空位表示该星不会落此宫，保持一致不做填充）
    "qingyangMin" to StarInfo(listOf("", "xian", "miao", "", "xian", "miao", "", "xian", "miao", "", "xian", "miao")),
    "tuoluoMin" to StarInfo(listOf("xian", "", "miao", "xian", "", "miao", "xian", "", "miao", "xian", "", "miao")),

    // ↓↓↓ 以下为「非《全书》原表」的扩展条目 ↓↓↓
    // 《紫微斗数全书》卷二庙旺表与上游 iztro STARS_INFO 都只覆盖主星与昌曲火铃羊陀；
    // 辅星杂曜的庙旺为各家自加，这里按各家通用表补齐，并叠加已确认的定点修正。
    // 六吉/禄马/空劫亮度（公开表 mangekj.com「庙旺利平陷」+ ziwei.my「天马庙旺」；
    // 主星及昌曲火铃羊陀沿用上方 iztro 原表，避免改动既有排盘结果）
    // 左辅/右弼：巳宫平、酉宫陷
    "zuofuMin" to StarInfo(listOf("wang", "ping", "miao", "ping", "ping", "miao", "wang", "xian", "miao", "xian", "xian", "miao")),
    "youbiMin" to StarInfo(listOf("wang", "ping", "miao", "ping", "ping", "miao", "wang", "xian", "miao", "xian", "xian", "miao")),
    // 天魁：丑宫（甲戊庚年所落）补平，十二宫皆有亮度
    "tiankuiMin" to StarInfo(listOf("miao", "ping", "wang", "ping", "ping", "ping", "miao", "ping", "wang", "xian", "xian", "ping")),
    "tianyueMin" to StarInfo(listOf("ping", "miao", "ping", "wang", "ping", "ping", "ping", "miao", "ping", "wang", "xian", "ping")),
    // 禄存：十二宫一律入庙
    "lucunMin" to StarInfo(ALL_MIAO),
    "tianmaMin" to StarInfo(listOf("wang", "ping", "ping", "ping", "wang", "wang", "wang", "ping", "ping", "ping", "wang", "wang")),
    // 地空：酉宫入庙，其余落陷
    "dikongMin" to StarInfo(listOf("xian", "xian", "xian", "xian", "xian", "xian", "xian", "miao", "xian", "xian", "xian", "xian")),
    "dijieMin" to StarInfo(listOf("ping", "ping", "miao", "ping", "ping", "wang", "ping", "ping", "miao", "xian", "xian", "wang")),
    // 天空：酉宫旺
    "tiankong" to StarInfo(listOf("ping", "ping", "miao", "ping", "ping", "wang", "ping", "wang", "miao", "xian", "xian", "wang")),
    // 空亡类：旬空/副旬空/截空/空亡/截路/天伤/天使/大耗恒为陷
    "fuxunkong" to StarInfo(ALL_XIAN),
    "xunkong" to StarInfo(ALL_XIAN),
    "jiekong" to StarInfo(ALL_XIAN),
    "kongwang" to StarInfo(ALL_XIAN),
    "jielu" to StarInfo(ALL_XIAN),
    "tianshang" to StarInfo(ALL_XIAN),
    "tianshi" to StarInfo(ALL_XIAN),
    "dahao" to StarInfo(ALL_XIAN),
    "jieshaAdj" to StarInfo(ALL_XIAN),
    // 年解恒为平
    "nianjie" to StarInfo(ALL_PING),

    // 乙/丙级杂曜：庙位沿用公开表，其余补「平」，盘面任一宫都读得到亮度
    "tianguan" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianfu" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianchu" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianyue" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tiancai" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "enguang" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tiangui" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "taifu" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "fenggao" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "longchi" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "fengge" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianshou" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "huagai" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tiande" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "yuede" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "jieshen" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianwu" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "longde" to StarInfo(MIAO_CHEN_WEI_XU_CHOU_WITH_PING),
    "tianxing" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "tianyao" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "yinsha" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "hongluan" to StarInfo(HONG_LUAN_TIAN_XI),
    "tianxi" to StarInfo(HONG_LUAN_TIAN_XI),
    "tianku" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "tianxu" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "feilian" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "posui" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "xianchi" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    "guasu" to StarInfo(MIAO_YIN_SI_SHEN_HAI_WITH_PING),
    // 三台：辰宫旺、戌宫旺；八座：辰宫旺
    "santai" to StarInfo(listOf("ping", "ping", "wang", "ping", "ping", "miao", "ping", "ping", "wang", "ping", "ping", "miao")),
    "bazuo" to StarInfo(listOf("ping", "ping", "wang", "ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping", "miao")),
    // 孤辰：亥宫落陷（其余寅巳申入庙）
    "guchen" to StarInfo(listOf("miao", "ping", "ping", "miao", "ping", "ping", "miao", "ping", "ping", "xian", "ping", "ping")),
)
