package zhiqiu.iztro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zhiqiu.iztro.ui.currentSolarDate

data class IztroState(
    val chart: DemoChart,
    val horoscope: DemoHoroscope,
    val horoscopeDate: String,
    val horoscopeHour: Int,
    val setHoroscope: (date: String, hour: Int?) -> Unit,
)

/**
 * 对齐 iztro-hook useIztro；外层 key(birthday,birthTime,…) 变化时整盘重建。
 * 本命排盘与运限计算放到 Default 线程，避免切 Tab 时主线程卡顿；未就绪时返回 null。
 */
@Composable
fun rememberIztro(input: IztroInput, initialHoroscopeDate: String? = null, initialHoroscopeHour: Int? = null): IztroState? {
    var chart by remember { mutableStateOf<DemoChart?>(null) }
    var horoscope by remember { mutableStateOf<DemoHoroscope?>(null) }
    var horoscopeDate by remember {
        mutableStateOf(initialHoroscopeDate ?: currentSolarDate())
    }
    var horoscopeHour by remember {
        mutableIntStateOf(initialHoroscopeHour ?: 0)
    }

    LaunchedEffect(input) {
        // 不提前清空：新盘算好前继续显示旧盘，避免「排盘中…」与整盘之间的一次跳变
        val natal = withContext(Dispatchers.Default) { createDemoChart(input) }
        chart = natal
    }

    LaunchedEffect(horoscopeDate, horoscopeHour, chart) {
        val natal = chart ?: return@LaunchedEffect
        val next = withContext(Dispatchers.Default) {
            computeHoroscope(natal, horoscopeDate, horoscopeHour)
        }
        horoscope = next
    }

    val natal = chart
    val h = horoscope
    if (natal == null || h == null) return null
    return IztroState(
        chart = natal,
        horoscope = h,
        horoscopeDate = horoscopeDate,
        horoscopeHour = horoscopeHour,
        setHoroscope = { date, hour ->
            horoscopeDate = date
            hour?.let { horoscopeHour = it }
        },
    )
}
