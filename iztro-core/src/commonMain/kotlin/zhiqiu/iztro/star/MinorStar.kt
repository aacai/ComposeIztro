package zhiqiu.iztro.star

import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.utils.fixLunarMonthIndex
import zhiqiu.iztro.utils.getBrightness
import zhiqiu.iztro.utils.getMutagen

fun getMinorStar(solarDateStr: String, timeIndex: Int, fixLeap: Boolean = false): List<List<Star>> {
    val stars = initStars()
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDateStr, timeIndex, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly
    val monthIndex = fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap)

    val (zuoIndex, youIndex) = getZuoYouIndex(monthIndex + 1)
    val (changIndex, quIndex) = getChangQuIndex(timeIndex)
    val (kuiIndex, yueIndex) = getKuiYueIndex(yearly.first)
    val (huoIndex, lingIndex) = getHuoLingIndex(yearly.second, timeIndex)
    val (kongIndex, jieIndex) = getKongJieIndex(timeIndex)
    val (luIndex, yangIndex, tuoIndex, maIndex) = getLuYangTuoMaIndex(yearly.first, yearly.second)
    val (hongluanIndex, tianxiIndex) = getLuanXiIndex(yearly.second)
    val dailyIndex = getDailyStarIndex(solarDateStr, timeIndex, fixLeap)

    fun add(idx: Int, key: String, type: String, withMutagen: Boolean = false) {
        val name = t<String>(key)
        stars[idx].add(
            Star(
                name = name,
                type = type,
                scope = "origin",
                brightness = getBrightness(name, idx),
                mutagen = if (withMutagen) getMutagen(name, yearly.first).ifEmpty { null } else null,
            ),
        )
    }

    add(zuoIndex, "zuofuMin", "soft", true)
    add(youIndex, "youbiMin", "soft", true)
    add(changIndex, "wenchangMin", "soft", true)
    add(quIndex, "wenquMin", "soft", true)
    add(kuiIndex, "tiankuiMin", "soft")
    add(yueIndex, "tianyueMin", "soft")
    add(luIndex, "lucunMin", "lucun")
    add(maIndex, "tianmaMin", "tianma")
    add(kongIndex, "dikongMin", "tough")
    add(jieIndex, "dijieMin", "tough")
    add(huoIndex, "huoxingMin", "tough")
    add(lingIndex, "lingxingMin", "tough")
    add(yangIndex, "qingyangMin", "tough")
    add(tuoIndex, "tuoluoMin", "tough")
    // 红鸾天喜三台八座：优先级较高的吉曜，竖排展示（同禄存天马）
    add(hongluanIndex, "hongluan", "flower")
    add(tianxiIndex, "tianxi", "flower")
    add(dailyIndex.santaiIndex, "santai", "soft")
    add(dailyIndex.bazuoIndex, "bazuo", "soft")

    return stars
}
