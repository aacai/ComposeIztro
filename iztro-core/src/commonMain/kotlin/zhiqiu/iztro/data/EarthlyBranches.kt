package zhiqiu.iztro.data

data class EarthlyBranchInfo(
    val yinYang: String,
    val fiveElements: String,
    val crash: String,
    val soul: String,
    val body: String,
    val inside: String,
    val outside: String,
    val healthTip: String,
)

val earthlyBranches = mapOf(
    "ziEarthly" to EarthlyBranchInfo("阳", "水", "wuEarthly", "tanlangMaj", "huoxingMin", "胆", "下体", "生殖系统、膀胱、尿道之疾病，听觉障碍"),
    "chouEarthly" to EarthlyBranchInfo("阴", "土", "weiEarthly", "jumenMaj", "tianxiangMaj", "肝", "小腿、脚（右）", "胸部、肋膜炎、胃病、脚部"),
    "yinEarthly" to EarthlyBranchInfo("阳", "木", "shenEarthly", "lucunMin", "tianliangMaj", "肺", "大腿（右）", "胆囊、关节、胫部、神经痛、风湿"),
    "maoEarthly" to EarthlyBranchInfo("阴", "木", "youEarthly", "wenquMin", "tiantongMaj", "大肠", "腰（右）、背", "肝病、颜面神经、失眠、神经衰弱"),
    "chenEarthly" to EarthlyBranchInfo("阳", "土", "xuEarthly", "lianzhenMaj", "wenchangMin", "胃", "胸、胳膊（右）", "消化系统、脊椎、皮肤疾病"),
    "siEarthly" to EarthlyBranchInfo("阴", "火", "haiEarthly", "wuquMaj", "tianjiMaj", "脾", "左肩", "喉头、牙病、感冒"),
    "wuEarthly" to EarthlyBranchInfo("阳", "火", "ziEarthly", "pojunMaj", "huoxingMin", "心", "头", "心脏、视觉、味觉障碍、火难"),
    "weiEarthly" to EarthlyBranchInfo("阴", "土", "chouEarthly", "wuquMaj", "tianxiangMaj", "小肠", "脸", "消化系统、胰脏、健忘症、疲倦、手腕、嘴唇"),
    "shenEarthly" to EarthlyBranchInfo("阳", "金", "yinEarthly", "lianzhenMaj", "tianliangMaj", "膀胱", "胸、胳膊（左）", "呼吸系统、肺部、消化系统、大肠"),
    "youEarthly" to EarthlyBranchInfo("阴", "金", "maoEarthly", "wenquMin", "tiantongMaj", "肾", "腰（左）、腹", "吐血、痢血、小肠之疾、脑出血、头腕部"),
    "xuEarthly" to EarthlyBranchInfo("阳", "土", "chenEarthly", "lucunMin", "wenchangMin", "心包", "大腿（左）", "下半身之疾、子宫、痔疮、脚部"),
    "haiEarthly" to EarthlyBranchInfo("阴", "水", "siEarthly", "jumenMaj", "tianjiMaj", "三焦", "小腿、脚（左）", "排泄机能障碍、肾脏、尿道、偏头痛"),
)
