package zhiqiu.iztro.star

import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.astro.getFiveElementsClass
import zhiqiu.iztro.astro.getSoulAndBody
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.data.FiveElementsClass
import zhiqiu.iztro.data.HEAVENLY_STEMS
import zhiqiu.iztro.data.PALACES
import zhiqiu.iztro.i18n.FiveElementsClassKey
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.utils.fixEarthlyBranchIndex
import zhiqiu.iztro.utils.fixIndex
import zhiqiu.iztro.utils.fixLunarDayIndex
import zhiqiu.iztro.utils.fixLunarMonthIndex
import kotlin.math.floor

/**
 * 起紫微星诀算法
 *
 * - 六五四三二，酉午亥辰丑，
 * - 局数除日数，商数宫前走；
 * - 若见数无余，便要起虎口，
 * - 日数小於局，还直宫中守。
 */
fun getStartIndex(param: AstrolabeParam): StartIndexResult {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val fixLeap = param.fixLeap
    val from = param.from

    val soulAndBody = getSoulAndBody(AstrolabeParam(solarDate = solarDate, timeIndex = timeIndex, fixLeap = fixLeap))
    val heavenlyStemOfSoul = soulAndBody.heavenlyStemOfSoul
    val earthlyBranchOfSoul = soulAndBody.earthlyBranchOfSoul
    val lunarDay = Calendar.solar2lunar(solarDate).lunarDay

    val baseHeavenlyStem = from?.heavenlyStem ?: heavenlyStemOfSoul
    val baseEarthlyBranch = from?.earthlyBranch ?: earthlyBranchOfSoul

    val fiveElements = kot<FiveElementsClassKey>(getFiveElementsClass(baseHeavenlyStem, baseEarthlyBranch))
    val fiveElementsValue = FiveElementsClass.valueOf(fiveElements).value

    var remainder = -1
    var quotient = 0
    var offset = -1

    val maxDays = Calendar.getTotalDaysOfLunarMonth(solarDate)
    var day = if (timeIndex == 12 && getConfig().dayDivide != "current") lunarDay + 1 else lunarDay

    if (day > maxDays) {
        day -= maxDays
    }

    do {
        offset++
        val divisor = day + offset
        quotient = divisor / fiveElementsValue
        remainder = divisor % fiveElementsValue
    } while (remainder != 0)

    quotient %= 12
    var ziweiIndex = quotient - 1

    if (offset % 2 == 0) {
        ziweiIndex += offset
    } else {
        ziweiIndex -= offset
    }

    ziweiIndex = fixIndex(ziweiIndex)
    val tianfuIndex = fixIndex(12 - ziweiIndex)

    return StartIndexResult(ziweiIndex = ziweiIndex, tianfuIndex = tianfuIndex)
}

/**
 * 按年干支计算禄存、擎羊，陀罗、天马的索引
 */
fun getLuYangTuoMaIndex(
    heavenlyStemName: HeavenlyStemName,
    earthlyBranchName: EarthlyBranchName,
): LuYangTuoMaIndexResult {
    var luIndex = -1
    var maIndex = 0

    val heavenlyStem = kot<HeavenlyStemKey>(heavenlyStemName, "Heavenly")
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")

    when (earthlyBranch) {
        "yinEarthly", "wuEarthly", "xuEarthly" ->
            maIndex = fixEarthlyBranchIndex("shen")
        "shenEarthly", "ziEarthly", "chenEarthly" ->
            maIndex = fixEarthlyBranchIndex("yin")
        "siEarthly", "youEarthly", "chouEarthly" ->
            maIndex = fixEarthlyBranchIndex("hai")
        "haiEarthly", "maoEarthly", "weiEarthly" ->
            maIndex = fixEarthlyBranchIndex("si")
    }

    when (heavenlyStem) {
        "jiaHeavenly" -> luIndex = fixEarthlyBranchIndex("yin")
        "yiHeavenly" -> luIndex = fixEarthlyBranchIndex("mao")
        "bingHeavenly", "wuHeavenly" -> luIndex = fixEarthlyBranchIndex("si")
        "dingHeavenly", "jiHeavenly" -> luIndex = fixEarthlyBranchIndex("woo")
        "gengHeavenly" -> luIndex = fixEarthlyBranchIndex("shen")
        "xinHeavenly" -> luIndex = fixEarthlyBranchIndex("you")
        "renHeavenly" -> luIndex = fixEarthlyBranchIndex("hai")
        "guiHeavenly" -> luIndex = fixEarthlyBranchIndex("zi")
    }

    return LuYangTuoMaIndexResult(
        luIndex = luIndex,
        maIndex = maIndex,
        yangIndex = fixIndex(luIndex + 1),
        tuoIndex = fixIndex(luIndex - 1),
    )
}

