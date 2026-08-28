package zhiqiu.iztro

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import zhiqiu.iztro.astro.bySolar
import zhiqiu.iztro.astro.withOptions
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
}
