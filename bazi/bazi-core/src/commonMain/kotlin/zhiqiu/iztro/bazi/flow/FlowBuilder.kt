package zhiqiu.iztro.bazi.flow

import com.tyme.eightchar.ChildLimit
import com.tyme.eightchar.DecadeFortune
import com.tyme.enums.Gender
import com.tyme.sixtycycle.EarthBranch
import com.tyme.sixtycycle.HeavenStem
import com.tyme.sixtycycle.SixtyCycle
import com.tyme.sixtycycle.SixtyCycleYear
import com.tyme.solar.SolarTerm
import com.tyme.solar.SolarTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import zhiqiu.iztro.bazi.original.OriginalBuilder
import zhiqiu.iztro.bazi.original.PillarView
import zhiqiu.iztro.bazi.original.formatBirthTermLabel
import zhiqiu.iztro.bazi.original.formatSolarTimeLabel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class DecadeOption(
    val index: Int,
    val startAge: Int,
    val endAge: Int,
    val startYear: Int,
    val endYear: Int,
    val cycle: SixtyCycle,
    val stemGod: String,
    val branchGod: String,
)

data class YearOption(
    val year: Int,
    val age: Int,
    val cycle: SixtyCycle,
    val stemGod: String,
    val branchGod: String,
    val hideBrief: String,
)

data class MonthOption(
    val index: Int,
    val label: String,
    val termName: String,
    val cycle: SixtyCycle,
    val stemGod: String,
    val branchGod: String,
)

data class FlowSelection(
    val decadeIndex: Int,
    val yearIndex: Int,
    val monthIndex: Int,
)

data class FlowChart(
    val solarLabel: String,
    val termLabel: String,
    val gender: String,
    val qiYunLabel: String,
    val pillars: List<PillarView>,
    val decades: List<DecadeOption>,
    val years: List<YearOption>,
    val months: List<MonthOption>,
    val selection: FlowSelection,
    val natalStemRelations: String,
    val luckStemRelations: String,
    val natalBranchRelations: String,
    val luckBranchRelations: String,
)

object FlowBuilder {

    private const val DecadeCount = 12