/** 获取天魁天钺所在宫位索引（按年干） */
fun getKuiYueIndex(heavenlyStemName: HeavenlyStemName): KuiYueIndexResult {
    var kuiIndex = -1
    var yueIndex = -1
    val heavenlyStem = kot<HeavenlyStemKey>(heavenlyStemName, "Heavenly")

    when (heavenlyStem) {
        "jiaHeavenly", "wuHeavenly", "gengHeavenly" -> {
            kuiIndex = fixEarthlyBranchIndex("chou")
            yueIndex = fixEarthlyBranchIndex("wei")
        }
        "yiHeavenly", "jiHeavenly" -> {
            kuiIndex = fixEarthlyBranchIndex("zi")
            yueIndex = fixEarthlyBranchIndex("shen")
        }
        "xinHeavenly" -> {
            kuiIndex = fixEarthlyBranchIndex("woo")
            yueIndex = fixEarthlyBranchIndex("yin")
        }
        "bingHeavenly", "dingHeavenly" -> {
            kuiIndex = fixEarthlyBranchIndex("hai")
            yueIndex = fixEarthlyBranchIndex("you")
        }
        "renHeavenly", "guiHeavenly" -> {
            kuiIndex = fixEarthlyBranchIndex("mao")
            yueIndex = fixEarthlyBranchIndex("si")
        }
    }

    return KuiYueIndexResult(kuiIndex = kuiIndex, yueIndex = yueIndex)
}

/** 获取左辅右弼的索引（按生月） */
fun getZuoYouIndex(lunarMonth: Int): ZuoYouIndexResult {
    val zuoIndex = fixIndex(fixEarthlyBranchIndex("chen") + (lunarMonth - 1))
    val youIndex = fixIndex(fixEarthlyBranchIndex("xu") - (lunarMonth - 1))
    return ZuoYouIndexResult(zuoIndex = zuoIndex, youIndex = youIndex)
}

/** 获取文昌文曲的索引（按时支） */
fun getChangQuIndex(timeIndex: Int): ChangQuIndexResult {
    val changIndex = fixIndex(fixEarthlyBranchIndex("xu") - fixIndex(timeIndex))
    val quIndex = fixIndex(fixEarthlyBranchIndex("chen") + fixIndex(timeIndex))
    return ChangQuIndexResult(changIndex = changIndex, quIndex = quIndex)
}

/** 获取日系星索引，包括三台，八座，恩光，天贵 */
fun getDailyStarIndex(
    solarDateStr: String,
    timeIndex: Int,
    fixLeap: Boolean? = null,
): DailyStarIndexResult {
    val lunarDay = Calendar.solar2lunar(solarDateStr).lunarDay
    val monthIndex = fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap ?: false)

    val zuoYou = getZuoYouIndex(monthIndex + 1)
    val changQu = getChangQuIndex(timeIndex)
    val dayIndex = fixLunarDayIndex(lunarDay, timeIndex)

    return DailyStarIndexResult(
        santaiIndex = fixIndex((zuoYou.zuoIndex + dayIndex) % 12),
        bazuoIndex = fixIndex((zuoYou.youIndex - dayIndex) % 12),
        enguangIndex = fixIndex(((changQu.changIndex + dayIndex) % 12) - 1),
        tianguiIndex = fixIndex(((changQu.quIndex + dayIndex) % 12) - 1),
    )
}

