package zhiqiu.iztro

import zhiqiu.iztro.astro.bySolar
import zhiqiu.iztro.astro.getPalaceNames as astroGetPalaceNames
import zhiqiu.iztro.astro.horoscope
import zhiqiu.iztro.astro.surroundedIndices
import zhiqiu.iztro.astro.withOptions
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.StemBranchOptions
import zhiqiu.iztro.data.heavenlyStems
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.model.Config
import zhiqiu.iztro.model.Horoscope
import zhiqiu.iztro.model.HoroscopeItem
import zhiqiu.iztro.model.Option
import zhiqiu.iztro.model.Palace
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.utils.fixEarthlyBranchIndex
import zhiqiu.iztro.utils.getMutagensByHeavenlyStem

internal object AstrolabeBridge {
    fun createDemoChart(input: IztroInput): DemoChart {
        val option = Option(
            type = input.birthdayType,
            dateStr = input.birthday,
            timeIndex = input.birthTime,
            gender = input.gender,
            isLeapMonth = input.isLeapMonth,
            fixLeap = input.fixLeap,
            language = input.lang,
            config = input.options?.toCoreConfig(),
            astroType = input.astroType,
        )
        return withOptions(option).toDemoChart(input.lang, input.name)
    }

    fun computeHoroscope(chart: DemoChart, targetDate: String, horoscopeHour: Int): DemoHoroscope {
        val astrolabe = bySolar(chart.solarDate, chart.timeIndex, chart.gender, fixLeap = true, chart.lang)
        return astrolabe.horoscope(targetDate, horoscopeHour).toDemoHoroscope()
    }

    fun surroundedPalaceIndices(index: Int): Set<Int> = surroundedIndices(index)

    fun getPalaceNames(fromIndex: Int): List<String> = astroGetPalaceNames(fromIndex)

    fun getMutagensByStem(stemDisplayName: String): List<String> {
        val key = kot<String>(stemDisplayName, "Heavenly")
        return getMutagensByHeavenlyStem(key)
    }

    fun defaultLineIndex(earthlyBranchOfSoulPalace: String): Int =
        fixEarthlyBranchIndex(earthlyBranchOfSoulPalace)
}

private fun IztroConfig.toCoreConfig() = Config(
    mutagens = mutagens,
    brightness = brightness,
    yearDivide = yearDivide,
    horoscopeDivide = horoscopeDivide,
    ageDivide = ageDivide,
    dayDivide = dayDivide,
    algorithm = algorithm,
)

private fun zhiqiu.iztro.model.Astrolabe.toDemoChart(lang: String, name: String): DemoChart {
    // 节气四柱：立春换年 + 节气换月
    val jieqi = Calendar.getHeavenlyStemAndEarthlyBranchBySolarDate(
        solarDate,
        timeIndex,
        StemBranchOptions(year = "exact", month = "exact"),
    )
    return DemoChart(
        solarDate = solarDate,
        lunarDate = lunarDate,
        rawLunar = rawDates.lunarDate.toDemoLunarRaw(),
        chineseDate = chineseDate,
        pillars = listOf(
            DemoFourPillar(rawDates.chineseDate.yearly.first, rawDates.chineseDate.yearly.second),
            DemoFourPillar(rawDates.chineseDate.monthly.first, rawDates.chineseDate.monthly.second),
            DemoFourPillar(rawDates.chineseDate.daily.first, rawDates.chineseDate.daily.second),
            DemoFourPillar(rawDates.chineseDate.hourly.first, rawDates.chineseDate.hourly.second),
        ),
        jieqiPillars = listOf(
            DemoFourPillar(jieqi.yearly.first, jieqi.yearly.second),
            DemoFourPillar(jieqi.monthly.first, jieqi.monthly.second),
            DemoFourPillar(jieqi.daily.first, jieqi.daily.second),
            DemoFourPillar(jieqi.hourly.first, jieqi.hourly.second),
        ),
        fiveElementsClass = fiveElementsClass,
        soul = soul,
        body = body,
        zodiac = zodiac,
        sign = sign,
        time = time,
        timeRange = timeRange,
        timeIndex = timeIndex,
        gender = gender,
        genderLabel = buildGenderLabel(gender, jieqi.yearly.first),
        earthlyBranchOfSoulPalace = earthlyBranchOfSoulPalace,
        earthlyBranchOfBodyPalace = earthlyBranchOfBodyPalace,
        name = name,
        copyright = copyright,
        lang = lang,
        palaces = palaces.map { it.toDemoPalace() },
    )
}
/** 按生年天干阴阳 + 性别 → 阳男/阴男/阳女/阴女 */
private fun buildGenderLabel(gender: String, yearStem: String): String {
    val stemKey = kot<String>(yearStem, "Heavenly")
    val yang = heavenlyStems[stemKey]?.yinYang == "阳"
    val male = gender == "男" || gender.equals("male", ignoreCase = true)
    return when {
        male && yang -> "阳男"
        male && !yang -> "阴男"
        !male && yang -> "阳女"
        else -> "阴女"
    }
}

private fun zhiqiu.iztro.calendar.LunarDate.toDemoLunarRaw() = DemoLunarRaw(
    lunarYear = lunarYear,
    lunarMonth = lunarMonth,
    lunarDay = lunarDay,
    isLeap = isLeap,
)

private fun Palace.toDemoPalace() = DemoPalace(
    index = index,
    name = name,
    heavenlyStem = heavenlyStem,
    earthlyBranch = earthlyBranch,
    isBodyPalace = isBodyPalace,
    majorStars = majorStars.map { it.toDemoStar() },
    minorStars = minorStars.map { it.toDemoStar() },
    adjectiveStars = adjectiveStars.map { it.toDemoStar() },
    changsheng12 = changsheng12,
    boshi12 = boshi12,
    jiangqian12 = jiangqian12,
    suiqian12 = suiqian12,
    ages = ages,
    decadalStart = decadal.range.first,
    decadalEnd = decadal.range.second,
)

private fun Star.toDemoStar() = DemoStar(name, type, scope, brightness, mutagen)

private fun Horoscope.toDemoHoroscope(): DemoHoroscope {
    val targetLunar = Calendar.solar2lunar(solarDate)
    return DemoHoroscope(
        solarDate = solarDate,
        lunarDate = lunarDate,
        rawLunar = targetLunar.toDemoLunarRaw(),
        decadal = decadal.toDemoItem(),
        age = age.toDemoItem(),
        yearly = yearly.toDemoItem(),
        monthly = monthly.toDemoItem(),
        daily = daily.toDemoItem(),
        hourly = hourly.toDemoItem(),
    )
}

private fun HoroscopeItem.toDemoItem() = DemoHoroscopeItem(
    index = index,
    name = name,
    heavenlyStem = heavenlyStem,
    earthlyBranch = earthlyBranch,
    palaceNames = palaceNames,
    mutagen = mutagen,
    stars = stars.map { row -> row.map { it.toDemoStar() } },
    nominalAge = nominalAge,
    suiqian12 = yearlyDecStar?.suiqian12,
    jiangqian12 = yearlyDecStar?.jiangqian12,
)
