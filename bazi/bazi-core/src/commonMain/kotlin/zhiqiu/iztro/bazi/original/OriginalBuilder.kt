package zhiqiu.iztro.bazi.original

import com.tyme.culture.Element
import com.tyme.sixtycycle.EarthBranch
import com.tyme.sixtycycle.HeavenStem
import com.tyme.sixtycycle.SixtyCycle

data class HideStemView(
    val stem: String,
    val element: String,
    val tenGod: String,
)

data class PillarView(
    val title: String,
    val stemGod: String,
    val stem: String,
    val branch: String,
    val hideStems: List<HideStemView>,
    val nayin: String,
    val kongWang: String,
    val diShi: String,
    val ziZuo: String,
    val shenSha: List<String>,
)

data class ElementPhase(
    val element: String,
    val phase: String, // 旺相休囚死
)

data class OriginalChart(
    val solarLabel: String,
    val termLabel: String,
    val gender: String,
    val pillars: List<PillarView>,
    val stemRelations: String,
    val branchRelations: String,
    val elementPhases: List<ElementPhase>,
)

object OriginalBuilder {

    fun build(
        eightChar: com.tyme.eightchar.EightChar,
        gender: String,
        solarLabel: String,
        termLabel: String,
    ): OriginalChart {
        val dayStem = eightChar.getDay().getHeavenStem()
        val dayBranch = eightChar.getDay().getEarthBranch()
        val monthBranch = eightChar.getMonth().getEarthBranch()
        val yearBranch = eightChar.getYear().getEarthBranch()
        val male = gender == "男"

        val cycles = listOf(
            "年柱" to eightChar.getYear(),
            "月柱" to eightChar.getMonth(),
            "日柱" to eightChar.getDay(),
            "时柱" to eightChar.getHour(),
        )

        val pillars = cycles.map { (title, cycle) ->
            buildPillar(
                title = title,
                cycle = cycle,
                dayStem = dayStem,
                dayBranch = dayBranch,
                monthBranch = monthBranch,
                yearBranch = yearBranch,
                male = male,
                isDayPillar = title == "日柱",
            )
        }

        return OriginalChart(
            solarLabel = solarLabel,
            termLabel = termLabel,
            gender = gender,
            pillars = pillars,
            stemRelations = stemRelations(cycles.map { it.second.getHeavenStem() }),
            branchRelations = branchRelations(cycles.map { it.second.getEarthBranch() }),
            elementPhases = wangXiangXiuQiuSi(monthBranch),
        )
    }

    fun buildPillar(
        title: String,
        cycle: SixtyCycle,
        dayStem: HeavenStem,
        dayBranch: EarthBranch,
        monthBranch: EarthBranch,
        yearBranch: EarthBranch,
        male: Boolean,
        isDayPillar: Boolean,
    ): PillarView {
        val stem = cycle.getHeavenStem()
        val branch = cycle.getEarthBranch()
        val stemGod = if (isDayPillar) {
            if (male) "男主" else "女主"
        } else {
            dayStem.getTenStar(stem).getName()
        }

        val hides = branch.getHideHeavenStems().map { hide ->
            val hs = hide.getHeavenStem()
            HideStemView(
                stem = hs.getName(),
                element = hs.getElement().getName(),
                tenGod = dayStem.getTenStar(hs).getName(),
            )
        }

        val kong = classicalKongWang(cycle)

        return PillarView(
            title = title,
            stemGod = stemGod,
            stem = stem.getName(),
            branch = branch.getName(),
            hideStems = hides,
            nayin = cycle.getSound().getName(),
            kongWang = kong,
            // 地势：日主在各柱地支上的长生状态
            diShi = dayStem.getTerrain(branch).getName(),
            // 自坐：本柱天干坐本柱地支
            ziZuo = stem.getTerrain(branch).getName(),
            shenSha = ShenShaTables.forPillar(
                pillar = cycle,
                dayStem = dayStem,
                dayBranch = dayBranch,
                monthBranch = monthBranch,
                yearBranch = yearBranch,
                genderMale = male,
            ),
        )
    }

    fun stemRelations(stems: List<HeavenStem>): String {
        val parts = linkedSetOf<String>()
        for (i in stems.indices) {
            for (j in i + 1 until stems.size) {
                val a = stems[i]
                val b = stems[j]
                parts += stemPairLabels(a, b)
            }
        }
        return parts.joinToString(" · ").ifEmpty { "无特殊天干作用" }
    }

