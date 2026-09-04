package zhiqiu.iztro.astro

import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.CHINESE_TIME
import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.data.HEAVENLY_STEMS
import zhiqiu.iztro.data.TIME_RANGE
import zhiqiu.iztro.data.earthlyBranches
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.setLanguage
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Astrolabe
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.model.Palace
import zhiqiu.iztro.model.RawDates
import zhiqiu.iztro.star.getAdjectiveStar
import zhiqiu.iztro.star.getBoShi12
import zhiqiu.iztro.star.getMajorStar
import zhiqiu.iztro.star.getMinorStar
import zhiqiu.iztro.star.getYearly12
import zhiqiu.iztro.star.getchangsheng12
import zhiqiu.iztro.star.gradeBStarNames
import zhiqiu.iztro.utils.fixIndex
import zhiqiu.iztro.utils.translateChineseDate

fun bySolar(
    solarDate: String,
    timeIndex: Int,
    gender: String,
    fixLeap: Boolean = true,
    language: String? = null,
): Astrolabe {
    language?.let { setLanguage(it) }

    val palaces = mutableListOf<Palace>()
    val dayDivide = getConfig().dayDivide
    var tIndex = timeIndex
    if (dayDivide == "current" && tIndex >= 12) {
        tIndex = 0
    }

    val chineseDateRaw = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate, tIndex,
        StemBranchOptions(year = getConfig().yearDivide, month = getConfig().horoscopeDivide),
    )
    val earthlyBranchOfYear = kot<String>(chineseDateRaw.yearly.second, "Earthly")
    val heavenlyStemOfYear = kot<String>(chineseDateRaw.yearly.first, "Heavenly")
    val param = AstrolabeParam(solarDate, tIndex, fixLeap, gender)
    val soulAndBody = getSoulAndBody(param)
    val palaceNames = getPalaceNames(soulAndBody.soulIndex)
    val majorStars = getMajorStar(param)
    val minorStars = getMinorStar(solarDate, tIndex, fixLeap)
    val adjectiveStars = getAdjectiveStar(param)
    val changsheng12 = getchangsheng12(param)
    val boshi12 = getBoShi12(solarDate, gender)
    val yearly12 = getYearly12(solarDate)
    val horoscope = getHoroscope(param)

    // 乙级星提升为辅星（竖排），丙级以下留宫位左下横排杂曜
    val gradeBNames = gradeBStarNames()
    for (i in 0 until 12) {
        val heavenlyStemOfPalace = HEAVENLY_STEMS[
            fixIndex(HEAVENLY_STEMS.indexOf(kot<String>(soulAndBody.heavenlyStemOfSoul, "Heavenly")) - soulAndBody.soulIndex + i, 10)
        ]
        val earthlyBranchOfPalace = EARTHLY_BRANCHES[fixIndex(2 + i)]
        val promoted = adjectiveStars[i].filter { it.name in gradeBNames }
        val remaining = adjectiveStars[i].filterNot { it.name in gradeBNames }
        palaces.add(
            Palace(
                index = i,
                name = palaceNames[i],
                isBodyPalace = soulAndBody.bodyIndex == i,
                isOriginalPalace = earthlyBranchOfPalace !in listOf("ziEarthly", "chouEarthly") &&
                    heavenlyStemOfPalace == heavenlyStemOfYear,
                heavenlyStem = t(heavenlyStemOfPalace),
                earthlyBranch = t(earthlyBranchOfPalace),
                majorStars = majorStars[i],
                minorStars = minorStars[i] + promoted,
                adjectiveStars = remaining,
                changsheng12 = changsheng12[i],
                boshi12 = boshi12[i],
                jiangqian12 = yearly12.jiangqian12[i],
                suiqian12 = yearly12.suiqian12[i],
                decadal = horoscope.decadals[i],
                ages = horoscope.ages[i],
            ),
        )
    }

    val earthlyBranchOfSoulPalace = EARTHLY_BRANCHES[fixIndex(soulAndBody.soulIndex + 2)]
    val earthlyBranchOfBodyPalace = t<String>(EARTHLY_BRANCHES[fixIndex(soulAndBody.bodyIndex + 2)])
    val lunarDateObj = Calendar.solar2lunar(solarDate)

    val soulStarKey = if (getConfig().algorithm == "zhongzhou") {
        earthlyBranches[earthlyBranchOfYear]!!.soul
    } else {
        earthlyBranches[earthlyBranchOfSoulPalace]!!.soul
    }

    return Astrolabe(
        gender = t(kot(gender)),
        solarDate = solarDate,
        lunarDate = lunarDateObj.format(useChinese = true),
        chineseDate = translateChineseDate(chineseDateRaw),
        rawDates = RawDates(lunarDateObj, chineseDateRaw),
        timeIndex = timeIndex,
        time = t(CHINESE_TIME[timeIndex]),
        timeRange = TIME_RANGE[timeIndex],
        sign = t(kot(Calendar.getSign(solarDate))),
        zodiac = t(kot(Calendar.getZodiac(chineseDateRaw.yearly.second))),
        earthlyBranchOfSoulPalace = t(earthlyBranchOfSoulPalace),
        earthlyBranchOfBodyPalace = earthlyBranchOfBodyPalace,
        soul = t(soulStarKey),
        body = t(earthlyBranches[earthlyBranchOfYear]!!.body),
        fiveElementsClass = getFiveElementsClass(soulAndBody.heavenlyStemOfSoul, soulAndBody.earthlyBranchOfSoul),
        palaces = palaces,
    )
}

fun byLunar(
    lunarDateStr: String,
    timeIndex: Int,
    gender: String,
    isLeapMonth: Boolean = false,
    fixLeap: Boolean = true,
    language: String? = null,
): Astrolabe {
    val solarDate = Calendar.lunar2solar(lunarDateStr, isLeapMonth)
    return bySolar(solarDate.toString(), timeIndex, gender, fixLeap, language)
}

fun getZodiacBySolarDate(solarDateStr: String, language: String? = null): String {
    language?.let { setLanguage(it) }
    val yearly = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDateStr, 0, StemBranchOptions(year = getConfig().yearDivide),
    ).yearly
    return t(kot(Calendar.getZodiac(yearly.second)))
}

fun getSignBySolarDate(solarDateStr: String, language: String? = null): String {
    language?.let { setLanguage(it) }
    return t(kot(Calendar.getSign(solarDateStr)))
}

fun getMajorStarBySolarDate(
    solarDateStr: String,
    timeIndex: Int,
    fixLeap: Boolean = true,
    language: String? = null,
): String {
    language?.let { setLanguage(it) }
    val param = AstrolabeParam(solarDateStr, timeIndex, fixLeap)
    val soulIndex = getSoulAndBody(param).soulIndex
    val majorStars = getMajorStar(param)
    val stars = majorStars[soulIndex].filter { it.type == "major" }
    if (stars.isNotEmpty()) {
        return stars.joinToString(",") { it.name }
    }
    return majorStars[fixIndex(soulIndex + 6)].filter { it.type == "major" }.joinToString(",") { it.name }
}
