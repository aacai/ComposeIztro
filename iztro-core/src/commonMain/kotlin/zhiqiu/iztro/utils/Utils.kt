package zhiqiu.iztro.utils

import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.HeavenlyStemAndEarthlyBranchDate
import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.data.MUTAGEN
import zhiqiu.iztro.data.STARS_INFO
import zhiqiu.iztro.data.heavenlyStems
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.star.initStars

fun fixIndex(index: Int, max: Int = 12): Int {
    var i = index
    while (i < 0) i += max
    while (i > max - 1) i -= max
    return if (1.0 / i == Double.NEGATIVE_INFINITY) 0 else i
}

fun earthlyBranchIndexToPalaceIndex(earthlyBranchName: String): Int {
    val earthlyBranch = kot<String>(earthlyBranchName, "Earthly")
    val yin = kot<String>("yinEarthly", "Earthly")
    return fixIndex(EARTHLY_BRANCHES.indexOf(earthlyBranch) - EARTHLY_BRANCHES.indexOf(yin))
}

fun getBrightness(starName: String, index: Int): String {
    val star = kot<String>(starName)
    val config = getConfig()
    val targetBrightness = config.brightness[star] ?: STARS_INFO[star]?.brightness
    if (targetBrightness.isNullOrEmpty()) return ""
    return t(targetBrightness[fixIndex(index)])
}

fun getMutagen(starName: String, heavenlyStemName: String): String {
    val heavenlyStem = kot<String>(heavenlyStemName, "Heavenly")
    val starKey = kot<String>(starName)
    val target = getTargetMutagens(heavenlyStem)
    val idx = target.indexOf(starKey)
    return if (idx >= 0) t(MUTAGEN[idx]) else ""
}

fun getMutagensByHeavenlyStem(heavenlyStemName: String): List<String> {
    val heavenlyStem = kot<String>(heavenlyStemName, "Heavenly")
    return getTargetMutagens(heavenlyStem).map { t(it) }
}

fun fixEarthlyBranchIndex(earthlyBranchName: String): Int {
    val resolved = shortEarthlyBranchMap[earthlyBranchName] ?: kot<String>(earthlyBranchName, "Earthly")
    return fixIndex(EARTHLY_BRANCHES.indexOf(resolved) - EARTHLY_BRANCHES.indexOf("yinEarthly"))
}

private val shortEarthlyBranchMap = mapOf(
    "zi" to "ziEarthly", "chou" to "chouEarthly", "yin" to "yinEarthly", "mao" to "maoEarthly",
    "chen" to "chenEarthly", "si" to "siEarthly", "woo" to "wuEarthly", "wu" to "wuEarthly",
    "wei" to "weiEarthly", "shen" to "shenEarthly", "you" to "youEarthly", "xu" to "xuEarthly", "hai" to "haiEarthly",
)

fun fixLunarMonthIndex(solarDateStr: String, timeIndex: Int, fixLeap: Boolean = false): Int {
    val lunar = Calendar.solar2lunar(solarDateStr)
    val firstIndex = EARTHLY_BRANCHES.indexOf("yinEarthly")
    val needToAdd = lunar.isLeap && fixLeap && lunar.lunarDay > 15 && timeIndex != 12
    return fixIndex(lunar.lunarMonth + 1 - firstIndex + if (needToAdd) 1 else 0)
}

fun fixLunarDayIndex(lunarDay: Int, timeIndex: Int): Int =
    if (timeIndex >= 12) lunarDay else lunarDay - 1

fun mergeStars(vararg stars: List<List<Star>>): List<List<Star>> {
    val finalStars = initStars()
    stars.forEach { item ->
        item.forEachIndexed { index, subItem ->
            finalStars[index].addAll(subItem)
        }
    }
    return finalStars
}

fun timeToIndex(hour: Int): Int = when (hour) {
    0 -> 0
    23 -> 12
    else -> (hour + 1) / 2
}

fun getAgeIndex(earthlyBranchName: String): Int {
    val earthlyBranch = kot<String>(earthlyBranchName, "Earthly")
    return when (earthlyBranch) {
        "yinEarthly", "wuEarthly", "xuEarthly" -> fixEarthlyBranchIndex("chen")
        "shenEarthly", "ziEarthly", "chenEarthly" -> fixEarthlyBranchIndex("xu")
        "siEarthly", "youEarthly", "chouEarthly" -> fixEarthlyBranchIndex("wei")
        "haiEarthly", "maoEarthly", "weiEarthly" -> fixIndex(fixEarthlyBranchIndex("chou"))
        else -> -1
    }
}

fun translateChineseDate(chineseDate: HeavenlyStemAndEarthlyBranchDate): String {
    fun part(pair: Pair<String, String>): String = "${t<String>(kot(pair.first))}${t<String>(kot(pair.second))}"
    val yearly = part(chineseDate.yearly)
    val monthly = part(chineseDate.monthly)
    val daily = part(chineseDate.daily)
    val hourly = part(chineseDate.hourly)
    return "$yearly $monthly $daily $hourly"
}

private fun getTargetMutagens(heavenlyStem: String): List<String> {
    val config = getConfig()
    return config.mutagens[heavenlyStem] ?: heavenlyStems[heavenlyStem]?.mutagen ?: emptyList()
}