/** 获取时系星耀索引，包括台辅，封诰 */
fun getTimelyStarIndex(timeIndex: Int): TimelyStarIndexResult {
    val taifuIndex = fixIndex(fixEarthlyBranchIndex("woo") + fixIndex(timeIndex))
    val fenggaoIndex = fixIndex(fixEarthlyBranchIndex("yin") + fixIndex(timeIndex))
    return TimelyStarIndexResult(taifuIndex = taifuIndex, fenggaoIndex = fenggaoIndex)
}

/** 获取地空地劫的索引（按时支） */
fun getKongJieIndex(timeIndex: Int): KongJieIndexResult {
    val fixedTimeIndex = fixIndex(timeIndex)
    val haiIndex = fixEarthlyBranchIndex("hai")
    val kongIndex = fixIndex(haiIndex - fixedTimeIndex)
    val jieIndex = fixIndex(haiIndex + fixedTimeIndex)
    return KongJieIndexResult(kongIndex = kongIndex, jieIndex = jieIndex)
}

/** 获取火星铃星索引（按年支以及时支） */
fun getHuoLingIndex(
    earthlyBranchName: EarthlyBranchName,
    timeIndex: Int,
): HuoLingIndexResult {
    var huoIndex = -1
    var lingIndex = -1
    val fixedTimeIndex = fixIndex(timeIndex)
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")

    when (earthlyBranch) {
        "yinEarthly", "wuEarthly", "xuEarthly" -> {
            huoIndex = fixEarthlyBranchIndex("chou") + fixedTimeIndex
            lingIndex = fixEarthlyBranchIndex("mao") + fixedTimeIndex
        }
        "shenEarthly", "ziEarthly", "chenEarthly" -> {
            huoIndex = fixEarthlyBranchIndex("yin") + fixedTimeIndex
            lingIndex = fixEarthlyBranchIndex("xu") + fixedTimeIndex
        }
        "siEarthly", "youEarthly", "chouEarthly" -> {
            huoIndex = fixEarthlyBranchIndex("mao") + fixedTimeIndex
            lingIndex = fixEarthlyBranchIndex("xu") + fixedTimeIndex
        }
        "haiEarthly", "weiEarthly", "maoEarthly" -> {
            huoIndex = fixEarthlyBranchIndex("you") + fixedTimeIndex
            lingIndex = fixEarthlyBranchIndex("xu") + fixedTimeIndex
        }
    }

    return HuoLingIndexResult(
        huoIndex = fixIndex(huoIndex),
        lingIndex = fixIndex(lingIndex),
    )
}