    @OptIn(ExperimentalTime::class)
    fun build(
        birth: SolarTime,
        gender: String,
        selection: FlowSelection? = null,
        now: SolarTime = currentSolarTime(),
    ): FlowChart {
        val tymeGender = if (gender == "男") Gender.MAN else Gender.WOMAN
        val child = ChildLimit.fromSolarTime(birth, tymeGender)
        val eightChar = child.getEightChar()
        val male = tymeGender == Gender.MAN
        val dayStem = eightChar.getDay().getHeavenStem()
        val dayBranch = eightChar.getDay().getEarthBranch()
        val monthBranch = eightChar.getMonth().getEarthBranch()
        val yearBranch = eightChar.getYear().getEarthBranch()

        val decades = (0 until DecadeCount).map { i ->
            val d = DecadeFortune.fromChildLimit(child, i)
            val cycle = d.getSixtyCycle()
            DecadeOption(
                index = i,
                startAge = d.getStartAge(),
                endAge = d.getEndAge(),
                startYear = d.getStartSixtyCycleYear().getYear(),
                endYear = d.getEndSixtyCycleYear().getYear(),
                cycle = cycle,
                stemGod = dayStem.getTenStar(cycle.getHeavenStem()).getName(),
                branchGod = dayStem.getTenStar(cycle.getEarthBranch().getHideHeavenStemMain()).getName(),
            )
        }

        val defaultDecade = defaultDecadeIndex(decades, now.year)
        val decadeIndex = selection?.decadeIndex?.coerceIn(0, decades.lastIndex) ?: defaultDecade
        val decade = decades[decadeIndex]
        val decadeFortune = DecadeFortune.fromChildLimit(child, decadeIndex)

        val years = (0 until 10).map { i ->
            val y = decadeFortune.getStartSixtyCycleYear().next(i)
            val cycle = y.getSixtyCycle()
            val age = decade.startAge + i
            YearOption(
                year = y.getYear(),
                age = age,
                cycle = cycle,
                stemGod = dayStem.getTenStar(cycle.getHeavenStem()).getName(),
                branchGod = dayStem.getTenStar(cycle.getEarthBranch().getHideHeavenStemMain()).getName(),
                hideBrief = cycle.getEarthBranch().getHideHeavenStems()
                    .joinToString("") { it.getHeavenStem().getName() },
            )
        }

        val defaultYear = years.indexOfFirst { it.year == now.year }.takeIf { it >= 0 }
            ?: years.indexOfFirst { it.year >= now.year }.takeIf { it >= 0 }
            ?: 0
        val yearIndex = selection?.yearIndex?.coerceIn(0, years.lastIndex) ?: defaultYear
        val yearOpt = years[yearIndex]
        val yearCycles = SixtyCycleYear.fromYear(yearOpt.year)

        val months = yearCycles.getMonths().mapIndexed { i, m ->
            val term = SolarTerm(yearOpt.year, 3 + m.getIndexInYear() * 2)
            val day = term.getSolarDay()
            val cycle = m.getSixtyCycle()
            MonthOption(
                index = i,
                label = "${day.month}.${day.day}",
                termName = term.getName(),
                cycle = cycle,
                stemGod = dayStem.getTenStar(cycle.getHeavenStem()).getName(),
                branchGod = dayStem.getTenStar(cycle.getEarthBranch().getHideHeavenStemMain()).getName(),
            )
        }

        val defaultMonth = defaultMonthIndex(months, now)
        val monthIndex = selection?.monthIndex?.coerceIn(0, months.lastIndex) ?: defaultMonth
        val monthOpt = months[monthIndex]

        val natalCycles = listOf(
            "年柱" to eightChar.getYear(),
            "月柱" to eightChar.getMonth(),
            "日柱" to eightChar.getDay(),
            "时柱" to eightChar.getHour(),
        )
        val luckCycles = listOf(
            "大运" to decade.cycle,
            "流年" to yearOpt.cycle,
            "流月" to monthOpt.cycle,
        )

        val pillars = (natalCycles + luckCycles).map { (title, cycle) ->
            OriginalBuilder.buildPillar(
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

        val natalStems = natalCycles.map { it.second.getHeavenStem() }
        val natalBranches = natalCycles.map { it.second.getEarthBranch() }
        val luckStems = luckCycles.map { it.second.getHeavenStem() }
        val luckBranches = luckCycles.map { it.second.getEarthBranch() }

        val end = child.getEndTime()
        val qiYunLabel = formatQiYunLabel(end, child)

        return FlowChart(
            solarLabel = formatSolarTimeLabel(birth),
            termLabel = formatBirthTermLabel(birth),
            gender = gender,
            qiYunLabel = qiYunLabel,
            pillars = pillars,
            decades = decades,
            years = years,
            months = months,
            selection = FlowSelection(decadeIndex, yearIndex, monthIndex),
            natalStemRelations = OriginalBuilder.stemRelations(natalStems),
            luckStemRelations = luckStemRelations(natalStems, luckStems),
            natalBranchRelations = OriginalBuilder.branchRelations(natalBranches),
            luckBranchRelations = luckBranchRelations(natalBranches, luckBranches),
        )
    }

    private fun luckStemRelations(
        natal: List<HeavenStem>,
        luck: List<HeavenStem>,
    ): String {
        val all = OriginalBuilder.stemRelations(natal + luck)
        val luckNames = luck.map { it.getName() }.toSet()
        val kept = all.split(" · ").filter { part ->
            luckNames.any { it in part }
        }
        return kept.joinToString(" · ").ifEmpty { "无特殊天干作用" }
    }

    private fun luckBranchRelations(
        natal: List<EarthBranch>,
        luck: List<EarthBranch>,
    ): String {
        val all = OriginalBuilder.branchRelations(natal + luck)
        val luckNames = luck.map { it.getName() }.toSet()
        val kept = all.split(" · ").filter { part ->
            luckNames.any { name -> part.contains(name) }
        }
        return kept.joinToString(" · ").ifEmpty { "无特殊地支作用" }
    }

    private fun defaultDecadeIndex(decades: List<DecadeOption>, year: Int): Int {
        val hit = decades.indexOfFirst { year in it.startYear..it.endYear }
        return if (hit >= 0) hit else 0
    }

    private fun defaultMonthIndex(months: List<MonthOption>, now: SolarTime): Int {
        val md = now.month * 100 + now.day
        var best = 0
        for (i in months.indices) {
            val parts = months[i].label.split('.')
            if (parts.size == 2) {
                val m = parts[0].toIntOrNull() ?: continue
                val d = parts[1].toIntOrNull() ?: continue
                val key = m * 100 + d
                if (key <= md) best = i
            }
        }
        return best
    }

    private fun formatQiYunLabel(end: SolarTime, child: ChildLimit): String {
        val y = end.year
        val m = end.month.toString().padStart(2, '0')
        val d = end.day.toString().padStart(2, '0')
        val years = child.getYearCount()
        val months = child.getMonthCount()
        val ageText = if (months > 0) "$years.$months" else "$years"
        return "${y}年${m}月${d}日 (${ageText})岁 起运"
    }

    @OptIn(ExperimentalTime::class)
    private fun currentSolarTime(): SolarTime {
        val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return SolarTime(
            local.year,
            local.month.number,
            local.day,
            local.hour,
            local.minute,
            local.second,
        )
    }
}
