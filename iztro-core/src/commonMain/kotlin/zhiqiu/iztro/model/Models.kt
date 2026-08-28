package zhiqiu.iztro.model

import zhiqiu.iztro.calendar.HeavenlyStemAndEarthlyBranchDate
import zhiqiu.iztro.calendar.LunarDate

typealias Language = String
typealias Scope = String
typealias StarType = String

data class Star(
    val name: String,
    val type: StarType,
    val scope: Scope,
    val brightness: String? = null,
    val mutagen: String? = null,
)

data class Decadal(
    val range: Pair<Int, Int>,
    val heavenlyStem: String,
    val earthlyBranch: String,
)

data class Palace(
    val index: Int,
    val name: String,
    val isBodyPalace: Boolean,
    val isOriginalPalace: Boolean,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val majorStars: List<Star>,
    val minorStars: List<Star>,
    val adjectiveStars: List<Star>,
    val changsheng12: String,
    val boshi12: String,
    val jiangqian12: String,
    val suiqian12: String,
    val decadal: Decadal,
    val ages: List<Int>,
)

data class SoulAndBody(
    val soulIndex: Int,
    val bodyIndex: Int,
    val heavenlyStemOfSoul: String,
    val earthlyBranchOfSoul: String,
)

data class HoroscopeItem(
    val index: Int,
    val name: String,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val palaceNames: List<String> = emptyList(),
    val mutagen: List<String> = emptyList(),
    val stars: List<List<Star>> = emptyList(),
    val nominalAge: Int? = null,
    val yearlyDecStar: Yearly12? = null,
)

data class Yearly12(
    val suiqian12: List<String>,
    val jiangqian12: List<String>,
)

data class Horoscope(
    val solarDate: String,
    val lunarDate: String,
    val decadal: HoroscopeItem,
    val age: HoroscopeItem,
    val yearly: HoroscopeItem,
    val monthly: HoroscopeItem,
    val daily: HoroscopeItem,
    val hourly: HoroscopeItem,
)

data class Astrolabe(
    val gender: String,
    val solarDate: String,
    val lunarDate: String,
    val chineseDate: String,
    val rawDates: RawDates,
    val timeIndex: Int,
    val time: String,
    val timeRange: String,
    val sign: String,
    val zodiac: String,
    val earthlyBranchOfSoulPalace: String,
    val earthlyBranchOfBodyPalace: String,
    val soul: String,
    val body: String,
    val fiveElementsClass: String,
    val palaces: List<Palace>,
    val copyright: String = "copyright © 2023-2026 iztro-kmp",
)

data class RawDates(
    val lunarDate: LunarDate,
    val chineseDate: HeavenlyStemAndEarthlyBranchDate,
)

data class StemBranchFrom(
    val heavenlyStem: String,
    val earthlyBranch: String,
)

data class AstrolabeParam(
    val solarDate: String,
    val timeIndex: Int,
    val fixLeap: Boolean = true,
    val gender: String? = null,
    val from: StemBranchFrom? = null,
)

data class Config(
    val mutagens: Map<String, List<String>> = emptyMap(),
    val brightness: Map<String, List<String>> = emptyMap(),
    val yearDivide: String = "normal",
    val horoscopeDivide: String = "normal",
    val ageDivide: String = "normal",
    val dayDivide: String = "forward",
    val algorithm: String = "default",
)

data class Option(
    val type: String = "solar",
    val dateStr: String,
    val timeIndex: Int,
    val gender: String,
    val isLeapMonth: Boolean = false,
    val fixLeap: Boolean = true,
    val language: Language? = null,
    val config: Config? = null,
    val astroType: String? = null,
)
