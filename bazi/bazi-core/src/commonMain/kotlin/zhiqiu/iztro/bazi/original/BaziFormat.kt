package zhiqiu.iztro.bazi.original

import com.tyme.solar.SolarTime

/** 例：真太阳时：2002年12月14日 03:30 */
fun formatSolarTimeLabel(solarTime: SolarTime): String {
    val y = solarTime.year
    val m = solarTime.month
    val d = solarTime.day
    val h = solarTime.hour.toString().padStart(2, '0')
    val min = solarTime.minute.toString().padStart(2, '0')
    return "真太阳时：${y}年${m}月${d}日 $h:$min"
}

/**
 * 例：出生节气：出生于大雪(2002.12.07 15:14)后6天11小时
 * 按出生时刻相对上一节气时刻的秒差换算，不用「第几天」日序号。
 */
fun formatBirthTermLabel(birth: SolarTime): String {
    val term = birth.getTerm()
    val termTime = term.getJulianDay().getSolarTime()
    val seconds = birth.subtract(termTime).toLong()
    val safeSeconds = seconds.coerceAtLeast(0L)
    val days = safeSeconds / 86400L
    val hours = (safeSeconds % 86400L) / 3600L
    val termStamp =
        "${termTime.year}." +
            "${termTime.month.toString().padStart(2, '0')}." +
            "${termTime.day.toString().padStart(2, '0')} " +
            "${termTime.hour.toString().padStart(2, '0')}:" +
            "${termTime.minute.toString().padStart(2, '0')}"
    return "出生节气：出生于${term.getName()}($termStamp)后${days}天${hours}小时"
}
