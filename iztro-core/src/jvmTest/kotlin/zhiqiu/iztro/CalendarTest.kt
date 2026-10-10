package zhiqiu.iztro

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zhiqiu.iztro.calendar.Calendar
import zhiqiu.iztro.calendar.LunarDate

class CalendarTest {
    @Test
    fun solarLunarRoundTrip() {
        for (solar in listOf("2026-8-29", "2024-2-10", "2025-7-25")) {
            val lunar = Calendar.solar2lunar(solar)
            val back = Calendar.lunar2solar(lunar.format(), lunar.isLeap)
            assertEquals(solar, back.toString())
        }
    }

    @Test
    fun lunarChineseFormat_notEmpty() {
        val lunar = Calendar.solar2lunar("2026-8-29")
        assertTrue(LunarDate(lunar.lunarYear, lunar.lunarMonth, lunar.lunarDay, lunar.isLeap).format(useChinese = true).isNotBlank())
    }

    @Test
    fun lunarMonthDays_inRange() {
        val days = Calendar.getTotalDaysOfLunarMonth("2026-8-29")
        assertTrue(days == 29 || days == 30)
    }
}
