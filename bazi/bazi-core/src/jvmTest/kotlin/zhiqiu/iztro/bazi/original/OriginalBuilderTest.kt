package zhiqiu.iztro.bazi.original

import com.tyme.solar.SolarTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OriginalBuilderTest {
    @Test
    fun referenceChart_2002_12_14_yinHour() {
        // 参考图：约 2002-12-14 03:09 → 寅时
        val eightChar = SolarTime(2002, 12, 14, 3, 9, 0).getLunarHour().getEightChar()
        assertEquals("壬午", eightChar.getYear().getName())
        assertEquals("壬子", eightChar.getMonth().getName())
        assertEquals("丙辰", eightChar.getDay().getName())
        assertEquals("庚寅", eightChar.getHour().getName())

        val chart = OriginalBuilder.build(
            eightChar = eightChar,
            gender = "女",
            solarLabel = "test",
            termLabel = "test",
        )
        assertEquals(4, chart.pillars.size)
        assertEquals("女主", chart.pillars[2].stemGod)

        assertEquals(listOf("丁", "己"), chart.pillars[0].hideStems.map { it.stem })
        assertEquals(listOf("癸"), chart.pillars[1].hideStems.map { it.stem })
        assertEquals(listOf("戊", "乙", "癸"), chart.pillars[2].hideStems.map { it.stem })
        assertEquals(listOf("甲", "丙", "戊"), chart.pillars[3].hideStems.map { it.stem })

        assertEquals("申酉", chart.pillars[0].kongWang)
        assertEquals("寅卯", chart.pillars[1].kongWang)
        assertEquals("子丑", chart.pillars[2].kongWang)
        assertEquals("午未", chart.pillars[3].kongWang)

        assertEquals("帝旺", chart.pillars[0].diShi)
        assertEquals("胎", chart.pillars[1].diShi)
        assertEquals("冠带", chart.pillars[2].diShi)
        assertEquals("长生", chart.pillars[3].diShi)

        assertTrue(chart.pillars.any { it.shenSha.isNotEmpty() })
        assertTrue("子午相冲" in chart.branchRelations, chart.branchRelations)
        assertTrue("午子" !in chart.branchRelations, chart.branchRelations)
        assertTrue("丙壬相冲" in chart.stemRelations, chart.stemRelations)
        // 神煞应明显多于基础集
        val shenCount = chart.pillars.sumOf { it.shenSha.size }
        assertTrue(shenCount >= 8, "shenSha too few: $shenCount ${chart.pillars.map { it.shenSha }}")
    }

    @Test
    fun birthTermLabel_usesExactDurationNotDayIndex() {
        val birth = SolarTime(2002, 12, 14, 3, 9, 0)
        val label = formatBirthTermLabel(birth)
        assertTrue(label.startsWith("出生节气：出生于大雪("), label)
        assertTrue("后6天11小时" in label, label)
        assertTrue(Regex("""\d{4}\.\d{2}\.\d{2} \d{2}:\d{2}""").containsMatchIn(label), label)
    }

    @Test
    fun branchRelations_includeXingHaiAnHe() {
        val chart = OriginalBuilder.build(
            eightChar = com.tyme.eightchar.EightChar("甲子", "乙丑", "丙午", "丁亥"),
            gender = "男",
            solarLabel = "t",
            termLabel = "t",
        )
        assertTrue("丑午相害" in chart.branchRelations, chart.branchRelations)
        assertTrue("午亥暗合" in chart.branchRelations, chart.branchRelations)
        assertTrue("子午相冲" in chart.branchRelations, chart.branchRelations)

        val xing = OriginalBuilder.build(
            eightChar = com.tyme.eightchar.EightChar("甲子", "丁卯", "戊寅", "癸巳"),
            gender = "女",
            solarLabel = "t",
            termLabel = "t",
        )
        assertTrue("子卯相刑" in xing.branchRelations, xing.branchRelations)
        assertTrue("寅巳相刑" in xing.branchRelations, xing.branchRelations)
    }

    @Test
    fun stemKe_sameYinYangOnly() {
        // 丁克辛（阴火克阴金）；丁不克庚（阴阳不同）
        val dingXin = OriginalBuilder.build(
            eightChar = com.tyme.eightchar.EightChar("甲子", "丁丑", "戊寅", "辛卯"),
            gender = "男",
            solarLabel = "t",
            termLabel = "t",
        )
        assertTrue("丁克辛" in dingXin.stemRelations, dingXin.stemRelations)

        val dingGeng = OriginalBuilder.build(
            eightChar = com.tyme.eightchar.EightChar("甲子", "丁丑", "戊寅", "庚辰"),
            gender = "男",
            solarLabel = "t",
            termLabel = "t",
        )
        assertTrue("丁克庚" !in dingGeng.stemRelations, dingGeng.stemRelations)
        assertTrue("庚克丁" !in dingGeng.stemRelations, dingGeng.stemRelations)

        // 丙克庚（阳火克阳金）
        val bingGeng = OriginalBuilder.build(
            eightChar = com.tyme.eightchar.EightChar("甲子", "丙寅", "戊辰", "庚午"),
            gender = "女",
            solarLabel = "t",
            termLabel = "t",
        )
        assertTrue("丙克庚" in bingGeng.stemRelations, bingGeng.stemRelations)
    }
}
