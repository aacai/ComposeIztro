package zhiqiu.iztro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zhiqiu.iztro.ui.Iztrolabe
import zhiqiu.iztro.ui.currentSolarDate

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(8.dp),
        ) {
            Iztrolabe(
                birthday = "2000-8-16",
                birthTime = 2,
                gender = "女",
                birthdayType = "solar",
                name = "示例",
                horoscopeDate = currentSolarDate(),
                horoscopeHour = 2,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
