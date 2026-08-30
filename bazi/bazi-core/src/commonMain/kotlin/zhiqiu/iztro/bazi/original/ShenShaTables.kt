package zhiqiu.iztro.bazi.original

import com.tyme.sixtycycle.EarthBranch
import com.tyme.sixtycycle.HeavenStem
import com.tyme.sixtycycle.SixtyCycle

/**
 * 八字常用神煞（日干/日支/月支/年支经典口诀）。
 * 只写有明确通行查法的项，避免臆造。
 */
object ShenShaTables {

    fun forPillar(
        pillar: SixtyCycle,
        dayStem: HeavenStem,
        dayBranch: EarthBranch,
        monthBranch: EarthBranch,
        yearBranch: EarthBranch,
        genderMale: Boolean,
    ): List<String> {
        val stem = pillar.getHeavenStem()
        val branch = pillar.getEarthBranch()
        val bn = branch.getName()
        val sn = stem.getName()
        val out = linkedSetOf<String>()

        // —— 贵人类 ——
        tianYiGuiRen(dayStem).forEach { if (bn == it) out += "天乙贵人" }
        if (bn == wenChang(dayStem)) out += "文昌贵人"
        if (bn in taiJiGuiRen(dayStem)) out += "太极贵人"
        if (bn == tianChu(dayStem)) out += "天厨贵人"
        if (bn == guoYin(dayStem)) out += "国印贵人"
        if (bn in fuXing(dayStem)) out += "福星贵人"
        if (bn == jinYu(dayStem)) out += "金舆"
        if (bn == luShen(dayStem)) out += "禄神"
        if (bn == yangRen(dayStem)) out += "羊刃"
        if (bn == feiRen(dayStem)) out += "飞刃"
        if (sn == yueDe(monthBranch)) out += "月德贵人"
        yueDeHe(monthBranch)?.let { if (sn == it) out += "月德合" }
        tianDe(monthBranch)?.let { td ->
            if (sn == td || bn == td) out += "天德贵人"
            tianDeHe(td)?.let { if (sn == it || bn == it) out += "天德合" }
        }
        if (bn == tianYi(monthBranch)) out += "天医"

        // —— 驿马 / 华盖 / 将星 / 桃花 ——
        if (bn == yiMa(yearBranch) || bn == yiMa(dayBranch)) out += "驿马"
        if (bn == huaGai(yearBranch) || bn == huaGai(dayBranch)) out += "华盖"
        if (bn == jiangXing(yearBranch) || bn == jiangXing(dayBranch)) out += "将星"
        if (bn == taoHua(yearBranch) || bn == taoHua(dayBranch)) out += "桃花"
        if (bn == taoHua(yearBranch) || bn == taoHua(dayBranch)) out += "咸池"

        // —— 凶煞类 ——
        if (bn == jieSha(yearBranch) || bn == jieSha(dayBranch)) out += "劫煞"
        if (bn == wangShen(yearBranch) || bn == wangShen(dayBranch)) out += "亡神"
        if (bn == zaiSha(yearBranch) || bn == zaiSha(dayBranch)) out += "灾煞"
        if (bn == liuXia(dayStem)) out += "流霞"
        if (bn == hongYan(dayStem)) out += "红艳"
        if (bn == xueRen(monthBranch)) out += "血刃"
        if (bn == liuE(yearBranch) || bn == liuE(dayBranch)) out += "六厄"

        val (gou, jiao) = gouJiao(yearBranch)
        if (bn == gou) out += "勾绞"
        if (bn == jiao) out += "勾绞"

        // —— 孤寡 / 鸾喜 / 丧吊披 ——
        val (gu, gua) = guChenGuaSu(yearBranch)
        if (bn == gu) out += "孤辰"
        if (bn == gua) out += "寡宿"
        val hong = hongLuan(yearBranch)
        if (bn == hong) out += "红鸾"
        if (bn == EarthBranch(hong).next(6).getName()) out += "天喜"
        if (bn == sangMen(yearBranch)) out += "丧门"
        if (bn == diaoKe(yearBranch)) out += "吊客"
        if (bn == piMa(yearBranch)) out += "披麻"

        // 元辰：阳男阴女与年支逆，阴男阳女与年支顺（前一位）
        if (bn == yuanChen(yearBranch, genderMale, dayStem)) out += "元辰"

        // 天罗地网（按日干五行）
        when (dayStem.getElement().getName()) {
            "火" -> if (bn == "戌" || bn == "亥") out += "天罗"
            "水", "土" -> if (bn == "辰" || bn == "巳") out += "地网"
        }

        // 魁罡（日柱）
        if (pillar.getName() in setOf("戊戌", "庚辰", "庚戌", "壬辰")) out += "魁罡"

        // 孤鸾煞（日柱）
        if (pillar.getName() in setOf("甲寅", "乙巳", "丙午", "丁巳", "戊午", "戊申", "辛亥", "壬子")) {
            out += "孤鸾"
        }

        // 十恶大败（日柱）
        if (pillar.getName() in setOf(
                "甲辰", "乙巳", "壬申", "丙申", "丁亥", "庚辰", "戊戌", "癸亥", "己丑", "辛巳",
            )
        ) {
            out += "十恶大败"
        }

        // 阴差阳错（日柱）
        if (pillar.getName() in setOf(
                "丙子", "丁丑", "戊寅", "辛卯", "壬辰", "癸巳", "丙午", "丁未", "戊申", "辛酉", "壬戌", "癸亥",
            )
        ) {
            out += "阴差阳错"
        }

        return out.toList()
    }

