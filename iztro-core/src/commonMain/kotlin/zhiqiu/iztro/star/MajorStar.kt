package zhiqiu.iztro.star

import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.utils.fixIndex
import zhiqiu.iztro.utils.getBrightness
import zhiqiu.iztro.utils.getMutagen

fun getMajorStar(param: AstrolabeParam): List<List<Star>> {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val (ziweiIndex, tianfuIndex) = getStartIndex(param)
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate, timeIndex, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly
    val stars = initStars()

    val ziweiGroup = listOf("ziweiMaj", "tianjiMaj", "", "taiyangMaj", "wuquMaj", "tiantongMaj", "", "", "lianzhenMaj")
    val tianfuGroup = listOf("tianfuMaj", "taiyinMaj", "tanlangMaj", "jumenMaj", "tianxiangMaj", "tianliangMaj", "qishaMaj", "", "", "", "pojunMaj")

    ziweiGroup.forEachIndexed { i, s ->
        if (s.isNotEmpty()) {
            val idx = fixIndex(ziweiIndex - i)
            val name = t<String>(s)
            stars[idx].add(
                Star(name, "major", "origin", getBrightness(name, idx), getMutagen(name, yearly.first)),
            )
        }
    }

    tianfuGroup.forEachIndexed { i, s ->
        if (s.isNotEmpty()) {
            val idx = fixIndex(tianfuIndex + i)
            val name = t<String>(s)
            stars[idx].add(
                Star(name, "major", "origin", getBrightness(name, idx), getMutagen(name, yearly.first)),
            )
        }
    }

    return stars
}