/** 获取红鸾天喜所在宫位索引 */
fun getLuanXiIndex(earthlyBranchName: EarthlyBranchName): LuanXiIndexResult {
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")
    val hongluanIndex = fixIndex(fixEarthlyBranchIndex("mao") - EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tianxiIndex = fixIndex(hongluanIndex + 6)
    return LuanXiIndexResult(hongluanIndex = hongluanIndex, tianxiIndex = tianxiIndex)
}

/** 安华盖、咸池 */
fun getHuagaiXianchiIndex(earthlyBranchName: EarthlyBranchName): HuagaiXianchiIndexResult {
    var hgIdx = -1
    var xcIdx = -1
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")

    when (earthlyBranch) {
        "yinEarthly", "wuEarthly", "xuEarthly" -> {
            hgIdx = fixEarthlyBranchIndex("xu")
            xcIdx = fixEarthlyBranchIndex("mao")
        }
        "shenEarthly", "ziEarthly", "chenEarthly" -> {
            hgIdx = fixEarthlyBranchIndex("chen")
            xcIdx = fixEarthlyBranchIndex("you")
        }
        "siEarthly", "youEarthly", "chouEarthly" -> {
            hgIdx = fixEarthlyBranchIndex("chou")
            xcIdx = fixEarthlyBranchIndex("woo")
        }
        "haiEarthly", "weiEarthly", "maoEarthly" -> {
            hgIdx = fixEarthlyBranchIndex("wei")
            xcIdx = fixEarthlyBranchIndex("zi")
        }
    }

    return HuagaiXianchiIndexResult(
        huagaiIndex = fixIndex(hgIdx),
        xianchiIndex = fixIndex(xcIdx),
    )
}

/** 安孤辰寡宿 */
fun getGuGuaIndex(earthlyBranchName: EarthlyBranchName): GuGuaIndexResult {
    var guIdx = -1
    var guaIdx = -1
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")

    when (earthlyBranch) {
        "yinEarthly", "maoEarthly", "chenEarthly" -> {
            guIdx = fixEarthlyBranchIndex("si")
            guaIdx = fixEarthlyBranchIndex("chou")
        }
        "siEarthly", "wuEarthly", "weiEarthly" -> {
            guIdx = fixEarthlyBranchIndex("shen")
            guaIdx = fixEarthlyBranchIndex("chen")
        }
        "shenEarthly", "youEarthly", "xuEarthly" -> {
            guIdx = fixEarthlyBranchIndex("hai")
            guaIdx = fixEarthlyBranchIndex("wei")
        }
        "haiEarthly", "ziEarthly", "chouEarthly" -> {
            guIdx = fixEarthlyBranchIndex("yin")
            guaIdx = fixEarthlyBranchIndex("xu")
        }
    }

    return GuGuaIndexResult(
        guchenIndex = fixIndex(guIdx),
        guasuIndex = fixIndex(guaIdx),
    )
}

/** 安劫杀诀（年支） */
fun getJieshaAdjIndex(earthlyBranchKey: EarthlyBranchKey): Int = when (earthlyBranchKey) {
    "shenEarthly", "ziEarthly", "chenEarthly" -> 3
    "haiEarthly", "maoEarthly", "weiEarthly" -> 6
    "yinEarthly", "wuEarthly", "xuEarthly" -> 9
    "siEarthly", "youEarthly", "chouEarthly" -> 0
    else -> 0
}

/** 安大耗诀（年支） */
fun getDahaoIndex(earthlyBranchKey: EarthlyBranchKey): Int {
    val matched = arrayOf(
        "weiEarthly",
        "wuEarthly",
        "youEarthly",
        "shenEarthly",
        "haiEarthly",
        "xuEarthly",
        "chouEarthly",
        "ziEarthly",
        "maoEarthly",
        "yinEarthly",
        "siEarthly",
        "chenEarthly",
    )[EARTHLY_BRANCHES.indexOf(earthlyBranchKey)]

    return fixIndex(EARTHLY_BRANCHES.indexOf(matched) - 2)
}

/** 获取年系星的索引 */
fun getYearlyStarIndex(param: AstrolabeParam): YearlyStarIndexResult {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val gender = param.gender
    val fixLeap = param.fixLeap
    val horoscopeDivide = getConfig().horoscopeDivide

    val stemBranch = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate,
        timeIndex,
        StemBranchOptions(year = horoscopeDivide),
    )
    val yearly = stemBranch.yearly
    val soulAndBody = getSoulAndBody(AstrolabeParam(solarDate = solarDate, timeIndex = timeIndex, fixLeap = fixLeap))
    val soulIndex = soulAndBody.soulIndex
    val bodyIndex = soulAndBody.bodyIndex

    val heavenlyStem = kot<HeavenlyStemKey>(yearly.first, "Heavenly")
    val earthlyBranch = kot<EarthlyBranchKey>(yearly.second, "Earthly")

    val huagaiXianchi = getHuagaiXianchiIndex(yearly.second)
    val guGua = getGuGuaIndex(yearly.second)

    val tiancaiIndex = fixIndex(soulIndex + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tianshouIndex = fixIndex(bodyIndex + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tianchuIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("si", "woo", "zi", "si", "woo", "shen", "yin", "woo", "you", "hai")[
                HEAVENLY_STEMS.indexOf(heavenlyStem)
            ],
        ),
    )
    val posuiIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("si", "chou", "you")[EARTHLY_BRANCHES.indexOf(earthlyBranch) % 3],
        ),
    )
    val feilianIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf(
                "shen", "you", "xu", "si", "woo", "wei",
                "yin", "mao", "chen", "hai", "zi", "chou",
            )[EARTHLY_BRANCHES.indexOf(earthlyBranch)],
        ),
    )
    val longchiIndex = fixIndex(fixEarthlyBranchIndex("chen") + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val fenggeIndex = fixIndex(fixEarthlyBranchIndex("xu") - EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tiankuIndex = fixIndex(fixEarthlyBranchIndex("woo") - EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tianxuIndex = fixIndex(fixEarthlyBranchIndex("woo") + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tianguanIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("wei", "chen", "si", "yin", "mao", "you", "hai", "you", "xu", "woo")[
                HEAVENLY_STEMS.indexOf(heavenlyStem)
            ],
        ),
    )
    val tianfuIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("you", "shen", "zi", "hai", "mao", "yin", "woo", "si", "woo", "si")[
                HEAVENLY_STEMS.indexOf(heavenlyStem)
            ],
        ),
    )
    val tiandeIndex = fixIndex(fixEarthlyBranchIndex("you") + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val yuedeIndex = fixIndex(fixEarthlyBranchIndex("si") + EARTHLY_BRANCHES.indexOf(earthlyBranch))
    val tiankongIndex = fixIndex(fixEarthlyBranchIndex(yearly.second) + 1)
    val jieluIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("shen", "woo", "chen", "yin", "zi")[HEAVENLY_STEMS.indexOf(heavenlyStem) % 5],
        ),
    )
    val kongwangIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("you", "wei", "si", "mao", "chou")[HEAVENLY_STEMS.indexOf(heavenlyStem) % 5],
        ),
    )
    var xunkongIndex = fixIndex(
        fixEarthlyBranchIndex(yearly.second) +
            HEAVENLY_STEMS.indexOf("guiHeavenly") -
            HEAVENLY_STEMS.indexOf(heavenlyStem) + 1,
    )

    val yinyang = EARTHLY_BRANCHES.indexOf(earthlyBranch) % 2

    if (yinyang != xunkongIndex % 2) {
        xunkongIndex = fixIndex(xunkongIndex + 1)
    }

    val jiekongIndex = if (yinyang == 0) jieluIndex else kongwangIndex
    val jieshaAdjIndex = getJieshaAdjIndex(earthlyBranch)
    val nianjieIndex = getNianjieIndex(yearly.second)
    val dahaoAdjIndex = getDahaoIndex(earthlyBranch)
    val tianshiTianshang = getTianshiTianshangIndex(gender!!, earthlyBranch, soulIndex)

    return YearlyStarIndexResult(
        xianchiIndex = huagaiXianchi.xianchiIndex,
        huagaiIndex = huagaiXianchi.huagaiIndex,
        guchenIndex = guGua.guchenIndex,
        guasuIndex = guGua.guasuIndex,
        tiancaiIndex = tiancaiIndex,
        tianshouIndex = tianshouIndex,
        tianchuIndex = tianchuIndex,
        posuiIndex = posuiIndex,
        feilianIndex = feilianIndex,
        longchiIndex = longchiIndex,
        fenggeIndex = fenggeIndex,
        tiankuIndex = tiankuIndex,
        tianxuIndex = tianxuIndex,
        tianguanIndex = tianguanIndex,
        tianfuIndex = tianfuIndex,
        tiandeIndex = tiandeIndex,
        yuedeIndex = yuedeIndex,
        tiankongIndex = tiankongIndex,
        jieluIndex = jieluIndex,
        kongwangIndex = kongwangIndex,
        xunkongIndex = xunkongIndex,
        tianshangIndex = tianshiTianshang.tianshangIndex,
        tianshiIndex = tianshiTianshang.tianshiIndex,
        jiekongIndex = jiekongIndex,
        jieshaAdjIndex = jieshaAdjIndex,
        nianjieIndex = nianjieIndex,
        dahaoAdjIndex = dahaoAdjIndex,
    )
}

