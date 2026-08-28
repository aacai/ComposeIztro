package zhiqiu.iztro.astro

import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.data.FiveElementsClass
import zhiqiu.iztro.data.GENDER
import zhiqiu.iztro.data.HEAVENLY_STEMS
import zhiqiu.iztro.data.PALACES
import zhiqiu.iztro.data.TIGER_RULE
import zhiqiu.iztro.data.earthlyBranches
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.model.Decadal
import zhiqiu.iztro.model.SoulAndBody
import zhiqiu.iztro.utils.fixEarthlyBranchIndex
import zhiqiu.iztro.utils.fixIndex
import zhiqiu.iztro.utils.fixLunarMonthIndex
import zhiqiu.iztro.utils.getAgeIndex

fun getSoulAndBody(param: AstrolabeParam): SoulAndBody {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val fixLeap = param.fixLeap
    val from = param.from

    val chineseDate = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate, timeIndex,
        StemBranchOptions(year = getConfig().yearDivide, month = getConfig().horoscopeDivide),
    )
    val earthlyBranchOfTime = kot<String>(chineseDate.hourly.second, "Earthly")
    val heavenlyStemOfYear = kot<String>(chineseDate.yearly.first, "Heavenly")
    val firstIndex = EARTHLY_BRANCHES.indexOf("yinEarthly")
    val monthIndex = fixLunarMonthIndex(solarDate, timeIndex, fixLeap)

    var soulIndex = fixIndex(monthIndex - EARTHLY_BRANCHES.indexOf(earthlyBranchOfTime))
    var bodyIndex = fixIndex(monthIndex + EARTHLY_BRANCHES.indexOf(earthlyBranchOfTime))

    if (from != null) {
        soulIndex = fixEarthlyBranchIndex(from.earthlyBranch)
        val bodyOffset = listOf(0, 2, 4, 6, 8, 10, 0, 2, 4, 6, 8, 10, 0)
        bodyIndex = fixIndex(bodyOffset[timeIndex] + soulIndex)
    }

    val startHeavenlyStem = TIGER_RULE[heavenlyStemOfYear]!!
    val heavenlyStemOfSoulIndex = fixIndex(HEAVENLY_STEMS.indexOf(startHeavenlyStem) + soulIndex, 10)
    val heavenlyStemOfSoul = t<String>(HEAVENLY_STEMS[heavenlyStemOfSoulIndex])
    val earthlyBranchOfSoul = t<String>(EARTHLY_BRANCHES[fixIndex(soulIndex + firstIndex)])

    return SoulAndBody(soulIndex, bodyIndex, heavenlyStemOfSoul, earthlyBranchOfSoul)
}

fun getFiveElementsClass(heavenlyStemName: String, earthlyBranchName: String): String {
    val fiveElementsTable = listOf("wood3rd", "metal4th", "water2nd", "fire6th", "earth5th")
    val heavenlyStem = kot<String>(heavenlyStemName, "Heavenly")
    val earthlyBranch = kot<String>(earthlyBranchName, "Earthly")
    val heavenlyStemNumber = HEAVENLY_STEMS.indexOf(heavenlyStem) / 2 + 1
    val earthlyBranchNumber = fixIndex(EARTHLY_BRANCHES.indexOf(earthlyBranch), 6) / 2 + 1
    var index = heavenlyStemNumber + earthlyBranchNumber
    while (index > 5) index -= 5
    return t(fiveElementsTable[index - 1])
}

fun getPalaceNames(fromIndex: Int): List<String> {
    return PALACES.indices.map { i ->
        t(PALACES[fixIndex(i - fromIndex)])
    }
}

fun getHoroscope(param: AstrolabeParam): HoroscopeResult {
    val solarDate = param.solarDate
    val timeIndex = param.timeIndex
    val gender = param.gender ?: throw IllegalArgumentException("gender is required")
    val from = param.from

    val decadals = Array<Decadal?>(12) { null }
    val genderKey = kot<String>(gender)
    val chineseDate = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate, timeIndex, StemBranchOptions(year = getConfig().yearDivide),
    )
    val heavenlyStem = kot<String>(chineseDate.yearly.first, "Heavenly")
    val earthlyBranch = kot<String>(chineseDate.yearly.second, "Earthly")
    val soulAndBody = getSoulAndBody(param)
    val fiveElementsClass = kot<String>(
        getFiveElementsClass(
            from?.heavenlyStem ?: soulAndBody.heavenlyStemOfSoul,
            from?.earthlyBranch ?: soulAndBody.earthlyBranchOfSoul,
        ),
    )
    val startHeavenlyStem = TIGER_RULE[heavenlyStem]!!

    for (i in 0 until 12) {
        val idx = if (GENDER[genderKey] == earthlyBranches[earthlyBranch]!!.yinYang) {
            fixIndex(soulAndBody.soulIndex + i)
        } else {
            fixIndex(soulAndBody.soulIndex - i)
        }
        val start = FiveElementsClass.fromKey(fiveElementsClass).value + 10 * i
        val heavenlyStemIndex = fixIndex(HEAVENLY_STEMS.indexOf(startHeavenlyStem) + idx, 10)
        val earthlyBranchIndex = fixIndex(EARTHLY_BRANCHES.indexOf("yinEarthly") + idx)
        decadals[idx] = Decadal(
            range = start to (start + 9),
            heavenlyStem = t(HEAVENLY_STEMS[heavenlyStemIndex]),
            earthlyBranch = t(EARTHLY_BRANCHES[earthlyBranchIndex]),
        )
    }

    val ageIdx = getAgeIndex(chineseDate.yearly.second)
    val ages = Array<List<Int>?>(12) { null }
    for (i in 0 until 12) {
        val age = (0 until 10).map { j -> 12 * j + i + 1 }
        val idx = if (genderKey == "male") fixIndex(ageIdx + i) else fixIndex(ageIdx - i)
        ages[idx] = age
    }

    return HoroscopeResult(decadals.map { it!! }, ages.map { it!! })
}

data class HoroscopeResult(
    val decadals: List<Decadal>,
    val ages: List<List<Int>>,
)
