package zhiqiu.iztro.ui

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/** 跨平台「今天」阳历，对齐运限「今」按钮 */
fun currentSolarDate(): String {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return "${today.year}-${today.month.number}-${today.day}"
}

data class SolarDate(val year: Int, val month: Int, val day: Int) {
    fun toIso(): String =
        "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

    fun plusDays(delta: Int): SolarDate {
        var y = year
        var m = month
        var d = day + delta
        while (d > daysInMonth(y, m)) {
            d -= daysInMonth(y, m)
            m++
            if (m > 12) { m = 1; y++ }
        }
        while (d < 1) {
            m--
            if (m < 1) { m = 12; y-- }
            d += daysInMonth(y, m)
        }
        return SolarDate(y, m, d)
    }

    fun plusMonths(delta: Int): SolarDate {
        var y = year
        var m = month + delta
        while (m > 12) { m -= 12; y++ }
        while (m < 1) { m += 12; y-- }
        val maxDay = daysInMonth(y, m)
        return SolarDate(y, m, minOf(day, maxDay))
    }

    fun plusYears(delta: Int): SolarDate {
        val y = year + delta
        return SolarDate(y, month, minOf(day, daysInMonth(y, month)))
    }

    fun isBefore(other: SolarDate): Boolean = when {
        year != other.year -> year < other.year
        month != other.month -> month < other.month
        else -> day < other.day
    }

    fun isAfter(other: SolarDate): Boolean = other.isBefore(this)

    companion object {
        fun parse(raw: String): SolarDate {
            val parts = raw.trim().split(Regex("[\\sT-]")).filter { it.isNotEmpty() }
            return SolarDate(
                parts[0].toInt(),
                parts.getOrElse(1) { "1" }.toInt(),
                parts.getOrElse(2) { "1" }.toInt(),
            )
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 30
}

val CHINESE_HOURS = listOf(
    "早子时", "丑时", "寅时", "卯时", "辰时", "巳时",
    "午时", "未时", "申时", "酉时", "戌时", "亥时",
)
