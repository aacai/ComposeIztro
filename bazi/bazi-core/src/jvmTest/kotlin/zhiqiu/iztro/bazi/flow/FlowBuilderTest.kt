package zhiqiu.iztro.bazi.flow

import com.tyme.eightchar.ChildLimit
import com.tyme.enums.Gender
import com.tyme.solar.SolarTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowBuilderTest {

    private val birth = SolarTime(2000, 8, 16, 3, 30, 0)
    private val gender = "女"
    private val now = SolarTime(2026, 8, 10, 12, 0, 0)

    @Test
    fun childLimit_andDecades_for_2000_08_16_woman() {
        val child = ChildLimit.fromSolarTime(birth, Gender.WOMAN)
        val end = child.getEndTime()
        // 起运应晚于出生，且落在数年内（与截图约 2005 起运同量级）
        assertTrue(end.year in 2002..2008, "qiYun year=${end.year}")
        val first = child.getStartDecadeFortune()
        assertTrue(first.getSixtyCycle().getName().isNotBlank())
        assertTrue(first.getStartAge() >= 1)
        assertEquals(first.getStartAge() + 9, first.getEndAge())
    }

    @Test
    fun flowChart_sevenPillars_andSelectors() {
        val chart = FlowBuilder.build(birth, gender, selection = null, now = now)
        assertEquals(7, chart.pillars.size)
        assertEquals(listOf("年柱", "月柱", "日柱", "时柱", "大运", "流年", "流月"), chart.pillars.map { it.title })
        assertEquals("女主", chart.pillars[2].stemGod)
        assertTrue(chart.decades.size >= 8)
        assertEquals(10, chart.years.size)
        assertEquals(12, chart.months.size)
        assertTrue(chart.qiYunLabel.contains("起运"), chart.qiYunLabel)

        val decade = chart.decades[chart.selection.decadeIndex]
        assertTrue(2026 in decade.startYear..decade.endYear, "decade=${decade.startYear}-${decade.endYear}")
        assertEquals(2026, chart.years[chart.selection.yearIndex].year)

        val picked = FlowBuilder.build(
            birth,
            gender,
            selection = FlowSelection(decadeIndex = 0, yearIndex = 0, monthIndex = 0),
            now = now,
        )
        assertEquals(0, picked.selection.decadeIndex)
        assertEquals(picked.decades[0].cycle.getName(), picked.pillars[4].stem + picked.pillars[4].branch)
    }

    @Test
    fun monthLabel_hasTermName() {
        val chart = FlowBuilder.build(birth, gender, now = now)
        assertTrue(chart.months.any { it.termName == "立秋" }, chart.months.map { it.termName }.toString())
        assertTrue(chart.months.any { it.label.startsWith("8.") }, chart.months.map { it.label }.toString())
    }
}