fun getTianshiTianshangIndex(
    gender: GenderName,
    earthlyBranch: EarthlyBranchKey,
    soulIndex: Int,
): TianshiTianshangIndexResult {
    val yinyang = EARTHLY_BRANCHES.indexOf(earthlyBranch) % 2
    val algorithm = getConfig().algorithm
    val genderYinyang = arrayOf("male", "female")
    val sameYinyang = yinyang == genderYinyang.indexOf(kot(gender))
    var tianshangIndex = fixIndex(PALACES.indexOf("friendsPalace") + soulIndex)
    var tianshiIndex = fixIndex(PALACES.indexOf("healthPalace") + soulIndex)

    if (algorithm == "zhongzhou" && !sameYinyang) {
        val temp = tianshiIndex
        tianshiIndex = tianshangIndex
        tianshangIndex = temp
    }

    return TianshiTianshangIndexResult(
        tianshangIndex = tianshangIndex,
        tianshiIndex = tianshiIndex,
    )
}

/** 获取年解的索引 */
fun getNianjieIndex(earthlyBranchName: EarthlyBranchName): Int {
    val earthlyBranch = kot<EarthlyBranchKey>(earthlyBranchName, "Earthly")
    return fixIndex(
        fixEarthlyBranchIndex(
            arrayOf(
                "xu", "you", "shen", "wei", "woo", "si",
                "chen", "mao", "yin", "chou", "zi", "hai",
            )[EARTHLY_BRANCHES.indexOf(earthlyBranch)],
        ),
    )
}

