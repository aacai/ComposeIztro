package zhiqiu.iztro.astro

import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Astrolabe
import zhiqiu.iztro.model.Horoscope
import zhiqiu.iztro.model.HoroscopeItem
import zhiqiu.iztro.model.Yearly12
import zhiqiu.iztro.star.getHoroscopeStar
import zhiqiu.iztro.star.getYearly12
import zhiqiu.iztro.utils.fixEarthlyBranchIndex
import zhiqiu.iztro.utils.fixIndex
import zhiqiu.iztro.utils.getMutagensByHeavenlyStem
import zhiqiu.iztro.utils.timeToIndex

fun Astrolabe.horoscope(targetSolarDate: String, timeIndex: Int? = null): Horoscope {
    val birthLunar = rawDates.lunarDate
    val targetLunar = Calendar.solar2lunar(targetSolarDate)
    val targetTimeIndex = timeIndex ?: timeToIndex(parseHour(targetSolarDate))
    val stemBranch = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        targetSolarDate,
        targetTimeIndex,
        StemBranchOptions(year = getConfig().horoscopeDivide, month = getConfig().horoscopeDivide),
    )

    var nominalAge = targetLunar.lunarYear - birthLunar.lunarYear
    var isChildhood = false
    if (getConfig().ageDivide == "birthday") {
        val addYear = (targetLunar.lunarYear == birthLunar.lunarYear &&
            targetLunar.lunarMonth == birthLunar.lunarMonth &&
            targetLunar.lunarDay > birthLunar.lunarDay) ||
            targetLunar.lunarMonth > birthLunar.lunarMonth
        if (addYear) nominalAge += 1
    } else {
        nominalAge += 1
    }

    var decadalIndex = -1
    var heavenlyStemOfDecade = "jiaHeavenly"
    var earthlyBranchOfDecade = "ziEarthly"
    var ageIndex = -1
    var heavenlyStemOfAge = "jiaHeavenly"
    var earthlyBranchOfAge = "ziEarthly"

    val yearlyIndex = fixEarthlyBranchIndex(stemBranch.yearly.second)

    for ((index, palace) in palaces.withIndex()) {
        if (decadalIndex < 0 && nominalAge in palace.decadal.range.first..palace.decadal.range.second) {
            decadalIndex = index
            heavenlyStemOfDecade = kot(palace.decadal.heavenlyStem, "Heavenly")
            earthlyBranchOfDecade = kot(palace.decadal.earthlyBranch, "Earthly")
        }
        if (ageIndex < 0 && nominalAge in palace.ages) {
            ageIndex = index
            heavenlyStemOfAge = kot(palace.heavenlyStem, "Heavenly")
            earthlyBranchOfAge = kot(palace.earthlyBranch, "Earthly")
        }
    }

    if (decadalIndex < 0) {
        val childhoodPalaces = listOf("命宫", "财帛", "疾厄", "夫妻", "福德", "官禄")
        val targetName = childhoodPalaces.getOrNull(nominalAge - 1)
        val targetPalace = targetName?.let { name -> palaces.firstOrNull { it.name == name } }
        if (targetPalace != null) {
            isChildhood = true
            decadalIndex = targetPalace.index
            heavenlyStemOfDecade = kot(targetPalace.heavenlyStem, "Heavenly")
            earthlyBranchOfDecade = kot(targetPalace.earthlyBranch, "Earthly")
        }
    }

    val birthHourBranchKey = rawDates.chineseDate.hourly.second
    val leapAddition = if (birthLunar.isLeap && birthLunar.lunarDay > 15) 1 else 0
    val dateLeapAddition = if (targetLunar.isLeap && targetLunar.lunarDay > 15) 1 else 0
    val monthlyIndex = fixIndex(
        yearlyIndex -
            (birthLunar.lunarMonth + leapAddition) +
            EARTHLY_BRANCHES.indexOf(birthHourBranchKey) +
            (targetLunar.lunarMonth + dateLeapAddition),
    )
    val dailyIndex = fixIndex(monthlyIndex + targetLunar.lunarDay - 1)
    val hourlyIndex = fixIndex(dailyIndex + EARTHLY_BRANCHES.indexOf(stemBranch.hourly.second))

    fun item(
        index: Int,
        name: String,
        heavenlyStemKey: String,
        earthlyBranchKey: String,
        scope: String,
        nominalAgeValue: Int? = null,
        yearlyDecStar: Yearly12? = null,
    ): HoroscopeItem {
        val palaceNames = getPalaceNames(index)
        return HoroscopeItem(
            index = index,
            name = name,
            heavenlyStem = t(heavenlyStemKey),
            earthlyBranch = t(earthlyBranchKey),
            palaceNames = palaceNames,
            mutagen = getMutagensByHeavenlyStem(heavenlyStemKey),
            stars = getHoroscopeStar(heavenlyStemKey, earthlyBranchKey, scope),
            nominalAge = nominalAgeValue,
            yearlyDecStar = yearlyDecStar,
        )
    }

    val yearly12 = getYearly12(targetSolarDate)
    return Horoscope(
        solarDate = normalizeSolarDate(targetSolarDate),
        lunarDate = targetLunar.format(useChinese = true),
        decadal = item(
            decadalIndex,
            if (isChildhood) t("childhood") else t("decadal"),
            heavenlyStemOfDecade,
            earthlyBranchOfDecade,
            "decadal",
        ),
        age = HoroscopeItem(
            index = ageIndex,
            name = t("turn"),
            heavenlyStem = t(heavenlyStemOfAge),
            earthlyBranch = t(earthlyBranchOfAge),
            palaceNames = getPalaceNames(ageIndex),
            mutagen = getMutagensByHeavenlyStem(heavenlyStemOfAge),
            stars = emptyList(),
            nominalAge = nominalAge,
        ),
        yearly = item(
            yearlyIndex,
            t("yearly"),
            stemBranch.yearly.first,
            stemBranch.yearly.second,
            "yearly",
            yearlyDecStar = Yearly12(yearly12.suiqian12, yearly12.jiangqian12),
        ),
        monthly = item(
            monthlyIndex,
            t("monthly"),
            stemBranch.monthly.first,
            stemBranch.monthly.second,
            "monthly",
        ),
        daily = item(
            dailyIndex,
            t("daily"),
            stemBranch.daily.first,
            stemBranch.daily.second,
            "daily",
        ),
        hourly = item(
            hourlyIndex,
            t("hourly"),
            stemBranch.hourly.first,
            stemBranch.hourly.second,
            "hourly",
        ),
    )
}

/** 获取三方四正宫位索引：本宫、对宫(+6)、财帛(+8)、官禄(+4) */
fun surroundedIndices(index: Int): Set<Int> = setOf(
    index,
    fixIndex(index + 6),
    fixIndex(index + 8),
    fixIndex(index + 4),
)

private fun normalizeSolarDate(dateStr: String): String {
    val parts = dateStr.trim().split(Regex("[\\sT-]")).filter { it.isNotEmpty() }
    if (parts.size < 3) return dateStr
    return "${parts[0]}-${parts[1].toInt()}-${parts[2].toInt()}"
}

private fun parseHour(dateStr: String): Int {
    val timePart = dateStr.trim().split(Regex("\\s+")).getOrNull(1) ?: return 0
    val segments = timePart.split(":")
    return segments.firstOrNull()?.toIntOrNull() ?: 0
}