    /**
     * 天干：合、冲、克。
     * 克仅取同性（阴阳相同）五行相克，如丁克辛；丁不克庚。
     * 合/冲成立时不再叠克。
     */
    private fun stemPairLabels(a: HeavenStem, b: HeavenStem): List<String> {
        val pair = orderedStemPair(a.getName(), b.getName())
        val (p, q) = pair
        val out = mutableListOf<String>()
        var strong = false
        if (a.getCombine() == b || b.getCombine() == a) {
            val hua = a.combine(b)?.getName() ?: b.combine(a)?.getName().orEmpty()
            out += if (hua.isNotEmpty()) "${p}${q}合化$hua" else "${p}${q}相合"
            strong = true
        }
        if (pair in stemChongOrdered) {
            out += "${p}${q}相冲"
            strong = true
        }
        if (!strong) {
            samePolarityStemKe(a, b)?.let { out += it }
        }
        return out
    }

    /** 天干同性相克：甲戊、乙己、丙庚、丁辛、戊壬、己癸、庚甲、辛乙、壬丙、癸丁 */
    private fun samePolarityStemKe(a: HeavenStem, b: HeavenStem): String? {
        if (a.getYinYang() != b.getYinYang()) return null
        return when {
            a.getElement().getRestrain() == b.getElement() -> "${a.getName()}克${b.getName()}"
            b.getElement().getRestrain() == a.getElement() -> "${b.getName()}克${a.getName()}"
            else -> null
        }
    }

    /** 五合口诀顺序：甲己、乙庚、丙辛、丁壬、戊癸 */
    private fun orderedStemPair(x: String, y: String): Pair<String, String> {
        val order = listOf(
            "甲" to "己", "乙" to "庚", "丙" to "辛", "丁" to "壬", "戊" to "癸",
        )
        for ((a, b) in order) {
            if ((x == a && y == b) || (x == b && y == a)) return a to b
        }
        val chong = listOf("甲" to "庚", "乙" to "辛", "丙" to "壬", "丁" to "癸")
        for ((a, b) in chong) {
            if ((x == a && y == b) || (x == b && y == a)) return a to b
        }
        return if (x <= y) x to y else y to x
    }

    private val stemChongOrdered = setOf(
        "甲" to "庚",
        "乙" to "辛",
        "丙" to "壬",
        "丁" to "癸",
    )

    fun branchRelations(branches: List<EarthBranch>): String {
        val parts = linkedSetOf<String>()
        val names = branches.map { it.getName() }
        val nameSet = names.toSet()

        // 成组：三合 / 半合 / 三会 / 三刑
        appendSanHeSanHui(parts, names)
        appendSanXing(parts, nameSet, names)

        // 成对：合/冲/刑/害/暗合/克（刑与害可并存；有合冲则不再叠克）
        for (i in branches.indices) {
            for (j in i + 1 until branches.size) {
                val a = branches[i]
                val b = branches[j]
                val pair = orderedBranchPair(a.getName(), b.getName())
                parts += pairRelationLabels(a, b, pair)
            }
        }

        return parts.joinToString(" · ").ifEmpty { "无特殊地支作用" }
    }

    private fun appendSanHeSanHui(parts: MutableSet<String>, names: List<String>) {
        val sanHe = listOf(
            listOf("申", "子", "辰") to "水",
            listOf("寅", "午", "戌") to "火",
            listOf("巳", "酉", "丑") to "金",
            listOf("亥", "卯", "未") to "木",
        )
        for ((group, wx) in sanHe) {
            val hit = group.filter { it in names }
            when (hit.size) {
                3 -> parts += "${group.joinToString("")}三合$wx"
                2 -> {
                    val mid = group[1]
                    if (mid in hit) {
                        parts += "${hit.sortedBy { group.indexOf(it) }.joinToString("")}半合$wx"
                    }
                }
            }
        }
        val sanHui = listOf(
            listOf("寅", "卯", "辰") to "木",
            listOf("巳", "午", "未") to "火",
            listOf("申", "酉", "戌") to "金",
            listOf("亥", "子", "丑") to "水",
        )
        for ((group, wx) in sanHui) {
            if (group.all { it in names }) parts += "${group.joinToString("")}三会$wx"
        }
    }

    /** 寅巳申三刑、丑未戌三刑；两支见则已在成对里写相刑，三支齐全再标三刑 */
    private fun appendSanXing(parts: MutableSet<String>, nameSet: Set<String>, names: List<String>) {
        val wuEn = listOf("寅", "巳", "申")
        if (wuEn.all { it in nameSet }) parts += "寅巳申三刑"
        val shiShi = listOf("丑", "未", "戌")
        if (shiShi.all { it in nameSet }) parts += "丑未戌三刑"

        // 自刑：辰午酉亥同支再见
        for (z in listOf("辰", "午", "酉", "亥")) {
            if (names.count { it == z } >= 2) parts += "${z}${z}自刑"
        }
    }

