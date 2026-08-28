package zhiqiu.iztro

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BirthStepRecalcTest {
    @Test
    fun dayChangeUpdatesSolarAndPillars() {
        val a = createDemoChart(IztroInput(birthday = "2000-8-16", birthTime = 2, gender = "女"))
        val b = createDemoChart(IztroInput(birthday = "2000-8-17", birthTime = 2, gender = "女"))
        assertNotEquals(a.solarDate, b.solarDate)
        assertNotEquals(a.chineseDate, b.chineseDate)
        assertNotEquals(a.trueSolarTimeText, b.trueSolarTimeText)
    }

    @Test
    fun hourChangeUpdatesTimeAndHourPillar() {
        val a = createDemoChart(IztroInput(birthday = "2000-8-16", birthTime = 2, gender = "女"))
        val c = createDemoChart(IztroInput(birthday = "2000-8-16", birthTime = 5, gender = "女"))
        assertNotEquals(a.timeIndex, c.timeIndex)
        assertNotEquals(a.time, c.time)
        assertNotEquals(a.trueSolarTimeText, c.trueSolarTimeText)
        assertTrue(a.jieqiPillars.last() != c.jieqiPillars.last() || a.chineseDate != c.chineseDate)
    }

    @Test
    fun yearChangeUpdatesDecadalAndAges() {
        val a = createDemoChart(IztroInput(birthday = "2000-8-16", birthTime = 2, gender = "女"))
        val y = createDemoChart(IztroInput(birthday = "1990-8-16", birthTime = 2, gender = "女"))
        assertNotEquals(a.palaces[0].decadalRangeText, y.palaces[0].decadalRangeText)
        assertNotEquals(a.palaces[0].ages, y.palaces[0].ages)
    }
}