    /** 甲戊庚牛羊，乙己鼠猴乡，丙丁猪鸡位，壬癸蛇兔藏，六辛逢马虎 */
    private fun tianYiGuiRen(dayStem: HeavenStem): List<String> = when (dayStem.getName()) {
        "甲", "戊", "庚" -> listOf("丑", "未")
        "乙", "己" -> listOf("子", "申")
        "丙", "丁" -> listOf("亥", "酉")
        "壬", "癸" -> listOf("巳", "卯")
        "辛" -> listOf("午", "寅")
        else -> emptyList()
    }

    private fun wenChang(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "巳"; "乙" -> "午"; "丙", "戊" -> "申"; "丁", "己" -> "酉"
        "庚" -> "亥"; "辛" -> "子"; "壬" -> "寅"; "癸" -> "卯"
        else -> ""
    }

    /** 甲乙子午中，丙丁卯酉同，戊己辰戌丑未，庚辛寅亥，壬癸巳申 */
    private fun taiJiGuiRen(dayStem: HeavenStem): List<String> = when (dayStem.getName()) {
        "甲", "乙" -> listOf("子", "午")
        "丙", "丁" -> listOf("卯", "酉")
        "戊", "己" -> listOf("辰", "戌", "丑", "未")
        "庚", "辛" -> listOf("寅", "亥")
        "壬", "癸" -> listOf("巳", "申")
        else -> emptyList()
    }

    /** 甲丙食在巳，乙丁食在午，戊食申，己食酉，庚亥辛子，壬寅癸卯 */
    private fun tianChu(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲", "丙" -> "巳"; "乙", "丁" -> "午"; "戊" -> "申"; "己" -> "酉"
        "庚" -> "亥"; "辛" -> "子"; "壬" -> "寅"; "癸" -> "卯"
        else -> ""
    }

    /** 甲戌乙亥丙丑丁寅，戊丑己寅庚辰辛巳，壬未癸申 */
    private fun guoYin(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "戌"; "乙" -> "亥"; "丙" -> "丑"; "丁" -> "寅"
        "戊" -> "丑"; "己" -> "寅"; "庚" -> "辰"; "辛" -> "巳"
        "壬" -> "未"; "癸" -> "申"
        else -> ""
    }

    /** 甲丙寅子，乙癸卯丑，戊己申子，丁庚酉亥？取通行：甲丙寅子、乙丁酉亥、戊己申子、庚壬午寅、辛癸巳卯 */
    private fun fuXing(dayStem: HeavenStem): List<String> = when (dayStem.getName()) {
        "甲", "丙" -> listOf("寅", "子")
        "乙", "丁" -> listOf("酉", "亥")
        "戊", "己" -> listOf("申", "子")
        "庚", "壬" -> listOf("午", "寅")
        "辛", "癸" -> listOf("巳", "卯")
        else -> emptyList()
    }

