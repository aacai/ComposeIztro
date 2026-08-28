package zhiqiu.iztro.i18n

typealias HeavenlyStemKey = String
typealias EarthlyBranchKey = String
typealias StarKey = String
typealias PalaceKey = String
typealias FiveElementsClassKey = String
typealias GenderKey = String
typealias BrightnessKey = String
typealias MutagenKey = String

private var currentLanguage: String = "zh-CN"

internal val zhCN = mapOf(
    "jiaHeavenly" to "甲", "yiHeavenly" to "乙", "bingHeavenly" to "丙", "dingHeavenly" to "丁", "wuHeavenly" to "戊",
    "jiHeavenly" to "己", "gengHeavenly" to "庚", "xinHeavenly" to "辛", "renHeavenly" to "壬", "guiHeavenly" to "癸",
    "ziEarthly" to "子", "chouEarthly" to "丑", "yinEarthly" to "寅", "maoEarthly" to "卯", "chenEarthly" to "辰",
    "siEarthly" to "巳", "wuEarthly" to "午", "weiEarthly" to "未", "shenEarthly" to "申", "youEarthly" to "酉",
    "xuEarthly" to "戌", "haiEarthly" to "亥",
    "soulPalace" to "命宫", "bodyPalace" to "身宫", "siblingsPalace" to "兄弟", "spousePalace" to "夫妻",
    "childrenPalace" to "子女", "wealthPalace" to "财帛", "healthPalace" to "疾厄", "surfacePalace" to "迁移",
    "friendsPalace" to "仆役", "careerPalace" to "官禄", "propertyPalace" to "田宅", "spiritPalace" to "福德",
    "parentsPalace" to "父母", "originalPalace" to "来因",
    "water2nd" to "水二局", "wood3rd" to "木三局", "metal4th" to "金四局", "earth5th" to "土五局", "fire6th" to "火六局",
    "male" to "男", "female" to "女",
    "miao" to "庙", "wang" to "旺", "de" to "得", "li" to "利", "ping" to "平", "bu" to "不", "xian" to "陷",
    "sihuaLu" to "禄", "sihuaQuan" to "权", "sihuaKe" to "科", "sihuaJi" to "忌",
    "ziweiMaj" to "紫微", "tianjiMaj" to "天机", "taiyangMaj" to "太阳", "wuquMaj" to "武曲", "tiantongMaj" to "天同",
    "lianzhenMaj" to "廉贞", "tianfuMaj" to "天府", "taiyinMaj" to "太阴", "tanlangMaj" to "贪狼", "jumenMaj" to "巨门",
    "tianxiangMaj" to "天相", "tianliangMaj" to "天梁", "qishaMaj" to "七杀", "pojunMaj" to "破军",
    "zuofuMin" to "左辅", "youbiMin" to "右弼", "wenchangMin" to "文昌", "wenquMin" to "文曲", "lucunMin" to "禄存",
    "tianmaMin" to "天马", "qingyangMin" to "擎羊", "tuoluoMin" to "陀罗", "huoxingMin" to "火星", "lingxingMin" to "铃星",
    "tiankuiMin" to "天魁", "tianyueMin" to "天钺", "dikongMin" to "地空", "dijieMin" to "地劫",
    "jieshaAdj" to "劫杀", "tiankong" to "天空", "tianxing" to "天刑", "tianyao" to "天姚", "jieshen" to "解神",
    "yinsha" to "阴煞", "tianxi" to "天喜", "tianguan" to "天官", "tianfu" to "天福", "tianku" to "天哭", "tianxu" to "天虚",
    "longchi" to "龙池", "fengge" to "凤阁", "hongluan" to "红鸾", "guchen" to "孤辰", "guasu" to "寡宿",
    "feilian" to "蜚廉", "posui" to "破碎", "taifu" to "台辅", "fenggao" to "封诰", "tianwu" to "天巫", "tianyue" to "天月",
    "santai" to "三台", "bazuo" to "八座", "enguang" to "恩光", "tiangui" to "天贵", "tiancai" to "天才", "tianshou" to "天寿",
    "jiekong" to "截空", "xunkong" to "旬空", "kongwang" to "空亡", "jielu" to "截路", "yuede" to "月德",
    "tianshang" to "天伤", "tianshi" to "天使", "tianchu" to "天厨",
    "changsheng" to "长生", "muyu" to "沐浴", "guandai" to "冠带", "linguan" to "临官", "diwang" to "帝旺",
    "shuai" to "衰", "bing" to "病", "si" to "死", "mu" to "墓", "jue" to "绝", "tai" to "胎", "yang" to "养",
    "boshi" to "博士", "lishi" to "力士", "qinglong" to "青龙", "xiaohao" to "小耗", "jiangjun" to "将军",
    "zhoushu" to "奏书", "faylian" to "飞廉", "xishen" to "喜神", "bingfu" to "病符", "dahao" to "大耗",
    "suipo" to "岁破", "fubing" to "伏兵", "guanfu" to "官府",
    "suijian" to "岁建", "huiqi" to "晦气", "sangmen" to "丧门", "guansuo" to "贯索", "gwanfu" to "官符",
    "longde" to "龙德", "baihu" to "白虎", "tiande" to "天德", "diaoke" to "吊客",
    "jiangxing" to "将星", "panan" to "攀鞍", "suiyi" to "岁驿", "xiishen" to "息神", "huagai" to "华盖",
    "jiesha" to "劫煞", "zhaisha" to "灾煞", "tiansha" to "天煞", "zhibei" to "指背", "xianchi" to "咸池",
    "yuesha" to "月煞", "wangshen" to "亡神", "nianjie" to "年解",
    "earlyRatHour" to "早子时", "oxHour" to "丑时", "tigerHour" to "寅时", "rabbitHour" to "卯时",
    "dragonHour" to "辰时", "snakeHour" to "巳时", "horseHour" to "午时", "goatHour" to "未时",
    "monkeyHour" to "申时", "roosterHour" to "酉时", "dogHour" to "戌时", "pigHour" to "亥时", "lateRatHour" to "晚子时",
    "rat" to "鼠", "ox" to "牛", "tiger" to "虎", "rabbit" to "兔", "dragon" to "龙", "snake" to "蛇",
    "horse" to "马", "sheep" to "羊", "monkey" to "猴", "rooster" to "鸡", "dog" to "狗", "pig" to "猪",
    "aries" to "白羊座", "taurus" to "金牛座", "gemini" to "双子座", "cancer" to "巨蟹座", "leo" to "狮子座",
    "virgo" to "处女座", "libra" to "天秤座", "scorpio" to "天蝎座", "sagittarius" to "射手座",
    "capricorn" to "摩羯座", "aquarius" to "水瓶座",     "pisces" to "双鱼座",
    "decadal" to "大限", "childhood" to "童限", "yearly" to "流年", "monthly" to "流月",
    "daily" to "流日", "hourly" to "流时", "turn" to "小限", "bodyPalaceLabel" to "身宫",
    "yunkui" to "运魁", "yunyue" to "运钺", "yunchang" to "运昌", "yunqu" to "运曲",
    "yunluan" to "运鸾", "yunxi" to "运喜", "yunlu" to "运禄", "yunyang" to "运羊",
    "yuntuo" to "运陀", "yunma" to "运马",
    "liukui" to "流魁", "liuyue" to "流钺", "liuchang" to "流昌", "liuqu" to "流曲",
    "liuluan" to "流鸾", "liuxi" to "流喜", "liulu" to "流禄", "liuyang" to "流羊",
    "liutuo" to "流陀", "liuma" to "流马",
    "yuekui" to "月魁", "yueyue" to "月钺", "yuechang" to "月昌", "yuequ" to "月曲",
    "yueluan" to "月鸾", "yuexi" to "月喜", "yuelu" to "月禄", "yueyang" to "月羊",
    "yuetuo" to "月陀", "yuema" to "月马",
    "rikui" to "日魁", "riyue" to "日钺", "richang" to "日昌", "riqu" to "日曲",
    "riluan" to "日鸾", "rixi" to "日喜", "rilu" to "日禄", "riyang" to "日羊",
    "rituo" to "日陀", "rima" to "日马",
    "shikui" to "时魁", "shiyue" to "时钺", "shichang" to "时昌", "shiqu" to "时曲",
    "shiluan" to "时鸾", "shixi" to "时喜", "shilu" to "时禄", "shiyang" to "时羊",
    "shituo" to "时陀", "shima" to "时马",
)

private val translations = mapOf("zh-CN" to zhCN)

fun setLanguage(language: String) {
    currentLanguage = language
}

fun <T> t(key: String): T {
    if (key.isEmpty()) return "" as T
    return (translations[currentLanguage]?.get(key) ?: key) as T
}

fun <T> kot(value: String, suffix: String? = null): T {
    val table = translations[currentLanguage] ?: return value as T
    for ((key, trans) in table) {
        if ((suffix == null || key.contains(suffix, ignoreCase = true)) && trans == value) {
            return key as T
        }
    }
    return value as T
}
