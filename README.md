# ComposeIztro

基于 [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) 的跨平台**紫微斗数**排盘与渲染引擎，支持 Android、iOS、Desktop (JVM)、Web (Wasm) 四个平台。

## 功能特性

### 紫微斗数核心引擎

- **排盘算法**：支持「通行版」和「中州派」两种排盘算法
- **三盘支持**：天盘、地盘、人盘
- **星曜系统**：14 主星、14 辅星、杂曜、长生十二神、博士十二神、四化飞星
- **宫位系统**：命宫、兄弟、夫妻、子女、财帛、疾厄、迁移、交友、官禄、田宅、福德、父母
- **运限系统**：大限、童限、流年、流月、流日、流时
- **日历转换**：公历 ↔ 农历互转，天干地支，四柱八字
- **五行局**：水二局、木三局、金四局、土五局、火六局

### 跨平台 UI

- 12 宫位网格布局（标准命盘格式）
- 星曜颜色区分、四化标记、自化高亮
- 宫位焦点/悬浮高亮，三方四正指示线
- 中央信息面板（姓名、日期、四柱、运限控制）
- 多语言：简体中文、繁体中文、英文、日文、韩文、越南文

## 使用方式

### 核心引擎 (`iztro-core`)

`iztro-core` 是一个纯 Kotlin Multiplatform 库，可独立使用，不依赖 UI 层。

#### 通过公历排盘

```kotlin
import zhiqiu.iztro.astro.bySolar

val astrolabe = bySolar(
    solarDate = "2000-8-16",
    timeIndex = 2,       // 时辰索引 0-12
    gender = "女",
)

// 获取命盘基本信息
println(astrolabe.soul)                    // 命主
println(astrolabe.body)                    // 身主
println(astrolabe.zodiac)                  // 生肖
println(astrolabe.sign)                    // 星座
println(astrolabe.fiveElementsClass)       // 五行局
println(astrolabe.chineseDate)             // 干支纪年

// 遍历十二宫
astrolabe.palaces.forEach { palace ->
    println("${palace.name} (${palace.heavenlyStem}${palace.earthlyBranch})")
    palace.majorStars.forEach { star ->
        println("  ${star.name} ${star.brightness ?: ""} ${star.mutagen ?: ""}")
    }
}
```

#### 通过农历排盘

```kotlin
import zhiqiu.iztro.astro.byLunar

val astrolabe = byLunar(
    lunarDateStr = "2000-7-17",
    timeIndex = 2,
    gender = "男",
    isLeapMonth = false,
)
```

#### 运限查询

```kotlin
import zhiqiu.iztro.astro.horoscope

val horoscope = astrolabe.horoscope(targetSolarDate = "2025-8-28")

// 大限
println("大限: ${horoscope.decadal.heavenlyStem}${horoscope.decadal.earthlyBranch}")
println("年龄: ${horoscope.decadal.range}")

// 流年
println("流年: ${horoscope.yearly.heavenlyStem}${horoscope.yearly.earthlyBranch}")

// 流月
println("流月: ${horoscope.monthly.heavenlyStem}${horoscope.monthly.earthlyBranch}")
```

#### 配置选项

```kotlin
import zhiqiu.iztro.astro.Config
import zhiqiu.iztro.astro.config

// 设置全局配置
config(Config(
    yearDivide = "exact",       // "normal" 立春分界 | "exact" 农历正月初一分界
    horoscopeDivide = "exact",  // "normal" 月序分界 | "exact" 节气分界
    algorithm = "zhongzhou",    // "default" 通行版 | "zhongzhou" 中州派
))
```

#### 工具函数

```kotlin
import zhiqiu.iztro.astro.getZodiacBySolarDate
import zhiqiu.iztro.astro.getSignBySolarDate
import zhiqiu.iztro.astro.getMajorStarBySolarDate

getZodiacBySolarDate("2000-8-16")     // "龙"
getSignBySolarDate("2000-8-16")       // "狮子座"
getMajorStarBySolarDate("2000-8-16", 2) // 命宫主星
```

### 跨平台 UI (`shared`)

`shared` 模块提供开箱即用的 Compose 命盘组件，直接集成到任意 Compose Multiplatform 项目中。

#### 基本用法

```kotlin
import zhiqiu.iztro.ui.Iztrolabe

@Composable
fun MyApp() {
    Iztrolabe(
        birthday = "2000-8-16",
        birthTime = 2,
        gender = "女",
        name = "示例",
    )
}
```

#### 完整参数

```kotlin
@Composable
fun MyApp() {
    Iztrolabe(
        birthday = "2000-8-16",
        birthTime = 2,                    // 时辰索引 0-12
        gender = "女",
        birthdayType = "solar",           // "solar" 公历 | "lunar" 农历
        isLeapMonth = false,              // 农历闰月标记
        fixLeap = true,                   // 闰月修复
        lang = "zh-CN",                   // zh-CN / zh-TW / en-US / ja-JP / ko-KR / vi-VN
        name = "示例",
        astroType = "heaven",             // "heaven" 天盘 | "earth" 地盘 | "人盘"
        horoscopeDate = "2025-8-28",      // 运限目标日期
        horoscopeHour = 0,                // 运限时辰
        centerPalaceAlign = false,        // 中央面板对齐方式
    )
}
```

#### 使用 Props 对象

```kotlin
import zhiqiu.iztro.IztrolabeProps

@Composable
fun MyApp() {
    val props = IztrolabeProps(
        birthday = "2000-8-16",
        birthTime = 2,
        gender = "女",
        name = "示例",
        lang = "zh-CN",
        options = IztroConfig(
            algorithm = "default",
            yearDivide = "normal",
        ),
    )

    Iztrolabe(props = props)
}
```

#### 状态管理

```kotlin
import zhiqiu.iztro.IztroInput
import zhiqiu.iztro.rememberIztro

@Composable
fun MyApp() {
    val state = rememberIztro(
        input = IztroInput(
            birthday = "2000-8-16",
            birthTime = 2,
            gender = "女",
        )
    )

    // state.chart    — 命盘数据
    // state.horoscope — 运限数据
    // state.setHoroscope(date, hour) — 切换运限日期
}
```

### 仅使用计算函数

如果只需要排盘计算结果，不使用 UI 组件，可以直接引入 `iztro-core`：

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.zhiqiu:iztro-core:0.1.0-SNAPSHOT")
}
```

## API 兼容性

本项目的 API 设计与 [react-iztro](https://github.com/zhiqiu/iztro-kmp) 保持兼容，可作为 React 版本的 Compose Multiplatform 替代方案。

## 许可证

MIT License
