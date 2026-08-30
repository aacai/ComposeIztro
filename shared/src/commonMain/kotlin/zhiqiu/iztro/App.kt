package zhiqiu.iztro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zhiqiu.iztro.ui.IztroColorMode
import zhiqiu.iztro.ui.IztroTheme
import zhiqiu.iztro.ui.IztroThemeProvider
import zhiqiu.iztro.ui.Iztrolabe

@Composable
@Preview
fun App() {
    // Demo：受控夜间模式；也可不传 colorMode，改由中宫「夜间/日间」按钮内部切换
    var colorMode by remember { mutableStateOf(IztroColorMode.Dark) }
    MaterialTheme {
        IztroThemeProvider(mode = colorMode) {
            Column(
                modifier = Modifier
                    .background(IztroTheme.boardBg)
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            ) {
                Iztrolabe(
                    birthday = "2000-8-16",
                    birthTime = 2,
                    gender = "女",
                    birthdayType = "solar",
                    name = "示例",
                    // 示例运限时刻：2026-8-29 13:30（属未时，时辰索引 7）
                    horoscopeDate = "2026-8-29",
                    horoscopeHour = 7,
                    colorMode = colorMode,
                    onColorModeChange = { colorMode = it },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
