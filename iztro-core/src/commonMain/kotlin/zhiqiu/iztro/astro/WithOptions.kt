package zhiqiu.iztro.astro

import zhiqiu.iztro.data.EARTHLY_BRANCHES
import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.i18n.setLanguage
import zhiqiu.iztro.i18n.t
import zhiqiu.iztro.model.Astrolabe
import zhiqiu.iztro.model.AstrolabeParam
import zhiqiu.iztro.model.Option
import zhiqiu.iztro.model.Palace
import zhiqiu.iztro.model.Star
import zhiqiu.iztro.model.StemBranchFrom
import zhiqiu.iztro.star.getMajorStar
import zhiqiu.iztro.star.getTianshiTianshangIndex
import zhiqiu.iztro.star.getchangsheng12
import zhiqiu.iztro.utils.fixIndex

fun Astrolabe.palace(nameOrIndex: String): Palace? {
    // 对齐 iztro analyzer.getPalace：身宫不是十二宫名，而是 isBodyPalace 标记
    val key = kot<String>(nameOrIndex)
    if (key == "bodyPalace" || nameOrIndex == t<String>("bodyPalace") || nameOrIndex == "身宫") {
        return palaces.find { it.isBodyPalace }
    }
    return palaces.find { it.name == nameOrIndex }
        ?: palaces.find { kot<String>(it.name) == key }
        ?: palaces.find { it.name == t<String>(nameOrIndex) }
}

fun Astrolabe.palace(index: Int): Palace? = palaces.getOrNull(index)

fun withOptions(option: Option): Astrolabe {
    option.config?.let { config(it) }
    option.language?.let { setLanguage(it) }

    val result = when (option.type) {
        "lunar" -> byLunar(
            option.dateStr,
            option.timeIndex,
            option.gender,
            option.isLeapMonth,
            option.fixLeap,
            option.language,
        )
        else -> bySolar(
            option.dateStr,
            option.timeIndex,
            option.gender,
            option.fixLeap,
            option.language,
        )
    }

    return when (option.astroType) {
        "earth" -> {
            val bodyPalace = result.palace("身宫")
                ?: result.palaces.find { it.isBodyPalace }
                ?: error("身宫 not found")
            rearrangeAstrolabe(
                result,
                StemBranchFrom(bodyPalace.heavenlyStem, bodyPalace.earthlyBranch),
                option,
            )
        }
        "human" -> {
            val spiritPalace = result.palace("福德")
                ?: result.palace("spiritPalace")
                ?: error("福德宫 not found")
            rearrangeAstrolabe(
                result,
                StemBranchFrom(spiritPalace.heavenlyStem, spiritPalace.earthlyBranch),
                option,
            )
        }
        else -> result
    }
}

private fun rearrangeAstrolabe(
    astrolabe: Astrolabe,
    from: StemBranchFrom,
    option: Option,
): Astrolabe {
    val dayDivide = getConfig().dayDivide
    var tIndex = option.timeIndex
    if (dayDivide == "current" && tIndex >= 12) {
        tIndex = 0
    }

    val genderKey = kot<String>(astrolabe.gender)
    val param = AstrolabeParam(
        solarDate = astrolabe.solarDate,
        timeIndex = tIndex,
        fixLeap = option.fixLeap,
        gender = genderKey,
        from = from,
    )
    val soulAndBody = getSoulAndBody(param)
    val soulIndex = soulAndBody.soulIndex
    val bodyIndex = soulAndBody.bodyIndex
    val fiveElementsClass = getFiveElementsClass(from.heavenlyStem, from.earthlyBranch)
    val palaceNames = getPalaceNames(soulIndex)
    val majorStars = getMajorStar(param)
    val changsheng12 = getchangsheng12(param)
    val horoscope = getHoroscope(param)

    val yearlyBranch = kot<String>(astrolabe.rawDates.chineseDate.yearly.second, "Earthly")
    val tianshiTianshang = getTianshiTianshangIndex(genderKey, yearlyBranch, soulIndex)
    val tiancaiIndex = fixIndex(soulIndex + EARTHLY_BRANCHES.indexOf(yearlyBranch))

    val updatedPalaces = astrolabe.palaces.mapIndexed { i, palace ->
        var adjectiveStars = palace.adjectiveStars.toMutableList()

        fun adjustStar(starKey: String, targetIndex: Int) {
            val existingIdx = adjectiveStars.indexOfFirst { kot<String>(it.name) == starKey }
            if (existingIdx != -1 && targetIndex != i) {
                adjectiveStars.removeAt(existingIdx)
            } else if (existingIdx == -1 && targetIndex == i) {
                adjectiveStars.add(
                    Star(name = t(starKey), type = "adjective", scope = "origin"),
                )
            }
        }

        adjustStar("tianshang", tianshiTianshang.tianshangIndex)
        adjustStar("tianshi", tianshiTianshang.tianshiIndex)
        adjustStar("tiancai", tiancaiIndex)

        palace.copy(
            name = palaceNames[i],
            majorStars = majorStars[i],
            adjectiveStars = adjectiveStars,
            changsheng12 = changsheng12[i],
            decadal = horoscope.decadals[i],
            ages = horoscope.ages[i],
            isBodyPalace = bodyIndex == i,
        )
    }

    return astrolabe.copy(
        fiveElementsClass = fiveElementsClass,
        palaces = updatedPalaces,
        earthlyBranchOfSoulPalace = t(EARTHLY_BRANCHES[fixIndex(soulIndex + 2)]),
    )
}
