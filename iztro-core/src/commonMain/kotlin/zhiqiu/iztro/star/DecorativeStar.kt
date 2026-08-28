package zhiqiu.iztro.star

import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.GENDER
import zhiqiu.iztro.data.earthlyBranches
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.utils.fixEarthlyBranchIndex
import zhiqiu.iztro.utils.fixIndex

fun getAdjectiveStar(param: AstrolabeParam): List<List<Star>> {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val fixLeap = param.fixLeap
    val algorithm = getConfig().algorithm
    val stars = initStars()
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate, timeIndex, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly

    val yearlyIndex = getYearlyStarIndex(param)
    val monthlyIndex = getMonthlyStarIndex(solarDate, timeIndex, fixLeap)
    val dailyIndex = getDailyStarIndex(solarDate, timeIndex, fixLeap)
    val timelyIndex = getTimelyStarIndex(timeIndex)
    val (hongluanIndex, tianxiIndex) = getLuanXiIndex(yearly.second)
    val suiqian12 = getYearly12(solarDate).suiqian12

    fun add(idx: Int, key: String, type: String) {
        stars[idx].add(Star(t(key), type, "origin"))
    }

    add(hongluanIndex, "hongluan", "flower")
    add(tianxiIndex, "tianxi", "flower")
    add(monthlyIndex.tianyaoIndex, "tianyao", "flower")
    add(yearlyIndex.xianchiIndex, "xianchi", "flower")
    add(monthlyIndex.yuejieIndex, "jieshen", "helper")
    add(dailyIndex.santaiIndex, "santai", "adjective")
    add(dailyIndex.bazuoIndex, "bazuo", "adjective")
    add(dailyIndex.enguangIndex, "enguang", "adjective")
    add(dailyIndex.tianguiIndex, "tiangui", "adjective")
    add(yearlyIndex.longchiIndex, "longchi", "adjective")
    add(yearlyIndex.fenggeIndex, "fengge", "adjective")
    add(yearlyIndex.tiancaiIndex, "tiancai", "adjective")
    add(yearlyIndex.tianshouIndex, "tianshou", "adjective")
    add(timelyIndex.taifuIndex, "taifu", "adjective")
    add(timelyIndex.fenggaoIndex, "fenggao", "adjective")
    add(monthlyIndex.tianwuIndex, "tianwu", "adjective")
    add(yearlyIndex.huagaiIndex, "huagai", "adjective")
    add(yearlyIndex.tianguanIndex, "tianguan", "adjective")
    add(yearlyIndex.tianfuIndex, "tianfu", "adjective")
    add(yearlyIndex.tianchuIndex, "tianchu", "adjective")
    add(monthlyIndex.tianyueIndex, "tianyue", "adjective")
    add(yearlyIndex.tiandeIndex, "tiande", "adjective")
    add(yearlyIndex.yuedeIndex, "yuede", "adjective")
    add(yearlyIndex.tiankongIndex, "tiankong", "adjective")
    add(yearlyIndex.xunkongIndex, "xunkong", "adjective")

    if (algorithm != "zhongzhou") {
        add(yearlyIndex.jieluIndex, "jielu", "adjective")
        add(yearlyIndex.kongwangIndex, "kongwang", "adjective")
    } else {
        val longdeIdx = suiqian12.indexOf(t<String>("longde"))
        if (longdeIdx >= 0) add(longdeIdx, "longde", "adjective")
        add(yearlyIndex.jiekongIndex, "jiekong", "adjective")
        add(yearlyIndex.jieshaAdjIndex, "jieshaAdj", "adjective")
        add(yearlyIndex.dahaoAdjIndex, "dahao", "adjective")
    }

    add(yearlyIndex.guchenIndex, "guchen", "adjective")
    add(yearlyIndex.guasuIndex, "guasu", "adjective")
    add(yearlyIndex.feilianIndex, "feilian", "adjective")
    add(yearlyIndex.posuiIndex, "posui", "adjective")
    add(monthlyIndex.tianxingIndex, "tianxing", "adjective")
    add(monthlyIndex.yinshaIndex, "yinsha", "adjective")
    add(yearlyIndex.tiankuIndex, "tianku", "adjective")
    add(yearlyIndex.tianxuIndex, "tianxu", "adjective")
    add(yearlyIndex.tianshiIndex, "tianshi", "adjective")
    add(yearlyIndex.tianshangIndex, "tianshang", "adjective")
    add(yearlyIndex.nianjieIndex, "nianjie", "helper")

    return stars
}