/** 获取以月份索引为基准的星耀索引 */
fun getMonthlyStarIndex(
    solarDate: String,
    timeIndex: Int,
    fixLeap: Boolean? = null,
): MonthlyStarIndexResult {
    val monthIndex = fixLunarMonthIndex(solarDate, timeIndex, fixLeap ?: false)

    val jieshenIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("shen", "xu", "zi", "yin", "chen", "woo")[floor(monthIndex / 2.0).toInt()],
        ),
    )
    val tianyaoIndex = fixIndex(fixEarthlyBranchIndex("chou") + monthIndex)
    val tianxingIndex = fixIndex(fixEarthlyBranchIndex("you") + monthIndex)
    val yinshaIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("yin", "zi", "xu", "shen", "woo", "chen")[monthIndex % 6],
        ),
    )
    val tianyueIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf(
                "xu", "si", "chen", "yin", "wei", "mao",
                "hai", "wei", "yin", "woo", "xu", "yin",
            )[monthIndex],
        ),
    )
    val tianwuIndex = fixIndex(
        fixEarthlyBranchIndex(
            arrayOf("si", "shen", "yin", "hai")[monthIndex % 4],
        ),
    )

    return MonthlyStarIndexResult(
        yuejieIndex = jieshenIndex,
        tianyaoIndex = tianyaoIndex,
        tianxingIndex = tianxingIndex,
        yinshaIndex = yinshaIndex,
        tianyueIndex = tianyueIndex,
        tianwuIndex = tianwuIndex,
    )
}

