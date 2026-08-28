package zhiqiu.iztro.calendar

import com.tyme.lunar.LunarDay
import com.tyme.lunar.LunarMonth
import com.tyme.lunar.LunarYear
import com.tyme.sixtycycle.SixtyCycleDay
import com.tyme.sixtycycle.SixtyCycleHour
import com.tyme.solar.SolarDay
import com.tyme.solar.SolarTime
import zhiqiu.iztro.data.CALENDAR_EARTHLY_BRANCHES
import zhiqiu.iztro.data.CALENDAR_HEAVENLY_STEMS
import zhiqiu.iztro.data.FIVE_TIGER
import zhiqiu.iztro.data.MONTHLY_EARTHLY_BRANCHES
import zhiqiu.iztro.data.ZODIAC
import zhiqiu.iztro.utils.fixIndex

data class LunarDate(
    val lunarYear: Int,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeap: Boolean,
) {
    fun format(useChinese: Boolean = false): String {
        if (useChinese) {
            return LunarDay.fromYmd(lunarYear, if (isLeap) -lunarMonth else lunarMonth, lunarDay).toString()
        }
        return "$lunarYear-$lunarMonth-$lunarDay"
    }
}

data class SolarDate(
    val solarYear: Int,
    val solarMonth: Int,
    val solarDay: Int,
) {
    override fun toString(): String = "$solarYear-$solarMonth-$solarDay"
}

typealias StemBranchPair = Pair<String, String>

data class HeavenlyStemAndEarthlyBranchDate(
    val yearly: StemBranchPair,
    val monthly: StemBranchPair,
    val daily: StemBranchPair,
    val hourly: StemBranchPair,
) {
    fun format(): String =
        "${yearly.first}${yearly.second} ${monthly.first}${monthly.second} ${daily.first}${daily.second} ${hourly.first}${hourly.second}"
}

data class StemBranchOptions(
    val year: String = "exact",
    val month: String = "exact",
)

object Calendar {
    private val constellationKeys = listOf(
        "capricorn", "aquarius", "pisces", "aries", "taurus", "gemini",
        "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius",
    )

    fun normalizeDateStr(date: String): List<Int> =
        date.split(Regex("[\\s]+"))
            .flatMap { it.split(Regex("[-:/.]")) }
            .map { kotlin.math.abs(it.toInt()) }

    fun solar2lunar(dateStr: String): LunarDate {
        val parts = normalizeDateStr(dateStr)
        val lunarDay = SolarDay.fromYmd(parts[0], parts[1], parts[2]).getLunarDay()
        val lunarMonth = lunarDay.getLunarMonth()
        val monthWithLeap = lunarMonth.getMonthWithLeap()
        return LunarDate(
            lunarYear = lunarMonth.year,
            lunarMonth = kotlin.math.abs(monthWithLeap),
            lunarDay = lunarDay.day,
            isLeap = monthWithLeap < 0,
        )
    }

    fun lunar2solar(dateStr: String, isLeapMonth: Boolean = false): SolarDate {
        val parts = normalizeDateStr(dateStr)
        val lunarMonthValue = if (isLeapMonth && LunarYear.fromYear(parts[0]).getLeapMonth() == parts[1]) -parts[1] else parts[1]
        val solarDay = LunarDay.fromYmd(parts[0], lunarMonthValue, parts[2]).getSolarDay()
        return SolarDate(solarDay.year, solarDay.month, solarDay.day)
    }

    fun getTotalDaysOfLunarMonth(solarDateStr: String): Int {
        val lunar = solar2lunar(solarDateStr)
        val monthValue = if (lunar.isLeap) -lunar.lunarMonth else lunar.lunarMonth
        return LunarMonth.fromYm(lunar.lunarYear, monthValue).getDayCount()
    }

    fun getSign(solarDateStr: String): String {
        val parts = normalizeDateStr(solarDateStr)
        val index = SolarDay.fromYmd(parts[0], parts[1], parts[2]).getConstellation().getIndex()
        return constellationKeys[index % constellationKeys.size]
    }

    fun getZodiac(earthlyBranchOfYear: String): String {
        val index = CALENDAR_EARTHLY_BRANCHES.indexOf(earthlyBranchOfYear)
        return if (index >= 0) ZODIAC[index] else ZODIAC[0]
    }

    fun getHeavenlyStemAndEarthlyBranchBySolarDate(
        dateStr: String,
        timeIndex: Int,
        options: StemBranchOptions = StemBranchOptions(),
    ): HeavenlyStemAndEarthlyBranchDate {
        val parts = normalizeDateStr(dateStr)
        val hour = maxOf(timeIndex * 2 - 1, 0)
        val solarTime = SolarTime(parts[0], parts[1], parts[2], hour, 30, 0)
        val solarDay = solarTime.getSolarDay()
        val sixtyCycleDay = solarDay.getSixtyCycleDay()
        val lunarDay = solarDay.getLunarDay()
        val lunarMonth = lunarDay.getLunarMonth()

        val yearly = if (options.year == "normal") {
            val cycle = lunarMonth.getLunarYear().getSixtyCycle()
            cycle.getHeavenStem().getName() to cycle.getEarthBranch().getName()
        } else {
            val cycle = sixtyCycleDay.getYear()
            cycle.getHeavenStem().getName() to cycle.getEarthBranch().getName()
        }

        val monthly = if (options.month == "exact") {
            val cycle = sixtyCycleDay.getMonth()
            cycle.getHeavenStem().getName() to cycle.getEarthBranch().getName()
        } else {
            calculateMonthlyGanZhi(yearly.first, lunarDay)
        }

        val dailyCycle = sixtyCycleDay.getSixtyCycle()
        val daily = dailyCycle.getHeavenStem().getName() to dailyCycle.getEarthBranch().getName()

        val hourlyCycle = findHourlyCycle(sixtyCycleDay, timeIndex)
        val hourCycle = hourlyCycle.getSixtyCycle()
        val hourly = hourCycle.getHeavenStem().getName() to hourCycle.getEarthBranch().getName()

        return HeavenlyStemAndEarthlyBranchDate(yearly, monthly, daily, hourly)
    }

    private fun calculateMonthlyGanZhi(yearlyGan: String, lunarDay: LunarDay): StemBranchPair {
        val lunarMonth = lunarDay.getLunarMonth()
        val monthWithLeap = lunarMonth.getMonthWithLeap()
        val fixLeap = if (monthWithLeap < 0 && lunarDay.day > 15) 1 else 0
        val ganIndex = fixIndex(
            CALENDAR_HEAVENLY_STEMS.indexOf(FIVE_TIGER[CALENDAR_HEAVENLY_STEMS.indexOf(yearlyGan)]) +
                kotlin.math.abs(monthWithLeap) - 1 + fixLeap,
            10,
        )
        val zhiIndex = kotlin.math.abs(monthWithLeap) - 1 + fixLeap
        return CALENDAR_HEAVENLY_STEMS[ganIndex] to MONTHLY_EARTHLY_BRANCHES[zhiIndex]
    }

    private fun findHourlyCycle(sixtyCycleDay: SixtyCycleDay, timeIndex: Int): SixtyCycleHour {
        val hours = sixtyCycleDay.getHours()
        val targetHour = maxOf(timeIndex * 2 - 1, 0)
        return hours.firstOrNull { it.getSolarTime().hour == targetHour }
            ?: hours[minOf(timeIndex + 1, hours.lastIndex)]
    }
}
