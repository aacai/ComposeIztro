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
    var snapshot by remember {
        mutableStateOf<Pair<DemoChart, DemoHoroscope>?>(null)
    }
    var horoscopeDate by remember {
        mutableStateOf(initialHoroscopeDate ?: currentSolarDate())
    }
    var horoscopeHour by remember {
        mutableIntStateOf(initialHoroscopeHour ?: 0)
    }

    // 本命排盘 + 运限计算一次算完再发布：避免「旧 chart + 新 horoscope」的中间态与两次整盘重组。
    // 计算期间保留旧快照，新盘就绪前不出现「排盘中…」跳变。
    LaunchedEffect(input, horoscopeDate, horoscopeHour) {
        val next = withContext(Dispatchers.Default) {
            val natal = createDemoChart(input)
            natal to computeHoroscope(natal, horoscopeDate, horoscopeHour)
        }
        snapshot = next
    }

    val ready = snapshot
    val natal = ready?.first
    val h = ready?.second
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
