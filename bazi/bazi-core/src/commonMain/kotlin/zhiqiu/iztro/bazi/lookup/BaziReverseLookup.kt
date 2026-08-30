package zhiqiu.iztro.bazi.lookup

import com.tyme.solar.SolarTime

/**
 * 四柱反查公历候选日期。
 *
 * 只依赖 [com.tyme]（历法换算），无宿主概念：
 * - 年柱按立春分界、月柱按节气（tyme 八字口径，对应「exact」选项）；
 * - 时柱五鼠遁、月柱五虎遁校验内置；
 * - 早子时（timeIndex 0）与晚子时（timeIndex 12）分别按当日 00:30 / 23:30 参与匹配。
 */
data class StemBranch(
    val stem: String,
    val branch: String,
) {
    fun format(): String = "$stem$branch"

    companion object {
        fun parse(text: String): StemBranch? {
            val t = text.trim()
            if (t.length < 2) return null
            val stem = t.substring(0, 1)
            val branch = t.substring(1, 2)
            if (stem !in STEMS || branch !in BRANCHES) return null
            return StemBranch(stem, branch)
        }
    }
}

data class BaziPillars(
    val year: StemBranch,
    val month: StemBranch,
    val day: StemBranch,
    val hour: StemBranch,
)

data class BaziCandidate(
    val solarDate: String,
    val lunarDate: String,
    val timeIndex: Int,
    val pillars: BaziPillars,
)

internal val STEMS = listOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
internal val BRANCHES = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

/** 节气月支序：寅正月起 */
private val MONTH_BRANCHES = listOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")

/** 五鼠遁：日干 → 子时天干 */
private val FIVE_RAT = listOf("甲", "丙", "戊", "庚", "壬", "甲", "丙", "戊", "庚", "壬")

/** 五虎遁：年干 → 正月天干 */
private val FIVE_TIGER = listOf("丙", "戊", "庚", "壬", "甲", "丙", "戊", "庚", "壬", "甲")

fun validateMonthPillar(yearStem: String, month: StemBranch): Boolean {
    val yearIdx = STEMS.indexOf(yearStem)
    val monthBranchIdx = MONTH_BRANCHES.indexOf(month.branch)
    if (yearIdx < 0 || monthBranchIdx < 0) return false
    val startIdx = STEMS.indexOf(FIVE_TIGER[yearIdx])
    val expectedStem = STEMS[(startIdx + monthBranchIdx) % 10]
    return month.stem == expectedStem
}

fun validateHourPillar(dayStem: String, hour: StemBranch): Boolean {
    val dayIdx = STEMS.indexOf(dayStem)
    val hourBranchIdx = BRANCHES.indexOf(hour.branch)
    if (dayIdx < 0 || hourBranchIdx < 0) return false
    val startIdx = STEMS.indexOf(FIVE_RAT[dayIdx])
    val expectedStem = STEMS[(startIdx + hourBranchIdx) % 10]
    return hour.stem == expectedStem
}

fun validatePillars(pillars: BaziPillars): String? {
    if (!validateMonthPillar(pillars.year.stem, pillars.month)) {
        return "月柱与年干不符合五虎遁"
    }
    if (!validateHourPillar(pillars.day.stem, pillars.hour)) {
        return "时柱与日干不符合五鼠遁"
    }
    return null
}

/** 时支 → 可能的 timeIndex（子时对应 0 与 12） */
fun timeIndexesForBranch(branch: String): List<Int> = when (branch) {
    "子" -> listOf(0, 12)
    "丑" -> listOf(1)
    "寅" -> listOf(2)
    "卯" -> listOf(3)
    "辰" -> listOf(4)
    "巳" -> listOf(5)
    "午" -> listOf(6)
    "未" -> listOf(7)
    "申" -> listOf(8)
    "酉" -> listOf(9)
    "戌" -> listOf(10)
    "亥" -> listOf(11)
    else -> emptyList()
}

/**
 * 四柱反查公历候选。按立春分年、节气分月（与主流软件 exact 口径一致）。
 * 每 60 天步进定位日柱，再展开该日可能时辰精确匹配年/月/时柱。
 */