fun getchangsheng12(param: AstrolabeParam): List<String> {
    val gender = param.gender ?: throw IllegalArgumentException("gender is required")
    val changsheng12 = Array<String?>(12) { null }
    val genderKey = kot<String>(gender)
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        param.solarDate, 0, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly
    val earthlyBranchOfYear = kot<String>(yearly.second, "Earthly")
    val soulAndBody = zhiqiu.iztro.astro.getSoulAndBody(param)
    val fiveElementClass = zhiqiu.iztro.astro.getFiveElementsClass(
        soulAndBody.heavenlyStemOfSoul, soulAndBody.earthlyBranchOfSoul,
    )
    val stars = listOf(
        "changsheng", "muyu", "guandai", "linguan", "diwang", "shuai",
        "bing", "si", "mu", "jue", "tai", "yang",
    )
    val startIdx = getChangesheng12StartIndex(fiveElementClass)
    for (i in stars.indices) {
        val idx = if (GENDER[genderKey] == earthlyBranches[earthlyBranchOfYear]!!.yinYang) {
            fixIndex(i + startIdx)
        } else {
            fixIndex(startIdx - i)
        }
        changsheng12[idx] = t(stars[i])
    }
    return changsheng12.map { it!! }
}

fun getBoShi12(solarDateStr: String, gender: String): List<String> {
    val genderKey = kot<String>(gender)
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDateStr, 0, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly
    val earthlyBranchOfYear = kot<String>(yearly.second, "Earthly")
    val stars = listOf(
        "boshi", "lishi", "qinglong", "xiaohao", "jiangjun", "zhoushu",
        "faylian", "xishen", "bingfu", "dahao", "fubing", "guanfu",
    )
    val luIndex = getLuYangTuoMaIndex(yearly.first, yearly.second).luIndex
    val boshi12 = Array<String?>(12) { null }
    for (i in stars.indices) {
        val idx = fixIndex(
            if (GENDER[genderKey] == earthlyBranches[earthlyBranchOfYear]!!.yinYang) luIndex + i else luIndex - i,
        )
        boshi12[idx] = t(stars[i])
    }
    return boshi12.map { it!! }
}

fun getYearly12(solarDateStr: String): Yearly12Result {
    val jiangqian12 = Array<String?>(12) { null }
    val suiqian12 = Array<String?>(12) { null }
    val algorithm = getConfig().algorithm
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDateStr, 0, StemBranchOptions(year = getConfig().horoscopeDivide),
    ).yearly

    val ts12shen = if (algorithm == "zhongzhou") {
        listOf("suijian", "huiqi", "sangmen", "guansuo", "gwanfu", "xiaohao", "suipo", "longde", "baihu", "tiande", "diaoke", "bingfu")
    } else {
        listOf("suijian", "huiqi", "sangmen", "guansuo", "gwanfu", "xiaohao", "dahao", "longde", "baihu", "tiande", "diaoke", "bingfu")
    }

    for (i in ts12shen.indices) {
        val idx = fixIndex(fixEarthlyBranchIndex(yearly.second) + i)
        suiqian12[idx] = t(ts12shen[i])
    }

    val jq12shen = listOf(
        "jiangxing", "panan", "suiyi", "xiishen", "huagai", "jiesha",
        "zhaisha", "tiansha", "zhibei", "xianchi", "yuesha", "wangshen",
    )
    val jiangqian12StartIndex = getJiangqian12StartIndex(yearly.second)
    for (i in jq12shen.indices) {
        val idx = fixIndex(jiangqian12StartIndex + i)
        jiangqian12[idx] = t(jq12shen[i])
    }

    return Yearly12Result(suiqian12.map { it!! }, jiangqian12.map { it!! })
}

data class Yearly12Result(val suiqian12: List<String>, val jiangqian12: List<String>)

private fun getChangesheng12StartIndex(fiveElementClassName: String): Int {
    return when (zhiqiu.iztro.data.FiveElementsClass.fromKey(kot(fiveElementClassName)).value) {
        2 -> fixEarthlyBranchIndex("shen")
        3 -> fixEarthlyBranchIndex("hai")
        4 -> fixEarthlyBranchIndex("si")
        5 -> fixEarthlyBranchIndex("shen")
        6 -> fixEarthlyBranchIndex("yin")
        else -> 0
    }
}

private fun getJiangqian12StartIndex(earthlyBranchName: String): Int {
    val earthlyBranchOfYear = kot<String>(earthlyBranchName, "Earthly")
    val jqStartIdx = when (earthlyBranchOfYear) {
        "yinEarthly", "wuEarthly", "xuEarthly" -> fixEarthlyBranchIndex("wuEarthly")
        "shenEarthly", "ziEarthly", "chenEarthly" -> fixEarthlyBranchIndex("ziEarthly")
        "siEarthly", "youEarthly", "chouEarthly" -> fixEarthlyBranchIndex("youEarthly")
        "haiEarthly", "maoEarthly", "weiEarthly" -> fixEarthlyBranchIndex("maoEarthly")
        else -> -1
    }
    return fixIndex(jqStartIdx)
}
