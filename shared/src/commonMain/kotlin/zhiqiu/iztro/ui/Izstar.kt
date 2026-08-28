package zhiqiu.iztro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zhiqiu.iztro.DemoStar
import zhiqiu.iztro.getMutagensByStem

data class HoroscopeMutagenDisplay(
    val scope: String,
    val mutagen: List<String>,
    val show: Boolean,
)

/**
 * 星曜竖排：
 * - 星名一字一行；亮度黑字无背景
 * - 本命四化：红底白字科权禄忌
 * - 点击宫位后：按该宫干飞星/自化，星名背景着色（禄绿/权棕/科蓝/忌红）
 */
@Composable
fun Izstar(
    star: DemoStar,
    palaceHeavenlyStem: String,
    activeHeavenlyStem: String?,
    hoverHeavenlyStem: String?,
    horoscopeMutagens: List<HoroscopeMutagenDisplay>,
    modifier: Modifier = Modifier,
    /** 杂曜等低级星：横向一字排开，不竖排 */
    horizontal: Boolean = false,
) {
    val style = LocalAstrolabeStyle.current
    val theme = IztroTheme
    val starColor = remember(star.type, star.name, theme) { starNameColor(star, theme) }

    // 仅在点击宫位（或点宫干）激活后着色；默认原盘无自化底色
    val flyMutagenIndex = remember(activeHeavenlyStem, star.name) {
        activeHeavenlyStem?.let { getMutagensByStem(it).indexOf(star.name).takeIf { i -> i >= 0 } }
    }
    val hoverMutagenIndex = remember(hoverHeavenlyStem, star.name) {
        hoverHeavenlyStem?.let { getMutagensByStem(it).indexOf(star.name).takeIf { i -> i >= 0 } }
    }

    val flyActive = flyMutagenIndex != null
    val chipBg = if (flyActive) theme.selfMutagenBgs[flyMutagenIndex] else Color.Transparent
    val nameColor = if (flyActive) Color.White else starColor
    val hoverBarColor = hoverMutagenIndex?.let { theme.selfMutagenBgs[it] }
    val barHeight = 3.dp
    val density = LocalDensity.current
    // 竖排星曜（主星/辅星）名字统一字号；仅横向杂曜保持较小字号
    val nameSize = if (horizontal) style.adjStarSp else style.majorStarSp
    val nameLineHeight = nameSize * 1.15f
    val brightSize = style.brightSp
    val brightLineHeight = brightSize * 1.2f
    val nameWeight = if (star.type == "major") FontWeight.Bold else FontWeight.SemiBold
    val brightColor = theme.focus

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .drawBehind {
                if (hoverBarColor != null && !flyActive) {
                    val h = with(density) { barHeight.toPx() }
                    drawRect(
                        color = hoverBarColor,
                        topLeft = Offset(0f, size.height - h),
                        size = Size(size.width, h),
                    )
                }
            },
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // 仅星名带飞星/自化背景（需先点宫）
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(chipBg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                if (horizontal) {
                    Text(
                        text = star.name,
                        fontSize = nameSize,
                        fontWeight = nameWeight,
                        color = nameColor,
                        lineHeight = nameLineHeight,
                        maxLines = 1,
                    )
                } else {
                    star.name.forEach { ch ->
                        Text(
                            text = ch.toString(),
                            fontSize = nameSize,
                            fontWeight = nameWeight,
                            color = nameColor,
                            lineHeight = nameLineHeight,
                            maxLines = 1,
                        )
                    }
                }
            }

            // 亮度：跟在星名正下方；行高留足避免裁切
            star.brightness?.takeIf { it.isNotEmpty() }?.let { b ->
                if (horizontal) {
                    Text(
                        text = b,
                        fontSize = brightSize,
                        color = brightColor,
                        lineHeight = brightLineHeight,
                        maxLines = 1,
                    )
                } else {
                    b.forEach { ch ->
                        Text(
                            text = ch.toString(),
                            fontSize = brightSize,
                            color = brightColor,
                            lineHeight = brightLineHeight,
                            maxLines = 1,
                        )
                    }
                }
            }

            star.mutagen?.takeIf { it.isNotEmpty() }?.let { mutagen ->
                Text(
                    text = mutagen,
                    fontSize = 11.sp,
                    color = Color.White,
                    lineHeight = 13.sp,
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(theme.mutagenBadgeRed)
                        .padding(horizontal = 2.dp, vertical = 0.dp),
                )
            }

            horoscopeMutagens.forEach { item ->
                if (item.show) {
                    val idx = item.mutagen.indexOf(star.name)
                    if (idx >= 0) {
                        Text(
                            text = MUTAGEN_LABELS[idx],
                            fontSize = 10.sp,
                            color = Color.White,
                            lineHeight = 12.sp,
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(theme.scopeColor(item.scope).copy(alpha = 0.75f))
                                .padding(horizontal = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

private val MUTAGEN_LABELS = listOf("禄", "权", "科", "忌")

/** 主星红；羊陀火铃空劫蓝；辅弼魁钺（含昌曲）紫；其余偏灰 */
private fun starNameColor(star: DemoStar, theme: IztroColors): Color = when (star.type) {
    "major" -> theme.starMajorRed
    "tough" -> theme.starToughBlue
    "soft" -> theme.starSoftPurple
    "lucun" -> theme.awesome
    "tianma" -> theme.active
    "flower" -> theme.happy
    "helper" -> theme.nice
    else -> theme.textMuted
}