/** 通过 大限/流年 天干获取流昌流曲 */
fun getChangQuIndexByHeavenlyStem(heavenlyStemName: HeavenlyStemName): ChangQuIndexResult {
    var changIndex = -1
    var quIndex = -1
    val heavenlyStem = kot<HeavenlyStemKey>(heavenlyStemName, "Heavenly")

    when (heavenlyStem) {
        "jiaHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("si"))
            quIndex = fixIndex(fixEarthlyBranchIndex("you"))
        }
        "yiHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("woo"))
            quIndex = fixIndex(fixEarthlyBranchIndex("shen"))
        }
        "bingHeavenly", "wuHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("shen"))
            quIndex = fixIndex(fixEarthlyBranchIndex("woo"))
        }
        "dingHeavenly", "jiHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("you"))
            quIndex = fixIndex(fixEarthlyBranchIndex("si"))
        }
        "gengHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("hai"))
            quIndex = fixIndex(fixEarthlyBranchIndex("mao"))
        }
        "xinHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("zi"))
            quIndex = fixIndex(fixEarthlyBranchIndex("yin"))
        }
        "renHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("yin"))
            quIndex = fixIndex(fixEarthlyBranchIndex("zi"))
        }
        "guiHeavenly" -> {
            changIndex = fixIndex(fixEarthlyBranchIndex("mao"))
            quIndex = fixIndex(fixEarthlyBranchIndex("hai"))
        }
    }

    return ChangQuIndexResult(changIndex = changIndex, quIndex = quIndex)
}

typealias HeavenlyStemName = String
typealias EarthlyBranchName = String
typealias HeavenlyStemKey = String
typealias EarthlyBranchKey = String
typealias FiveElementsClassKey = String
typealias GenderName = String

data class StartIndexResult(
    val ziweiIndex: Int,
    val tianfuIndex: Int,
)

data class LuYangTuoMaIndexResult(
    val luIndex: Int,
    val maIndex: Int,
    val yangIndex: Int,
    val tuoIndex: Int,
)

data class KuiYueIndexResult(
    val kuiIndex: Int,
    val yueIndex: Int,
)

data class ZuoYouIndexResult(
    val zuoIndex: Int,
    val youIndex: Int,
)

data class ChangQuIndexResult(
    val changIndex: Int,
    val quIndex: Int,
)

data class DailyStarIndexResult(
    val santaiIndex: Int,
    val bazuoIndex: Int,
    val enguangIndex: Int,
    val tianguiIndex: Int,
)

data class TimelyStarIndexResult(
    val taifuIndex: Int,
    val fenggaoIndex: Int,
)

data class KongJieIndexResult(
    val kongIndex: Int,
    val jieIndex: Int,
)

data class HuoLingIndexResult(
    val huoIndex: Int,
    val lingIndex: Int,
)

data class LuanXiIndexResult(
    val hongluanIndex: Int,
    val tianxiIndex: Int,
)

data class HuagaiXianchiIndexResult(
    val huagaiIndex: Int,
    val xianchiIndex: Int,
)

data class GuGuaIndexResult(
    val guchenIndex: Int,
    val guasuIndex: Int,
)

data class TianshiTianshangIndexResult(
    val tianshangIndex: Int,
    val tianshiIndex: Int,
)

data class MonthlyStarIndexResult(
    val yuejieIndex: Int,
    val tianyaoIndex: Int,
    val tianxingIndex: Int,
    val yinshaIndex: Int,
    val tianyueIndex: Int,
    val tianwuIndex: Int,
)

data class YearlyStarIndexResult(
    val xianchiIndex: Int,
    val huagaiIndex: Int,
    val guchenIndex: Int,
    val guasuIndex: Int,
    val tiancaiIndex: Int,
    val tianshouIndex: Int,
    val tianchuIndex: Int,
    val posuiIndex: Int,
    val feilianIndex: Int,
    val longchiIndex: Int,
    val fenggeIndex: Int,
    val tiankuIndex: Int,
    val tianxuIndex: Int,
    val tianguanIndex: Int,
    val tianfuIndex: Int,
    val tiandeIndex: Int,
    val yuedeIndex: Int,
    val tiankongIndex: Int,
    val jieluIndex: Int,
    val kongwangIndex: Int,
    val xunkongIndex: Int,
    val tianshangIndex: Int,
    val tianshiIndex: Int,
    val jiekongIndex: Int,
    val jieshaAdjIndex: Int,
    val nianjieIndex: Int,
    val dahaoAdjIndex: Int,
)
