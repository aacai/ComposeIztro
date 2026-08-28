package zhiqiu.iztro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import zhiqiu.iztro.ui.currentSolarDate

data class IztroState(
    val chart: DemoChart,
    val horoscope: DemoHoroscope,
    val horoscopeDate: String,
    val horoscopeHour: Int,
    val setHoroscope: (date: String, hour: Int?) -> Unit,
)

/** 对齐 iztro-hook useIztro；外层 key(birthday,birthTime,…) 变化时整盘重建 */
@Composable
fun rememberIztro(input: IztroInput, initialHoroscopeDate: String? = null, initialHoroscopeHour: Int? = null): IztroState {
    // 随外层 key 重建；同 key 内避免每次重组都重算本命
    val chart = remember { createDemoChart(input) }
    var horoscopeDate by remember {
        mutableStateOf(initialHoroscopeDate ?: currentSolarDate())
    }
    var horoscopeHour by remember {
        mutableIntStateOf(initialHoroscopeHour ?: 0)
    }
    val horoscope = remember(horoscopeDate, horoscopeHour, chart.solarDate, chart.timeIndex) {
        computeHoroscope(chart, horoscopeDate, horoscopeHour)
    }
    val setHoroscope: (String, Int?) -> Unit = { date, hour ->
        horoscopeDate = date
        hour?.let { horoscopeHour = it }
    }
    return IztroState(chart, horoscope, horoscopeDate, horoscopeHour, setHoroscope)
}
