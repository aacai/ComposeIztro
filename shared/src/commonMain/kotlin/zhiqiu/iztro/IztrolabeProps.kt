package zhiqiu.iztro

typealias Algorithm = String
typealias AstroType = String
typealias BirthdayType = String

/** 对齐 iztro Config，供 UI 层使用 */
data class IztroConfig(
    val mutagens: Map<String, List<String>> = emptyMap(),
    val brightness: Map<String, List<String>> = emptyMap(),
    val yearDivide: String = "normal",
    val horoscopeDivide: String = "normal",
    val ageDivide: String = "normal",
    val dayDivide: String = "forward",
    val algorithm: String = "default",
)

/** 对齐 iztro-hook IztroInput */
data class IztroInput(
    val birthday: String = "2000-8-16",
    val birthTime: Int = 2,
    val gender: String = "女",
    val birthdayType: BirthdayType = "solar",
    val isLeapMonth: Boolean = false,
    val fixLeap: Boolean = true,
    val lang: String = "zh-CN",
    val name: String = "",
    val astroType: AstroType? = "heaven",
    val options: IztroConfig? = null,
)

/** 对齐 react-iztro IztrolabeProps */
data class IztrolabeProps(
    val birthday: String,
    val birthTime: Int,
    val gender: String,
    val birthdayType: BirthdayType = "solar",
    val isLeapMonth: Boolean = false,
    val fixLeap: Boolean = true,
    val lang: String = "zh-CN",
    val name: String = "",
    val options: IztroConfig? = null,
    val astroType: AstroType? = "heaven",
    val horoscopeDate: String? = null,
    val horoscopeHour: Int? = null,
    val centerPalaceAlign: Boolean = false,
) {
    fun toInput(algorithm: Algorithm, resolvedAstroType: AstroType): IztroInput {
        val mergedConfig = (options ?: IztroConfig()).copy(algorithm = algorithm)
        val effectiveAstroType = if (algorithm == "default") "heaven" else resolvedAstroType
        return IztroInput(
            birthday = birthday,
            birthTime = birthTime,
            gender = gender,
            birthdayType = birthdayType,
            isLeapMonth = isLeapMonth,
            fixLeap = fixLeap,
            lang = lang,
            name = name,
            astroType = effectiveAstroType,
            options = mergedConfig,
        )
    }
}
