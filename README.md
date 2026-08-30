# ComposeIztro

基于 [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) 的跨平台**紫微斗数**排盘与渲染引擎，附带**八字**排盘模块（`bazi-core` / `bazi-ui`），支持 Android、iOS、Desktop (JVM)、Web (Wasm) 四个平台。

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

## 八字模块 (`bazi-core` / `bazi-ui`)

跨平台**八字**排盘模块，与紫微同一套工程结构：

| 模块 | 内容 | 依赖 |
|---|---|---|
| `bazi/bazi-core` | 四柱、藏干、十神、神煞、空亡、地势、干支作用、大运/流年/流月、四柱反查 | 仅 `tyme4kt` + `kotlinx-datetime` |
| `bazi/bazi-ui` | Compose 盘面：原局页、流盘页、五行配色 | `bazi-core` + Compose |

设计原则：**无宿主概念**（输入输出都是纯数据）、**页面无导航**（Tab 组织由宿主实现，参考 `androidApp` / `desktopApp` 演示）。接入方式：

```kotlin
// settings.gradle.kts
include(":bazi-core")
project(":bazi-core").projectDir = file("bazi/bazi-core")

include(":bazi-ui")
project(":bazi-ui").projectDir = file("bazi/bazi-ui")
```

### 排原局

```kotlin
import com.tyme.solar.SolarTime
import zhiqiu.iztro.bazi.original.OriginalBuilder
import zhiqiu.iztro.bazi.original.formatBirthTermLabel
import zhiqiu.iztro.bazi.original.formatSolarTimeLabel

val birth = SolarTime(2000, 8, 16, 3, 30, 0)          // 寅时（时辰取中点）
val eight = birth.getLunarHour().getEightChar()

val chart = OriginalBuilder.build(
    eightChar = eight,
    gender = "女",
    solarLabel = formatSolarTimeLabel(birth),          // 真太阳时：2000年8月16日 03:30
    termLabel = formatBirthTermLabel(birth),           // 出生节气：出生于立秋(…)后X天X小时
)
// chart.pillars：四柱（干支/藏干/十神/纳音/空亡/地势/神煞）
// chart.stemRelations / branchRelations：天干五合·冲·克、地支三合·三会·六冲·刑·害·暗合
// chart.elementPhases：五行旺相休囚死
```

### 排流盘（大运 / 流年 / 流月）

```kotlin
import zhiqiu.iztro.bazi.flow.FlowBuilder
import zhiqiu.iztro.bazi.flow.FlowSelection

// selection 传 null 时自动定位到当前日期；点选后把新 FlowSelection 传回重排
val flow = FlowBuilder.build(
    birth = birth,
    gender = "女",
    selection = FlowSelection(decadeIndex = 0, yearIndex = 0, monthIndex = 0),
)
// flow.pillars：七柱（年月日时 + 大运 + 流年 + 流月）
// flow.decades / years / months：三级选择器数据
```

### 四柱反查公历

```kotlin
import zhiqiu.iztro.bazi.lookup.BaziPillars
import zhiqiu.iztro.bazi.lookup.StemBranch
import zhiqiu.iztro.bazi.lookup.reverseLookup

val pillars = BaziPillars(
    year = StemBranch("庚", "辰"),
    month = StemBranch("甲", "申"),
    day = StemBranch("丙", "子"),
    hour = StemBranch("庚", "寅"),
)
val candidates = reverseLookup(pillars, yearFrom = 1940, yearTo = 2030)
// candidates：List<BaziCandidate>（公历日期 + 农历日期 + 时辰序号 + 四柱）
// 反查前可用 validatePillars(pillars) 校验五虎遁 / 五鼠遁
```

### Compose 盘面

```kotlin
import zhiqiu.iztro.bazi.ui.BaziFlowPage
import zhiqiu.iztro.bazi.ui.BaziOriginalPage

// 原局页（宿主自己排盘后传入）
BaziOriginalPage(chart = chart)

// 流盘页（选择变化通过回调上抛，宿主重排后传入新 chart）
var selection by remember { mutableStateOf<FlowSelection?>(null) }
val flow = remember(selection) { FlowBuilder.build(birth, "女", selection) }
BaziFlowPage(chart = flow, onSelectionChange = { selection = it })
```

