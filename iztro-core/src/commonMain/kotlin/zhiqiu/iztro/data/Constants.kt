package zhiqiu.iztro.data

val LANGUAGES = listOf("en-US", "ja-JP", "ko-KR", "zh-CN", "zh-TW", "vi-VN")

val HEAVENLY_STEMS = listOf(
    "jiaHeavenly", "yiHeavenly", "bingHeavenly", "dingHeavenly", "wuHeavenly",
    "jiHeavenly", "gengHeavenly", "xinHeavenly", "renHeavenly", "guiHeavenly",
)

val EARTHLY_BRANCHES = listOf(
    "ziEarthly", "chouEarthly", "yinEarthly", "maoEarthly", "chenEarthly", "siEarthly",
    "wuEarthly", "weiEarthly", "shenEarthly", "youEarthly", "xuEarthly", "haiEarthly",
)

val ZODIAC = listOf(
    "rat", "ox", "tiger", "rabbit", "dragon", "snake",
    "horse", "sheep", "monkey", "rooster", "dog", "pig",
)

val PALACES = listOf(
    "soulPalace", "parentsPalace", "spiritPalace", "propertyPalace", "careerPalace",
    "friendsPalace", "surfacePalace", "healthPalace", "wealthPalace", "childrenPalace",
    "spousePalace", "siblingsPalace",
)

val GENDER = mapOf("male" to "阳", "female" to "阴")

enum class FiveElementsClass(val value: Int) {
    water2nd(2),
    wood3rd(3),
    metal4th(4),
    earth5th(5),
    fire6th(6);

    companion object {
        fun fromKey(key: String): FiveElementsClass =
            entries.first { it.name == key }
    }
}

val CHINESE_TIME = listOf(
    "earlyRatHour", "oxHour", "tigerHour", "rabbitHour", "dragonHour", "snakeHour",
    "horseHour", "goatHour", "monkeyHour", "roosterHour", "dogHour", "pigHour", "lateRatHour",
)

val TIME_RANGE = listOf(
    "00:00~01:00", "01:00~03:00", "03:00~05:00", "05:00~07:00", "07:00~09:00", "09:00~11:00",
    "11:00~13:00", "13:00~15:00", "15:00~17:00", "17:00~19:00", "19:00~21:00", "21:00~23:00", "23:00~00:00",
)

val TIGER_RULE = mapOf(
    "jiaHeavenly" to "bingHeavenly", "yiHeavenly" to "wuHeavenly", "bingHeavenly" to "gengHeavenly",
    "dingHeavenly" to "renHeavenly", "wuHeavenly" to "jiaHeavenly", "jiHeavenly" to "bingHeavenly",
    "gengHeavenly" to "wuHeavenly", "xinHeavenly" to "gengHeavenly", "renHeavenly" to "renHeavenly",
    "guiHeavenly" to "jiaHeavenly",
)

val RAT_RULE = mapOf(
    "jiaHeavenly" to "jiaHeavenly", "yiHeavenly" to "bingHeavenly", "bingHeavenly" to "wuHeavenly",
    "dingHeavenly" to "gengHeavenly", "wuHeavenly" to "renHeavenly", "jiHeavenly" to "jiaHeavenly",
    "gengHeavenly" to "bingHeavenly", "xinHeavenly" to "wuHeavenly", "renHeavenly" to "gengHeavenly",
    "guiHeavenly" to "renHeavenly",
)

val MUTAGEN = listOf("sihuaLu", "sihuaQuan", "sihuaKe", "sihuaJi")

val CALENDAR_HEAVENLY_STEMS = listOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
val CALENDAR_EARTHLY_BRANCHES = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
val MONTHLY_EARTHLY_BRANCHES = listOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")
val FIVE_TIGER = listOf("丙", "戊", "庚", "壬", "甲", "丙", "戊", "庚", "壬", "甲")
