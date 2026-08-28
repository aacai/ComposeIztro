package zhiqiu.iztro

data class DemoStar(
    val name: String,
    val type: String = "major",
    val scope: String = "origin",
    val brightness: String? = null,
    val mutagen: String? = null,
)

data class DemoLunarRaw(
    val lunarYear: Int,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeap: Boolean,
)

data class DemoPalace(
    val index: Int,
    val name: String,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val isBodyPalace: Boolean,
    val majorStars: List<DemoStar>,
    val minorStars: List<DemoStar>,
    val adjectiveStars: List<DemoStar>,
    val changsheng12: String,
    val boshi12: String,
    val jiangqian12: String,
    val suiqian12: String,
    val ages: List<Int>,
    val decadalStart: Int,
    val decadalEnd: Int,
) {
    val stemBranch: String get() = "$heavenlyStem$earthlyBranch"
    val decadalRangeText: String get() = "$decadalStart - $decadalEnd"
}

data class DemoHoroscopeItem(
    val index: Int,
    val name: String,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val palaceNames: List<String>,
    val mutagen: List<String>,
    val stars: List<List<DemoStar>>,
    val nominalAge: Int? = null,
    val suiqian12: List<String>? = null,
    val jiangqian12: List<String>? = null,
)

data class DemoHoroscope(
    val solarDate: String,
    val lunarDate: String,
    val rawLunar: DemoLunarRaw,
    val decadal: DemoHoroscopeItem,
    val age: DemoHoroscopeItem,
    val yearly: DemoHoroscopeItem,
    val monthly: DemoHoroscopeItem,
    val daily: DemoHoroscopeItem,
    val hourly: DemoHoroscopeItem,
)

data class DemoFourPillar(
    val stem: String,
    val branch: String,
)

data class DemoChart(
    val solarDate: String,
    val lunarDate: String,
    val rawLunar: DemoLunarRaw,
    val chineseDate: String,
    val pillars: List<DemoFourPillar> = emptyList(),
    /** 节气四柱（立春换年、节气换月） */
    val jieqiPillars: List<DemoFourPillar> = emptyList(),
    val fiveElementsClass: String,
    val soul: String,
    val body: String,
    val zodiac: String,
    val sign: String,
    val time: String,
    val timeRange: String,
    val timeIndex: Int,
    val gender: String,
    /** 阳男 / 阴男 / 阳女 / 阴女 */
    val genderLabel: String = "",
    val earthlyBranchOfSoulPalace: String,
    val earthlyBranchOfBodyPalace: String,
    val name: String = "",
    val copyright: String = "",
    val lang: String = "zh-CN",
    val palaces: List<DemoPalace>,
) {
    /** 真太阳时展示（暂无经度修正时用排盘阳历+时辰起点） */
    val trueSolarTimeText: String
        get() {
            val start = timeRange.substringBefore("~").ifEmpty {
                val hour = if (timeIndex <= 0) 0 else timeIndex * 2 - 1
                hour.toString().padStart(2, '0') + ":00"
            }
            return "$solarDate $start"
        }
}
fun createDemoChart(input: IztroInput = IztroInput()): DemoChart = AstrolabeBridge.createDemoChart(input)

fun computeHoroscope(chart: DemoChart, targetDate: String, horoscopeHour: Int): DemoHoroscope =
    AstrolabeBridge.computeHoroscope(chart, targetDate, horoscopeHour)

fun surroundedPalaceIndices(index: Int): Set<Int> = AstrolabeBridge.surroundedPalaceIndices(index)

fun getPalaceNames(fromIndex: Int): List<String> = AstrolabeBridge.getPalaceNames(fromIndex)

fun getMutagensByStem(stemDisplayName: String): List<String> = AstrolabeBridge.getMutagensByStem(stemDisplayName)

fun defaultLineIndex(earthlyBranchOfSoulPalace: String): Int =
    AstrolabeBridge.defaultLineIndex(earthlyBranchOfSoulPalace)
