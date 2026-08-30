package zhiqiu.iztro.bazi.lookup

import com.tyme.solar.SolarTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaziReverseLookupTest {
    @Test
    fun reverseLookup_findsKnownSolarDate() {
        // 2000-8-16 寅时：用 tyme 正算得到四柱后再反查
        val pillars = pillarsFor("2000-8-16", 2)
        val found = reverseLookup(
            pillars = pillars,
            yearFrom = 2000,
            yearTo = 2000,
        )
        assertTrue(
            found.any { it.solarDate == "2000-8-16" && it.timeIndex == 2 },
            "expected to find 2000-8-16 timeIndex=2 in $found",
        )
    }

    /** 用 tyme 正算四柱（与 reverseLookup 相同引擎，交叉验证日柱步进逻辑） */
    private fun pillarsFor(solarDate: String, timeIndex: Int): BaziPillars {
        val parts = solarDate.split("-").map { it.toInt() }
        val solar = solarAt(parts[0], parts[1], parts[2], timeIndex)
        val eight = solar.getLunarHour().getEightChar()
        fun stem(c: com.tyme.sixtycycle.SixtyCycle) = StemBranch(
            c.getHeavenStem().getName(),
            c.getEarthBranch().getName(),
        )
        return BaziPillars(
            year = stem(eight.getYear()),
            month = stem(eight.getMonth()),
            day = stem(eight.getDay()),
            hour = stem(eight.getHour()),
        )
    }
}