页面 Tab 组织（原局 / 流盘切换、紫微 / 八字切换）由宿主实现——参考 `androidApp` 与 `desktopApp` 演示（顶栏「紫微斗数 / 八字」Tab，八字页内「原局 / 流盘」Tab，示例命造与紫微演示一致：2000-8-16 寅时 女）。

## 以 Git Submodule 方式引入

本仓库的每个模块（`:iztro-core`、`:shared`、`:bazi-core`、`:bazi-ui`）都是标准的 Gradle 子工程，
因此可以不发布到 Maven，而是作为 **Git submodule** 直接挂到你的 Compose Multiplatform 工程里使用。

> 仓库地址：https://github.com/aacai/ComposeIztro

### 1. 添加 submodule

```bash
git submodule add https://github.com/aacai/ComposeIztro thirdparty/ComposeIztro
git submodule update --init --recursive
```

上面把本仓库挂到宿主工程根目录下的 `thirdparty/ComposeIztro`（路径可自定义）。

### 2. 在宿主的 `settings.gradle.kts` 中引入所需模块

```kotlin
// settings.gradle.kts
include(":iztro-core")
project(":iztro-core").projectDir = file("thirdparty/ComposeIztro/iztro-core")

include(":shared")
project(":shared").projectDir = file("thirdparty/ComposeIztro/shared")

// 八字模块按需引入
include(":bazi-core")
project(":bazi-core").projectDir = file("thirdparty/ComposeIztro/bazi/bazi-core")
include(":bazi-ui")
project(":bazi-ui").projectDir = file("thirdparty/ComposeIztro/bazi/bazi-ui")
```

`shared` 通过 project 引用依赖 `iztro-core`、`bazi-ui` 依赖 `bazi-core`，因此二者需一并引入，否则 Gradle 解析依赖时找不到对应工程。

### 3. 版本目录（`libs.versions.toml`）对齐

各模块通过宿主工程的 `gradle/libs.versions.toml` 解析插件与依赖版本，版本不一致会报插件/依赖找不到或版本冲突。
请保证宿主版本目录包含以下关键条目（与子模块保持一致）：

| 条目 | 版本 |
|---|---|
| kotlin | 2.4.10 |
| composeMultiplatform | 1.11.1 |
| agp | 9.0.1 |
| androidx-lifecycle | 2.11.0-beta01 |
| material3 | 1.11.0-alpha07 |
| kotlinx-datetime | 0.7.1 |
| tyme4kt | 1.5.0 |

最简单的做法：直接合并 `thirdparty/ComposeIztro/gradle/libs.versions.toml` 中的 `[versions]`/`[libraries]`/`[plugins]` 到宿主版本目录。
插件别名（`libs.plugins.*`）需能在宿主根 `build.gradle.kts` 的 `pluginManagement` 中解析（本仓库已用别名声明）。

### 4. 在宿主模块里声明依赖

```kotlin
// 任意 KMP 模块（如 :composeApp）的 build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))      // 紫微 UI 组件
            // implementation(project(":iztro-core")) // 仅需算法时
            // implementation(project(":bazi-ui"))
        }
    }
}
```

### 5. 平台接入注意事项

- **Android**：`iztro-core`/`bazi-core` 已声明 `namespace`，宿主无需重复；`minSdk ≥ 24`。
- **Web (Wasm)**：`iztro-core` 与 `shared` 仅提供 `wasmJs` target（与 `tyme4kt` 一致），宿主需 `wasmJs { browser() }`，并用 `@OptIn(ExperimentalWasmDsl::class)` 开启。
- **iOS**：各模块产出静态 framework（`baseName = "Shared"`），在宿主 iOS 工程中按常规 Compose 方式链接。
- **Desktop (JVM)**：直接 `jvm()` 即可。

### 6. 仅用算法（不引入 UI）

若只想要排盘数据、不需要 Compose 组件，只需引入 `:iztro-core`（紫微）或 `:bazi-core`（八字），
二者均为纯 Kotlin Multiplatform 库、不依赖 UI 层，用法见上文「核心引擎」与「八字模块」。

## 许可证

MIT License
