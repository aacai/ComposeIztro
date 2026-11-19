package zhiqiu.iztro

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import zhiqiu.iztro.astro.bySolar
import zhiqiu.iztro.astro.config
import zhiqiu.iztro.astro.horoscope
import zhiqiu.iztro.astro.getConfig
import zhiqiu.iztro.astro.withOptions
import zhiqiu.iztro.data.STARS_INFO
import zhiqiu.iztro.model.Astrolabe
import zhiqiu.iztro.model.Config
import zhiqiu.iztro.model.Option

class AstroTest {
    @Test
    fun bySolar_basicCase() {
        val chart = bySolar("2000-8-16", 2, "女", fixLeap = true, language = "zh-CN")
        assertEquals("2000-8-16", chart.solarDate)
        assertEquals(12, chart.palaces.size)
        assertTrue(chart.palaces.any { it.name == "命宫" })
        assertTrue(chart.palaces.any { it.majorStars.isNotEmpty() })
        assertEquals("龙", chart.zodiac)
        assertTrue(chart.fiveElementsClass.isNotEmpty())
        assertTrue(chart.chineseDate.isNotEmpty())
    }

    @Test
    fun withOptions_earthAndHuman_doNotCrash() {
        val heaven = withOptions(
            Option(
                type = "solar",
                dateStr = "2000-8-16",
                timeIndex = 2,
                gender = "女",
                language = "zh-CN",
                config = Config(algorithm = "zhongzhou"),
                astroType = "heaven",
            ),
        )
        val earth = withOptions(
            Option(
                type = "solar",
                dateStr = "2000-8-16",
                timeIndex = 2,
                gender = "女",
                language = "zh-CN",
                config = Config(algorithm = "zhongzhou"),
                astroType = "earth",
            ),
        )
        val human = withOptions(
            Option(
                type = "solar",
                dateStr = "2000-8-16",
                timeIndex = 2,
                gender = "女",
                language = "zh-CN",
                config = Config(algorithm = "zhongzhou"),
                astroType = "human",
            ),
        )
        assertEquals(12, earth.palaces.size)
        assertEquals(12, human.palaces.size)
        assertTrue(earth.palaces.any { it.isBodyPalace })
        assertTrue(
            earth.fiveElementsClass != heaven.fiveElementsClass ||
                earth.earthlyBranchOfSoulPalace != heaven.earthlyBranchOfSoulPalace,
        )
        assertNotEquals(human.earthlyBranchOfSoulPalace, heaven.earthlyBranchOfSoulPalace)
    }

    @Test
    fun lateRatHour_switchesWholeChartToNextDay() {
        config(Config())
        val late = bySolar("1996-8-13", 12, "女", language = "zh-CN")
        val early = bySolar("1996-8-14", 0, "女", language = "zh-CN")

        assertEquals("1996-8-13", late.solarDate)
        assertEquals(12, late.timeIndex)
        assertEquals("晚子时", late.time)
        assertEquals("农历丙子年七月初一", late.lunarDate)
        assertEquals("丙子 丙申 癸未 壬子", late.chineseDate)
        assertEquals("申", late.earthlyBranchOfSoulPalace)
        assertEquals("申", late.earthlyBranchOfBodyPalace)
        assertEquals(early.chineseDate, late.chineseDate)
        assertEquals(early.lunarDate, late.lunarDate)
        assertEquals(early.fiveElementsClass, late.fiveElementsClass)
        assertEquals(early.palaces, late.palaces)

        val yearLate = bySolar("2020-1-24", 12, "女", language = "zh-CN")
        val yearEarly = bySolar("2020-1-25", 0, "女", language = "zh-CN")
        assertEquals(yearEarly.chineseDate, yearLate.chineseDate)
        assertEquals(yearEarly.lunarDate, yearLate.lunarDate)
        assertEquals(yearEarly.palaces, yearLate.palaces)
    }

    @Test
    fun zhongzhouEarthAndHuman_keepBirthBodyPalace() {
        config(Config())
        fun chart(astroType: String): Astrolabe = withOptions(
            Option(
                type = "solar",
                dateStr = "1979-8-21",
                timeIndex = 8,
                gender = "男",
                language = "zh-CN",
                config = Config(algorithm = "zhongzhou"),
                astroType = astroType,
            ),
        )
        val heaven = chart("heaven")
        val earth = chart("earth")
        val human = chart("human")

        assertEquals("辰", heaven.earthlyBranchOfBodyPalace)
        assertEquals(heaven.earthlyBranchOfBodyPalace, earth.earthlyBranchOfBodyPalace)
        assertEquals(heaven.earthlyBranchOfBodyPalace, human.earthlyBranchOfBodyPalace)
        assertEquals(1, earth.palaces.count { it.isBodyPalace })
        assertEquals(1, human.palaces.count { it.isBodyPalace })
    }

    @Test
    fun nominalAge_advancesAfterBirthdayInLaterLunarYears() {
        val previous = getConfig()
        config(Config(ageDivide = "birthday"))
        val chart = bySolar("2000-8-16", 2, "女", language = "zh-CN")
        assertEquals(23, chart.horoscope("2023-9-1").age.nominalAge)
        assertEquals(24, chart.horoscope("2023-9-2").age.nominalAge)
        assertEquals(24, chart.horoscope("2023-9-16").age.nominalAge)
        config(previous)
    }

    @Test
    fun youPalaceBrightness_matchesUpstream() {
        assertEquals("ping", STARS_INFO["taiyangMaj"]!!.brightness!![7])
        assertEquals("wang", STARS_INFO["taiyinMaj"]!!.brightness!![7])
        assertEquals("wang", STARS_INFO["qishaMaj"]!!.brightness!![7])
    }
}