fun reverseLookup(
    pillars: BaziPillars,
    yearFrom: Int = 1940,
    yearTo: Int = 2030,
): List<BaziCandidate> {
    validatePillars(pillars)?.let { return emptyList() }

    val timeIndexes = timeIndexesForBranch(pillars.hour.branch)
    if (timeIndexes.isEmpty()) return emptyList()

    val results = mutableListOf<BaziCandidate>()
    for (year in yearFrom..yearTo) {
        var day = 1
        while (day <= daysInYear(year)) {
            val (month, dom) = dayOfYearToMonthDay(year, day)
            // 正午取日柱（不受早晚子时换日影响），比直接构造 SolarDay 更稳
            val dayCycle = SolarTime(year, month, dom, 12, 0, 0)
                .getLunarHour().getEightChar().getDay()
            val delta = dayPillarDelta(dayCycle, pillars.day)
            if (delta != 0) {
                day += delta
                continue
            }
            for (timeIndex in timeIndexes) {
                val solar = solarAt(year, month, dom, timeIndex)
                val eight = solar.getLunarHour().getEightChar()
                if (matches(eight, pillars)) {
                    results += BaziCandidate(
                        solarDate = "${year}-${month}-${dom}",
                        lunarDate = formatLunarDate(solar),
                        timeIndex = timeIndex,
                        pillars = pillars,
                    )
                }
            }
            day += 60
        }
    }
    return results.distinctBy { "${it.solarDate}-${it.timeIndex}" }
}

/** timeIndex → 该时辰中点时刻（早子 00:30 … 晚子 23:30），与宿主档案口径一致 */
internal fun solarAt(year: Int, month: Int, day: Int, timeIndex: Int): SolarTime =
    SolarTime(year, month, day, maxOf(timeIndex * 2 - 1, 0), 30, 0)

private fun matches(
    eight: com.tyme.eightchar.EightChar,
    target: BaziPillars,
): Boolean =
    eight.getYear().getHeavenStem().getName() == target.year.stem &&
        eight.getYear().getEarthBranch().getName() == target.year.branch &&
        eight.getMonth().getHeavenStem().getName() == target.month.stem &&
        eight.getMonth().getEarthBranch().getName() == target.month.branch &&
        eight.getDay().getHeavenStem().getName() == target.day.stem &&
        eight.getDay().getEarthBranch().getName() == target.day.branch &&
        eight.getHour().getHeavenStem().getName() == target.hour.stem &&
        eight.getHour().getEarthBranch().getName() == target.hour.branch

private fun dayPillarDelta(
    actual: com.tyme.sixtycycle.SixtyCycle,
    target: StemBranch,
): Int {
    val actualIdx = sixtyJiaZiIndex(
        actual.getHeavenStem().getName(),
        actual.getEarthBranch().getName(),
    ) ?: return 1
    val targetIdx = sixtyJiaZiIndex(target.stem, target.branch) ?: return 1
    return (targetIdx - actualIdx + 60) % 60
}

private fun sixtyJiaZiIndex(stem: String, branch: String): Int? {
    val s = STEMS.indexOf(stem)
    val b = BRANCHES.indexOf(branch)
    if (s < 0 || b < 0) return null
    for (i in 0 until 60) {
        if (STEMS[i % 10] == stem && BRANCHES[i % 12] == branch) {
            return i
        }
    }
    return null
}

private fun formatLunarDate(solar: SolarTime): String =
    solar.getLunarHour().getLunarDay().toString().removePrefix("农历")

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

private fun daysInYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

private fun dayOfYearToMonthDay(year: Int, dayOfYear: Int): Pair<Int, Int> {
    val monthDays = intArrayOf(
        31,
        if (isLeapYear(year)) 29 else 28,
        31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
    )
    var remaining = dayOfYear
    for (month in 1..12) {
        val dim = monthDays[month - 1]
        if (remaining <= dim) return month to remaining
        remaining -= dim
    }
    return 12 to monthDays[11]
}
