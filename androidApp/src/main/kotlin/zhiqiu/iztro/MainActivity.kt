package zhiqiu.iztro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tyme.solar.SolarTime
import zhiqiu.iztro.bazi.flow.FlowBuilder
import zhiqiu.iztro.bazi.flow.FlowSelection
import zhiqiu.iztro.bazi.original.OriginalBuilder
import zhiqiu.iztro.bazi.original.formatBirthTermLabel
import zhiqiu.iztro.bazi.original.formatSolarTimeLabel
import zhiqiu.iztro.bazi.ui.BaziFlowPage
import zhiqiu.iztro.bazi.ui.BaziOriginalPage

/** 八字演示：与紫微示例同一人（2000-8-16 寅时 女），bazi-core 排盘 + bazi-ui 盘面 */
@Composable
fun BaziDemo(modifier: Modifier = Modifier) {
    var subTab by remember { mutableIntStateOf(0) }
    val birth = SolarTime(2000, 8, 16, 3, 30, 0)

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = subTab) {
            Tab(selected = subTab == 0, onClick = { subTab = 0 }, text = { Text("原局") })
            Tab(selected = subTab == 1, onClick = { subTab = 1 }, text = { Text("流盘") })
        }
        when (subTab) {
            0 -> {
                val chart = remember {
                    val eight = birth.getLunarHour().getEightChar()
                    OriginalBuilder.build(
                        eightChar = eight,
                        gender = "女",
                        solarLabel = formatSolarTimeLabel(birth),
                        termLabel = formatBirthTermLabel(birth),
                    )
                }
                BaziOriginalPage(chart)
            }
            else -> {
                var selection by remember { mutableStateOf(FlowSelection(0, 0, 0)) }
                val chart = remember(selection) { FlowBuilder.build(birth, "女", selection) }
                BaziFlowPage(chart = chart, onSelectionChange = { selection = it })
            }
        }
    }
}

@Composable
fun AppWithTabs() {
    var page by remember { mutableIntStateOf(0) }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = page) {
                Tab(selected = page == 0, onClick = { page = 0 }, text = { Text("紫微斗数") })
                Tab(selected = page == 1, onClick = { page = 1 }, text = { Text("八字") })
            }
            when (page) {
                0 -> App()
                else -> BaziDemo()
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AppWithTabs()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AppWithTabs()
}
