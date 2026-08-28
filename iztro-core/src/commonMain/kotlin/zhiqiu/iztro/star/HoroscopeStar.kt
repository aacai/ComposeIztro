package zhiqiu.iztro.star

import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Star

private data class HoroscopeStarNames(
    val tiankui: String,
    val tianyue: String,
    val wenchang: String,
    val wenqu: String,
    val lucun: String,
    val qingyang: String,
    val tuoluo: String,
    val tianma: String,
    val hongluan: String,
    val tianxi: String,
)

private fun namesForScope(scope: String): HoroscopeStarNames = when (scope) {
    "decadal" -> HoroscopeStarNames("yunkui", "yunyue", "yunchang", "yunqu", "yunlu", "yunyang", "yuntuo", "yunma", "yunluan", "yunxi")
    "yearly" -> HoroscopeStarNames("liukui", "liuyue", "liuchang", "liuqu", "liulu", "liuyang", "liutuo", "liuma", "liuluan", "liuxi")
    "monthly" -> HoroscopeStarNames("yuekui", "yueyue", "yuechang", "yuequ", "yuelu", "yueyang", "yuetuo", "yuema", "yueluan", "yuexi")
    "daily" -> HoroscopeStarNames("rikui", "riyue", "richang", "riqu", "rilu", "riyang", "rituo", "rima", "riluan", "rixi")
    "hourly" -> HoroscopeStarNames("shikui", "shiyue", "shichang", "shiqu", "shilu", "shiyang", "shituo", "shima", "shiluan", "shixi")
    else -> HoroscopeStarNames("tiankuiMin", "tianyueMin", "wenchangMin", "wenquMin", "lucunMin", "qingyangMin", "tuoluoMin", "tianmaMin", "hongluanMin", "tianxi")
}

fun getHoroscopeStar(
    heavenlyStemKey: String,
    earthlyBranchKey: String,
    scope: String,
): List<List<Star>> {
    val stars = initStars()
    val trans = namesForScope(scope)
    val (kuiIndex, yueIndex) = getKuiYueIndex(heavenlyStemKey)
    val (changIndex, quIndex) = getChangQuIndexByHeavenlyStem(heavenlyStemKey)
    val luYang = getLuYangTuoMaIndex(heavenlyStemKey, earthlyBranchKey)
    val (hongluanIndex, tianxiIndex) = getLuanXiIndex(earthlyBranchKey)

    if (scope == "yearly") {
        val nianjieIndex = getNianjieIndex(earthlyBranchKey)
        stars[nianjieIndex].add(Star(name = t("nianjie"), type = "helper", scope = "yearly"))
    }

    fun add(idx: Int, key: String, type: String) {
        stars[idx].add(Star(name = t(key), type = type, scope = scope))
    }

    add(kuiIndex, trans.tiankui, "soft")
    add(yueIndex, trans.tianyue, "soft")
    add(changIndex, trans.wenchang, "soft")
    add(quIndex, trans.wenqu, "soft")
    add(luYang.luIndex, trans.lucun, "lucun")
    add(luYang.yangIndex, trans.qingyang, "tough")
    add(luYang.tuoIndex, trans.tuoluo, "tough")
    add(luYang.maIndex, trans.tianma, "tianma")
    add(hongluanIndex, trans.hongluan, "flower")
    add(tianxiIndex, trans.tianxi, "flower")

    return stars
}