    /**
     * 成对关系：六合/冲优先；刑与害可同时列出；另列暗合；
     * 无合冲刑害暗合时再列五行相克。
     */
    private fun pairRelationLabels(
        a: EarthBranch,
        b: EarthBranch,
        pair: Pair<String, String>,
    ): List<String> {
        val (p, q) = pair
        val out = mutableListOf<String>()
        var strong = false
        if (a.getCombine() == b) {
            val hua = a.combine(b)?.getName().orEmpty()
            out += if (hua.isNotEmpty()) "${p}${q}合化$hua" else "${p}${q}六合"
            strong = true
        }
        if (a.getOpposite() == b) {
            out += "${p}${q}相冲"
            strong = true
        }
        if (isXiangXing(p, q)) out += "${p}${q}相刑"
        if (a.getHarm() == b) out += "${p}${q}相害"
        if (isAnHe(p, q)) out += "${p}${q}暗合"
        if (!strong && out.none { it.endsWith("相刑") || it.endsWith("相害") || it.endsWith("暗合") }) {
            when {
                a.getElement().getRestrain() == b.getElement() -> out += "${a.getName()}克${b.getName()}"
                b.getElement().getRestrain() == a.getElement() -> out += "${b.getName()}克${a.getName()}"
            }
        }
        return out
    }

    private fun isXiangXing(x: String, y: String): Boolean {
        val pairs = setOf(
            "子" to "卯", "卯" to "子",
            "寅" to "巳", "巳" to "寅",
            "巳" to "申", "申" to "巳",
            "寅" to "申", "申" to "寅",
            "丑" to "未", "未" to "丑",
            "未" to "戌", "戌" to "未",
            "丑" to "戌", "戌" to "丑",
        )
        return (x to y) in pairs
    }

    private fun isAnHe(x: String, y: String): Boolean {
        val pairs = setOf(
            "丑" to "寅", "寅" to "丑",
            "卯" to "申", "申" to "卯",
            "午" to "亥", "亥" to "午",
            "子" to "巳", "巳" to "子",
        )
        return (x to y) in pairs
    }

    /**
     * 地支常用口诀顺序：
     * 六冲子午、丑未、寅申、卯酉、辰戌、巳亥
     * 六合子丑、寅亥、卯戌、辰酉、巳申、午未
     * 暗合丑寅、卯申、午亥、子巳
     * 相刑子卯、寅巳、巳申、寅申、丑未、未戌、丑戌
     */
    private fun orderedBranchPair(x: String, y: String): Pair<String, String> {
        val presets = listOf(
            "子" to "午", "丑" to "未", "寅" to "申", "卯" to "酉", "辰" to "戌", "巳" to "亥",
            "子" to "丑", "寅" to "亥", "卯" to "戌", "辰" to "酉", "巳" to "申", "午" to "未",
            "丑" to "寅", "卯" to "申", "午" to "亥", "子" to "巳",
            "子" to "卯", "寅" to "巳", "巳" to "申", "丑" to "戌", "未" to "戌",
            "子" to "未", "丑" to "午", "寅" to "巳", "卯" to "辰", "申" to "亥", "酉" to "戌",
        )
        for ((a, b) in presets) {
            if ((x == a && y == b) || (x == b && y == a)) return a to b
        }
        return if (x <= y) x to y else y to x
    }

    /** 旺相休囚死：旺=当令，相=我生，休=生我，囚=克我，死=我克 */
    private fun wangXiangXiuQiuSi(monthBranch: EarthBranch): List<ElementPhase> {
        val wang = monthBranch.getElement()
        val xiang = wang.getReinforce()
        val xiu = wang.getReinforced()
        val qiu = wang.getRestrained()
        val si = wang.getRestrain()
        return listOf(
            ElementPhase(wang.getName(), "旺"),
            ElementPhase(xiang.getName(), "相"),
            ElementPhase(xiu.getName(), "休"),
            ElementPhase(qiu.getName(), "囚"),
            ElementPhase(si.getName(), "死"),
        )
    }

    /**
     * 经典旬空：按六十甲子所在旬，未配地支即为空亡。
     * （不直接用 tyme getExtraEarthBranches，避免个别版本偏差）
     */
    internal fun classicalKongWang(cycle: SixtyCycle): String {
        val idx = cycle.getIndex()
        val xunStart = (idx / 10) * 10
        val startBranch = xunStart % 12
        val used = (0 until 10).map { EarthBranch.NAMES[(startBranch + it) % 12] }.toSet()
        return EarthBranch.NAMES.filter { it !in used }.joinToString("")
    }
}