    private fun yangRen(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "卯"; "乙" -> "辰"; "丙" -> "午"; "丁" -> "未"
        "戊" -> "午"; "己" -> "未"; "庚" -> "酉"; "辛" -> "戌"
        "壬" -> "子"; "癸" -> "丑"
        else -> ""
    }

    /** 飞刃：羊刃对冲 */
    private fun feiRen(dayStem: HeavenStem): String {
        val yr = yangRen(dayStem)
        return if (yr.isEmpty()) "" else EarthBranch(yr).next(6).getName()
    }

    private fun luShen(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "寅"; "乙" -> "卯"; "丙", "戊" -> "巳"; "丁", "己" -> "午"
        "庚" -> "申"; "辛" -> "酉"; "壬" -> "亥"; "癸" -> "子"
        else -> ""
    }

    private fun jinYu(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "辰"; "乙" -> "巳"; "丙", "戊" -> "未"; "丁", "己" -> "申"
        "庚" -> "戌"; "辛" -> "亥"; "壬" -> "丑"; "癸" -> "寅"
        else -> ""
    }

    private fun yueDe(monthBranch: EarthBranch): String = when (monthBranch.getName()) {
        "寅", "午", "戌" -> "丙"
        "申", "子", "辰" -> "壬"
        "亥", "卯", "未" -> "甲"
        "巳", "酉", "丑" -> "庚"
        else -> ""
    }

    /** 月德合：丙合辛、壬合丁、甲合己、庚合乙 */
    private fun yueDeHe(monthBranch: EarthBranch): String? = when (yueDe(monthBranch)) {
        "丙" -> "辛"; "壬" -> "丁"; "甲" -> "己"; "庚" -> "乙"
        else -> null
    }

    private fun tianDe(monthBranch: EarthBranch): String? {
        val byYinMonth = listOf("丁", "申", "壬", "辛", "亥", "甲", "癸", "寅", "丙", "乙", "巳", "庚")
        val monthOrder = listOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")
        val idx = monthOrder.indexOf(monthBranch.getName())
        return if (idx >= 0) byYinMonth[idx] else null
    }

    /** 天德合：干合五合，支取六合 */
    private fun tianDeHe(tianDe: String): String? = when (tianDe) {
        "甲" -> "己"; "乙" -> "庚"; "丙" -> "辛"; "丁" -> "壬"; "戊" -> "癸"
        "己" -> "甲"; "庚" -> "乙"; "辛" -> "丙"; "壬" -> "丁"; "癸" -> "戊"
        "子" -> "丑"; "丑" -> "子"; "寅" -> "亥"; "亥" -> "寅"
        "卯" -> "戌"; "戌" -> "卯"; "辰" -> "酉"; "酉" -> "辰"
        "巳" -> "申"; "申" -> "巳"; "午" -> "未"; "未" -> "午"
        else -> null
    }

    /** 天医：月支前一位（寅月在丑…） */
    private fun tianYi(monthBranch: EarthBranch): String =
        monthBranch.next(-1).getName()

    private fun yiMa(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "寅"
        "寅", "午", "戌" -> "申"
        "巳", "酉", "丑" -> "亥"
        "亥", "卯", "未" -> "巳"
        else -> ""
    }

    private fun taoHua(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "酉"
        "寅", "午", "戌" -> "卯"
        "巳", "酉", "丑" -> "午"
        "亥", "卯", "未" -> "子"
        else -> ""
    }

    private fun huaGai(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "辰"
        "寅", "午", "戌" -> "戌"
        "巳", "酉", "丑" -> "丑"
        "亥", "卯", "未" -> "未"
        else -> ""
    }

    private fun jiangXing(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "子"
        "寅", "午", "戌" -> "午"
        "巳", "酉", "丑" -> "酉"
        "亥", "卯", "未" -> "卯"
        else -> ""
    }

    private fun wangShen(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "亥"
        "寅", "午", "戌" -> "巳"
        "巳", "酉", "丑" -> "申"
        "亥", "卯", "未" -> "寅"
        else -> ""
    }

    private fun jieSha(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "巳"
        "寅", "午", "戌" -> "亥"
        "巳", "酉", "丑" -> "寅"
        "亥", "卯", "未" -> "申"
        else -> ""
    }

