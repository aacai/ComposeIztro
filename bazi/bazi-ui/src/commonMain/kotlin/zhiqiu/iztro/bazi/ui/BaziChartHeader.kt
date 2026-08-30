package zhiqiu.iztro.bazi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BaziChartHeader(
    solarLabel: String,
    termLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(text = solarLabel, color = WuXingColors.Muted, fontSize = 11.sp, lineHeight = 13.sp)
        Spacer(modifier = Modifier.height(1.dp))
        Text(text = termLabel, color = WuXingColors.Muted, fontSize = 10.sp, lineHeight = 12.sp)
    }
}