    /** 灾煞：将星之冲 */
    private fun zaiSha(branch: EarthBranch): String =
        EarthBranch(jiangXing(branch)).next(6).getName().takeIf { jiangXing(branch).isNotEmpty() }.orEmpty()

    /** 六厄：申子辰见卯，寅午戌见酉，巳酉丑见午，亥卯未见子？通行：申子辰卯、寅午戌酉、巳酉丑午、亥卯未子 */
    private fun liuE(branch: EarthBranch): String = when (branch.getName()) {
        "申", "子", "辰" -> "卯"
        "寅", "午", "戌" -> "酉"
        "巳", "酉", "丑" -> "午"
        "亥", "卯", "未" -> "子"
        else -> ""
    }

    /** 甲酉乙戌丙未丁申戊巳己午庚辰辛卯壬亥癸寅 */
    private fun liuXia(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲" -> "酉"; "乙" -> "戌"; "丙" -> "未"; "丁" -> "申"; "戊" -> "巳"
        "己" -> "午"; "庚" -> "辰"; "辛" -> "卯"; "壬" -> "亥"; "癸" -> "寅"
        else -> ""
    }

    /** 甲乙午，丙寅，丁未，戊己辰，庚戌，辛子，壬子，癸酉 */
    private fun hongYan(dayStem: HeavenStem): String = when (dayStem.getName()) {
        "甲", "乙" -> "午"; "丙" -> "寅"; "丁" -> "未"; "戊", "己" -> "辰"
        "庚" -> "戌"; "辛" -> "子"; "壬" -> "子"; "癸" -> "酉"
        else -> ""
    }

    /** 正月丑二月未…（月支起） */
    private fun xueRen(monthBranch: EarthBranch): String {
        val map = mapOf(
            "寅" to "丑", "卯" to "未", "辰" to "寅", "巳" to "申",
            "午" to "卯", "未" to "酉", "申" to "辰", "酉" to "戌",
            "戌" to "巳", "亥" to "亥", "子" to "午", "丑" to "子",
        )
        return map[monthBranch.getName()].orEmpty()
    }

    /** 勾绞：寅午戌见卯勾丑绞；申子辰见酉勾未绞；亥卯未见子勾戌绞；巳酉丑见午勾辰绞 */
    private fun gouJiao(yearBranch: EarthBranch): Pair<String, String> = when (yearBranch.getName()) {
        "寅", "午", "戌" -> "卯" to "丑"
        "申", "子", "辰" -> "酉" to "未"
        "亥", "卯", "未" -> "子" to "戌"
        "巳", "酉", "丑" -> "午" to "辰"
        else -> "" to ""
    }

    private fun guChenGuaSu(yearBranch: EarthBranch): Pair<String, String> = when (yearBranch.getName()) {
        "亥", "子", "丑" -> "寅" to "戌"
        "寅", "卯", "辰" -> "巳" to "丑"
        "巳", "午", "未" -> "申" to "辰"
        "申", "酉", "戌" -> "亥" to "未"
        else -> "" to ""
    }

    private fun hongLuan(yearBranch: EarthBranch): String = mapOf(
        "子" to "卯", "丑" to "寅", "寅" to "丑", "卯" to "子",
        "辰" to "亥", "巳" to "戌", "午" to "酉", "未" to "申",
        "申" to "未", "酉" to "午", "戌" to "巳", "亥" to "辰",
    )[yearBranch.getName()].orEmpty()

    /** 丧门：年支前二位 */
    private fun sangMen(yearBranch: EarthBranch): String = yearBranch.next(2).getName()

    /** 吊客：年支后二位 */
    private fun diaoKe(yearBranch: EarthBranch): String = yearBranch.next(-2).getName()

    /** 披麻：年支前三位 */
    private fun piMa(yearBranch: EarthBranch): String = yearBranch.next(3).getName()

    /**
     * 元辰：阳干男命 / 阴干女命 → 年支逆一位；阴干男命 / 阳干女命 → 年支顺一位
     */
    private fun yuanChen(yearBranch: EarthBranch, genderMale: Boolean, dayStem: HeavenStem): String {
        val yangStem = dayStem.getIndex() % 2 == 0
        val reverse = (yangStem && genderMale) || (!yangStem && !genderMale)
        return yearBranch.next(if (reverse) -1 else 1).getName()
    }
}
